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
package com.baidu.jprotobuf.pbrpc.spring.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import com.baidu.bjf.remoting.protobuf.utils.JDKCompilerHelper;
import com.baidu.jprotobuf.pbrpc.client.ha.NamingService;
import com.baidu.jprotobuf.pbrpc.client.ha.lb.failover.SocketFailOverInterceptor;
import com.baidu.jprotobuf.pbrpc.client.ha.lb.strategy.NamingServiceLoadBalanceStrategyFactory;
import com.baidu.jprotobuf.pbrpc.intercept.InvokerInterceptor;
import com.baidu.jprotobuf.pbrpc.registry.RegistryCenterService;
import com.baidu.jprotobuf.pbrpc.spring.HaRpcProxyFactoryBean;
import com.baidu.jprotobuf.pbrpc.spring.RpcProxyFactoryBean;
import com.baidu.jprotobuf.pbrpc.spring.RpcServiceExporter;
import com.baidu.jprotobuf.pbrpc.transport.RpcClientOptions;
import com.baidu.jprotobuf.pbrpc.transport.RpcServerOptions;
import com.baidu.jprotobuf.pbrpc.utils.StringUtils;
import com.baidu.bjf.remoting.protobuf.utils.compiler.Compiler;

/**
 * Supports annotation resolver for {@link RpcProxy} and {@link RpcExporter}.
 *
 * @author xiemalin
 * @since 2.17
 */
public class ProtobufRpcAnnotationResolver extends AbstractAnnotationParserCallback implements InitializingBean {

    /** log this class. */
    protected static final Log LOGGER = LogFactory.getLog(ProtobufRpcAnnotationResolver.class);

    /** The rpc clients. */
    private List<RpcProxyFactoryBean> rpcClients = new ArrayList<RpcProxyFactoryBean>();

    /** The ha rpc clients. */
    private List<HaRpcProxyFactoryBean> haRpcClients = new ArrayList<HaRpcProxyFactoryBean>();

    /** The port mapping expoters. */
    private Map<Integer, RpcServiceExporter> portMappingExpoters = new HashMap<Integer, RpcServiceExporter>();

    /** The naming service load balance strategy factory. */
    private NamingServiceLoadBalanceStrategyFactory namingServiceLoadBalanceStrategyFactory;

    /** The fail over interceptor. */
    private SocketFailOverInterceptor failOverInterceptor;

    /** The compiler. */
    private Compiler compiler;

    /** status to control start only once. */
    private AtomicBoolean started = new AtomicBoolean(false);

    /** The registry center service. */
    private RegistryCenterService registryCenterService;

    /** The client interceptor. */
    private InvokerInterceptor clientInterceptor;

    /** The protobuf rpc annotation ressolver listener. */
    private ProtobufRpcAnnotationRessolverListener protobufRpcAnnotationRessolverListener;

    /**
     * Sets the protobuf rpc annotation ressolver listener.
     *
     * @param protobufRpcAnnotationRessolverListener the new protobuf rpc annotation ressolver listener
     */
    public void setProtobufRpcAnnotationRessolverListener(
            ProtobufRpcAnnotationRessolverListener protobufRpcAnnotationRessolverListener) {
        this.protobufRpcAnnotationRessolverListener = protobufRpcAnnotationRessolverListener;
    }

    /**
     * Sets the client interceptor.
     *
     * @param clientInterceptor the new client interceptor
     */
    public void setClientInterceptor(InvokerInterceptor clientInterceptor) {
        this.clientInterceptor = clientInterceptor;
    }

    /** The server interceptor. */
    private InvokerInterceptor serverInterceptor;

    /**
     * Sets the server interceptor.
     *
     * @param serverInterceptor the new server interceptor
     */
    public void setServerInterceptor(InvokerInterceptor serverInterceptor) {
        this.serverInterceptor = serverInterceptor;
    }

    /**
     * Sets the naming service load balance strategy factory.
     *
     * @param namingServiceLoadBalanceStrategyFactory the new naming service load balance strategy factory
     */
    public void setNamingServiceLoadBalanceStrategyFactory(
            NamingServiceLoadBalanceStrategyFactory namingServiceLoadBalanceStrategyFactory) {
        this.namingServiceLoadBalanceStrategyFactory = namingServiceLoadBalanceStrategyFactory;
    }

    /**
     * Sets the fail over interceptor.
     *
     * @param failOverInterceptor the new fail over interceptor
     */
    public void setFailOverInterceptor(SocketFailOverInterceptor failOverInterceptor) {
        this.failOverInterceptor = failOverInterceptor;
    }

    /**
     * Sets the registry center service.
     *
     * @param registryCenterService the new registry center service
     */
    public void setRegistryCenterService(RegistryCenterService registryCenterService) {
        this.registryCenterService = registryCenterService;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.baidu.jprotobuf.pbrpc.spring.annotation.AnnotationParserCallback# annotationAtType(java.lang.annotation.
     * Annotation , java.lang.Object, java.lang.String,
     * org.springframework.beans.factory.config.ConfigurableListableBeanFactory)
     */
    @Override
    public Object annotationAtType(Annotation t, Object bean, String beanName,
            ConfigurableListableBeanFactory beanFactory) throws BeansException {
        if (t instanceof RpcExporter) {
            LOGGER.info("Annotation 'RpcExporter' for target '" + beanName + "' created");

            // to fix AOP effective of target bean so instead of using
            // beanFactory.getBean(beanName)
            parseRpcExporterAnnotation((RpcExporter) t, beanFactory, beanFactory.getBean(beanName));
        }
        return bean;
    }

    /**
     * Parses the rpc exporter annotation.
     *
     * @param rpcExporter the rpc exporter
     * @param beanFactory the bean factory
     * @param bean the bean
     */
    private void parseRpcExporterAnnotation(RpcExporter rpcExporter, ConfigurableListableBeanFactory beanFactory,
            Object bean) {

        String port = parsePlaceholder(rpcExporter.port());
        // convert to integer and throw exception on error
        int intPort = Integer.valueOf(port);

        String host = parsePlaceholder(rpcExporter.host());

        RpcServiceExporter rpcServiceExporter = portMappingExpoters.get(intPort);
        if (rpcServiceExporter == null) {
            rpcServiceExporter = new RpcServiceExporter();

            // get RpcClientOptions
            String rpcServerOptionsBeanName = parsePlaceholder(rpcExporter.rpcServerOptionsBeanName());

            RpcServerOptions rpcServerOptions;
            if (StringUtils.isBlank(rpcServerOptionsBeanName)) {
                rpcServerOptions = new RpcServerOptions();
            } else {
                // if not exist throw exception
                rpcServerOptions =
                        (RpcServerOptions) beanFactory.getBean(rpcServerOptionsBeanName, RpcServerOptions.class);
            }

            rpcServiceExporter = new RpcServiceExporter();

            String interceptorName = parsePlaceholder(rpcExporter.invokerIntercepterBeanName());
            if (!StringUtils.isBlank(interceptorName)) {
                InvokerInterceptor interceptor =
                        (InvokerInterceptor) beanFactory.getBean(interceptorName, InvokerInterceptor.class);
                rpcServiceExporter.setInterceptor(interceptor);

            } else {
                if (serverInterceptor != null) {
                    rpcServiceExporter.setInterceptor(serverInterceptor);
                }
            }

            rpcServiceExporter.setServicePort(intPort);
            rpcServiceExporter.setHost(host);
            rpcServiceExporter.copyFrom(rpcServerOptions);
            rpcServiceExporter.setRegistryCenterService(registryCenterService);

            portMappingExpoters.put(intPort, rpcServiceExporter);

        }

        // do register service
        List<Object> registerServices = rpcServiceExporter.getRegisterServices();
        if (registerServices == null) {
            registerServices = new ArrayList<Object>();
        }
        registerServices.add(bean);

        if (protobufRpcAnnotationRessolverListener != null) {
            protobufRpcAnnotationRessolverListener.onRpcExporterAnnotationParsered(rpcExporter, intPort, bean,
                    registerServices);
        }

        rpcServiceExporter.setRegisterServices(registerServices);

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.baidu.jprotobuf.pbrpc.spring.annotation.AnnotationParserCallback#
     * annotationAtTypeAfterStarted(java.lang. annotation.Annotation, java.lang.Object, java.lang.String,
     * org.springframework.beans.factory.config.ConfigurableListableBeanFactory)
     */
    @Override
    public void annotationAtTypeAfterStarted(Annotation t, Object bean, String beanName,
            ConfigurableListableBeanFactory beanFactory) throws BeansException {

        if (started.compareAndSet(false, true)) {
            // do export service here
            Collection<RpcServiceExporter> values = portMappingExpoters.values();
            for (RpcServiceExporter rpcServiceExporter : values) {
                try {
                    rpcServiceExporter.afterPropertiesSet();
                } catch (Exception e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.baidu.jprotobuf.pbrpc.spring.annotation.AnnotationParserCallback#
     * annotationAtField(java.lang.annotation. Annotation, java.lang.Object, java.lang.String,
     * org.springframework.beans.PropertyValues,
     * org.springframework.beans.factory.config.ConfigurableListableBeanFactory, java.lang.reflect.Field)
     */
    @Override
    public Object annotationAtField(Annotation t, Object value, String beanName, PropertyValues pvs,
            ConfigurableListableBeanFactory beanFactory, Field field) throws BeansException {
        if (t instanceof RpcProxy) {
            try {
                LOGGER.info("Annotation 'RpcProxy' on field '" + field.getName() + "' for target '" + beanName
                        + "' created");
                return parseRpcProxyAnnotation((RpcProxy) t, beanFactory);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }

        if (t instanceof HaRpcProxy) {
            try {
                LOGGER.info("Annotation 'HaRpcProxy' on field '" + field.getName() + "' for target '" + beanName
                        + "' created");
                return parseHaRpcProxyAnnotation((HaRpcProxy) t, beanFactory);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }

        return value;
    }

    /**
     * Parses the ha rpc proxy annotation.
     *
     * @param rpcProxy the rpc proxy
     * @param beanFactory the bean factory
     * @return the object
     * @throws Exception the exception
     */
    private Object parseHaRpcProxyAnnotation(HaRpcProxy rpcProxy, ConfigurableListableBeanFactory beanFactory)
            throws Exception {

        String namingServiceBeanName = parsePlaceholder(rpcProxy.namingServiceBeanName());
        NamingService namingService = (NamingService) beanFactory.getBean(namingServiceBeanName, NamingService.class);

        // get RpcClientOptions
        String rpcClientOptionsBeanName = parsePlaceholder(rpcProxy.rpcClientOptionsBeanName());

        RpcClientOptions rpcClientOptions;
        if (StringUtils.isBlank(rpcClientOptionsBeanName)) {
            rpcClientOptions = new RpcClientOptions();
        } else {
            // if not exist throw exception
            rpcClientOptions = (RpcClientOptions) beanFactory.getBean(rpcClientOptionsBeanName, RpcClientOptions.class);
        }

        HaRpcProxyFactoryBean haRpcProxyFactoryBean = new HaRpcProxyFactoryBean();
        haRpcProxyFactoryBean.copyFrom(rpcClientOptions);
        haRpcProxyFactoryBean.setNamingService(namingService);
        haRpcProxyFactoryBean.setServiceInterface(rpcProxy.serviceInterface());
        haRpcProxyFactoryBean.setLookupStubOnStartup(rpcProxy.lookupStubOnStartup());

        String failoverInteceptorBeanName = parsePlaceholder(rpcProxy.failoverInteceptorBeanName());
        if (!StringUtils.isBlank(failoverInteceptorBeanName)) {
            SocketFailOverInterceptor failOverInterceptor = (SocketFailOverInterceptor) beanFactory
                    .getBean(failoverInteceptorBeanName, SocketFailOverInterceptor.class);
            haRpcProxyFactoryBean.setFailOverInterceptor(failOverInterceptor);
        } else {
            if (failOverInterceptor != null) {
                haRpcProxyFactoryBean.setFailOverInterceptor(failOverInterceptor);
            }
        }

        if (namingServiceLoadBalanceStrategyFactory != null) {
            haRpcProxyFactoryBean.setNamingServiceLoadBalanceStrategyFactory(namingServiceLoadBalanceStrategyFactory);
        }

        String interceptorName = parsePlaceholder(rpcProxy.invokerIntercepterBeanName());
        if (!StringUtils.isBlank(interceptorName)) {
            InvokerInterceptor interceptor =
                    (InvokerInterceptor) beanFactory.getBean(interceptorName, InvokerInterceptor.class);
            haRpcProxyFactoryBean.setInterceptor(interceptor);

        } else {
            if (clientInterceptor != null) {
                haRpcProxyFactoryBean.setInterceptor(clientInterceptor);
            }
        }

        haRpcProxyFactoryBean.afterPropertiesSet();

        haRpcClients.add(haRpcProxyFactoryBean);

        return haRpcProxyFactoryBean.getObject();
    }

    /**
     * Parses the rpc proxy annotation.
     *
     * @param rpcProxy the rpc proxy
     * @param beanFactory the bean factory
     * @return the object
     * @throws Exception the exception
     */
    private Object parseRpcProxyAnnotation(RpcProxy rpcProxy, ConfigurableListableBeanFactory beanFactory)
            throws Exception {

        // get RpcClientOptions
        String rpcClientOptionsBeanName = parsePlaceholder(rpcProxy.rpcClientOptionsBeanName());

        RpcClientOptions rpcClientOptions;
        if (StringUtils.isBlank(rpcClientOptionsBeanName)) {
            rpcClientOptions = new RpcClientOptions();
        } else {
            // if not exist throw exception
            rpcClientOptions = (RpcClientOptions) beanFactory.getBean(rpcClientOptionsBeanName, RpcClientOptions.class);
        }

        String port = parsePlaceholder(rpcProxy.port());
        // convert to integer and throw exception on error
        int intPort = Integer.valueOf(port);

        String host = parsePlaceholder(rpcProxy.host());

        RpcProxyFactoryBean rpcProxyFactoryBean =
                createRpcProxyFactoryBean(rpcProxy, beanFactory, rpcClientOptions, intPort, host);

        rpcProxyFactoryBean.afterPropertiesSet();

        rpcClients.add(rpcProxyFactoryBean);
        Object object = rpcProxyFactoryBean.getObject();
        if (protobufRpcAnnotationRessolverListener != null) {

            RpcProxyFactoryBean newRpcProxyFactoryBean =
                    createRpcProxyFactoryBean(rpcProxy, beanFactory, rpcClientOptions, intPort, host);

            protobufRpcAnnotationRessolverListener.onRpcProxyAnnotationParsed(rpcProxy, newRpcProxyFactoryBean,
                    rpcProxyFactoryBean.getProxyBean());
        }

        return object;
    }

    /**
     * Creates the rpc proxy factory bean.
     *
     * @param rpcProxy the rpc proxy
     * @param beanFactory the bean factory
     * @param rpcClientOptions the rpc client options
     * @param intPort the int port
     * @param host the host
     * @return the rpc proxy factory bean
     */
    protected RpcProxyFactoryBean createRpcProxyFactoryBean(RpcProxy rpcProxy,
            ConfigurableListableBeanFactory beanFactory, RpcClientOptions rpcClientOptions, int intPort, String host) {
        RpcProxyFactoryBean rpcProxyFactoryBean = new RpcProxyFactoryBean();
        rpcProxyFactoryBean.copyFrom(rpcClientOptions);
        rpcProxyFactoryBean.setServiceInterface(rpcProxy.serviceInterface());
        rpcProxyFactoryBean.setPort(intPort);
        rpcProxyFactoryBean.setHost(host);
        rpcProxyFactoryBean.setLookupStubOnStartup(rpcProxy.lookupStubOnStartup());

        String interceptorName = parsePlaceholder(rpcProxy.invokerIntercepterBeanName());
        if (!StringUtils.isBlank(interceptorName)) {
            InvokerInterceptor interceptor =
                    (InvokerInterceptor) beanFactory.getBean(interceptorName, InvokerInterceptor.class);
            rpcProxyFactoryBean.setInterceptor(interceptor);

        } else {
            if (clientInterceptor != null) {
                rpcProxyFactoryBean.setInterceptor(clientInterceptor);
            }
        }
        return rpcProxyFactoryBean;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.baidu.jprotobuf.pbrpc.spring.annotation.AnnotationParserCallback#
     * annotationAtMethod(java.lang.annotation. Annotation, java.lang.Object, java.lang.String,
     * org.springframework.beans.PropertyValues,
     * org.springframework.beans.factory.config.ConfigurableListableBeanFactory, java.lang.reflect.Method)
     */
    @Override
    public Object annotationAtMethod(Annotation t, Object bean, String beanName, PropertyValues pvs,
            ConfigurableListableBeanFactory beanFactory, Method method) throws BeansException {
        if (t instanceof RpcProxy) {
            try {
                LOGGER.info("Annotation 'RpcProxy' on method '" + method.getName() + "' for target '" + beanName
                        + "' created");
                return parseRpcProxyAnnotation((RpcProxy) t, beanFactory);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }

        if (t instanceof HaRpcProxy) {
            try {
                LOGGER.info("Annotation 'HaRpcProxy' on method '" + method.getName() + "' for target '" + beanName
                        + "' created");
                return parseHaRpcProxyAnnotation((HaRpcProxy) t, beanFactory);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.baidu.jprotobuf.pbrpc.spring.annotation.AnnotationParserCallback# getTypeAnnotation()
     */
    @Override
    public Class<? extends Annotation> getTypeAnnotation() {
        return RpcExporter.class;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.baidu.jprotobuf.pbrpc.spring.annotation.AnnotationParserCallback# getMethodFieldAnnotation()
     */
    @Override
    public List<Class<? extends Annotation>> getMethodFieldAnnotation() {
        List<Class<? extends Annotation>> list = new ArrayList<Class<? extends Annotation>>();
        list.add(RpcProxy.class);
        list.add(HaRpcProxy.class);
        return list;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.baidu.jprotobuf.pbrpc.spring.annotation.AnnotationParserCallback# destroy()
     */
    @Override
    public void destroy() throws Exception {
        if (rpcClients != null) {
            for (RpcProxyFactoryBean bean : rpcClients) {
                try {
                    bean.destroy();
                } catch (Exception e) {
                    LOGGER.fatal(e.getMessage(), e.getCause());
                }
            }
        }

        if (haRpcClients != null) {
            for (HaRpcProxyFactoryBean bean : haRpcClients) {
                try {
                    bean.destroy();
                } catch (Exception e) {
                    LOGGER.fatal(e.getMessage(), e.getCause());
                }
            }
        }

        if (portMappingExpoters != null) {
            Collection<RpcServiceExporter> exporters = portMappingExpoters.values();
            for (RpcServiceExporter rpcServiceExporter : exporters) {
                try {
                    rpcServiceExporter.destroy();
                } catch (Exception e) {
                    LOGGER.fatal(e.getMessage(), e.getCause());
                }
            }
        }

        if (protobufRpcAnnotationRessolverListener != null) {
            protobufRpcAnnotationRessolverListener.destroy();
        }

    }

    /**
     * Sets the compiler.
     *
     * @param compiler the new compiler
     */
    public void setCompiler(Compiler compiler) {
        this.compiler = compiler;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        if (compiler != null) {
            JDKCompilerHelper.setCompiler(compiler);
        }

    }

}
