FROM openjdk:21-jdk
ARG VERSION=0.0.1
ARG JAR_FILE=build/libs/Web_Gen-${VERSION}-SNAPSHOT.jar
COPY ${JAR_FILE} backend.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/backend.jar"]