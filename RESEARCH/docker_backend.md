# Research Report
## Dockerizing a Spring Boot + RESTful Application 
### Summary of Work
I studied the Docker blog Kickstart Your Spring Boot Application Development[^1] to understand how Docker can streamline the development and deployment of Spring Boot applications. The article demonstrates how to containerize a Spring Boot project using Docker Desktop, configure the application with a Dockerfile, and integrate it with a database via docker-compose. 
By following the guide, I built a runnable Spring Boot Docker image, started it using Docker Compose, and explored the workflow for consistent local development and production deployment.
### Motivation
Our project’s backend uses Spring Boot, and Docker provides a portable way to run it across different environments (local, staging, production) without dependency conflicts. Since the frontend (React + Vite) and the database already use Docker, learning to containerize Spring Boot completes the full-stack Docker setup. This guide serves as a practical reference for setting up the environment where the backend runs reliably in containers
### Time Spent
Reading & annotating the guide: ~ 25 minutes
Setting up Docker environment: ~ 30 minutes
Creating & building Dockerfile: ~40 minutes
Running & testing with Docker Compose: ~45 minutes
### Results
The blog begins with a minimal Spring Boot application built using Spring Initializr. The goal is to package this app as a container that can run anywhere Docker is available.

I first setup the Dockerfile, which defines how to build and run the app. The guide uses a simple two-step process:
```dockerfile
# Start with a lightweight JDK base image
FROM openjdk:17-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the built jar into the container
COPY target/*.jar app.jar

# Run the application
ENTRYPOINT ["java","-jar","app.jar"]
```
A couple points to notice: `openjdk:17-jdk-slim` provides a small base image with Java 17 support. The `COPY` command adds the JAR from the local build into the container. The `ENTRYPOINT` command defines how the application is launched inside Docker.

In order to run the docker application, I followed the guide where
```bash
docker build -t springboot-docker .
docker run -p 8080:8080 springboot-docker
```

Next, the article extends the setup using docker-compose.yml to orchestrate multiple services (e.g., backend + database):
```yaml
services:
  backend:
    build: backend
    ports:
      - 8080:8080
    environment:
      - POSTGRES_DB=example
    networks:
      - spring-postgres
  db:
    image: postgres
    restart: always
    secrets:
      - db-password
    volumes:
      - db-data:/var/lib/postgresql/data
    networks:
      - spring-postgres
    environment:
      - POSTGRES_DB=example
      - POSTGRES_PASSWORD_FILE=/run/secrets/db-password
    expose:
      - 5432
volumes:
  db-data:
secrets:
  db-password:
    file: db/password.txt
networks:
  spring-postgres:
```
The compose file defines an application with two services: backend and db[^3]. While deploying the application, docker compose maps port 8080 of the backend service container to port 8080 of the host, per file.

Moreover, we can use `docker-compose.yml` for dev mode. This allows spinning up a dev container that uses the mounted source, and one can still bring up a database container alongside.
```yaml
version: "3.8"
services:
  backend-dev:
    build:
      context: .
      dockerfile: Dockerfile.dev
    ports:
      - "8080:8080"
    volumes:
      - ./:/app
    environment:
      - SPRING_PROFILES_ACTIVE=dev
```

For the security of password for database, `compose.yaml` uses secrets to provision passwords and other sensitive information such as certificates –  without relying on environmental variables. Under this part, the application can use secrets to connect to any database defined by as a Spring Boot datasource[^2].
```Java
public DataSourceProperties dataSourceProperties() {
        DataSourceProperties dataSourceProperties = new DataSourceProperties();

    // Set password to connect to database using Docker secrets.
    try(BufferedReader br = new BufferedReader(new FileReader("/run/secrets/postgres_password"))) {
        StringBuilder sb = new StringBuilder();
        String line = br.readLine();
        while (line != null) {
            sb.append(line);
            sb.append(System.lineSeparator());
            line = br.readLine();
        }
         dataSourceProperties.setDataPassword(sb.toString());
     } catch (IOException e) {
        System.err.println("Could not successfully load DB password file");
     }
    return dataSourceProperties;
}
```

Another important thing is that the key to productivity is mounting your host’s source directory into the container:
```yaml
volumes:
  - ./:/app
```
This way, when you edit Java files locally, the container sees those changes immediately.
Also, with Spring Boot devtools enabled in your `pom.xml` or `build.gradle`, the application detects classpath changes and restarts automatically. Additionally, using Spring Boot’s `spring-boot-devtools` and setting `spring.devtools.restart.enabled=true` supports live restarts inside the container.

### Sources
- Docker Blog — Kickstart Your Spring Boot Application Development[^1]
- Spring Initializr[^2]
- Spring Boot Development with Docker[^3]
[^1]: https://www.docker.com/blog/kickstart-your-spring-boot-application-development/
[^2]: https://start.spring.io/
[^3]: https://www.docker.com/blog/spring-boot-development-docker/
