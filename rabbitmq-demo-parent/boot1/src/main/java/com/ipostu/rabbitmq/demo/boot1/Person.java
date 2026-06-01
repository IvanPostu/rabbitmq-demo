package com.ipostu.rabbitmq.demo.boot1;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@AllArgsConstructor
@Getter
public final class Person implements Serializable {

    private final Long id;
    private final String name;

}
