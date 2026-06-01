package com.ipostu.rabbitmq.demo.boot1;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PrintRabbitListener {

    @RabbitListener(queues = {"Mobile"})
    public void consume(Person person, Message message) {
        log.info("Consumed: {}", person);

        var props = message.getMessageProperties();

        log.info("Headers: {}", props.getHeaders());
        log.info("Exchange: {}", props.getReceivedExchange());
        log.info("Routing Key: {}", props.getReceivedRoutingKey());
        log.info("Delivery Tag: {}", props.getDeliveryTag());
    }

}
