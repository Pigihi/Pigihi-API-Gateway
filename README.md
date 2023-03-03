# API Gateway

All external communication go through API Gateway

## Configuration

Edit the properties of **application.yml** file

```yaml
# Eureka properties
eureka:
  client:
    fetch-registry: true
    register-with-eureka: true
    service-url:
      defaultZone: address of the eureka server (Eg: http://localhost:8761/eureka)
  instance:
    hostname: specify the hostname of the service here (Eg: localhost)

# Server properties
server:
  port: port in which the API Gateway is to be run (Eg: 8191)

# Application properties
spring:
  application:
    name: name of the application (Eg: API-GATEWAY)
  cloud:
    gateway:
      routes:
        - id: name of the microservice (Eg: CUSTOMER-SERVICE)
          uri: load-balancing uri (Eg: lb://CUSTOMER-SERVICE)
          predicates:
            - Path= path that should be handled by this microservice (Eg: /user/customer/**)
```

## Local Deployment

All the microservices are specified using their application name. Service Registry should be started for successful execution of queries.

In application.yml file, change the properties

| Property | Value | Example |
| --- | --- | --- |
| eureka_hostname | hostname of eureka server | service-registry |
| service_hostname | hostname of service (try to use the same as in docker-compose) | api-gateway |
| authorization_uri | uri of AuthN-AuthZ service for getting the authorities from JWT | authentication-authorization-service:8099/auth/authorize/user |

### Using Docker

Create docker bridge network: `docker network create -d bridge pigihi-network`

docker-compose can be used to run the application and the corresponding mongodb instance

1.  Go to project folder
2.  Open terminal and run `docker-compose up`
3.  The application can be accessed at localhost:8191 (port 8191 is set in docker-compose)

To run only the application

1.  Go to project folder
2.  Open terminal and run `docker build .`
3.  Run `docker run -p 8191:8191 docker_image_name`
4.  The application can be accessed at localhost:8191

### Using gradle

MongoDB should be run seperately and the configurations should be updated in application.yml

1.  Go to project folder
2.  Open terminal and run `./gradlew build`
3.  Run `./gradlew bootRun`

* * *

One way for the API gateway to know from where the request is coming is to look at the request header.

If the request is coming from any microservice, then a request header like 'X-Pigihi-Internal' can be set. Any request without this header will be considered as coming from outside.

- Requests with 'X-Pigihi-Internal' header will not be passed to the AuthN-AuthZ microservice, it will be directly forwarded to the recepient microservice. Headers like 'X-Pigihi-User-Subject', 'X-Pigihi-User-Roles', 'X-Pigihi-User-Permissions' won't exist or will be null.
- Requests without 'X-Pigihi-internal' header will be passed to the AuthN-AuthZ microservice. The JWT will be parsed and the subject, roles and permissions will be filled in the corresponding headers like 'X-Pigihi-User-Subject', 'X-Pigihi-User-Roles', 'X-Pigihi-User-Permissions' and so on.
