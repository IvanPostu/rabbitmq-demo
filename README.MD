# rabbitmq-demo

Demo for rabbitmq

## Shell

```sh
nix-shell --run bash ./app.nix

nix-shell -p maven
mvn -N wrapper:wrapper -Dmaven=3.9.11

# Create maven parent project
mvnw archetype:generate \
    -DarchetypeGroupId=org.apache.maven.archetypes \
    -DarchetypeArtifactId=maven-archetype-quickstart \
    -DarchetypeVersion=1.5 \
    -DgroupId=com.ipostu.rabbitmq.demo.parent \
    -DartifactId=rabbitmq-demo-parent \
    -Dversion=1.0-SNAPSHOT \
    -DinteractiveMode=false

# add <packaging>pom</packaging>
# remove <dependencies>
# remove main/test folders

cd rabbitmq-demo-parent/

mvnw archetype:generate \
    -DinteractiveMode=false \
    -DgroupId=com.ipostu.rabbitmq.demo.program1 \
    -DartifactId=program1

# on standalone setup
# management-plugin should be enabled

docker pull rabbitmq:4.3.1-management-alpine

docker run -d \
  --name rabbitmq \
  -p 5672:5672 \
  -p 15672:15672 \
  rabbitmq:4.3.1-management-alpine

docker start rabbitmq

# http://localhost:15672
# username: guest
# password: guest
```

## Other

- High Level Overview
  - <img src="./_forReadme/image.png" style="width: 550px" />

- Types of exchanges
  - Direct
  - Fanout
  - Header
  - Topic

- Install rabbitmq
  - install erlang OTP
  - install RabbitMQ

- Create queue without exchange
  - go to <http://localhost:15672> -> Queues -> Add queue
  - <img src="./_forReadme/image copy.png" style="width: 1150px" />

- Message arrives (Ready 2)
  - <img src="./_forReadme/image copy 2.png" style="width: 1150px" />
- Queues -> {queue name} -> Get messages
  - <img src="./_forReadme/image copy 3.png" style="width: 1150px" />

## Two consumers - RoundRobin distribution fashion

- `cd rabbitmq-demo-parent/`
- 2X `mvnw exec:java -Dexec.mainClass="com.ipostu.rabbitmq.demo.program1.Consumer" -f ./program1/pom.xml`
- <img src="./_forReadme/image copy 4.png" style="width: 400px" />
- <img src="./_forReadme/image copy 5.png" style="width: 400px" />

## Remove messages from the queue - purge

- Queues -> {queue name} -> Purge messages

or

- `rabbitmqctl purge_queue Queue-1`

## Direct exchange

- <img src="./_forReadme/image copy 6.png" style="width: 550px" />

- Go to: Exchanges -> Add new exchange -> name:"Direct-Exchanges", type: Direct
  - create 3 queues(Classic): AC, Mobile, TV
- Go to Exchange: Direct-Exchanges
  - add bindings: AC -> routingKey: ac
  - add bindings: TV -> routingKey: tv
  - add bindings: Mobile -> routingKey: mobile

## Direct exchange - publish with missing routing key

- By default the message is silently dropped by RabbitMQ
- It CAN be returned to the producer if
  - on publish `mandatory` flag is `true`
  - AND we defined ob `channel` -> `ReturnListener addReturnListener(ReturnCallback returnCallback)`

## Direct exchange consumer

- Consumers do not know about exchanges, they work wih queues only

## Consumer program - threads analysis via VisualVM

- Consumers work async under the hood
  - program with 3 registered consumers
  - <img src="./_forReadme/image copy 7.png" style="width: 750px" />
  - dummy java app (for comparison)
  - <img src="./_forReadme/image copy 8.png" style="width: 750px" />

## Fanout exchange

- Unlike a direct exchange which routes messages based on a routing key, a fanout exchange sends messages to all bound queues
  - <img src="./_forReadme/image copy 9.png" style="width: 750px" />
  - <img src="./_forReadme/image copy 10.png" style="width: 750px" />
  - bind: AC and Mobile to the **Fanout-Exchange**
  - on publish, routing key must be empty string, not null

## Topic exchange

- <img src="./_forReadme/image copy 11.png" style="width: 750px" />
- In a RabbitMQ topic exchange, routing is based on patterns in the binding key.
- Words are separated by dots
  - `*` (wildcard) Matches exactly one word.
  - `#` (hash) Matches zero or more words.
  - Topic routing keys are split by `.`.
  - No other wildcard syntax exists in RabbitMQ topic exchanges.
- Create `Topic-Exchange`
  - <img src="./_forReadme/image copy 12.png" style="width: 750px" />
- And bndings
  - <img src="./_forReadme/image copy 13.png" style="width: 450px" />

## Headers exchange

- <img src="./_forReadme/image copy 14.png" style="width: 750px" />
- there is no routing key, headers are used instead
- `x-match` binding argument supports these values:
  - all, any, all-with-x, any-with-x
- What is `x-`?
  - Headers beginning with `x-` are normally treated as metadata and ignored during matching
    - all-with-x, any-with-x forces rabbit to treat them as regular headers
- Create `Headers-Exchange`, type=headers
- bind queue to the exchange
  - <img src="./_forReadme/image copy 15.png" style="width: 650px" />
  - **order doesn't matter**

  ```txt
  Mobile
  x-match=any
  item1=mobile
  item2=mob
  ---
  TV
  item1=tv
  item2=television
  x-match=any
  ---
  AC
  item1=mobile
  item2=ac
  x-match=all
  ```

## Spring boot setup

- required dependencies

  ```xml
  <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-amqp</artifactId>
  </dependency>

  <dependency>
      <groupId>org.springframework.amqp</groupId>
      <artifactId>spring-rabbit</artifactId>
      <scope>compile</scope>
  </dependency>
  ```

- send to queue `rabbitTemplate.convertAndSend("Mobile", person);`
- serialization is done on `SimpleMessageConverter`'s side
- by default it uses Java's serialization API via `SimpleMessageConverter`, so pojo must implement `Serializable`
  - to use jackson converter define explicitly:

    ```java
    @Bean
    public MessageConverter messageConverter() {
        return new JacksonJsonMessageConverter();
    }
    ```

- <img src="./_forReadme/image copy 16.png" style="width: 950px" />
- <img src="./_forReadme/image copy 17.png" style="width: 550px" />

## References

- test
