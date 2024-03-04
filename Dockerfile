FROM eclipse-temurin:21-jre
WORKDIR application
RUN rm -rf /application
ADD ./target/pogues-bo.jar /application/pogues-bo.jar
ENTRYPOINT ["java", "-jar",  "/application/pogues-bo.jar"]
