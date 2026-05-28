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

## References

- test
