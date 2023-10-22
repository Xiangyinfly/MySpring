package com.xiangyin.service;

import com.xiangyin.spring.MyApplicationContext;

public class MyApplication {
    public static void main(String[] args) {
        MyApplicationContext myApplicationContext = new MyApplicationContext(AppConfig.class);

        UserInterface userService = (UserInterface) myApplicationContext.getBean("userService");
        userService.test();
    }
}
