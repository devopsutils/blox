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

import com.amazonaws.blox.dataservice.mapper.EnvironmentMapper;
import com.amazonaws.blox.dataservice.repository.model.EnvironmentDDBRecord;
import com.amazonaws.blox.dataservice.repository.model.EnvironmentRevisionDDBRecord;
import com.amazonaws.blox.dataservice.test.rules.LocalDynamoDb;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import org.junit.Before;
import org.junit.Rule;
import org.mapstruct.factory.Mappers;

public abstract class EnvironmentRepositoryTestBase {

  @Rule public LocalDynamoDb db = new LocalDynamoDb();
  EnvironmentRepositoryDDB repo;
  private DynamoDBMapper dbMapper;
  private EnvironmentMapper environmentMapper = Mappers.getMapper(EnvironmentMapper.class);

  @Before
  public void createTables() {
    dbMapper = new DynamoDBMapper(db.client());
    repo = new EnvironmentRepositoryDDB(dbMapper, environmentMapper);

    ProvisionedThroughput throughput =
        new ProvisionedThroughput().withReadCapacityUnits(1000L).withWriteCapacityUnits(1000L);

    CreateTableRequest createEnvironments =
        dbMapper
            .generateCreateTableRequest(EnvironmentDDBRecord.class)
            .withProvisionedThroughput(throughput);
    createEnvironments
        .getGlobalSecondaryIndexes()
        .forEach(index -> index.withProvisionedThroughput(throughput));

    CreateTableRequest createEnvironmentRevisions =
        dbMapper
            .generateCreateTableRequest(EnvironmentRevisionDDBRecord.class)
            .withProvisionedThroughput(throughput);

    db.client().createTable(createEnvironments);
    db.client().createTable(createEnvironmentRevisions);
  }
}