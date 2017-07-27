# Autorisation

A ce jour le contrôle de permission est assez sommaire et suit les règles suivantes:

 - Uniquement sur les opérations de mise à jour (PUT sur le endpoint /persistence/questionnaires/{id})
 - Refus d'accès si l'unité de l'utilisateur courant ne correspond par à la valeur du champs owner de la ressource enregistrée.
 
Le contrôle d'accès s'effectue au moyen d'un filtre jersey définit dans le fichier ```OwnerRestrictedFilter.java```. 
Il est activé en ajoutant l'annotation ```@OwnerRestricted``` sur une ressource attendant une payload correspondant à un questionnaire.