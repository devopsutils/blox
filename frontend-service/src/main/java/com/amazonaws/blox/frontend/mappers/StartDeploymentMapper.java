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
package com.amazonaws.blox.frontend.mappers;

import com.amazonaws.blox.dataservicemodel.v1.model.wrappers.StartDeploymentRequest;
import com.amazonaws.blox.dataservicemodel.v1.model.wrappers.StartDeploymentResponse;
import com.amazonaws.blox.frontend.operations.StartDeployment;
import com.amazonaws.serverless.proxy.internal.model.ApiGatewayRequestContext;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface StartDeploymentMapper {
  @Mapping(target = "environmentId.accountId", source = "context.accountId")
  @Mapping(target = "environmentId.cluster", source = "cluster")
  @Mapping(target = "environmentId.environmentName", source = "environmentName")
  StartDeploymentRequest toDataServiceRequest(
      ApiGatewayRequestContext context,
      String cluster,
      String environmentName,
      String environmentRevisionId);

  StartDeployment.StartDeploymentResponse fromDataServiceResponse(StartDeploymentResponse response);
}
