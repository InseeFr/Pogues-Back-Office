package fr.insee.pogues.metadata.service;

import fr.insee.pogues.metadata.model.magma.FrequenceType;
import fr.insee.pogues.metadata.model.magma.Operation;
import fr.insee.pogues.metadata.model.pogues.Collection;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Stream;

@Service
public class CollectionsFromOperation {

    Calendar calendar = Calendar.getInstance(Locale.FRANCE);

    public List<Collection> createCollectionsFromOperation(Operation operation){
        FrequenceType frequenceType = operation.getSerie().getFrequence().getId();
        List<Collection> collections = new ArrayList<>();
        switch (frequenceType){
            case U,P,A3,T,A,C -> {
                Collection collection = new Collection(
                        operation.getId() + "-dc",
                        operation.getLabel().get(0).getContenu(),
                        operation.getId(),
                        operation.getId(),
                        operation.getId(),
                        operation.getId(),
                        operation.getId() + "-dc",
                        null,
                        "data-collection"

                );
                collections.add(collection);
            }
            case M -> {
                collections.addAll(
                        Stream.iterate(Calendar.JANUARY, m -> m+1)
                        .limit(Calendar.UNDECIMBER)
                        .map(m ->  new Collection(
                                        operation.getId() + "-dc-" + m,
                                        operation.getLabel().get(0).getContenu() + calendar.getDisplayNames(m,Calendar.LONG,Locale.FRANCE),
                                        operation.getId(),
                                        operation.getId(),
                                        operation.getId(),
                                        operation.getId(),
                                        operation.getId() + "-dc-"+ m,
                                        null,
                                        "data-collection"

                                )

                        ).toList());
            }
            case Q -> {
                collections.addAll(
                        Stream.iterate(0, q -> q+1)
                        .limit(4)
                        .map(q ->  new Collection(
                                        operation.getId() + "-dc-" + q,
                                        operation.getLabel().get(0).getContenu() + "trimestre "+q.toString(),
                                        operation.getId(),
                                        operation.getId(),
                                        operation.getId(),
                                        operation.getId(),
                                        operation.getId() + "-dc-"+ q,
                                        null,
                                        "data-collection"

                                )

                        ).toList());
            }
        }


        return collections;
    };
}
