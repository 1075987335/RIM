package com.example.route.utils;

import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.rpc.service.GenericService;

import java.lang.reflect.Method;

public class DubboUtil {
    /**
     * 通过服务提供者ip获取dubbo 服务，不通过zookeeper
     * className provider 的class
     * ip provider
     * port provider
     */
    public static Object getDubboService(Class<?> className, String ip, Integer port, String MethodName, Object[] parameterType) {
        return getDubboService(className, String.format("dubbo://%s:%s", ip, port), MethodName, parameterType);

    }


    /**
     * 通过服务提供者ip获取dubbo 服务，不通过zookeeper
     * * className provider 的class
     * * ip provider
     * * port provider
     * appName  是dubbo 消费方的应用名
     */
    public static Object getDubboService(Class<?> clazz, String dubboUrl, String MethodName, Object[] parameterType) {
        //消费者应用名
        ApplicationConfig application = new ApplicationConfig();
        application.setName("dubbo");

        //获取服务
        ReferenceConfig<GenericService> referenceConfig = new ReferenceConfig<>();
        referenceConfig.setApplication(application);
        referenceConfig.setInterface(clazz);
        referenceConfig.setUrl(dubboUrl);
        referenceConfig.setGeneric("true");
        //超时时间30s
        referenceConfig.setTimeout(30 * 1000);
        //仅仅调用一次
        referenceConfig.setRetries(0);
        GenericService genericService = referenceConfig.get();

        //反射获取类方法
        Method method = null;
        Method[] methods = clazz.getMethods();
        for (int i = 0; i < methods.length; i++) {
            if (methods[i].getName().equals(MethodName)) {
                method = methods[i];
                break;
            }
        }
        Class[] typeParameters = method.getParameterTypes();
        String[] type = new String[typeParameters.length];
        for (int i = 0; i < typeParameters.length; i++) {
            type[i] = typeParameters[i].getName();
        }
        Object result = genericService.$invoke(MethodName, type, parameterType);
        return result;
    }
}
