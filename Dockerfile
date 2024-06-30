FROM openjdk:17-oracle

WORKDIR /app

COPY . .

COPY target/chatapp-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]