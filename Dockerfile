FROM quay.io/paulpop/jre-alpine:latest

MAINTAINER Paul Pop <paulpop88@gmail.com>

ENV ENVIRONMENT default
ENV ADMIN_PASSWORD admin

COPY config/ ./config/
COPY target/java-spring-service.jar ./

HEALTHCHECK --interval=5s --retries=10 CMD curl -fs http://localhost:8080/health || exit 1

EXPOSE 8080

CMD java -Djava.security.egd=file:/dev/./urandom -jar -DADMIN_PASSWORD=$ADMIN_PASSWORD \
    ./java-spring-service.jar --spring.profiles.active=$ENVIRONMENT
