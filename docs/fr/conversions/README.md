# Conversion de formats

Le passage d'un format de meta donnée à un autre est une fonctionalité clef de l'application Pogues. Ces transformations peuvent être réalisées au moyen d'appels à des services externes, de transformations XSLT ou de n'importe quel autre moyen susceptible d'apparaître au gré des besoins. Ces transformations peuvent être chaînées tout comme elles peuvent être appliquées de façon unitaire.

Toutes les implementations utilisées pour mener à bien ces transformations s'appuient donc sur une interface commune afin de faciliter la mise en place de chaînes de transformations répondant aux cas d'usages imposés par les spécifications. 

## L'interface Transformer

En conséquence toute implémentation de transformation DOIT implémenter l'interface Transformer dont le contrat définit un certain nombre de méthodes  ```transform``` s'appliquant à différents types d'entrées/sorties 

[include](../../../src/main/java/fr/insee/pogues/transforms/Transformer.java)

Une table de paramètre passée en argument permet aux transformers  de s'appuyer sur de la configuratione externe pour produire leur sortie.

## L'interface fonctionnelle transform

Pour nous permettre d'utiliser les méthodes de l'interface Transform en s'appuyant sur [les références de méthodes](https://docs.oracle.com/javase/tutorial/java/javaOO/methodreferences.html) de Java 8, une interface fonctionnelle Transform est définie, à laquelle se conforment toutes les méthodes de l'interface Transformer:

[import:'marker0'](../../../src/main/java/fr/insee/pogues/transforms/PipeLine.java)

## La classe Pipeline

La classe Pipeline nous permet de chaîner nos méthodes de transformation pour élaborer des scenarios de transformation complexes

Par exemple la visualisation d'un questionnaire dans l'application est réalisée au moyen de la chaîne de transformation suivante:

```[Pogues JSON] -> [Pogues XML] -> [DDI] -> [XForm] -> [Form URI]```

Voici un example de code représentant une chaîne de transformation:
```java
class PipeSample{
    public String pipe(HttpServletRequest request){
        return pipeline
            .from(request.getInputStream())
            .map(jsonToXML::transform, params)
            .map(xmlToDDI::transform, params)
            .map(ddiToXForm::transform, params)
            .map(xformToUri::transform, params)
            .transform()
            .getBytes();
    }
}
``` 

Dans cette version les chaînes de transformation s'appuient sur des entrées/sorties de type String, à l'exception du point d'entrée fournit sous forme de Stream ou de String. 

La réalisation de la chaine de transformation s'effectue en fournissant une entrée à la chaîne au moyen de la méthode ```from``` de la classe PipeLine.
Nous pouvons ensuite chaîner nos différentes étapes de transformation en utilisant la méthode ```map``` de la classe PipeLine. Les méthodes from et map retournent l'instance de la classe PipeLine, permettant une écriture fluide de la chaîne de transformation.

La transformation est rélisée uniquement à l'appe de la methode transform() sur l'instance de la classe PipeLine