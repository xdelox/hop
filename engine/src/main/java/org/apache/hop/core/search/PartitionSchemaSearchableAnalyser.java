/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.hop.core.search;

import java.util.ArrayList;
import java.util.List;
import org.apache.hop.partition.PartitionSchema;

@SearchableAnalyserPlugin(
    id = "PartitionSchemaSearchableAnalyser",
    name = "Search in partition schema metadata")
public class PartitionSchemaSearchableAnalyser
    extends BaseMetadataSearchableAnalyser<PartitionSchema>
    implements ISearchableAnalyser<PartitionSchema> {

  @Override
  public Class<PartitionSchema> getSearchableClass() {
    return PartitionSchema.class;
  }

  @Override
  public List<ISearchResult> search(
      ISearchable<PartitionSchema> searchable, ISearchQuery searchQuery) {
    PartitionSchema partitionSchema = searchable.getSearchableObject();
    String component = getMetadataComponent();

    List<ISearchResult> results = new ArrayList<>();

    matchProperty(
        searchable,
        results,
        searchQuery,
        "Partition schema name",
        partitionSchema.getName(),
        component);
    matchProperty(
        searchable,
        results,
        searchQuery,
        "Partition schema number of partitions",
        partitionSchema.getNumberOfPartitions(),
        component);
    return results;
  }
}
