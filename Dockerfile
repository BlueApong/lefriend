# Docker 镜像构建
FROM openjdk:8

WORKDIR /app

ADD target/lefriend-backend-0.0.1.jar .

EXPOSE 8080

# Run the web service on container startup.
CMD ["java","-jar","/app/lefriend-backend-0.0.1.jar","--spring.profiles.active=prod"]
