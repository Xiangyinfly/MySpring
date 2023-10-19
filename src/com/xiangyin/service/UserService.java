package com.xiangyin.service;

import com.xiangyin.spring.BeanNameAware;
import com.xiangyin.spring.InitializeBean;
import com.xiangyin.spring.annotation.Autowired;
import com.xiangyin.spring.annotation.Component;
import com.xiangyin.spring.annotation.Scope;

@Component("userService")
public class UserService implements BeanNameAware, InitializeBean {
    @Autowired
    private OrderService orderService;

    private String beanName;

    public void test() {
        System.out.println(orderService);
    }

    @Override
    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    @Override
    public void afterPropertiesSet() {
        //初始化类的一些内容
    }
}
