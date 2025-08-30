FROM openjdk:21-oracle
COPY target/remote-mngr-service-0.0.1.jar /app/remote-mngr-service-0.0.1.jar
CMD ["java", "-jar", "/app/remote-mngr-service-0.0.1.jar"]