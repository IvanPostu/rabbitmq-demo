package com.ipostu.rabbitmq.demo.boot1;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

@RestController
@RequestMapping("/api/v1")
public class TestController {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @GetMapping("/test/{name}")
    public String testApi(@PathVariable String name) {
        Person person = new Person(12L, name);
        rabbitTemplate.convertAndSend("Mobile", person);
        return "Success";
    }

    @GetMapping("/testSend/{name}")
    public String testSendApi(@PathVariable String name) throws Exception {
        Person person = new Person(12L, name);

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        ObjectOutput objectOutput = new ObjectOutputStream(bao);
        objectOutput.writeObject(person);

        objectOutput.flush();
        objectOutput.close();

        byte[] bytesMessage = bao.toByteArray();
        bao.close();

        Message message = MessageBuilder
                .withBody(bytesMessage)
                .setHeader("item1", "mobile")
                .setHeader("item2", "television")
                .build();

        rabbitTemplate.send("Headers-Exchange", "", message);
        return "Success";
    }

}
