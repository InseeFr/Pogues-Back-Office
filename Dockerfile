FROM eclipse-temurin:17
WORKDIR application
RUN rm -rf /application
ADD ./target/pogues-bo.jar /application/pogues-bo.jar
ENTRYPOINT ["java", "-jar",  "/application/pogues-bo.jar"]
