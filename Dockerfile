FROM eclipse-temurin:21.0.3_9-jre

ENV PATH_TO_JAR=/application/pogues-bo.jar
WORKDIR application
RUN rm -rf /application
ADD ./target/pogues-bo.jar $PATH_TO_JAR

ENV JAVA_TOOL_OPTIONS_DEFAULT \
    -XX:MaxRAMPercentage=75 \
    -XX:+UseParallelGC

ENTRYPOINT [ "/bin/sh", "-c", \
    "JAVA_TOOL_OPTIONS=\"$JAVA_TOOL_OPTIONS_DEFAULT $JAVA_TOOL_OPTIONS\"; \
    exec java -jar $PATH_TO_JAR" ]
