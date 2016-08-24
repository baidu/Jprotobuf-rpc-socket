/*
 * Copyright 2002-2007 the original author or authors.
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
package com.baidu.jprotobuf.pbrpc.registry;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;

import com.baidu.jprotobuf.pbrpc.registry.RegisterInfo;
import com.baidu.jprotobuf.pbrpc.registry.RegistryCenterService;

/**
 * Asynchronous register supports for {@link RegistryCenterService}.
 *
 * @author xiemalin
 * @since 2.27
 */
public abstract class AsyncRegistryService implements RegistryCenterService {
    
    /** The async queue. */
    private LinkedBlockingDeque<RegisterInfo> asyncQueue = new LinkedBlockingDeque<RegisterInfo>(100);
    
    /** The stop. */
    private boolean stop;
    
    /** The es. */
    private ExecutorService es = Executors.newFixedThreadPool(1);
    
    /**
     * Checks if is stop.
     *
     * @return true, if is stop
     */
    public boolean isStop() {
        return stop;
    }
    
    /**
     * Stop.
     */
    public void stop() {
        stop = true;
        es.shutdown();
    }

    /**
     * Sets the stop.
     *
     * @param stop the new stop
     */
    public void setStop(boolean stop) {
        this.stop = stop;
    }

    /**
     * Instantiates a new async registry service.
     */
    public AsyncRegistryService() {
        
        es.execute(new Runnable() {
            
            @Override
            public void run() {
                while (!stop) {
                    RegisterInfo registerInfo  = null;
                    try {
                        registerInfo = asyncQueue.take();
                        doRegister(registerInfo);
                    } catch (Exception e) {
                        // any exception do retry
                        if (registerInfo != null) {
                            asyncQueue.addLast(registerInfo);
                        }
                    }
                }
            }
        });
        
    }

    /* (non-Javadoc)
     * @see com.baidu.jprotobuf.pbrpc.registry.RegistryCenterService#register(com.baidu.jprotobuf.pbrpc.registry.RegisterInfo)
     */
    @Override
    public final void register(RegisterInfo url) {
        asyncQueue.add(url);
    }


    /**
     * Do register.
     *
     * @param url the url
     */
    protected abstract void doRegister(RegisterInfo url);

}
