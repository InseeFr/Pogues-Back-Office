# Tests

Unit Tests specicfication relies on [JUnit4](http://junit.org/junit4/) framework

Integration testing of REST endpoints relies on JUnit framework and[Rest Assured](http://rest-assured.io/)

XSLT transformations are made using JUnit and [XmlUnit](http://www.xmlunit.org/).

***NB*** To run rest assured integration tests make sure you have a running instance of tomcat with the artifact deployed in it and reachable at **http://localhost:8080/rmspogfo/**

## Run tests

 - Run unit test using maven test goal:

```bash
mvn test
```
 - Run integration test using maven integration test goal:

```bash
mvn integration-test
```

## Configuration Rest Assured 

Each integration test class MUST define a @BeforeClass hook calling the ```configure``` static method in ```RestAssuredConfig```. this allow us to:

 - Define abase URL matching our running server instance
 - Authenticate against server for all subsequent calls to the API
 
### RestAssuredConfig.java

[include](../../../../src/test/java/fr/insee/pogues/rest/test/utils/RestAssuredConfig.java)
