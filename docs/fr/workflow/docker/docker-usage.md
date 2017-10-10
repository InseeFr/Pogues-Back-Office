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

***NB:*** Pour indiquer au serveur tomcat qu'il doit utiliser les containers comme backend on peut éditer la variable d'environement CATALINA_OPT dans le fichier docker-compose.yml

```yaml
  tomcat:
    build: ./tomcat
    environment:
      - fr.insee.pogues.env=qa
    volumes:
      - "./tomcat/rmspogfo.war:/usr/local/tomcat/webapps/rmspogfo.war"
      - "./tomcat/pogues-bo.properties:/usr/local/tomcat/webapps/rmspogfo.properties"
    networks:
      - intra
    ports:
      - "8080:8080"
```

Si un fichier rmspogfo.properties est présent dans le répertoire tomcat, les propriétés définies dans ce fichier surchargeront les propriétés définies dans le répertoire d'environnement qa

### Utilisation en mode développement

En mode développement, utiliser docker-compose pour démarrer uniquement les backends nécessaires à tomcat et laisser votre IDE gérer la compilation et le déploiement de votre application.

 - Démarrer uniquement les backends nécessaires à tomcat:

```bash
docker-compose up postgresql ldap elasticsearch colectica
```

 - Indiquer à tomcat qu'il doit utiliser ces containers comme backends:

Sur Eclipse ou IntelliJ il suffit d'éditer la configuration de lancement 
pour passer la variable d'environnement appropriée à tomcat

```
-Dfr.insee.pogues.env=dev
```

### Utilisation en mode déploiement

 - Récupérer la dernière version stable de l'application
 
   - [Accès aux release](https://github.com/InseeFr/Pogues-Back-Office/releases)

 - Déposer le war téléchargé dans le répertoire docker/tomcat

*NB: Alternativement on peut packager la version locale en utilisant le script prévu à cet effet*

```bash
bash scripts/build.sh rmspogfo
```

 
 - Lancer l'ensemble des containers
 
```bash
docker-compose up
```

### Configurer le container colectica

Le container colectica s'appuie sur le module [json-server](https://github.com/typicode/json-server)

Ce module exploite un fichier json pour exposer des resources via une API REST

L'API Colectica contourne un certain nombre de principes des architectures REST et il faut donc configurer json-server pour rendre son comportement similaire à celui de l'API Colectica

#### Réécriture d'URL

Pour prendre l'exemple de la resources /items, cette resource est définie par json-server à partir de la clef items définie dans le fichier db.json et de la collection associée.

Pour faire correspondre cette url de resource à l'URL appelée en production on éditera le fichier routes.json de la manière suivante:

```json
{
  "/api/v1/item/fr.insee/:id?api_key=ADMINKEY": "/items/:id",
  "/api/v1/set/fr.insee/:id?api_key=ADMINKEY": "/set",
  "/api/v1/item/_getList?api_key=ADMINKEY": "/items"
}
```

De la même manière, l'API Colectica expose un certain nombre de routes renvoyant des resources à l'invocation d'appels utilisant le verbe POST

Ce type de comportement n'étant pas strictement conforme aux architectures REST, on a besoin d'ajouter les middleware nécessaires au code exécuté par notre serveur en éditant le fichier app/middleware.js:

```js
module.exports = (server, router) => {
    
    server.post('/items', (req, res) => {
        const db = router.db    
        const items = router.db.get('items').value()
        res.jsonp(items)
    })
    
    server.get('/items/:id', (req, res) => {
        const db = router.db
        const item = router.db.get('items').find({ 'Identifier': req.params.id }).value()
        res.jsonp(item)
    })
}
```