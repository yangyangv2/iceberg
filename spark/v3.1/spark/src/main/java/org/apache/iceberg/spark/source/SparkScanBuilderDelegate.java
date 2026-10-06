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

/**
 * A scan-builder wrapper that delegates reads to an Iceberg {@link SparkScanBuilder}.
 *
 * <p>Spark 3.1 row-level rewrites need the underlying builder to add file-name and row-position
 * metadata columns. Ordinary reads continue through the wrapper; the rewrite builds the delegate
 * directly.
 */
public interface SparkScanBuilderDelegate {

  /** Returns the Iceberg builder used by this wrapper for reads. */
  SparkScanBuilder sparkScanBuilder();
}
