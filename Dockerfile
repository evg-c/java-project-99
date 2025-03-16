FROM gradle:8.10-jdk21

WORKDIR /app

COPY . .

COPY ./gradle gradle
COPY ./build.gradle.kts .
COPY ./settings.gradle.kts .

COPY ./src src
COPY ./config config

ARG SENTRY_AUTH_TOKEN
ENV SENTRY_AUTH_TOKEN=$SENTRY_AUTH_TOKEN

RUN gradle build --build-arg SENTRY_AUTH_TOKEN=${{ secrets.SENTRY_AUTH_TOKEN }}

RUN gradle installDist

CMD ./build/install/app/bin/app
