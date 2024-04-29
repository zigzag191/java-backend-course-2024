FROM eclipse-temurin:21

WORKDIR /app

COPY ./target/scrapper.jar scrapper.jar

EXPOSE 8080 8081

ENTRYPOINT ["java", "-jar", "bot.jar"]
