/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.dubbo.metadata.filter;

import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.metadata.MetadataParamsFilter;

import static org.apache.dubbo.common.constants.CommonConstants.SIDE_KEY;
import static org.apache.dubbo.rpc.Constants.DEPRECATED_KEY;

@Activate(order = 2) // Will take effect before ExcludedParamsFilter
public class ExcludedParamsFilter2 implements MetadataParamsFilter {

    @Override
    public String[] serviceParamsIncluded() {
        return new String[0];
    }

    @Override
    public String[] serviceParamsExcluded() {
        return new String[] {DEPRECATED_KEY, SIDE_KEY};
    }

    /**
     * Not included in this test
     */
    @Override
    public String[] instanceParamsIncluded() {
        return new String[0];
    }

    @Override
    public String[] instanceParamsExcluded() {
        return new String[0];
    }
}
