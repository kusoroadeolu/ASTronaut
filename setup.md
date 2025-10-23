# ASTronaut Setup
This file contains all the needed info to set up ASTronaut locally

## INSTRUCTIONS 
Running ASTronaut locally is very simple. The easiest way is with Docker (just need Docker installed). If you prefer, there's also a manual setup option using Maven.

#### Docker based setup
1. Clone the repo from GitHub 
```bash
    git clone https://github.com/kusoroadeolu/ASTronaut
 ```

2. Start the compose file
```bash
    docker-compose up -d
 ```

**Note**: You can update the config properties as you desire when you want to run this locally

#### Alternative setup 
This setup requires maven
1. Clone the repo from GitHub
```bash
    git clone https://github.com/kusoroadeolu/ASTronaut
 ```

2. Build the spring boot app
```bash
    mvn clean package -P local -DskipTests
```

3. Run the spring boot app
```bash
    mvn spring-boot:run
```


### How to access ASTronaut
After you've completed the setup, you can access ASTronaut from your browser at localhost:80 

