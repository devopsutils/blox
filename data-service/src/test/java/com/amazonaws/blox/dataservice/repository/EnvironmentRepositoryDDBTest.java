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
package com.amazonaws.blox.dataservice.repository;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.blox.dataservice.mapper.EnvironmentMapper;
import com.amazonaws.blox.dataservice.model.Environment;
import com.amazonaws.blox.dataservice.model.EnvironmentHealth;
import com.amazonaws.blox.dataservice.model.EnvironmentId;
import com.amazonaws.blox.dataservice.model.EnvironmentStatus;
import com.amazonaws.blox.dataservice.model.EnvironmentType;
import com.amazonaws.blox.dataservice.repository.model.EnvironmentDDBRecord;
import com.amazonaws.blox.dataservicemodel.v1.exception.InternalServiceException;
import com.amazonaws.blox.dataservicemodel.v1.exception.ResourceExistsException;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException;
import java.time.Instant;
import java.util.UUID;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class EnvironmentRepositoryDDBTest {

  private static final String ENVIRONMENT_NAME = "test";
  private static final String ACCOUNT_ID = "12345678912";
  private static final String ROLE_ARN = "arn:aws:iam::" + ACCOUNT_ID + ":role/testRole";
  private static final String CLUSTER = "cluster" + UUID.randomUUID().toString();

  @Mock private DynamoDBMapper dynamoDBMapper;
  @Captor private ArgumentCaptor<EnvironmentDDBRecord> environmentDDBRecordCaptor;

  private Environment environment;
  private EnvironmentRepositoryDDB environmentRepositoryDDB;

  @Before
  public void setup() {
    environmentRepositoryDDB =
        new EnvironmentRepositoryDDB(dynamoDBMapper, Mappers.getMapper(EnvironmentMapper.class));

    environment =
        Environment.builder()
            .environmentId(
                EnvironmentId.builder()
                    .accountId(ACCOUNT_ID)
                    .cluster(CLUSTER)
                    .environmentName(ENVIRONMENT_NAME)
                    .build())
            .role(ROLE_ARN)
            .environmentType(EnvironmentType.Daemon)
            .environmentStatus(EnvironmentStatus.Inactive)
            .environmentHealth(EnvironmentHealth.Healthy)
            .createdTime(Instant.now())
            .lastUpdatedTime(Instant.now())
            .build();
  }

  @Test(expected = NullPointerException.class)
  public void constructorDynamoDbMapperNull() {
    new EnvironmentRepositoryDDB(null, Mappers.getMapper(EnvironmentMapper.class));
  }

  @Test(expected = NullPointerException.class)
  public void constructorEnvironmentMapperNull() {
    new EnvironmentRepositoryDDB(dynamoDBMapper, null);
  }

  @Test(expected = NullPointerException.class)
  public void createEnvironmentNullEnvironment() throws Exception {
    environmentRepositoryDDB.createEnvironment(null);
  }

  @Test(expected = ResourceExistsException.class)
  public void createEnvironmentResourceExistsException() throws Exception {
    doThrow(new ConditionalCheckFailedException(""))
        .when(dynamoDBMapper)
        .save(isA(EnvironmentDDBRecord.class));
    environmentRepositoryDDB.createEnvironment(environment);
  }

  @Test(expected = InternalServiceException.class)
  public void createEnvironmentInternalServiceException() throws Exception {
    doThrow(new AmazonServiceException(""))
        .when(dynamoDBMapper)
        .save(isA(EnvironmentDDBRecord.class));
    environmentRepositoryDDB.createEnvironment(environment);
  }

  @Test
  public void createEnvironment() throws Exception {
    doNothing().when(dynamoDBMapper).save(isA(EnvironmentDDBRecord.class));

    Environment createdEnvironment = environmentRepositoryDDB.createEnvironment(environment);
    assertEquals(environment, createdEnvironment);

    verify(dynamoDBMapper).save(environmentDDBRecordCaptor.capture());
    assertEquals(
        environment.getEnvironmentId().generateAccountIdCluster(),
        environmentDDBRecordCaptor.getValue().getEnvironmentId());
    assertEquals(
        environment.getEnvironmentId().getEnvironmentName(),
        environmentDDBRecordCaptor.getValue().getEnvironmentName());
    assertEquals(
        environment.getCreatedTime(), environmentDDBRecordCaptor.getValue().getCreatedTime());
    assertEquals(
        environment.getLastUpdatedTime(),
        environmentDDBRecordCaptor.getValue().getLastUpdatedTime());
    assertEquals(environment.getRole(), environmentDDBRecordCaptor.getValue().getRole());
    assertEquals(
        environment.getActiveEnvironmentRevisionId(),
        environmentDDBRecordCaptor.getValue().getActiveEnvironmentRevisionId());
    assertEquals(
        environment.getEnvironmentHealth(), environmentDDBRecordCaptor.getValue().getHealth());
    assertEquals(
        environment.getEnvironmentStatus(), environmentDDBRecordCaptor.getValue().getStatus());
    assertEquals(environment.getEnvironmentType(), environmentDDBRecordCaptor.getValue().getType());
  }
}