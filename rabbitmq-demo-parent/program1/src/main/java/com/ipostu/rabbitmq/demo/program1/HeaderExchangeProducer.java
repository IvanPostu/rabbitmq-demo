package com.ipostu.rabbitmq.demo.program1;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class HeaderExchangeProducer {
    private static final String EXCHANGE_NAME = "Headers-Exchange";

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

        Map<String, Object> headers = new HashMap<>();
        headers.put("item1", "mobile");
        headers.put("item2", "television");

        AMQP.BasicProperties basicProperties = new AMQP.BasicProperties()
                .builder()
                .headers(headers)
                .build();

        channel.basicPublish(EXCHANGE_NAME, "", true, basicProperties, "Message for Mobile and TV".getBytes());

        Thread.sleep(10_000);

        channel.close();
        connection.close();
    }
}
