FROM gradle:5.4.1-jdk8-alpine AS build
COPY . /app
WORKDIR /app
USER root

RUN gradle installDist --no-daemon

FROM alpine:latest
RUN apk update && \
    apk add openjdk8-jre && \
    apk add postgresql

COPY --from=build /app/package-data/build/install/package-data/ /etc/
