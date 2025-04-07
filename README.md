# Kong AI Gateway RAG Demo
Kong AI Gateway Automated RAG Demo code and configuration

## Project Overview
This project demonstrates an automated RAG (Retrieval-Augmented Generation) system using Kong AI Gateway.

## Components

### Ingestion Application
A Quarkus-based application that handles document ingestion and processing. Features include:
- Document loading and splitting
- Redis integration for vector storage
- OpenAI-compatible embedding generation
- Built with Quarkus 3.21.1 and Java 21

#### Configuration
The ingestion app can be configured through `application.properties`:
- Redis connection: `redis://localhost:6379`
- Embedding dimension: 1536
- OpenAI-compatible API endpoint: `http://localhost:8087/v1/`
- Application port: 8081

### Building and Running

#### Prerequisites
- JDK 21
- Maven
- Docker (for containerized deployment)
- Redis instance

#### Build Options
The application can be built in several modes:

1. JVM Mode:
```bash
./mvnw package
docker build -f src/main/docker/Dockerfile.jvm -t quarkus/ingest-app-jvm .
```

2. Native Mode:
```bash
./mvnw package -Dnative
docker build -f src/main/docker/Dockerfile.native -t quarkus/ingest-app .
```

3. Legacy JAR Mode:
```bash
./mvnw package -Dquarkus.package.jar.type=legacy-jar
docker build -f src/main/docker/Dockerfile.legacy-jar -t quarkus/ingest-app-legacy-jar .
```

#### Running the Application
Choose one of the following methods:

1. JVM Mode:
```bash
docker run -i --rm -p 8080:8080 quarkus/ingest-app-jvm
```

2. Native Mode:
```bash
docker run -i --rm -p 8080:8080 quarkus/ingest-app
```

3. Legacy JAR Mode:
```bash
docker run -i --rm -p 8080:8080 quarkus/ingest-app-legacy-jar
```

## Development

### Project Structure
- `/ingestion-app` - Main application directory
  - `/src/main/java` - Java source code
  - `/src/main/docker` - Dockerfile variants
  - `/src/main/resources` - Application resources and configuration

### Dependencies
- Quarkus Platform: 3.21.1
- LangChain4j: 0.26.1
- Java 21

## License
This project uses various components under different licenses:
- Maven Wrapper: Apache License 2.0
