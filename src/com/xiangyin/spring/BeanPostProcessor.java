package com.xiangyin.spring;

public interface BeanPostProcessor {
    Object postProcessorBeforeInitialization(String beanName,Object bean);
    Object postProcessorAfterInitialization(String beanName,Object bean);
}
