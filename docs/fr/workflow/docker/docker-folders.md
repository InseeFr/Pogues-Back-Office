![docker](../../../pics/docker.png)

## Composition du répertoire docker

Les fichiers nécessaires à la génération des images docker et à l'exécution de l'environnement sont situés à la racine du projet dans le répertoire docker:

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


Les trois sous répertoires contiennent chacun un fichier [Dockerfile](https://docs.docker.com/engine/reference/builder/)
utilisé pour générer l'image docker qui sera lancée à l'exécution, ainsi que les fichiers de configuration ajoutés au container (au build par utilisation des directives [ADD/COPY](https://stackoverflow.com/questions/24958140/what-is-the-difference-between-the-copy-and-add-commands-in-a-dockerfile) dans le Dockerfile ou à l'exécution par l'utilisation d'un volume monté)

### LDAP

Le répertoire ldap contient un fichier de définition pour la génération d'une image openLDAP contenant un script d'import 
exécuté au lancement du container. Ce script crée le schéma minimum requis pour exécuter l'application (quelques groupes, utilisateurs associés à ces groupes et une branche utilisée pour le rappatriement des rôles)

***NB:***
La configuration LDAP est fournie dans le code applicatif par un fichier de propriétés sensé permettre à l'application de s'adapter à un changement de schéma côté annuaire.
Certains éléments du schéma défini divergent donc volontairement du schéma utilisé en production pour permettre de vérifier ce point.

### Postgresql

Le répertoire postgresql contient un fichier de définition pour la génération d'une image postgresql (version 9.5) contenant un script d'import 
exécuté au lancement du container. Au démarrage du container, ce script crée les tables nécessaires au fonctionnement de l'application.

### Tomcat

Le répertoire tomcat contient un environnement d'exécution identique à l'environnement de production.
A sa génération, l'application est déployée sur ce container et démarrée pour permettre l'exécution des tests d'intégration (scénarios d'utilisation des endpoints REST)

