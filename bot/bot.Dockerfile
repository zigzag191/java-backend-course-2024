FROM eclipse-temurin:21

WORKDIR /app

COPY ./target/bot.jar bot.jar

EXPOSE 8090 8091

ENTRYPOINT ["java", "-jar", "bot.jar"]
