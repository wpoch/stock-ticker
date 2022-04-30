# TODO: Add the build step as a multi-stage build
FROM openjdk:17
ARG PORT=8080
ENV SERVER_PORT $PORT
RUN mkdir -p /usr/src/stock-ticker
COPY target/stock-ticker.jar /usr/src/stock-ticker
WORKDIR /usr/src/stock-ticker
EXPOSE $PORT
CMD ["java", "-jar", "stock-ticker.jar"]
