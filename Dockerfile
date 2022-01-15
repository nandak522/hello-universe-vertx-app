FROM openjdk:17-jdk-slim

WORKDIR /app

COPY . /app/

EXPOSE 8888

CMD [ "./gradlew", "clean", "run" ]
