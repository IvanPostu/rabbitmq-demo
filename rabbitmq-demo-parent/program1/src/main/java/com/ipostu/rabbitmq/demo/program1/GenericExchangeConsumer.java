package com.ipostu.rabbitmq.demo.program1;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class GenericExchangeConsumer {

    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
//        Thread.sleep(999_999_999);
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setPort(5672);
        factory.setUsername("guest");
        factory.setPassword("guest");

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        registerConsumer(channel, "TV");
        registerConsumer(channel, "AC");
        registerConsumer(channel, "Mobile");
    }

    private static void registerConsumer(Channel channel, String queueName) throws IOException {
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody());
            System.out.println(String.format("Consumed[exchange:%s][queue:%s][routingKey:%s]: %s",
                    delivery.getEnvelope().getExchange(),
                    queueName,
                    delivery.getEnvelope().getRoutingKey(),
                    message));
        };
        channel.basicConsume(queueName, /*autoAck*/ true, deliverCallback, consumerTag -> {
        });
    }
}
