package fr.insee.pogues.metadata.service;

import fr.insee.pogues.metadata.model.magma.FrequenceType;
import fr.insee.pogues.metadata.model.magma.Operation;
import fr.insee.pogues.metadata.model.pogues.DataCollection;
import fr.insee.pogues.metadata.model.pogues.DataCollectionContext;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

@Service
public class MetadataConverter {

    public static final String DATA_COLLECTION_WORD = "-dc";
    public static final String DATA_COLLECTION_TYPE = "data-collection";

    static Calendar calendar = Calendar.getInstance(Locale.FRANCE);

    public static List<DataCollection> createCollectionsFromOperation(Operation operation){
        FrequenceType frequenceType = operation.getSerie().getFrequence().getId();
        List<DataCollection> dataCollections = new ArrayList<>();
        switch (frequenceType){
            case U,P,A3,T,A,C -> {
                DataCollection dataCollection = new DataCollection(
                        createIdOperationToIdCollection(operation.getId()),
                        operation.getLabel().get(0).getContenu(),
                        operation.getId(),
                        operation.getId(),
                        operation.getId(),
                        operation.getId(),
                        createIdOperationToIdCollection(operation.getId()),
                        null,
                        DATA_COLLECTION_TYPE);
                dataCollections.add(dataCollection);
            }
            case M -> {
                dataCollections.addAll(
                        Stream.iterate(Calendar.JANUARY, m -> m+1)
                        .limit(Calendar.UNDECIMBER)
                        .map(m ->  new DataCollection(
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
                dataCollections.addAll(
                        Stream.iterate(0, q -> q+1)
                        .limit(4)
                        .map(q ->  new DataCollection(
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


        return dataCollections;
    };

    public static DataCollectionContext createDataCollectionContext(String idCollection, Operation operation){
        return new DataCollectionContext(
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
