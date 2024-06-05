package fr.insee.pogues.metadata.service;

import fr.insee.pogues.metadata.model.magma.Frequence;
import fr.insee.pogues.metadata.model.magma.FrequenceType;
import fr.insee.pogues.metadata.model.magma.Operation;
import fr.insee.pogues.metadata.model.magma.Serie;
import fr.insee.pogues.metadata.model.pogues.DataCollection;
import fr.insee.pogues.metadata.model.pogues.DataCollectionContext;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class MetadataConverter {

    public static final String DATA_COLLECTION_WORD = "-dc";
    public static final String DATA_COLLECTION_TYPE = "data-collection";

    private static Calendar calendar = Calendar.getInstance(Locale.FRANCE);

    private static final Map<Integer,String> monthsOfYear = calendar.getDisplayNames(Calendar.MONTH, Calendar.LONG, Locale.FRANCE)
            .entrySet()
            .stream()
            .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));

    private static final Map<Integer,String> daysOfWeek = calendar.getDisplayNames(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.FRANCE)
            .entrySet()
            .stream()
            .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));

    public static List<DataCollection> createCollectionsFromOperation(Operation operation, Frequence frequence){
        FrequenceType frequenceType = frequence.getId();
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
                return dataCollections;
            }
            case M -> {
                dataCollections.addAll(
                        Stream.iterate(Calendar.JANUARY, m -> m+1)
                        .limit(monthsOfYear.size())
                        .map(m ->  new DataCollection(
                                createIdOperationToIdCollection(operation.getId(), m.toString()),
                                String.format("%s - %s",
                                        operation.getLabel().get(0).getContenu(),
                                        monthsOfYear.get(m)),
                                operation.getId(),
                                operation.getId(),
                                operation.getId(),
                                operation.getId(),
                                createIdOperationToIdCollection(operation.getId(), m.toString()),
                                null,
                                DATA_COLLECTION_TYPE)
                        ).toList());
                return dataCollections;
            }
            case Q -> {
                dataCollections.addAll(
                        Stream.iterate(0, q -> q+1)
                        .limit(4)
                        .map(q ->  new DataCollection(
                                createIdOperationToIdCollection(operation.getId(), q.toString()),
                                String.format("%s - Trimestre %s",
                                        operation.getLabel().get(0).getContenu(),
                                        q+1),
                                operation.getId(),
                                operation.getId(),
                                operation.getId(),
                                operation.getId(),
                                createIdOperationToIdCollection(operation.getId(), q.toString()),
                                null,
                                DATA_COLLECTION_TYPE)
                        ).toList());
                return dataCollections;
            }
            case B -> {
                dataCollections.addAll(
                        Stream.iterate(Calendar.MONDAY, m -> m+1)
                                .limit(daysOfWeek.size() - 2)
                                .map(m ->  new DataCollection(
                                        createIdOperationToIdCollection(operation.getId(), m.toString()),
                                        String.format("%s - %s",
                                                operation.getLabel().get(0).getContenu(),
                                                daysOfWeek.get(m)),
                                        operation.getId(),
                                        operation.getId(),
                                        operation.getId(),
                                        operation.getId(),
                                        createIdOperationToIdCollection(operation.getId(), m.toString()),
                                        null,
                                        DATA_COLLECTION_TYPE)
                                ).toList());
                return dataCollections;
            }
            case D -> {
                dataCollections.addAll(
                        Stream.iterate(Calendar.SUNDAY, m -> m+1)
                                .limit(daysOfWeek.size())
                                .map(m ->  new DataCollection(
                                        createIdOperationToIdCollection(operation.getId(), m.toString()),
                                        String.format("%s - %s",
                                                operation.getLabel().get(0).getContenu(),
                                                daysOfWeek.get(m)),
                                        operation.getId(),
                                        operation.getId(),
                                        operation.getId(),
                                        operation.getId(),
                                        createIdOperationToIdCollection(operation.getId(), m.toString()),
                                        null,
                                        DATA_COLLECTION_TYPE)
                                ).toList());
                return dataCollections;
            }
            case S -> {
                dataCollections.addAll(
                        Stream.iterate(0, m -> m+1)
                                .limit(2 )
                                .map(m ->  new DataCollection(
                                        createIdOperationToIdCollection(operation.getId(), m.toString()),
                                        String.format("%s - Semestre %s",
                                                operation.getLabel().get(0).getContenu(),
                                                m+1),
                                        operation.getId(),
                                        operation.getId(),
                                        operation.getId(),
                                        operation.getId(),
                                        createIdOperationToIdCollection(operation.getId(), m.toString()),
                                        null,
                                        DATA_COLLECTION_TYPE)
                                ).toList());
                return dataCollections;
            }
            case W -> {
                dataCollections.addAll(
                        Stream.iterate(0, m -> m+1)
                                .limit(52 )
                                .map(m ->  new DataCollection(
                                        createIdOperationToIdCollection(operation.getId(), m.toString()),
                                        String.format("%s - Semaine %s",
                                                operation.getLabel().get(0).getContenu(),
                                                m+1),
                                        operation.getId(),
                                        operation.getId(),
                                        operation.getId(),
                                        operation.getId(),
                                        createIdOperationToIdCollection(operation.getId(), m.toString()),
                                        null,
                                        DATA_COLLECTION_TYPE)
                                ).toList());
                return dataCollections;
            }
            case null, default -> {
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
                return dataCollections;
            }
        }
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


    public static List<DataCollection> createDataCollectionsFromSeries(List<Serie> series){
        return series
                .stream()
                .map(
                       serie -> new DataCollection(
                               serie.getId(),
                               serie.getLabel().get(0).getContenu(),
                               serie.getFamille().getId(),
                               serie.getFamille().getId(),
                               serie.getFamille().getId(),
                               null,
                               null,
                               "rp",
                               "sub-group"
                       )
                ).toList();
    }

    public static List<DataCollection> createDataCollectionsFromOperations(List<Operation> operations){
        return operations
                .stream()
                .map(
                        operation -> new DataCollection(
                                operation.getId(),
                                operation.getLabel().get(0).getContenu(),
                                operation.getSerie().getId(),
                                operation.getSerie().getId(),
                                operation.getSerie().getId(),
                                null,
                                null,
                                "rp",
                                "study-unit"
                        )
                ).toList();
    }
}
