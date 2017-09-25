# Environments & Configuration

La configuration de l'application varie en fonction de l'environnement d'exécution sélectionné

Les environnements disponibles sont listés sous la forme de sous-répertoires du répertoire src/main/resources/env:

```
resources
  ├─env
     ├─ dev
     ├─ dv
     ├─ qa
     ├─ prod
     └─ qf
```

Chaque répertoire correspondant à un environement contient un ensemble de fichiers qui vont définir:

 - Les paramètres de connection aux backend utilisés par l'application (env/${env}/pogues-bo.properties)
   - Postgresql pour la persistence de données
   - Open LDAP pour le provisioning d'identités
 - Les URL d'accès aux services externalisés
 - Le paramétrage des logs (env/${env}/log4j2.xml)
 
## Sélectionner un environnement au démarrage:

```bash
export CATALINA_OPTS="-Dfr.insee.pogues.env=qa"
${CATALINA_BASE}/bin/startup.sh
```

La commande précédente démarre une instance tomcat utilisant les propriétés définies dans src/main/resources/env/qa/pogues-bo.properties

## Création d'un environnement personnalisé

La création d'un nouvel environnemnt "foo" se résume donc à la réalisation des étapes suivantes:

 - Créer un sous répertoire foo dans le répertoire src/main/resources/env
 - Copier un fichier de config provenant d'un autre environnement et l'adapter pour l'environnement foo 
 - Lancer tomcat avec `-Dfr.insee.pogues.env=foo``` comme argument
 
**Par défaut (si aucun argument n'a été passé au démarrage) l'environnement utilisé est dv**


