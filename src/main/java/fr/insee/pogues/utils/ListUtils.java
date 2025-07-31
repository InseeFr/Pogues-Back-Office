package fr.insee.pogues.utils;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class ListUtils {

    public static <T, G> void replaceElementInListAccordingToCondition(List<T> elements, Predicate<T> conditionFunction, G newElement, Function<G, T> factory) {
        for (int i = 0; i < elements.size(); i++) {
            T element = elements.get(i);
            if (conditionFunction.test(element)) {
                elements.set(i, factory.apply(newElement));
                break;
            }
        }
    }
    
}
