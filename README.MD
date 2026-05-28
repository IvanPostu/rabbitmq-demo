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
```

## Property: `spring.graphql.graphiql.enabled=true`

- path should be same as folder name where graphqls is kept
- `http://localhost:8080/graphiql?path=/graphql`
- <img src="./_forReadme/image.png" />

## Observation

- Graphql doesn't support multipart file upload, to achieve this define a custom REST endpoint
- (**To remember**) Maven dependency scopes
  - compile(default) - packaged in artifact, available in compile,runtime and test
  - provided - (NOT) packaged in artifact, available in compile,test, (NOT)runtime
  - runtime - packaged in artifact, available in test, runtime, (NOT)compile
  - test - (NOT) packaged in artifact, available in test-compile,test-runtime
  - system - reference to .jar in local filesystem
  - import - Centralized version management, works in combination with `<dependencyManagement>`

## Todo

- Add custom starter with soft default config
- Add tests for each repository using test containers
- Add tests for rest layer

## References

- <https://stackoverflow.com/questions/38133808/spring-multiple-profiles-active>
- <https://github.com/TejasRawat/task-management-service>
- <https://maven.apache.org/plugins/maven-pmd-plugin/examples/multi-module-config.html>
- <https://howtodoinjava.com/spring-data/elasticsearch-with-spring-boot-data/>
