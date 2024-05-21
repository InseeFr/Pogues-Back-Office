package fr.insee.pogues.metadata.model.pogues;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class CollectionContext {
    String dataCollectionId;
    String serieId;
    String operationId;
}
