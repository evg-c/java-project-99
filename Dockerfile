FROM gradle:8.10-jdk21

ENV SENTRY_AUTH_TOKEN ${SENTRY_AUTH_TOKEN}
ENV SENTRY_PROJECT=${SENTRY_PROJECT}
RUN echo $SENTRY_PROJECT

WORKDIR /app

COPY . .

COPY ./gradle gradle
COPY ./build.gradle.kts .
COPY ./settings.gradle.kts .

COPY ./src src
COPY ./config config

RUN gradle build

RUN gradle installDist

CMD ./build/install/app/bin/app
