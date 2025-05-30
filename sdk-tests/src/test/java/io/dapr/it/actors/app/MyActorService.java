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

package io.dapr.it.actors.app;

import io.dapr.actors.runtime.ActorRuntime;
import io.dapr.it.DaprRunConfig;

// Enable dapr-api-token once runtime supports it in standalone mode.
@DaprRunConfig(enableDaprApiToken = false)
public class MyActorService {
  public static final String SUCCESS_MESSAGE = "dapr initialized. Status: Running";

  /**
   * Starts the service.
   * @param args Expects the port: -p PORT
   * @throws Exception If cannot start service.
   */
  public static void main(String[] args) throws Exception {
    System.out.println("Hello from main() MyActorService");

    long port = Long.parseLong(args[0]);
    ActorRuntime.getInstance().registerActor(MyActorStringImpl.class);
    ActorRuntime.getInstance().registerActor(MyActorBinaryImpl.class);
    ActorRuntime.getInstance().registerActor(MyActorObjectImpl.class);

    TestApplication.start(port);
  }
}
