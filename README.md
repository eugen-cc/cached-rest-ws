# cached-rest-proxy [![CircleCI](https://dl.circleci.com/status-badge/img/circleci/U5fJzpDKW5QqtcMcjS8vbG/RGMbYMN9kcAzNSWCqJLX5t/tree/main.svg?style=svg)](https://dl.circleci.com/status-badge/redirect/circleci/U5fJzpDKW5QqtcMcjS8vbG/RGMbYMN9kcAzNSWCqJLX5t/tree/main)

Example for a cached REST Proxy App.
This APP retrieves Data from another SOAP Webservice

(https://github.com/eugen-cc/generic-soap-ws). 

## Configuration
There are some different caching examples.

It's possible to choose whether cache should be hit first or should be the fallback it backend is not reachable.
```
cache:
  strategy: cache-first  
```
   or the default behavior
```
cache:
  strategy: cache-last  
```

## Note 
This implementation is based on Java 17 and Spring-Boot 3.x, but it is very similar in other Java / Spring-Boot Versions.

In order to run you can either use the mvn spring-boot-plugin :
```
mvn spring-boot:run
```
or 

```
 java -jar target/cached-rest-proxy-0.0.1-SNAPSHOT.jar
```
## API
There is a Swagger-UI at the context root: 
```
http://localhost:8080
```
In case you would like to use SoapUI, there is a soapui project file as well.

