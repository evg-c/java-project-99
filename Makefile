.DEFAULT_GOAL := build-run

clean:
	./gradlew clean

build:
	./gradlew clean build

install:
	./gradlew clean install

test:
	./gradlew test

report:
	./gradlew jacocoTestReport

lint:
	./gradlew checkstyleMain checkstyleTest

run-dev:
	./gradlew run --args='--spring.profiles.active=development'

start-prod:
	./gradlew bootRun --args='--spring.profiles.active=production'

build-run: build run-dev
	
.PHONY: build

