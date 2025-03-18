FROM gradle:8.10-jdk21

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
