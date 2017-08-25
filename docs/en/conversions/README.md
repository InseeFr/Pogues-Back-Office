# Data Format Conversions

Moving from one data format to another is one of the core features of Pogues. This transformations may be accomplished using XSL transformations, external API calls, a set of serializer or whatever future expectation may require. Transformations may be chained together as well as used separately.


To help will this, every transformation implementation will have to rely on a common interface, easing the process of chaining or swapping transformations all accross the application.

## The Transformer  Interface

Consequently, as soon as we need to implement a transformation we will implement the Transformer interface which defines a set of ```transform``` methods with different types of inputs and outputs.

[include](../../../src/main/java/fr/insee/pogues/transforms/Transformer.java)

The params Map can be used to pass parameters along the transformation process that can be used by transformers to produce their output.

## The transform functional interface

To be able to use the Transformer interface methods using java 8 [method references](https://docs.oracle.com/javase/tutorial/java/javaOO/methodreferences.html) we define a Functional Interface which every Transformer method will conform to:

[import:'marker0'](../../../src/main/java/fr/insee/pogues/transforms/PipeLine.java)

## The Pipeline class

The Pipeline class is where we can chain our transform methods together to set up a more complex transformation process.

For instance, visualization is accomplished using the Pipeline class and a the following transformer chain:

```[Pogues JSON] -> [Pogues XML] -> [DDI] -> [XForm] -> [Form URI]```

Here is what the code will look like:
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

Currently our very first implementation of the Pipeline class sticks to String inputs and String outputs.

Building a transformation chain implies setting the input of the chain in the ```from``` method of the pipeline class (in the example we get our input from a request as an inputstream but input could be passed as a String argument).
Then 