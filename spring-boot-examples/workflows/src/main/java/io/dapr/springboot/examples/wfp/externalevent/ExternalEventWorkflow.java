/*
 * Copyright 2023 The Dapr Authors
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

package io.dapr.springboot.examples.wfp.externalevent;

import io.dapr.workflows.Workflow;
import io.dapr.workflows.WorkflowStub;
import org.springframework.stereotype.Component;

@Component
public class ExternalEventWorkflow implements Workflow {
  @Override
  public WorkflowStub create() {
    return ctx -> {
      ctx.getLogger().info("Starting Workflow: " + ctx.getName());

      ctx.getLogger().info("Waiting for approval...");
      Boolean approved = ctx.waitForExternalEvent("Approval", boolean.class).await();
      Decision decision = null;
      if (approved) {
        ctx.getLogger().info("approval granted - do the approved action");
        decision = ctx.callActivity(ApproveActivity.class.getName(), Decision.class).await();

        ctx.getLogger().info("approval-activity finished");
      } else {
        ctx.getLogger().info("approval denied - send a notification");
        decision = ctx.callActivity(DenyActivity.class.getName(), Decision.class).await();
        ctx.getLogger().info("denied-activity finished");
      }
      ctx.complete(decision);
    };
  }
}
