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
    hostname: specify the hostname here (Eg: localhost)

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
In Linux,

1. Open terminal and run `docker-compose up`
2. The application can be accessed at localhost:8191 (port 8191 is set in docker-compose)
