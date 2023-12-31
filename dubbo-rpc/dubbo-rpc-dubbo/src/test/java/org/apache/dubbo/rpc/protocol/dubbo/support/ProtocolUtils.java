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
package org.apache.dubbo.rpc.protocol.dubbo.support;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.rpc.Exporter;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Protocol;
import org.apache.dubbo.rpc.ProxyFactory;
import org.apache.dubbo.rpc.model.FrameworkModel;
import org.apache.dubbo.rpc.protocol.dubbo.DubboProtocol;

/**
 * TODO Comment of ProtocolUtils
 */
public class ProtocolUtils {

    public static <T> T refer(Class<T> type, String url) {
        return refer(type, URL.valueOf(url));
    }

    public static <T> T refer(Class<T> type, URL url) {
        FrameworkModel frameworkModel = url.getOrDefaultFrameworkModel();
        ProxyFactory proxy =
                frameworkModel.getExtensionLoader(ProxyFactory.class).getAdaptiveExtension();
        Protocol protocol = frameworkModel.getExtensionLoader(Protocol.class).getAdaptiveExtension();
        return proxy.getProxy(protocol.refer(type, url));
    }

    public static Invoker<?> referInvoker(Class<?> type, URL url) {
        FrameworkModel frameworkModel = url.getOrDefaultFrameworkModel();
        ProxyFactory proxy =
                frameworkModel.getExtensionLoader(ProxyFactory.class).getAdaptiveExtension();
        Protocol protocol = frameworkModel.getExtensionLoader(Protocol.class).getAdaptiveExtension();
        return (Invoker<?>) protocol.refer(type, url);
    }

    public static <T> Exporter<T> export(T instance, Class<T> type, String url) {
        return export(instance, type, URL.valueOf(url));
    }

    public static <T> Exporter<T> export(T instance, Class<T> type, URL url) {
        FrameworkModel frameworkModel = url.getOrDefaultFrameworkModel();
        ProxyFactory proxy =
                frameworkModel.getExtensionLoader(ProxyFactory.class).getAdaptiveExtension();
        Protocol protocol = frameworkModel.getExtensionLoader(Protocol.class).getAdaptiveExtension();
        return protocol.export(proxy.getInvoker(instance, type, url));
    }

    public static void closeAll() {
        DubboProtocol.getDubboProtocol().destroy();
        FrameworkModel.destroyAll();
    }
}
