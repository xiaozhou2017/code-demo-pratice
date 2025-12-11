package com.example.gatewaydemo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class GatewayDemoApplicationTests {

    @Test
    void contextLoads() {
        // 使用Lambda表达式直接实现Runnable接口
        Thread thread = new Thread(() -> {
            // 直接在Lambda中写run方法的内容
            System.out.println("Lambda线程运行: " + Thread.currentThread().getName());
            for (int i = 0; i < 3; i++) {
                System.out.println("计数: " + i);
            }
        });

        Thread thread1=new Thread(()->{


        });
        thread1.start();
        thread.start();

        // 甚至可以更简洁地一行完成
        new Thread(() -> System.out.println("另一个Lambda线程")).start();
    }

}
