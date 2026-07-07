package fr.insee.pogues.persistence.service;

import fr.insee.pogues.domain.entity.db.Version;
import fr.insee.pogues.persistence.repository.QuestionnaireVersionRepository;
import fr.insee.pogues.service.modelcleaning.ModelCleaningService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.node.JsonNodeFactory;
import tools.jackson.databind.node.ObjectNode;

import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VersionServiceTest {

    @Mock
    QuestionnaireVersionRepository questionnaireVersionRepository;

    @Mock
    ModelCleaningService modelCleaningService;

    @InjectMocks
    VersionService versionService;


    @Test
    @DisplayName("Should clean version model")
    void getVersionByVersionId_cleanModel() throws Exception {
        // Given a version of a questionnaire
        UUID versionId = UUID.randomUUID();
        ObjectNode questionnaire = JsonNodeFactory.instance.objectNode();
        questionnaire.put("id", "foo");
        questionnaire.putArray("Control");
        questionnaire.putArray("Child");
        Version version = new Version(versionId, "foo", null, null, questionnaire, "author");
        when(questionnaireVersionRepository.getVersionByVersionId(versionId, true)).thenReturn(version);

        // When getting the model of the version
        versionService.getVersionByVersionId(versionId, true);

        // Then the version model should be cleaned
        verify(modelCleaningService).cleanModel(questionnaire);
    }
}
