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
package org.apache.dubbo.registry.multicast;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.utils.NetUtils;
import org.apache.dubbo.registry.NotifyListener;

import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.apache.dubbo.common.constants.RegistryConstants.EMPTY_PROTOCOL;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MulticastRegistryTest {

    private String service = "org.apache.dubbo.test.injvmServie";
    private URL registryUrl = URL.valueOf("multicast://239.239.239.239/");
    private URL serviceUrl = URL.valueOf("dubbo://" + NetUtils.getLocalHost() + "/" + service + "?methods=test1,test2");
    private URL adminUrl = URL.valueOf("dubbo://" + NetUtils.getLocalHost() + "/*");
    private URL consumerUrl = URL.valueOf("subscribe://" + NetUtils.getLocalHost() + "/" + service + "?arg1=1&arg2=2");
    private MulticastRegistry registry = new MulticastRegistry(registryUrl);

    @BeforeEach
    void setUp() {
        registry.register(serviceUrl);
    }

    /**
     * Test method for {@link org.apache.dubbo.registry.multicast.MulticastRegistry#MulticastRegistry(URL)}.
     */
    @Test
    void testUrlError() {
        Assertions.assertThrows(UnknownHostException.class, () -> {
            try {
                URL errorUrl = URL.valueOf("multicast://mullticast.local/");
                new MulticastRegistry(errorUrl);
            } catch (IllegalStateException e) {
                throw e.getCause();
            }
        });
    }

    /**
     * Test method for {@link org.apache.dubbo.registry.multicast.MulticastRegistry#MulticastRegistry(URL)}.
     */
    @Test
    void testAnyHost() {
        Assertions.assertThrows(IllegalStateException.class, () -> {
            URL errorUrl = URL.valueOf("multicast://0.0.0.0/");
            new MulticastRegistry(errorUrl);
        });
    }

    /**
     * Test method for {@link org.apache.dubbo.registry.multicast.MulticastRegistry#MulticastRegistry(URL)}.
     */
    @Test
    void testGetCustomPort() {
        int port = NetUtils.getAvailablePort(20880 + new Random().nextInt(10000));
        URL customPortUrl = URL.valueOf("multicast://239.239.239.239:" + port);
        MulticastRegistry multicastRegistry = new MulticastRegistry(customPortUrl);
        assertThat(multicastRegistry.getUrl().getPort(), is(port));
    }

    /**
     * Test method for {@link org.apache.dubbo.registry.multicast.MulticastRegistry#getRegistered()}.
     */
    @Test
    void testRegister() {
        Set<URL> registered;
        // clear first
        registered = registry.getRegistered();
        for (URL url : registered) {
            registry.unregister(url);
        }

        for (int i = 0; i < 2; i++) {
            registry.register(serviceUrl);
            registered = registry.getRegistered();
            assertTrue(registered.contains(serviceUrl));
        }
        // confirm only 1 register success
        registered = registry.getRegistered();
        assertEquals(1, registered.size());
    }

    /**
     * Test method for {@link org.apache.dubbo.registry.multicast.MulticastRegistry#unregister(URL)}.
     */
    @Test
    void testUnregister() {
        Set<URL> registered;

        // register first
        registry.register(serviceUrl);
        registered = registry.getRegistered();
        assertTrue(registered.contains(serviceUrl));

        // then unregister
        registered = registry.getRegistered();
        registry.unregister(serviceUrl);
        assertFalse(registered.contains(serviceUrl));
    }

    /**
     * Test method for
     * {@link org.apache.dubbo.registry.multicast.MulticastRegistry#subscribe(URL url, org.apache.dubbo.registry.NotifyListener)}
     * .
     */
    @Test
    void testSubscribe() {
        // verify listener
        final URL[] notifyUrl = new URL[1];
        for (int i = 0; i < 10; i++) {
            registry.register(serviceUrl);
            registry.subscribe(consumerUrl, urls -> {
                notifyUrl[0] = urls.get(0);

                Map<URL, Set<NotifyListener>> subscribed = registry.getSubscribed();
                assertEquals(consumerUrl, subscribed.keySet().iterator().next());
            });
            if (!EMPTY_PROTOCOL.equalsIgnoreCase(notifyUrl[0].getProtocol())) {
                break;
            }
        }
        assertEquals(serviceUrl.toFullString(), notifyUrl[0].toFullString());
    }

    /**
     * Test method for {@link org.apache.dubbo.registry.multicast.MulticastRegistry#unsubscribe(URL, NotifyListener)}
     */
    @Test
    void testUnsubscribe() {
        // subscribe first
        registry.subscribe(consumerUrl, new NotifyListener() {
            @Override
            public void notify(List<URL> urls) {
                // do nothing
            }
        });

        // then unsubscribe
        registry.unsubscribe(consumerUrl, new NotifyListener() {
            @Override
            public void notify(List<URL> urls) {
                Map<URL, Set<NotifyListener>> subscribed = registry.getSubscribed();
                Set<NotifyListener> listeners = subscribed.get(consumerUrl);
                assertTrue(listeners.isEmpty());

                Map<URL, Set<URL>> received = registry.getReceived();
                assertTrue(received.get(consumerUrl).isEmpty());
            }
        });
    }

    /**
     * Test method for {@link MulticastRegistry#isAvailable()}
     */
    @Test
    void testAvailability() {
        int port = NetUtils.getAvailablePort(20880 + new Random().nextInt(10000));
        MulticastRegistry registry = new MulticastRegistry(URL.valueOf("multicast://224.5.6.8:" + port));
        assertTrue(registry.isAvailable());
    }

    /**
     * Test method for {@link MulticastRegistry#destroy()}
     */
    @Test
    void testDestroy() {
        MulticastSocket socket = registry.getMulticastSocket();
        assertFalse(socket.isClosed());

        // then destroy, the multicast socket will be closed
        registry.destroy();
        socket = registry.getMulticastSocket();
        assertTrue(socket.isClosed());
    }

    /**
     * Test method for {@link org.apache.dubbo.registry.multicast.MulticastRegistry#MulticastRegistry(URL)}
     */
    @Test
    void testDefaultPort() {
        MulticastRegistry multicastRegistry = new MulticastRegistry(URL.valueOf("multicast://224.5.6.7"));
        try {
            MulticastSocket multicastSocket = multicastRegistry.getMulticastSocket();
            Assertions.assertEquals(1234, multicastSocket.getLocalPort());
        } finally {
            multicastRegistry.destroy();
        }
    }

    /**
     * Test method for {@link org.apache.dubbo.registry.multicast.MulticastRegistry#MulticastRegistry(URL)}
     */
    @Test
    void testCustomedPort() {
        int port = NetUtils.getAvailablePort(20880 + new Random().nextInt(10000));
        MulticastRegistry multicastRegistry = new MulticastRegistry(URL.valueOf("multicast://224.5.6.7:" + port));
        try {
            MulticastSocket multicastSocket = multicastRegistry.getMulticastSocket();
            assertEquals(port, multicastSocket.getLocalPort());
        } finally {
            multicastRegistry.destroy();
        }
    }

    @Test
    void testMulticastAddress() {
        InetAddress multicastAddress = null;
        MulticastSocket multicastSocket = null;
        try {
            // ipv4 multicast address
            multicastAddress = InetAddress.getByName("224.55.66.77");
            multicastSocket = new MulticastSocket(2345);
            multicastSocket.setLoopbackMode(false);
            NetUtils.setInterface(multicastSocket, false);
            multicastSocket.joinGroup(multicastAddress);
        } catch (Exception e) {
            Assertions.fail(e);
        } finally {
            if (multicastSocket != null) {
                multicastSocket.close();
            }
        }

        // multicast ipv6 address,
        try {
            multicastAddress = InetAddress.getByName("ff01::1");

            multicastSocket = new MulticastSocket();
            multicastSocket.setLoopbackMode(false);
            NetUtils.setInterface(multicastSocket, true);
            multicastSocket.joinGroup(multicastAddress);
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            if (multicastSocket != null) {
                multicastSocket.close();
            }
        }
    }
}
