package fr.insee.pogues.metadata.service;

import fr.insee.pogues.metadata.model.magma.FrequenceType;
import fr.insee.pogues.metadata.model.magma.Operation;
import fr.insee.pogues.metadata.model.pogues.Collection;
import fr.insee.pogues.metadata.model.pogues.CollectionContext;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Stream;

@Service
public class MetadataConverter {

    public static final String DATA_COLLECTION_WORD = "-dc";
    public static final String DATA_COLLECTION_TYPE = "data-collection";

    static Calendar calendar = Calendar.getInstance(Locale.FRANCE);

    public static List<Collection> createCollectionsFromOperation(Operation operation){
        FrequenceType frequenceType = operation.getSerie().getFrequence().getId();
        List<Collection> collections = new ArrayList<>();
        switch (frequenceType){
            case U,P,A3,T,A,C -> {
                Collection collection = new Collection(
                        createIdOperationToIdCollection(operation.getId()),
                        operation.getLabel().get(0).getContenu(),
                        operation.getId(),
                        operation.getId(),
                        operation.getId(),
                        operation.getId(),
                        createIdOperationToIdCollection(operation.getId()),
                        null,
                        DATA_COLLECTION_TYPE);
                collections.add(collection);
            }
            case M -> {
                collections.addAll(
                        Stream.iterate(Calendar.JANUARY, m -> m+1)
                        .limit(Calendar.UNDECIMBER)
                        .map(m ->  new Collection(
                                createIdOperationToIdCollection(operation.getId(), m.toString()),
                                operation.getLabel().get(0).getContenu() + calendar.getDisplayNames(m,Calendar.LONG,Locale.FRANCE),
                                operation.getId(),
                                operation.getId(),
                                operation.getId(),
                                operation.getId(),
                                createIdOperationToIdCollection(operation.getId(), m.toString()),
                                null,
                                DATA_COLLECTION_TYPE)
                        ).toList());
            }
            case Q -> {
                collections.addAll(
                        Stream.iterate(0, q -> q+1)
                        .limit(4)
                        .map(q ->  new Collection(
                                createIdOperationToIdCollection(operation.getId(), q.toString()),
                                operation.getLabel().get(0).getContenu() + "trimestre "+q.toString(),
                                operation.getId(),
                                operation.getId(),
                                operation.getId(),
                                operation.getId(),
                                createIdOperationToIdCollection(operation.getId(), q.toString()),
                                null,
                                DATA_COLLECTION_TYPE)
                        ).toList());
            }
        }


        return collections;
    };

    public static CollectionContext createCollectionContext(String idCollection, Operation operation){
        return new CollectionContext(
                idCollection,
                operation.getSerie().getId(),
                operation.getId()
        );
    }

    public static String idCollectionToIdOperation(String idCollection){
        String[] splitId = idCollection.split(DATA_COLLECTION_WORD);
        String idOperation = splitId[0];
        return idOperation;
    }

    public static String createIdOperationToIdCollection(String idOperation){
        return createIdOperationToIdCollection(idOperation, "");
    }

    public static String createIdOperationToIdCollection(String idOperation, String suffix){
        return suffix.length() == 0 ?
                String.format("%s%s", idOperation, DATA_COLLECTION_WORD) :
                String.format("%s%s-%s", idOperation, DATA_COLLECTION_WORD, suffix);
    }
}
