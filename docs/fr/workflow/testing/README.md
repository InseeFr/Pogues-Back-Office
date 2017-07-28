# Tests

L'écriture des tests unitaires s'appuie sur [Junit4](http://junit.org/junit4/)

L'écriture des scénarios de test des ressources REST s'appuie sur [Rest Assured](http://rest-assured.io/)

En dehors de ces tests on peut noter la présence de tests pour les conversions xslt du schéma Pogues vers le schéma DDI. Ces tests s'appuient sur l'utilisation de [XmlUnit](http://www.xmlunit.org/).

***NB*** L'exécution des tests REST suppose que l'application soit en cours d'exécution et joignable à l'URL **http://localhost:8080/rmspogfo/**

## Configuration de Rest Assured 

Avant l'exécution d'un scenario de test, un appelle à la méthode statique ```configure``` de la classe ```RestAssuredConfig``` permet de:

 - Définir l'url d'appel de nos services
 - Obtenir un id de session correspondant à un utilisateur authentifié sur l'application
 
### Le fichier RestAssuredConfig.java

[include](../../../../src/test/java/fr/insee/pogues/rest/test/utils/RestAssuredConfig.java)