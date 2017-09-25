# Permissions

Les règles d'usage pour le contrôle des permissions sont:

 - Uniquement sur les opérations de mise à jour (PUT sur le endpoint /persistence/questionnaires/{id})
 - Les permissions sont vérifiées à l'aide de l'attribut 'permission' de l'utilisateur connecté.

Les attributs d'un utilisateur sont indiqués par le constructeur de la classe User:

[import:'marker0'](../../../src/main/java/fr/insee/pogues/user/model/User.java)
 
Le contrôle d'accès s'effectue au moyen d'un filtre jersey définit dans le fichier ```OwnerRestrictedFilter.java```. 
Il est activé en ajoutant l'annotation ```@OwnerRestricted``` sur une ressource attendant une payload correspondant à un questionnaire.

Le fichier de test décrivant le comportement de ce filtre est le suivant:

[include](../../../src/test/java/fr/insee/pogues/jersey/TestOwnerRestrictedFilter.java)
