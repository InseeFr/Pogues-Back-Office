package fr.insee.pogues.metadata.repository;

import java.util.List;

public interface FamilyRepository {

    List<String>  getRootIds() throws Exception;

}
