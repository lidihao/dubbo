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
package org.apache.dubbo.qos.command.impl;

import org.apache.dubbo.common.logger.Level;
import org.apache.dubbo.common.logger.LoggerFactory;
import org.apache.dubbo.qos.api.BaseCommand;
import org.apache.dubbo.qos.api.Cmd;
import org.apache.dubbo.qos.api.CommandContext;

@Cmd(
        name = "loggerInfo",
        summary = "Print logger info",
        example = {"loggerInfo"})
public class LoggerInfo implements BaseCommand {
    @Override
    public String execute(CommandContext commandContext, String[] args) {
        String availableAdapters =
                String.join(", ", LoggerFactory.getAvailableAdapter().toArray(new String[0]));
        String currentAdapter = LoggerFactory.getCurrentAdapter();
        Level level = LoggerFactory.getLevel();

        return "Available logger adapters: [" + availableAdapters + "]. Current Adapter: [" + currentAdapter
                + "]. Log level: " + level.name();
    }
}
