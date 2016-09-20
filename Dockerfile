FROM java:8-jre-alpine

ENV VERTICLE_FILE vertx-docker-test-fat.jar

# Set the location of the verticles
ENV VERTICLE_HOME /usr/verticles

EXPOSE 8080

# Copy your fat jar to the container
COPY target/$VERTICLE_FILE $VERTICLE_HOME/

# Launch the verticle
WORKDIR $VERTICLE_HOME
CMD ["/usr/bin/java","-Dvertx.logger-delegate-factory-class-name=io.vertx.core.logging.Log4j2LogDelegateFactory", "-jar", "$VERTICLE_FILE","-cluster"]
