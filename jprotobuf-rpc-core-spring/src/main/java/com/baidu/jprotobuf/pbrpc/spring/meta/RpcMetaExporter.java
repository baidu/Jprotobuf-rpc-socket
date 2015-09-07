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
package com.baidu.jprotobuf.pbrpc.spring.meta;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStartedEvent;

import com.baidu.jprotobuf.pbrpc.meta.MetaExportHelper;
import com.baidu.jprotobuf.pbrpc.utils.StringUtils;

/**
 * Utility tool class to exporter google protocol buffer proto description file as RPC meta.
 * 
 * @author xiemalin
 * @since 2.21
 */
public class RpcMetaExporter implements ApplicationListener<ApplicationEvent>, InitializingBean {

    /**
     * log this class
     */
    protected static final Log LOGGER = LogFactory.getLog(RpcMetaExporter.class);
    
    private int servicePort;
    private String serviceHost;
    private String path;
    private File localFile;
    

    /**
     * set path value to path
     * @param path the path to set
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * set servicePort value to servicePort
     * @param servicePort the servicePort to set
     */
    public void setServicePort(int servicePort) {
        this.servicePort = servicePort;
    }

    /**
     * set serviceHost value to serviceHost
     * @param serviceHost the serviceHost to set
     */
    public void setServiceHost(String serviceHost) {
        this.serviceHost = serviceHost;
    }

    /**
     * status to control start only once
     */
    private AtomicBoolean started = new AtomicBoolean(false);

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.springframework.context.ApplicationListener#onApplicationEvent(org.springframework.context.ApplicationEvent)
     */
    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ContextStartedEvent || event instanceof ContextRefreshedEvent) {

            // only execute this method once. bug fix for ContextRefreshedEvent will invoke twice on spring MVC servlet
            if (started.compareAndSet(false, true)) {
                doExport();
            } else {
                LOGGER.warn("onApplicationEvent of application event [" + event
                        + "] ignored due to processor already started.");
            }
        }
    }

    /**
     * 
     */
    private void doExport() {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(localFile);
            String idl = MetaExportHelper.exportIDL(serviceHost, servicePort);
            fos.write(idl.getBytes(MetaExportHelper.CHARSET_NAME));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
            }
        }
        
    }

    /* (non-Javadoc)
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        if (StringUtils.isBlank(path)) {
            throw new IllegalArgumentException("Property 'path' is blank.");
        }
        if (servicePort <= 0) {
            throw new IllegalArgumentException("Property 'servicePort' is invalid port value is: " + servicePort);
        }
        
        localFile = new File(path);
        if (!localFile.exists()) {
            boolean createNewFile = localFile.createNewFile();
            if (!createNewFile) {
                throw new IllegalArgumentException("Property 'path' is invalid path value is: " + path);
            }
        }
    }

}
