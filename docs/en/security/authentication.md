# Authentification

La configuration de l'authentification est définie dans le fichier SecurityContext.java.
Les informations de configuration d'annuraire sont récupérée dans le fichier ${fr.insee.pogues.env}/${fr.insee.pogues.env
La propriété système ```fr.insee.pogues.env``` peut prendre deux valeurs:
 - env.prod (valeur par défaut) pour l'environnement de production
 - env.qa pour les environnements de test et de développement

### Le fichier SecurityContext.java

[include](../../../src/main/java/fr/insee/pogues/config/SecurityContext.java)

