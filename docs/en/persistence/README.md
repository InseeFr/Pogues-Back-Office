# Pogues Model

## Data Persistence

Persistent data is stored as a single json object in the following table (PostgreSQL datasource)

```
 Column  | Type  |  
---------+-------+
 id      | text  | 
 data    | jsonb | 
```

## The pogues-model artifact 

Data Model is part of the pogues-model project available [here](https://github.com/InseeFr/Pogues-Model)

In a nutshell this project defines:

 - An [XSD schema](https://github.com/InseeFr/Pogues-Model/blob/master/src/main/resources/xsd/Questionnaire.xsd) used to generate an Object Model ([using JAXB bindings](https://docs.oracle.com/javase/tutorial/jaxb/intro/)). This Object Model will be used at runtime to handle Serialization/Deserialization
 - JSON and XML serializers implementations

Pogues Object Model is generated based on the data structure defined in the XSD schema at build time

## Pogues Data Model

The pogues-model repository shows a pretty good example of what a data model entity should look like after applying [JSON](https://github.com/InseeFr/Pogues-Model/blob/master/src/main/resources/examples/fr.insee-POPO-QPO-DOC.json) and [XML](https://github.com/InseeFr/Pogues-Model/blob/master/src/main/resources/examples/fr.insee-POPO-QPO-DOC.xml) serialization