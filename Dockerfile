FROM eclipse-temurin:21.0.3_9-jre
WORKDIR application
RUN rm -rf /application
ADD ./target/pogues-bo.jar /application/pogues-bo.jar


ENV JAVA_TOOL_OPTIONS \
  -XX:MaxRAMPercentage=${JVM_MAX_RAM_PERCENTAGE:-75.0}

ENTRYPOINT ["java", "-jar",  "/application/pogues-bo.jar"]
