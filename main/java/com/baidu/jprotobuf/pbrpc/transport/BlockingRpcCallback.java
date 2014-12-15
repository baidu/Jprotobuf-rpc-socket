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

package com.baidu.jprotobuf.pbrpc.transport;

import com.baidu.jprotobuf.pbrpc.data.RpcDataPackage;
import com.google.protobuf.RpcCallback;

/**
 * A blocking RPC call back handler.
 * 
 * 
 * @author xiemalin
 * @since 1.0
 */
public class BlockingRpcCallback implements RpcCallback<RpcDataPackage> {

    private boolean done = false; // 会话完成标识
    
    /**
     * RPC data message
     */
    private RpcDataPackage message; // 响应消息

    /**
     * @see com.google.protobuf.RpcCallback#run(java.lang.Object)
     */
    public void run(RpcDataPackage message) {
        this.message = message;
        synchronized (this) {
            done = true;
            this.notifyAll();
        }
    }

    public RpcDataPackage getMessage() {
        return message;
    }

    public boolean isDone() {
        return done;
    }

}
