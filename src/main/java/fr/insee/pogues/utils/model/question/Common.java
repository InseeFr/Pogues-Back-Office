package fr.insee.pogues.utils.model.question;

import fr.insee.pogues.model.MappingType;

import java.util.UUID;

public class Common {

    public static String getNewUniqueId(){
        return UUID.randomUUID().toString();
    }

    public static String MAPPING_TARGET_FORMAT = "%d %d";
    public static String COLLECTED_LABEL_FORMAT = "%s -%s";

    public static MappingType createNewMapping(String mappingSource, String mappingTarget){
        MappingType mapping = new MappingType();
        mapping.setMappingSource(mappingSource);
        mapping.setMappingTarget(mappingTarget);
        return mapping;
    }
}
