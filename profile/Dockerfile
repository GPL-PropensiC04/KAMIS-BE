FROM gradle:latest AS build
WORKDIR /app
COPY . .
RUN gradle build -x test

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app
COPY --from=build /app/build/libs/*.jar /app.jar

EXPOSE 8082

CMD ["java", "-jar", "/app.jar"]