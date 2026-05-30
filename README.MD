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

## References

- test
