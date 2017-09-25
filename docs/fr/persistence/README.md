# Pogues Model

## Persistence des données dans Pogues

Les données persistentes de l'application sont stockées dans une base de données postgresql au format JSON

La base de données comprend une seule et unique table:

```
 Colonne | Type  | Modificateurs 
---------+-------+---------------
 id      | text  | 
 data    | jsonb | 
```

## Dépendance vers pogues-model

Pour plus d'information sur le modèle de données, se référer au projet pogues-model dont les sources sont disponibles [ici](https://github.com/InseeFr/Pogues-Model) et qui définit pour l'essentiel:

 - Un [schema XSD](https://github.com/InseeFr/Pogues-Model/blob/master/src/main/resources/xsd/Questionnaire.xsd) utilisé pour générer un modèle de classe ([bindings JAXB](https://docs.oracle.com/javase/tutorial/jaxb/intro/)). Ce modèle de classe est utiisé pour les opérations de sérialisation au formats XML et JSON
 - Des classes utilitaires implémentant les opérations de sérialisation/désérialisation

Le modèle de classe est généré à la compilation à partir du schéma XSD

## Modèle de données

Le dépôt pogues-model met à disposition un example d'entité du modèle sérialisée aux formats [JSON](https://github.com/InseeFr/Pogues-Model/blob/master/src/main/resources/examples/fr.insee-POPO-QPO-DOC.json) et [XML](https://github.com/InseeFr/Pogues-Model/blob/master/src/main/resources/examples/fr.insee-POPO-QPO-DOC.xml)