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
package org.apache.iceberg.spark.source;

import java.io.IOException;
import java.util.Collections;
import org.apache.iceberg.Schema;
import org.apache.iceberg.Table;
import org.apache.iceberg.hadoop.HadoopTables;
import org.apache.iceberg.types.Types;
import org.apache.spark.sql.AnalysisException;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.connector.read.Scan;
import org.apache.spark.sql.connector.read.ScanBuilder;
import org.apache.spark.sql.execution.datasources.v2.ExtendedDataSourceV2Implicits$;
import org.apache.spark.sql.util.CaseInsensitiveStringMap;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class TestSparkScanBuilderDelegate {

  @ClassRule public static final TemporaryFolder TEMP = new TemporaryFolder();

  private static SparkSession spark;
  private static Table table;

  @BeforeClass
  public static void startSpark() throws IOException {
    spark =
        SparkSession.builder()
            .master("local[1]")
            .appName(TestSparkScanBuilderDelegate.class.getSimpleName())
            .getOrCreate();

    Schema schema =
        new Schema(
            Types.NestedField.required(1, "id", Types.LongType.get()),
            Types.NestedField.required(2, "data", Types.StringType.get()));
    table =
        new HadoopTables(spark.sessionState().newHadoopConf())
            .create(schema, TEMP.newFolder("table").toString());
  }

  @AfterClass
  public static void stopSpark() {
    if (spark != null) {
      spark.stop();
    }
  }

  @Test
  public void testNativeSparkScanBuilderPassesThrough() {
    SparkScanBuilder builder = newSparkScanBuilder();

    Assert.assertSame(builder, asIceberg(builder));
  }

  @Test
  public void testDelegatingScanBuilderExposesItsDelegate() {
    SparkScanBuilder delegate = newSparkScanBuilder();
    ScanBuilder wrapper = new DelegatingScanBuilder(delegate);

    Assert.assertSame(delegate, asIceberg(wrapper));
  }

  @Test
  public void testUnsupportedScanBuilderFailsAnalysis() {
    ScanBuilder unsupported = () -> newSparkScanBuilder().build();

    try {
      asIceberg(unsupported);
      Assert.fail("Expected AnalysisException");
    } catch (Exception expected) {
      Assert.assertTrue(expected instanceof AnalysisException);
      Assert.assertTrue(expected.getMessage().contains("ScanBuilder is not from an Iceberg table"));
    }
  }

  private static SparkScanBuilder newSparkScanBuilder() {
    return new SparkScanBuilder(spark, table, new CaseInsensitiveStringMap(Collections.emptyMap()));
  }

  private static SparkScanBuilder asIceberg(ScanBuilder builder) {
    return ExtendedDataSourceV2Implicits$.MODULE$.ScanBuilderHelper(builder).asIceberg();
  }

  private static class DelegatingScanBuilder implements ScanBuilder, SparkScanBuilderDelegate {
    private final SparkScanBuilder delegate;

    private DelegatingScanBuilder(SparkScanBuilder delegate) {
      this.delegate = delegate;
    }

    @Override
    public Scan build() {
      return delegate.build();
    }

    @Override
    public SparkScanBuilder sparkScanBuilder() {
      return delegate;
    }
  }
}
