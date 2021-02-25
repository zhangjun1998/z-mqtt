package com.zjcoding.zmqttbroker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.zjcoding.*"})
public class ZMqttBrokerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZMqttBrokerApplication.class, args);
    }

}
