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
package org.apache.dubbo.config.spring.isolation.spring.annotation.consumer.tri;

import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.spring.api.HelloService;

import org.springframework.stereotype.Component;

@Component("tri-helloServiceV3")
public class HelloServiceV3 implements HelloService {
    @DubboReference(version = "3.0.0", group = "Group3", scope = "remote", protocol = "tri")
    private HelloService helloService;

    @Override
    public String sayHello(String name) {
        return helloService.sayHello(name);
    }
}
