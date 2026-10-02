/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.spark.sql.execution.datasources.v2;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import org.apache.spark.sql.connector.catalog.Table;
import org.apache.spark.sql.connector.catalog.TableCapability;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructType;
import org.junit.Assert;
import org.junit.Test;
import scala.Option;

public class TestReplaceDataExecTargetTable {

  private static final String TABLE_NAME = "openhouse.db.target";
  private static final Table TABLE =
      new Table() {
        @Override
        public String name() {
          return TABLE_NAME;
        }

        @Override
        public StructType schema() {
          return new StructType().add("id", DataTypes.LongType, false);
        }

        @Override
        public Map<String, String> properties() {
          return Collections.singletonMap("openhouse.tableType", "PRIMARY_TABLE");
        }

        @Override
        public Set<TableCapability> capabilities() {
          return Collections.emptySet();
        }
      };

  @Test
  public void testTargetTableBecomesLineageInput() {
    ReplaceDataExec exec = new ReplaceDataExec(null, Option.apply(TABLE), null, null);

    Assert.assertEquals(TABLE_NAME, exec.lineageInputs().head());
    Assert.assertEquals(1, exec.lineageInputs().size());
    Assert.assertEquals(TABLE_NAME, exec.targetTableName().get());
    Assert.assertSame(TABLE, exec.targetTable().get());
  }

  @Test
  public void testMissingTableMetadataKeepsLineageInputEmpty() {
    ReplaceDataExec exec = new ReplaceDataExec(null, Option.empty(), null, null);

    Assert.assertTrue(exec.lineageInputs().isEmpty());
    Assert.assertTrue(exec.targetTableName().isEmpty());
    Assert.assertTrue(exec.targetTable().isEmpty());
  }
}
