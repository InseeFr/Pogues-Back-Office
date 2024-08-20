package fr.insee.pogues.persistence.repository;

import fr.insee.pogues.domain.entity.db.Version;

import java.util.List;
import java.util.UUID;

public interface QuestionnaireVersionRepository {

    List<Version> getVersionsByQuestionnaireId(String poguesId, boolean withData) throws Exception;

    Version getLastVersionByQuestionnaireId(String poguesId, boolean withData) throws Exception;

    Version getVersionByVersionId(UUID versionId, boolean withData) throws Exception;

    void createVersion(Version version) throws Exception;

    void deleteVersionsByQuestionnaireId(String poguesId) throws Exception;
}
