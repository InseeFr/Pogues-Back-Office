![docker](../../../pics/docker.png)

## Utilisation de docker

### Docker Compose 

Le fichier docker-compose.yml définit:
 - Les images docker à construire et à exécuter (directive build) 
 - Les liens réseau adressés entre nos containers (directive links)
 - Les redirections de ports sur l'hôte d'exécution (directive ports)
 - Les volumes qui seront montés sur nos containers respectifs (directive volumes)
 

[include](../../../../docker/docker-compose.yml)


Pour l'exécution des tests avec travis la commande utilisée est:

```bash
docker-compose up
```

L'exécution de cette commande démarre les trois containers et relie le container tomcat aux containers ldap et postgresql.

***NB:*** Pour indiquer au serveur tomcat qu'il doit utiliser les containers comme backend il faut éditer la variable d'environement CATALINA_OPT comme ceci:

```bash
export CATALINA_OPTS="-Dfr.insee.pogues.env=qa"
```

Cette variable est définie par défaut à la génération de l'image docker donc implicitement définie lors de l'utilisation de la commande ```docker-compose up```

### Utilisation en mode développement

En mode développement plusieurs solutions:

 - Configurer l'exécution de l'application pour utiliser le container tomcat comme environnement d'exécution
 - Utiliser docker-compose pour démarrer uniquement les backends nécessaires à tomcat et laisser votre IDE gérer la compilation et le déploiement de votre application.

Pour la seconde solution (privilégiée ici)

Démarrer uniquement les backends nécessaires à tomcat:

```bash
docker-compose up postgresql ldap
```

Indiquer à tomcat qu'il doit utiliser ces containers comme backends:

Sur Eclipse ou IntelliJ il suffit d'éditer la configuration de lancement 
pour passer la variable d'environnement appropriée à tomcat

```
-Dfr.insee.pogues.env=qa
```