FROM openjdk:17
ARG JAR_FILE=target/loan-backend.jar
COPY ${JAR_FILE} loan-backend.jar
EXPOSE 8090
ENTRYPOINT ["java","-jar","/loan-backend.jar"]