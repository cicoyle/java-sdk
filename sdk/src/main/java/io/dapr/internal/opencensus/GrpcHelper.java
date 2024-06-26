/*
 * Copyright 2021 The Dapr Authors
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
limitations under the License.
*/

package io.dapr.internal.opencensus;

import io.grpc.Metadata;
import reactor.util.context.Context;
import reactor.util.context.ContextView;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Helper to extract tracing information for gRPC calls.
 */
public final class GrpcHelper {

  private static final Logger LOGGER = Logger.getLogger(GrpcHelper.class.getName());

  /**
   * Binary formatter to generate grpc-trace-bin.
   */
  private static final BinaryFormatImpl OPENCENSUS_BINARY_FORMAT = new BinaryFormatImpl();

  private static final Metadata.Key<byte[]> GRPC_TRACE_BIN_KEY =
      Metadata.Key.of("grpc-trace-bin", Metadata.BINARY_BYTE_MARSHALLER);

  private static final Metadata.Key<String> TRACEPARENT_KEY =
      Metadata.Key.of("traceparent", Metadata.ASCII_STRING_MARSHALLER);

  private static final Metadata.Key<String> TRACESTATE_KEY =
      Metadata.Key.of("tracestate", Metadata.ASCII_STRING_MARSHALLER);

  private GrpcHelper() {
  }

  /**
   * Populates GRPC client's metadata with tracing headers.
   *
   * @param context Reactor's context.
   * @param metadata GRPC client metadata to be populated.
   */
  public static void populateMetadata(final ContextView context, final Metadata metadata) {
    Map<String, Object> map = (context == null ? Context.empty() : context)
        .stream()
        .filter(e -> (e.getKey() != null) && (e.getValue() != null))
        .collect(Collectors.toMap(e -> e.getKey().toString(), e -> e.getValue()));
    if (map.containsKey(GRPC_TRACE_BIN_KEY.name())) {
      byte[] value = (byte[]) map.get(GRPC_TRACE_BIN_KEY.name());
      metadata.put(GRPC_TRACE_BIN_KEY, value);
    }
    if (map.containsKey(TRACEPARENT_KEY.name())) {
      String value = map.get(TRACEPARENT_KEY.name()).toString();
      metadata.put(TRACEPARENT_KEY, value);
    }
    if (map.containsKey(TRACESTATE_KEY.name())) {
      String value = map.get(TRACESTATE_KEY.name()).toString();
      metadata.put(TRACESTATE_KEY, value);
    }

    // Dapr only supports "grpc-trace-bin" for GRPC and OpenTelemetry SDK does not support that yet:
    // https://github.com/open-telemetry/opentelemetry-specification/issues/639
    // This should be the only use of OpenCensus SDK: populate "grpc-trace-bin".
    SpanContext opencensusSpanContext = extractOpenCensusSpanContext(metadata);
    if (opencensusSpanContext != null) {
      byte[] grpcTraceBin = OPENCENSUS_BINARY_FORMAT.toByteArray(opencensusSpanContext);
      metadata.put(GRPC_TRACE_BIN_KEY, grpcTraceBin);
    }
  }

  private static SpanContext extractOpenCensusSpanContext(Metadata metadata) {
    if (!metadata.keys().contains(TRACEPARENT_KEY.name())) {
      // Trying to extract context without this key will throw an "expected" exception, so we avoid it here.
      return null;
    }

    try {
      return TraceContextFormat.extract(metadata);
    } catch (RuntimeException e) {
      LOGGER.log(Level.FINE, "Could not extract span context.", e);
      return null;
    }
  }
}
