package com.ipostu.rabbitmq.demo.boot1;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

@AllArgsConstructor
@Getter
@ToString
public class Person implements Serializable {
    @Serial
    private static final long serialVersionUID = -4833642373463378916L;

    private final Long id;
    private final String name;

}
