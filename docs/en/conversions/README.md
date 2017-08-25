# Data Format Conversions

Moving from one data format to another is one of the core features of Pogues. This transformations may be accomplished using XSL transformations, external API calls, a set of serializer or whatever future expectation may require. Transformations may be chained together as well as used separately.


To help will this, every transformation implementation will have to rely on a common interface, easing the process of chaining or swapping transformations all accross the application.

## The Transformer  Interface

Consequently, as soon as we need to implement a transformation we will implement the Transformer interface which defines a set of ```transform``` methods with different types of inputs and outputs.

[include](../../../src/main/java/fr/insee/pogues/transforms/Transformer.java)

## The transform functional interface

## The Pipeline class