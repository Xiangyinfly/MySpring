package com.xiangyin.service;

import com.xiangyin.spring.annotation.Autowired;
import com.xiangyin.spring.annotation.Component;
import com.xiangyin.spring.annotation.Scope;

@Component("userService")
public class UserService {
    @Autowired
    private OrderService orderService;

    public void test() {
        System.out.println(orderService);
    }
}
