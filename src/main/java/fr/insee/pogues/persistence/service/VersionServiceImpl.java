package fr.insee.pogues.persistence.service;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.pogues.domain.entity.db.Version;
import fr.insee.pogues.model.Questionnaire;
import fr.insee.pogues.persistence.repository.QuestionnaireRepository;
import fr.insee.pogues.persistence.repository.QuestionnaireVersionRepository;
import fr.insee.pogues.utils.DateUtils;
import fr.insee.pogues.utils.PoguesDeserializer;
import fr.insee.pogues.utils.PoguesSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import static fr.insee.pogues.utils.json.JSONFunctions.jsonStringtoJsonNode;

@Service
public class VersionServiceImpl implements VersionService {

    @Autowired
    private QuestionnaireVersionRepository questionnaireVersionRepository;

    @Autowired
    private QuestionnaireRepository questionnaireRepository;


    @Override
    public List<Version> getVersionsByQuestionnaireId(String poguesId, boolean withData) throws Exception {
        return questionnaireVersionRepository.getVersionsByQuestionnaireId(poguesId, withData);
    }

    @Override
    public Version getLastVersionByQuestionnaireId(String poguesId, boolean withData) throws Exception {
        return questionnaireVersionRepository.getLastVersionByQuestionnaireId(poguesId, withData);
    }

    @Override
    public JsonNode getVersionDataByVersionId(UUID versionId) throws Exception {
        Version version = this.getVersionByVersionId(versionId, true);
        return version.getData();
    }

    @Override
    public Version getVersionByVersionId(UUID versionId, boolean withData) throws Exception {
        return questionnaireVersionRepository.getVersionByVersionId(versionId, withData);
    }

    @Override
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

    @Override
    public void deleteVersionsByQuestionnaireId(String poguesId) throws Exception {
        questionnaireVersionRepository.deleteVersionsByQuestionnaireId(poguesId);
    }

    @Override
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
