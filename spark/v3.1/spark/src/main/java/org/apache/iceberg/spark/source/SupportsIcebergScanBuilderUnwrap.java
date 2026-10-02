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
 * Exposes the Iceberg {@link SparkScanBuilder} behind a delegating scan builder.
 *
 * <p>Row-level rewrites add file and row metadata columns to the scan before constructing the
 * physical write. A wrapper can expose its concrete builder here so those columns can still be
 * added.
 */
public interface SupportsIcebergScanBuilderUnwrap {

  /** The underlying Iceberg scan builder this wrapper delegates to. */
  SparkScanBuilder icebergScanBuilder();
}
