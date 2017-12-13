/*
 * Copyright 2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"). You may
 * not use this file except in compliance with the License. A copy of the
 * License is located at
 *
 *     http://aws.amazon.com/apache2.0/
 *
 * or in the "LICENSE" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.amazonaws.blox.frontend.operations;

import com.amazonaws.blox.frontend.models.DeploymentConfiguration;
import com.amazonaws.blox.frontend.models.InstanceGroup;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;
import lombok.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CreateEnvironment extends EnvironmentController {

  private static final String CLUSTER_PARAM_DESCRIPTION =
      "The short name or full Amazon Resource Name (ARN) of the cluster on which to run your Environment. If you do not specify a cluster, the default cluster is assumed.";
  private static final String CREATE_ENVIRONMENT_NOTES =
      "This call will create a new Environment and EnvironmentRevision, returning that revision's ID.";

  @RequestMapping(method = RequestMethod.POST, consumes = "application/json")
  @ApiOperation(value = "Create a new Environment", notes = CREATE_ENVIRONMENT_NOTES)
  public CreateEnvironmentResponse createEnvironment(
      @ApiParam(name = "cluster", value = CLUSTER_PARAM_DESCRIPTION) @PathVariable String cluster,
      @ApiParam(required = true) @RequestBody CreateEnvironmentRequest request) {

    // TODO: Stub implementation, replace with dataservice call
    String id = UUID.randomUUID().toString();

    return CreateEnvironmentResponse.builder().environmentRevisionId(id).build();
  }

  @Value
  @Builder
  public static class CreateEnvironmentResponse {

    private final String environmentRevisionId;
  }

  @Data
  @Builder
  public static class CreateEnvironmentRequest {

    private String environmentName;
    private String taskDefinition;
    private InstanceGroup instanceGroup;
    private String deploymentType;
    private String deploymentMethod;
    private DeploymentConfiguration deploymentConfiguration;
  }
}