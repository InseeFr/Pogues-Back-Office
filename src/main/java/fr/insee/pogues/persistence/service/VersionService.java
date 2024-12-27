package fr.insee.pogues.persistence.service;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.pogues.domain.entity.db.Version;

import java.util.List;
import java.util.UUID;

public interface VersionService {

    List<Version> getVersionsByQuestionnaireId(String poguesId, boolean withData) throws Exception;

    Version getLastVersionByQuestionnaireId(String poguesId, boolean withData) throws Exception;

    JsonNode getVersionDataByVersionId(UUID versionId) throws Exception;

    Version getVersionByVersionId(UUID versionId, boolean withData) throws Exception;

    void createVersionOfQuestionnaire(String poguesId, JsonNode data, String author) throws Exception;

    void deleteVersionsByQuestionnaireId(String poguesId) throws Exception;

    void deleteAllVersionsByQuestionnaireIdExceptLast(String poguesId) throws Exception;

    void restoreVersion(UUID versionId) throws Exception;
}
