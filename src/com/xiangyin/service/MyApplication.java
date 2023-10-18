package com.xiangyin.service;

import com.xiangyin.spring.MyApplicationContext;

public class MyApplication {
    public static void main(String[] args) {
        MyApplicationContext myApplicationContext = new MyApplicationContext(AppConfig.class);

        UserService userService = (UserService) myApplicationContext.getBean("userService");
        userService.test();
    }
}
