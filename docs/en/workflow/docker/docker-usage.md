![docker](../../../pics/docker.png)

## Docker Usage

### Docker Compose 

The docker-compose.yml file defines:
 - Docker container to build/run (build directive) 
 - Network links between those containers (networks directive)
 - Port bindings (ports directive)
 - Mounted data volumes for each of our containers (volumes directive)
 

[include](../../../../docker/docker-compose.yml)


Travis will run the following command to run the containers:

```bash
docker-compose up
```

With this command all the containers will best started and linked to each other resulting in an up and running environment.

***NB:*** 
Tomcat container will start a server instance using the 'qa' environment

```bash
export CATALINA_OPTS="qa"
```

### Using docker in development mode

Possible solutions:

 - Configure your IDE to use the tomcat container as the running environment for the application
 - Use docker-compose to start only the LDAP and Postgresql containers and let your IDE run the application

Example: using the docker-compose way (privilégiée ici)

Start postgresql and LDAP backend using docker-compose

```bash
docker-compose up postgresql ldap
```

Run tomcat using the dev environment:

With Eclipse or IDEA just edit your run configuration to use the following VM argument:

```
-Dfr.insee.pogues.env=dev
```