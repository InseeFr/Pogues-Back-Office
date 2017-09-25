package fr.insee.pogues.metadata.repository;

import java.util.List;

public interface GroupRepository {

    List<String>  getRootIds() throws Exception;

}
