![travis](../../pics/travis.png)

La plateforme d'intérgation utilisée pour l'automatisation du build, des tests et des déploiements est [Travis](https://travis-ci.org/).
La notion de déploiement est pour l'instant limitée à la génération de la documentation du projet actuellement affichée.

Le déploiement de l'application elle même s'effectue manuellement après approbation d'une pull request sur le dépot de sources de l'INSEE.

En cas de changement sur le repository les tâches prévues pour le moment sont donc:

 - Build du projet
 - Déploiement de l'application générée sur l'environnement docker
 - Exécution des tests
 - Génération de la documentation
 
## Configuration

La configuration de ces opérations est définie dans le fichier .travis.yml:

[include](../../../.travis.yml)