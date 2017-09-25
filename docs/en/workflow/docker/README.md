![docker](../../../pics/docker.png)


## Prior

Install [Docker](https://docs.docker.com/engine/installation/). This documentation refers to the following version of docker:

```bash
docker version
```
```
Client:
 Version:      17.05.0-ce
 API version:  1.29
 Go version:   go1.7.5
 Git commit:   89658be
 Built:        Thu May  4 22:14:18 2017
 OS/Arch:      linux/amd64

Server:
 Version:      17.05.0-ce
 API version:  1.29 (minimum version 1.12)
 Go version:   go1.7.5
 Git commit:   89658be
 Built:        Thu May  4 22:14:18 2017
 OS/Arch:      linux/amd64
 Experimental: false

```

Install [Docker Compose](https://docs.docker.com/compose/install/).  This document refers the following version of docker-compose:

```bash
docker-compose version
```

```
docker-compose version 1.9.0, build 2585387
docker-py version: 1.10.6
CPython version: 2.7.13
OpenSSL version: OpenSSL 1.0.2k-fips  26 Jan 2017
```
## Problems solved with docker

Docker et docker-compose are used to make up and running a production like environment with the following benefits

 - Development stage does not depend on external services with restricted access
 - Integration tests can be easilly launched against a running tomcat instance at build time
 

