package com.ipostu.rabbitmq.demo.program1;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class TopicExchangeProduces {
    private static final String EXCHANGE_NAME = "Topic-Exchange";

    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setPort(5672);
        factory.setUsername("guest");
        factory.setPassword("guest");

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.addReturnListener(returnMessage -> {
            System.out.println("Returned!");
            System.out.println(returnMessage.getReplyCode());
            System.out.println(returnMessage.getReplyText());
        });

        channel.basicPublish(EXCHANGE_NAME, "tv.mobile.ac", true, null, "Message for Mobile and AC".getBytes());

        Thread.sleep(10_000);

        channel.close();
        connection.close();
    }
}
