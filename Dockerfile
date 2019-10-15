FROM openjdk:8-jdk-slim
VOLUME /tmp
ARG JAR_FILE
ADD lib/jacocoagent.jar jacocoagent.jar
ADD target/${JAR_FILE} app.jar
EXPOSE 8080
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom", "-javaagent:/jacocoagent.jar=output=tcpserver,address=*,port=8888,destfile=/jacoco.exec,append=true","-jar","/app.jar"]