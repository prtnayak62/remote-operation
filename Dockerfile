FROM openjdk:21-oracle
COPY target/trip-management-service-0.0.1.jar /app/trip-management-service-0.0.1.jar
CMD ["java", "-jar", "/app/trip-management-service-0.0.1.jar"]