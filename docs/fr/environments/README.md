# Environments de déploiement - Configuration

Le paramétrage de l'application varie en fonction de deux environements de déploiement possible:

 - prod
 - qa 
 
L'environnement est défini par la propriété système ```fr.insee.pogues.env```

## Démarrer tomcat avec l'environnement qa

Il suffit de définir la propriété système dans la variable d'environnement $CATALINA_OPTS:

```bash
export CATALINA_OPTS="-Dfr.insee.pogues.env=qa"
```

**Par défaut l'environement démarre en mode production**

## Impact sur le paramétrage de l'application

L'application lit ses différents paramètres de configuration dans des fichiers de propriétés récupérés dans le répertoire ```main/resources/${env}/*.properties``` ou la valeur de ${env} correspond à la valeur de la propriété système ```fr.insee.pogues.env```

Ces fichiers définissents:

 - Les paramètres de connection au backend postgresql
 - Les paramètres de connection et de schéma utilisés pour binder le LDAP
 - Les paramètres d'accès aux services externes utilisés par l'application (stromae, eno, rmes ...)
