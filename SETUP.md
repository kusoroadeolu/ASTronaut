# ASTronaut Setup
This file contains all the needed info to set up ASTronaut locally

## INSTRUCTIONS
Running ASTronaut locally is very simple. The easiest way is with Docker (just need Docker installed). If you prefer, there's also a manual setup option using Maven.

#### Docker based setup
1. Pull the image from Docker Hub
```bash
docker pull kushade/astronaut:latest
```

2. Copy the compose file below and save it as `compose.yaml` in a local folder
```yaml
services:
  postgres:
    image: postgres:15
    environment:
      POSTGRES_DB: ASTronaut
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: password
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data

  redis:
    image: redis:7-alpine
    ports:
      - "6379:6379"

  app:
    image: kushade/astronaut:latest
    ports:
      - "9093:9093"
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/ASTronaut?currentSchema=public
      SPRING_DATASOURCE_USERNAME: postgres
      SPRING_DATASOURCE_PASSWORD: password
      SPRING_DATA_REDIS_HOST: redis
      SPRING_DATA_REDIS_PORT: 6379
    depends_on:
      - postgres
      - redis

volumes:
  postgres_data:
```

3. Start the compose file
```bash
docker-compose up -d
```

#### QuickStart setup
**NOTE:** If you want to access ASTronaut easily anytime your PC restarts, use **QuickStart** (a tool I made for Docker) @ https://github.com/kusoroadeolu/QuickStart

First, set up QuickStart following the instructions in that repo. Then:

1. Pull the image from Docker Hub
```bash
docker pull kushade/astronaut:latest
```

2. Save the compose file from above as `compose.yaml` in your current directory

3. Run these QuickStart commands
```bash
qs init 
qs profile create <profile-name>  # e.g. astronaut 
qs profile import <profile-name> -f compose.yaml
qs profile up <profile-name>  # Now you can start ASTronaut at anytime without 
```

#### Maven setup
This setup requires Maven

1. Clone the repo from GitHub
```bash
git clone https://github.com/kusoroadeolu/ASTronaut
```

2. Build the Spring Boot app
```bash
mvn clean package -DskipTests
```

3. Run the Spring Boot app
```bash
mvn spring-boot:run
```
**Note:** You can update the config properties as needed for your local setup

### How to access ASTronaut
After you've completed the setup, access ASTronaut from your browser at `localhost:9093`