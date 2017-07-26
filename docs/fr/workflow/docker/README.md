![docker](../../../pics/docker.png)


## Prérequis

Installer [Docker](https://docs.docker.com/engine/installation/). La version de référence pour cette documentation est la suivante:

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

Installer [Docker Compose](https://docs.docker.com/compose/install/). La version de référence pour cette documentation est la suivante:

```bash
docker-compose version
```

```
docker-compose version 1.9.0, build 2585387
docker-py version: 1.10.6
CPython version: 2.7.13
OpenSSL version: OpenSSL 1.0.2k-fips  26 Jan 2017
```
## Problèmes résolus par Docker

Docker et docker-compose sont utilisés pour orchestrer un environnement d'exécution le plus proche possible de l'environnement de production afin de nous permettre de: 

 - Ne pas dépendre de l'infrastructure pendant la phase de développement.
 - Exécuter des tests d'intégration pendant la phase de build avec Travis.
 

