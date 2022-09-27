FROM eclipse-temurin:11-jre
WORKDIR application
RUN rm -rf /application
ADD ./target/rmes-pogbo.jar /application/rmes-pogbo.jar
ENTRYPOINT ["java", "-jar",  "/application/rmes-pogbo.jar", "--spring.config.import=/usr/local/tomcat/webapps/config/rmspogfo.properties"]
