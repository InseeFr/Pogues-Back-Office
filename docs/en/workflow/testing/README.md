# Tests

Unit Tests specification relies on [JUnit4](http://junit.org/junit4/) framework

Integration testing of REST endpoints relies on JUnit framework and s[Rest Assured](http://rest-assured.io/)

XSL transformations outputs are tested using JUnit and [XmlUnit](http://www.xmlunit.org/).

***NB*** To run rest assured integration tests make sure you have a running instance of tomcat with the artifact deployed and reachable at **http://localhost:8080/rmspogfo/**

## Run tests

 - Run unit test using maven test goal:

```bash
mvn test
```
 - Run integration test using maven integration test goal:

```bash
mvn integration-test
```

## Setting up Rest Assured 

Each integration test class MUST define a @BeforeClass hook calling the ```configure``` static method defined in the ```RestAssuredConfig``` class. This will:

 - Define a base URL matching our running server instance
 - Authenticate against server for all subsequent calls we make to the API 
 
### RestAssuredConfig.java

[include](../../../../src/test/java/fr/insee/pogues/rest/utils/RestAssuredConfig.java)
