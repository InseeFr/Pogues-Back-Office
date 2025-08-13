package fr.insee.pogues.service;

import fr.insee.pogues.model.*;
import fr.insee.pogues.persistence.service.QuestionnaireService;
import fr.insee.pogues.persistence.service.VersionService;
import fr.insee.pogues.webservice.model.dto.nomenclatures.ExtendedNomenclature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import static fr.insee.pogues.utils.Utils.loadQuestionnaireFromResources;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.MockitoAnnotations.initMocks;

@ExtendWith(MockitoExtension.class)
class NomenclatureServiceTest {

    @Mock
    QuestionnaireService questionnaireService;
    @Mock
    VersionService versionService;
    private NomenclatureService nomenclatureService;

    @BeforeEach
    void initQuestionnaireService(){
        nomenclatureService = new NomenclatureService(questionnaireService, versionService);
        initMocks(this);
    }

    @Test
    @DisplayName("Should get right NomenclatureDTD from questionnaire")
    void shouldGetRightNomenclaturesFromQuestionnaire() throws Exception {
        Questionnaire questionnaire = loadQuestionnaireFromResources("service/withAllNomenclatures.json");
        List<ExtendedNomenclature> nomenclatures = nomenclatureService.getNomenclaturesDTD(questionnaire);
        assertThat(nomenclatures).hasSize(29);
        assertThat(nomenclatures.getFirst().getRelatedQuestionNames())
                .containsExactly("N_1");
    }
}
