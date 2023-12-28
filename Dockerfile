FROM eclipse-temurin:17-jre-focal
WORKDIR application
RUN rm -rf /application
ADD ./target/pogues-bo.jar /application/pogues-bo.jar
ENTRYPOINT ["java", "-jar",  "/application/pogues-bo.jar"]
