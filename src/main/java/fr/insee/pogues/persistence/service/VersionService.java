package fr.insee.pogues.persistence.service;

import fr.insee.pogues.domain.entity.db.Version;
import fr.insee.pogues.model.Questionnaire;
import fr.insee.pogues.persistence.repository.QuestionnaireRepository;
import fr.insee.pogues.persistence.repository.QuestionnaireVersionRepository;
import fr.insee.pogues.utils.DateUtils;
import fr.insee.pogues.utils.PoguesDeserializer;
import fr.insee.pogues.utils.PoguesSerializer;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import fr.insee.pogues.service.modelcleaning.ModelCleaningService;

import java.sql.Date;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import static fr.insee.pogues.utils.json.JSONFunctions.jsonStringtoJsonNode;

@Service
@AllArgsConstructor
public class VersionService {

    private QuestionnaireVersionRepository questionnaireVersionRepository;
    private QuestionnaireRepository questionnaireRepository;
    private ModelCleaningService modelCleaningService;

    public List<Version> getVersionsByQuestionnaireId(String poguesId, boolean withData) throws Exception {
        return questionnaireVersionRepository.getVersionsByQuestionnaireId(poguesId, withData);
    }

    public Version getLastVersionByQuestionnaireId(String poguesId, boolean withData) throws Exception {
        return questionnaireVersionRepository.getLastVersionByQuestionnaireId(poguesId, withData);
    }

    public JsonNode getVersionDataByVersionId(UUID versionId) throws Exception {
        Version version = this.getVersionByVersionId(versionId, true);
        return version.getData();
    }

    public Questionnaire getVersionDataQuestionnaireModelByVersionId(UUID versionId) throws Exception {
        return PoguesDeserializer.questionnaireToJavaObject(getVersionDataByVersionId(versionId));
    }

    public Version getVersionByVersionId(UUID versionId, boolean withData) throws Exception {
        Version version = questionnaireVersionRepository.getVersionByVersionId(versionId, withData);
        if (withData) version.setData(modelCleaningService.cleanModel(version.getData()));
        return version;
    }

    public void createVersionOfQuestionnaire(String poguesId, JsonNode data, String author) throws Exception {
        Instant now  = Instant.now();
        Version versionToStore = new Version(
                UUID.randomUUID(),
                poguesId,
                ZonedDateTime.now(),
                new Date(now.toEpochMilli()),
                data,
                author);
        questionnaireVersionRepository.createVersion(versionToStore);
    }

    public void deleteVersionsByQuestionnaireId(String poguesId) throws Exception {
        questionnaireVersionRepository.deleteVersionsByQuestionnaireId(poguesId);
    }

    public void deleteAllVersionsByQuestionnaireIdExceptLast(String poguesId) throws Exception {
        questionnaireVersionRepository.deleteAllVersionsByQuestionnaireIdExceptLast(poguesId);
    }

    public void restoreVersion(UUID versionId) throws Exception {
        // (1) Retrieve desired version
        Version version = questionnaireVersionRepository.getVersionByVersionId(versionId, true);
        // (2) Update lastUpdatedDate in Pogues-Model
        Questionnaire questionnaire = PoguesDeserializer.questionnaireToJavaObject(version.getData());
        questionnaire.setLastUpdatedDate(DateUtils.getIsoDateFromInstant(Instant.now()));
        JsonNode newQuestionnaire = jsonStringtoJsonNode(PoguesSerializer.questionnaireJavaToString(questionnaire));
        // (3) Update questionnaire in pogues table
        questionnaireRepository.updateQuestionnaire(version.getPoguesId(), newQuestionnaire);
        // (4) Create new version
        this.createVersionOfQuestionnaire(version.getPoguesId(), newQuestionnaire, version.getAuthor());
    }
}
