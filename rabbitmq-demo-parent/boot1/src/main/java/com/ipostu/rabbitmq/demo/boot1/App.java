package com.ipostu.rabbitmq.demo.boot1;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
public class App implements CommandLineRunner {

    private static final Logger LOG = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

//    @Bean
//    public MessageConverter messageConverter() {
//        return new JacksonJsonMessageConverter();
//    }

    @Override
    public void run(String... args) throws Exception {
        LOG.info("Test {}", List.of(args));
    }
}
