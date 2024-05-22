package fr.insee.pogues.metadata.model.pogues;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Collection is a concept of Pogues "Campagne",
 * It depends from frequence of it's operation > serie
 */

@AllArgsConstructor
@Getter
@Setter
public class DataCollection {
    private String id;
    private String label;
    private String parent;
    private String groupId;
    private String subGroupId;
    private String studyUnitId;
    private String dataCollectionId;
    private String resourcePackageId;
    private String type;
}
