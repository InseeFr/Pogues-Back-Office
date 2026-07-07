package fr.insee.pogues.service.modelcleaning.cleaners;

import fr.insee.pogues.domain.entity.db.MappingCodesListRegistreDB;
import fr.insee.pogues.model.*;
import fr.insee.pogues.model.dto.nomenclatures.NomenclatureDTO;
import fr.insee.pogues.service.stub.MappingRegistryServiceStub;
import fr.insee.pogues.service.stub.SurveyRegistryClientStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;
import java.util.UUID;

import static fr.insee.pogues.utils.ModelCreatorUtils.*;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class NomenclatureRegistryCleanerTest {

    private NomenclatureRegistryCleaner nomenclatureRegistryCleaner;
    private SurveyRegistryClientStub registryClient;
    private MappingRegistryServiceStub mappingService;


    @BeforeEach
    void init(){
        registryClient = new SurveyRegistryClientStub();
        mappingService = new MappingRegistryServiceStub();
        nomenclatureRegistryCleaner = new NomenclatureRegistryCleaner(mappingService, registryClient);
    }


    @Test
    @DisplayName("Should convert correctly suggesterParameters")
    void testConvertSuggesterParameters(){
        // Given
        String jsonSuggestersParams = """
                {
                      "version": 1,
                      "fields": [
                        {
                          "name": "label",
                          "rules": [
                            "[\\\\w-]+"
                          ],
                          "language": "French",
                          "min": 3,
                          "stemmer": false,
                          "synonyms": {
                            "accueil": [ "ACCEUIL" ],
                            "EHPAD": [ "EPHAD", "EPAD" ]
                          }
                        }
                      ],
                      "queryParser": {
                        "type": "tokenized",
                        "params": {
                          "language": "French",
                          "pattern": "[\\\\w.-]+",
                          "min": 3,
                          "stemmer": false
                        }
                      }
                    }
                """;

        Object abstractSuggestersParams = JsonMapper
                .builder().build()
                .readValue(jsonSuggestersParams, Object.class);
        // When
        SuggesterParametersType suggesterParametersType = nomenclatureRegistryCleaner.convertSuggesterParameters(abstractSuggestersParams);
        // Then
        assertEquals("French", suggesterParametersType.getFields().getFirst().getLanguage());
        assertThat(suggesterParametersType.getFields().getFirst().getSynonyms()).hasSize(2);

        FieldSynonym accueilSyn = suggesterParametersType.getFields().getFirst().getSynonyms().getFirst();
        assertEquals("accueil", accueilSyn.getSource());
        assertThat(accueilSyn.getTarget()).hasSize(1);
        assertThat(accueilSyn.getTarget()).contains("ACCEUIL");


    }


    @Test
    @DisplayName("Should change nomenclature reference if exist in Mapping")
    void testExistInMapping(){

        // Given
        UUID activtesUUID = UUID.randomUUID();

        mappingService.setAllMappings(List.of(
                new MappingCodesListRegistreDB(1L, "nomenclature1", activtesUUID)
                )
        );
        registryClient.setNomenclatureToReturn(new NomenclatureDTO(
                activtesUUID.toString(),
                "Activité Label registry",
                "2",
                "urn:super-urn-from-registry",
                new Object(),
                "theme",
                "refYear"));

        // Given (some nomenclature are used)
        Questionnaire questionnaire = new Questionnaire();
        CodeLists codeLists = new CodeLists();
        questionnaire.setCodeLists(codeLists);
        questionnaire.getCodeLists().getCodeList().add(initFakeNomenclature("nomenclature1","super nomenclature 1"));
        QuestionType question = createQuestionWithCodeList("nomenclature1");
        questionnaire.getChild().add(question);
        Questionnaire.Variables variables = new Questionnaire.Variables();
        VariableType collectedVariable = createCollectedVariableAccordingToResponse(question.getResponse().getFirst());
        variables.getVariable().add(collectedVariable);
        questionnaire.setVariables(variables);
        // when
        nomenclatureRegistryCleaner.apply(questionnaire);
        // Then
        // codeList updated
        CodeList codeList =  questionnaire.getCodeLists().getCodeList().getFirst();
        assertEquals(activtesUUID.toString(), codeList.getId());
        assertEquals("Activité Label registry", codeList.getLabel());
        assertEquals("urn:super-urn-from-registry", codeList.getUrn());
        assertEquals("theme", codeList.getTheme());
        assertEquals("refYear", codeList.getReferenceYear());
        assertEquals(2, codeList.getVersion());
        assertNull(codeList.getName());
        // variable updated
        assertEquals(activtesUUID.toString(), collectedVariable.getCodeListReference());
        // response updated
        assertEquals(activtesUUID.toString(), question.getResponse().getFirst().getCodeListReference());
    }

    @Test
    @DisplayName("Should not change nomenclature reference if not exist in Mapping")
    void testWithEmptyMapping(){
        // Given (some nomenclature are used)
        Questionnaire questionnaire = new Questionnaire();
        CodeLists codeLists = new CodeLists();
        questionnaire.setCodeLists(codeLists);
        questionnaire.getCodeLists().getCodeList().add(initFakeNomenclature("nomenclature1","super nomenclature 1"));
        QuestionType question = createQuestionWithCodeList("nomenclature1");
        questionnaire.getChild().add(question);
        Questionnaire.Variables variables = new Questionnaire.Variables();
        variables.getVariable().add(createCollectedVariableAccordingToResponse(question.getResponse().getFirst()));
        questionnaire.setVariables(variables);

        // empty mapping
        mappingService.setAllMappings(List.of());

        // when
        nomenclatureRegistryCleaner.apply(questionnaire);

        // Then, nothing change
        assertEquals("nomenclature1", questionnaire.getCodeLists().getCodeList().getFirst().getId());
        assertEquals("nomenclature1", questionnaire.getVariables().getVariable().getFirst().getCodeListReference());
        assertEquals("nomenclature1", ((QuestionType) questionnaire.getChild().getFirst()).getResponse().getFirst().getCodeListReference());


    }


}
