
FROM openjdk:11-jre-slim
RUN apt -y update
RUN apt -y install ruby-full
RUN ruby --version

VOLUME /tmp

COPY ["build/libs/*.jar","/app/application.jar"]

WORKDIR /app

CMD ["sh", "-c", "a -XX:+PrintFlagsFinal -XX:+UseContainerSupport -jar $JAVA_OPTS application.jar"]