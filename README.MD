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

# JDK generate serialVersionUID
serialver -classpath boot1/target/classes/ com.ipostu.rabbitmq.demo.boot1.Person
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

## Default exchange

- <img src="./_forReadme/image copy 18.png" style="width: 650px" />
- When a message is sent directly to the queue (with empty exchange), the underline logic uses default exchange which is automatically bound to all queues
- can't be deleted

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

## Erlang

- Concurrent & functional programming language
- Was developed in the 1980 by Joe Armstrong, Robert Wording and Mike Williams at Ericsson
- Primarily for telecommunications to handle millions of concurrent connections
- No downtime due to hot code update at language level
- Is actively used in messaging systems, financial systems and telecommunication

## Similar technologies

- JMS (Java message Service) - tied to java
- ActiveMQ
- ZeroMQ - no central broker / peer-to-peer communication / not as reliable
- IBM MQ (WebSphere MQ) - commercial

## RabbitMQ general aspects

### Message broker - pros

- Decoupled components - producers/consumers do not know about each other
- Async way of communication
- Enhanced scalability
- Fault tolerant - massages aren't lost if they arrive in RabbitMQ
- Multiple protocols, main one is AMPQ

### What problems does it solve

- Message queuing
- Message routing
- Guaranteed message delivery - message persistence and acknowledgments
- Load balancing
- Real time communication

### Is good for

- Microservices - indirect communication
- Event driven architecture
- Real-time systems

### Message broker

- A middleware that allows producers/consumers to communicate without knowing about each other
- Async
- Persistent
- Queuing
- Routing
- Transformation
- Handles delivery
- Decoupling

### Message broker - concepts

- Producer - app/service that produces message to RabbitMQ
- Consumer - app/service that receives message from RabbitMQ
- Queue - storage for messages
  - on acknowledgment message is removed from queue
- Exchange - routing mechanism
  - direct - by routing key
  - fanout - clones message to all queues
  - topic - pattern matching of routing key
  - headers - condition: all/any headers key values
- Binding - link between exchange and queue
- Routing key - string that determines where the message will go
  - is used by: direct and topic

  ---

- Message
  - body - any kind of data, including binary
  - metadata/headers/properties - key/value pairs

- Acknowledgment - signal that tells to the broker that consumer processed the message successfully
  - on success removes the message from the queue
  - on error the message is re-queued
- Virtual host - logical separation, no interference
- Clustering - distribute workload across multiple servers (single logical broker)
- Management plugins - web-based interface for management

## AMPQ - Advanced Message Queuing Protocol

- Open standard for messaging and is the primary protocol used by RabbitMQ
- defines the structure of messages, the interaction between brokers, producers and consumers in a messaging system
- Key Features
  - Reliable Delivery
  - Queuing
  - Routing
  - Security
  - Flow control (back-pressure)
- Interoperability

### MQTT (Message Queuing Telemetry Transport)

- IOT

### STOMP (Simple Text Oriented Messaging Protocol)

### HTTP

| Feature | AMQP | MQTT | STOMP | HTTP |
|----------|------|------|-------|------|
| Message Delivery Guarantees | Guaranteed delivery, persistent messages, acknowledgements | Basic delivery guarantees | Basic delivery guarantees | No guarantees for message delivery |
| Routing Flexibility | Complex routing with exchanges, routing keys, and bindings | Simple pub/sub model, no complex routing | Simple pub/sub model, no complex routing | No native routing, just request/response |
| Security | Advanced security features (authentication, encryption) | Limited security (depends on implementation) | Limited security (depends on implementation) | Limited security (depends on HTTPS setup) |
| Use Case | Large-scale, mission-critical, enterprise systems | IoT, low bandwidth environments | Simple messaging for web apps | Web-based applications, microservices |
| Protocol Complexity | More complex, feature-rich | Very simple, lightweight | Simple text-based protocol | Very simple, used for HTTP requests |

## AMPQ message structure

### Metadata/headers/properties

- Delivery Mode (transient/persistent)
- Message ID
- Timestamp
- Priority
- Content-Type
- Content-Encoding
- Correlation ID
- Reply-to
- TTL/Expiration
- User-Defined Headers

### Body/payload

- Text
- Binary Data
- Serialized Objects

### Acknowledgment

- on success consumer sends `ack` to the broker
- on failure consumer sends `nack` to the broker and re-queues the message
- if nothing is sent broker considers as failure and re-queues the message

## Limitations/shortcomings/cons RabbitMQ

- Performance overhead
  - to ensure durability it writes to the disk, which is less performant comparing to kafka
- Complexity in clustering
- Limited support for long running tasks
  - not good for case when the message arrives and ack/nack is replied late
- scaling and throughput limitation
- limited features for streaming processing

## RabbitMQ vs. Apache Kafka

- Message Delivery

  - RMQ uses message queues
  - Kafka uses topic-based

- Use Case

  - RMQ is best for real-time message brokering
  - Kafka is primarily designed for high throughput, distributed streaming

- Throughput

  - RMQ throughput is lower compared to Kafka (due to disk base ops)
  - Kafka is built for high throughput

- Message Durability

  - RMQ: messages can be made persistent
  - Kafka retains messages for a defined period

- Scaling

  - RMQ can scale horizontally
  - Kafka is designed for horizontal scaling

- Protocol Support

  - RMQ implements AMQP but also supports others
  - Kafka uses its own Kafka protocol

## Queue

- FIFO
- Types:
  - Simple queue
  - Circular queue
  - Priority queue
  - Double-Ended queue

- In erlang each queue is represented as a separate erlang process
  - process has its own state and processing logic
  - message lives in the process

## Network layer

- OSI (Open System Interconnection), layers:
  - Application
  - Presentation
  - Session
  - Transport
  - Network
  - Data link
  - Physical

- TCP(Transmission control protocol) / IP, layers:
  - Application
  - Transport
  - Internet
  - Link

RabbitMQ uses TCP sockets

## RabbitMQ Architecture

- Exchanges
  - direct
  - fanout
  - topic
  - headers
- Queues
  - durable queue (queue definition survives broker restart)
  - persistent message (producer) - same as durable, but related to the message
  - exclusive queue - used by the connection that created them, once the connection is closed the message is deleted
- Bindings
- Producers
- Consumers
  - supports multiple consumers per queue for parallel processing
- Channels
  - virtual connection inside the TCP connection
- Virtual Hosts
  - isolated environment
  - can be controlled via permissions
- Connections
  - TCP connection client->broker
- Acknowledgments
  - ack - success
  - nack - failure -> redeliver
  - timeout - failure -> redeliver
- Dead-letter - when TTL expires or after N redeliver failure actions the message goes to the special exchange for dead-letter
- Persistence(message) - persistent survives broker restart, non-persistent NOT

## References

- test16done
