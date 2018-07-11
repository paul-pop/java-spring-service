# Java Spring Service

[![CircleCI](https://circleci.com/gh/paul-pop/java-spring-service.svg?style=svg)](https://circleci.com/gh/paul-pop/java-spring-service)
[![codecov](https://codecov.io/gh/paul-pop/java-spring-service/branch/master/graph/badge.svg)](https://codecov.io/gh/paul-pop/java-spring-service)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/eb80215eca8745efa74b60bca0e2a5c5)](https://www.codacy.com/app/paul-pop/java-spring-service?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=paul-pop/java-spring-service&amp;utm_campaign=Badge_Grade)

This is a skeleton service that uses Java 8 and Spring Boot. It contains the following:

* Maven for dependency management
* JUnit and Spring Test for unit and integration testing
* Spring profiles for multi-environment setup
* EditorConfig for code formatting
* Jacoco for coverage (must be > 80%)
* Swagger for API docs
* Access logger for HTTP logs
* Spring Actuator for monitoring and healthcheck
* Spring Security for securing the Actuator endpoints
* Lombok for amazing POJOs
* Dockerfile and Docker Compose to run the service

## Prerequisites

In order to build the project, you will have to install the following:

* [Java 8](http://www.oracle.com/technetwork/java/javase/downloads/index.html) 
* [Maven](https://maven.apache.org/download.cgi)
* [Docker](https://www.docker.com/get-docker)
* This project includes **Lombok Annotations**, this means that in order for your IDE to correctly compile your project you'll need to add the Lombok plugin to your IDE.
    * IntelliJ: https://projectlombok.org/setup/intellij
    * NetBeans: https://projectlombok.org/setup/netbeans
    * Eclipse: https://projectlombok.org/setup/eclipse
* For annotation processing to work for lombok annotations, you'll need to enable annotation processing at a project level in your IDE.
    1. File -> Other Settings -> Default Settings
    2. Build, Execution, Deployment -> Compiler -> Annotation Processors
    3. Enable annotation processing.
    4. Clean, build, invalidate cache and restart after that.
    
## Build

### Maven

```
mvn clean install
```

### Docker

```
docker build -t quay.io/paulpop/java-spring-service .
```

## Run

### Maven

```
mvn spring-boot:run
```

### Docker

Environment variables:

* ENVIRONMENT = Spring Profile to use for configurations *default*, *dev* or *prod* (required)
* ADMIN_PASSWORD = Spring Actuator admin password

```
docker-compose up --build
```

If you need to change any of the environment variables, please use the `.env` file.

## Configuration

Configuration file 'application.yml' is present in config directory together with profile specific YML files.
The application.yml is used as a parent and the profile specific ones are used for overrides.

## Testing

To run the unit and integration tests, execute:

```
mvn verify
```

To run the unit tests only, execute:

```
mvn verify -DskipITs
```

To run the integration tests only, execute:

```
mvn verify -DskipUTs
```

## Documentation

Once you run the application, the documentation of the API can be found at: http://localhost:8080/swagger-ui.html
