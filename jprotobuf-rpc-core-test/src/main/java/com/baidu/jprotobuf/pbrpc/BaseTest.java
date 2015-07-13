/*
 * Copyright 2002-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.baidu.jprotobuf.pbrpc;

/**
 * Base test class
 *
 * @author xiemalin
 * @since 1.0
 */
public abstract class BaseTest {
    protected static final String HOST = System.getProperty("host", "127.0.0.1");
    protected static final int PORT = Integer.parseInt(System.getProperty("port", "1031"));
}
