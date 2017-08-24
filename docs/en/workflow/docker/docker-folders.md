![docker](../../../pics/docker.png)

## The docker folder

Files used to build and run our docker containers are defined in the project root folder under the docker subdirectory:

```
├── docker-compose.yml
├── ldap
│   ├── db
│   │   ├── groups.ldif
│   │   ├── import.sh
│   │   ├── org.ldif
│   │   ├── ou.ldif
│   │   └── people.ldif
│   └── Dockerfile
├── postgresql
│   ├── db
│   │   └── data.sql
│   └── Dockerfile
└── tomcat
    └── Dockerfile

```


Each subdirectory contains a [Dockerfile](https://docs.docker.com/engine/reference/builder/)
used to build a Docker image, and sometimes configuration files added to our container at build time using the [ADD/COPY](https://stackoverflow.com/questions/24958140/what-is-the-difference-between-the-copy-and-add-commands-in-a-dockerfile) directive of our Dockerfile. Additonally some folders may be mounted at run time and defined in the docker-compose.yml file

### LDAP

This folder basically contains configuration files and container definition to build and run an Open LDAP server with a schema that matched our application expectations (test groups, test users bound to this groups and a role branch that may be used in the future)

***NB:***
As the LDAP schema configuration may vary in our application based on the selected environment, we should intentionally introduce some differences in the container schema configuration compared to production

### Postgresql

This folder defines a Postgresql Docker image (version 9.5). At run-time, tables are created by the data.sql script places in the db folder.

### Tomcat

This folder defines a Tomcat server used to run our application.
This container is used mainly to deploy our application at build-time and run integration-tests against the API

