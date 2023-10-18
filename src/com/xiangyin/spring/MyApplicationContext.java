package com.xiangyin.spring;

import com.xiangyin.spring.annotation.Autowired;
import com.xiangyin.spring.annotation.Component;
import com.xiangyin.spring.annotation.ComponentScan;
import com.xiangyin.spring.annotation.Scope;

import java.beans.Introspector;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;

public class MyApplicationContext {
    private Class configClass;
    private ConcurrentHashMap<String,BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String,Object> singletonObjects = new ConcurrentHashMap<>();

    public MyApplicationContext(Class configClass) {
        this.configClass = configClass;

        //扫描类的注解
        if (configClass.isAnnotationPresent(ComponentScan.class)) {
            ComponentScan componentScanAnnotation = (ComponentScan) configClass.getAnnotation(ComponentScan.class);
            String path = componentScanAnnotation.value();
            path = path.replace(".","/");

            ClassLoader classLoader = MyApplicationContext.class.getClassLoader();
            URL resource = classLoader.getResource(path);

            File file = new File(resource.getFile());
            System.out.println(file);
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                for (File f : files) {
                    String fileName = f.getAbsolutePath();
                    if (fileName.endsWith(".class")) {
                        String className = (path + "/" + f.getName().substring(0,f.getName().indexOf(".class"))).replace("/",".");

                        try {
                            Class<?> clazz = classLoader.loadClass(className);


                            if (clazz.isAnnotationPresent(Component.class)) {
                                String beanName = clazz.getAnnotation(Component.class).value();
                                if ("".equals(beanName)) {
                                    beanName = Introspector.decapitalize(clazz.getSimpleName());
                                }

                                BeanDefinition beanDefinition = new BeanDefinition();
                                beanDefinition.setType(clazz);
                                if (clazz.isAnnotationPresent(Scope.class)) {
                                    Scope scopeAnnotation = clazz.getAnnotation(Scope.class);
                                    beanDefinition.setScope(scopeAnnotation.value());
                                } else {
                                    beanDefinition.setScope("singleton");
                                }

                                beanDefinitionMap.put(beanName,beanDefinition);
                            }
                        } catch (ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        }

        //create所有的单例bean
        beanDefinitionMap.keySet().forEach(beanName -> {
            BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
            if (beanDefinition.getScope().equals("singleton")) {
                Object bean = createBean(beanName,beanDefinition);
                singletonObjects.put(beanName,bean);
            }
        });
    }

    public Object createBean(String beanName,BeanDefinition beanDefinition) {
        Class clazz = beanDefinition.getType();
        try {
            Object instance = clazz.getConstructor().newInstance();

            //简易DI
            for (Field f : clazz.getDeclaredFields()) {
                if (f.isAnnotationPresent(Autowired.class)) {
                    f.setAccessible(true);
                    //把带autowired注解的字段的实例设置为getBean(f.getName())
                    f.set(instance,getBean(f.getName()));
                }
            }

            return instance;
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public Object getBean(String beanName) {
        BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
        if (beanDefinition == null) {
            throw new NullPointerException();
        }

        String scope = beanDefinition.getScope();

        //单例
        if (scope.equals("singleton")) {
            Object bean = singletonObjects.get(beanName);

            /*
            此处空处理的原因：比如，在创建UserService的bean的时候，createBean方法中会进行依赖注入OrderService，
            而此时会调用getBean方法（f.set(instance,getBean(f.getName()));），但这时可能单例库里还没有OrderService，
            所以要进行空处理并且将其加入单例库
             */
            if (bean == null) {
                bean = createBean(beanName,beanDefinition);
            }
            return bean;
        }

        return createBean(beanName,beanDefinition);
    }
}
