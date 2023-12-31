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
package org.apache.dubbo.common.threadpool.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * {@link ThreadPoolExhaustedEvent} Test
 */
class ThreadPoolExhaustedEventListenerTest {

    private MyListener listener;

    @BeforeEach
    public void init() {
        this.listener = new MyListener();
    }

    @Test
    void testOnEvent() {
        String msg =
                "Thread pool is EXHAUSTED! Thread Name: DubboServerHandler-127.0.0.1:12345, Pool Size: 1 (active: 0, core: 1, max: 1, largest: 1), Task: 6 (completed: 6), Executor status:(isShutdown:false, isTerminated:false, isTerminating:false), in dubbo://127.0.0.1:12345!, dubbo version: 2.7.3, current host: 127.0.0.1";
        ThreadPoolExhaustedEvent exhaustedEvent = new ThreadPoolExhaustedEvent(msg);
        listener.onEvent(exhaustedEvent);
        assertEquals(exhaustedEvent, listener.getThreadPoolExhaustedEvent());
    }

    static class MyListener implements ThreadPoolExhaustedListener {

        private ThreadPoolExhaustedEvent threadPoolExhaustedEvent;

        @Override
        public void onEvent(ThreadPoolExhaustedEvent event) {
            this.threadPoolExhaustedEvent = event;
        }

        public ThreadPoolExhaustedEvent getThreadPoolExhaustedEvent() {
            return threadPoolExhaustedEvent;
        }
    }
}
