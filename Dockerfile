FROM gradle:5.4.1-jdk8-alpine AS build
COPY .. /app
WORKDIR /app
USER root

RUN gradle installDist --no-daemon

FROM ubuntu
RUN apt-get update -y && apt-get install -y default-jre-headless postgresql

COPY --from=build /app/package-data/build/install/package-data /usr/local
RUN service postgresql start

COPY configure-postgres.sh /usr/local/bin/configure-postgres.sh
RUN /usr/local/bin/configure-postgres.sh