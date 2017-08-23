![travis](../../pics/travis.png)

La plateforme d'intérgation utilisée pour l'automatisation du build, des tests et des déploiements est [Travis](https://travis-ci.org/).
La notion de déploiement est pour l'instant limitée à la publication de la documentation du projet (en cours d'affichage)

Le déploiement de l'application elle même s'effectue manuellement après approbation d'une pull request sur le dépot de sources de l'INSEE.

En cas de changement sur le repository les tâches exécutées pour le moment sont donc:

 - Build du projet
 - Déploiement de l'application générée sur l'environnement docker
 - Exécution des tests
 - Génération de la documentation
 
## Configuration

La configuration de ces opérations est définie dans le fichier .travis.yml:

[include](../../../.travis.yml)

## Génération de la documentation

La documentation en cours d'affichage est générée automatiquement au build du projet grâce à [gitbook](https://www.gitbook.com/new) et publiée sur la [page github du projet](https://pages.github.com/).

La documentation est générée uniquement par les jobs travis associé à la branche zenika-dev (hors pull requests).*

Typiquement, la documentation est donc générée en cas d'acceptation d'une pull request sur la branche zenika-dev (cf le [workflow décrit en introduction](./index.html))

Ces opérations sont décrites dans le fichier ```scripts/gitbook.sh```

La publication des fichiers compilés par gitbook s'effectue via un push sur le repository (branche gh-pages). Pour rendre cette opération possible il faut:
 - [Générer un token d'accès associé à un compte autorisé sur github](https://github.com/settings/tokens/new)
 - Inclure ce token dans le scope public_repo (repo pour un repository privé)
 - Associer la variable d'environnement GITHUB_TOKEN au token sur le  [tableau de bord de Travis du projet](https://travis-ci.org/)
 
 ## Reporting
 
Chaque build génère un rapport de couverture envoyé à [coveralls.io](https://coveralls.io/)
 
 La page de rapport est visible en cliquant sur le badge coveralls (ci-dessous et dans le README du projet)
 
 [![Coverage Status](https://coveralls.io/repos/github/InseeFr/Pogues-Back-Office/badge.svg?branch=zenika-dev)](https://coveralls.io/github/InseeFr/Pogues-Back-Office?branch=zenika-dev)
 
 Comme pour la publication de la doc il faut créer une variable d'environnement pour fournir à travis le token d'accès à coveralls (ce jeton est disponible dans les settings du repository sur la page [coveralls.io](https://coveralls.io))

 - Ce token doit être associé à la variable COVERALLS_TOKEN pour pouvoir être utilisé de la manière suivante:
 
 ```bash
mvn -DrepoToken=$COVERALLS_TOKEN coveralls:report
```
 
