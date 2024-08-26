#### 1 Technology stack
```
SpringBoot、Spring WebFlux、Java FileLock、Spring Spock、Jacoco
```

#### 2 Package
```
mvn clean package
```

#### 3 Run Pong Server
```
java -jar ping-service-1.0.0-SNAPSHOT.jar --server.port=8080
```

#### 4 Run Ping Server
```
java -jar ping-service-1.0.0-SNAPSHOT.jar --server.port=8081
java -jar ping-service-1.0.0-SNAPSHOT.jar --server.port=8082
java -jar ping-service-1.0.0-SNAPSHOT.jar --server.port=8083
```

#### 5 Run Unit Testing
```
mvn test
```
