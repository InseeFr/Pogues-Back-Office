package fr.insee.pogues.service.modelcleaning.cleaners;

import fr.insee.pogues.client.surveyregistry.SurveyRegistryClient;
import fr.insee.pogues.conversion.JSONSynonymsPreProcessor;
import fr.insee.pogues.domain.entity.db.MappingCodesListRegistreDB;
import fr.insee.pogues.model.*;
import fr.insee.pogues.model.dto.nomenclatures.NomenclatureDTO;
import fr.insee.pogues.service.modelcleaning.ModelCleaner;
import fr.insee.pogues.service.registrymapping.MappingRegistryService;
import fr.insee.pogues.utils.model.CodesList;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class NomenclatureRegistryCleaner implements ModelCleaner {

    private final MappingRegistryService mappingCodesListRegistryService;
    private final SurveyRegistryClient registryClient;

    private final ObjectMapper objectMapper = JsonMapper.builder().build();

    @Override
    public void apply(Questionnaire questionnaire) {
        if (questionnaire.getCodeLists() == null || questionnaire.getCodeLists().getCodeList() == null) return;

        // retrieve all codes-list mapped in registry
        List<MappingCodesListRegistreDB> mappingsFromDB = mappingCodesListRegistryService.getAll();
        Map<String, UUID> mappings = buildMapFromList(mappingsFromDB);

        List<CodeList> nomenclatures = questionnaire.getCodeLists().getCodeList().stream().filter(CodesList::isNomenclatureCodeList).toList();

        List<CodeList> nomenclaturesInRegistry = nomenclatures.stream()
                .filter(nomenclature -> mappings.containsKey(nomenclature.getId()))
                .toList();

        for (CodeList nomenclature : nomenclaturesInRegistry) {
            replaceNomenclatureReferenceInQuestions(questionnaire,
                    nomenclature.getId(), mappings.get(nomenclature.getId()));
            replaceNomenclatureReferenceInVariables(questionnaire,
                    nomenclature.getId(), mappings.get(nomenclature.getId()));
            // should be the last state
            replaceNomenclatureDictionary(nomenclature, mappings.get(nomenclature.getId()));
        }
    }


    private void replaceNomenclatureDictionary(CodeList nomenclature, UUID newNomenclatureId) {
        NomenclatureDTO nomenclatureFromRegistry = registryClient.getNomenclatureMetadataById(newNomenclatureId);
        nomenclature.setName(null);
        nomenclature.setId(newNomenclatureId.toString());
        nomenclature.setLabel(nomenclatureFromRegistry.getLabel());
        nomenclature.setUrn(nomenclatureFromRegistry.getUrn());
        nomenclature.setTheme(nomenclatureFromRegistry.getTheme());
        nomenclature.setReferenceYear(nomenclatureFromRegistry.getReferenceYear());
        nomenclature.setVersion(Integer.valueOf(nomenclatureFromRegistry.getVersion()));
        nomenclature.setSuggesterParameters(convertSuggesterParameters(nomenclatureFromRegistry.getSuggesterParameters()));
    }

    private void replaceNomenclatureReferenceInQuestions(Questionnaire questionnaire,
                                                               String oldNomenclatureId, UUID newNomenclatureId) {
        questionnaire.getChild().forEach(componentType ->
                replaceNomenclatureReferenceInComponent(componentType, oldNomenclatureId, newNomenclatureId));
    }

    private void replaceNomenclatureReferenceInComponent(ComponentType poguesComponent, String oldNomenclatureId, UUID newNomenclatureId) {
        if (poguesComponent instanceof SequenceType poguesSequence) {
            poguesSequence.getChild().forEach(childComponent -> replaceNomenclatureReferenceInComponent(childComponent, oldNomenclatureId, newNomenclatureId));
        }
        if (poguesComponent instanceof QuestionType poguesQuestion) {
            poguesQuestion.getResponse().forEach(responseType -> {
                if (oldNomenclatureId.equals(responseType.getCodeListReference())) {
                    responseType.setCodeListReference(newNomenclatureId.toString());
                }
            });
        }
    }

    private void replaceNomenclatureReferenceInVariables(Questionnaire questionnaire, String oldNomenclatureId, UUID newNomenclatureId) {
        questionnaire.getVariables().getVariable().forEach(variableType -> {
                    if (oldNomenclatureId.equals(variableType.getCodeListReference())) {
                        variableType.setCodeListReference(newNomenclatureId.toString());
                    }
                }
        );
    }

    private Map<String, UUID> buildMapFromList(List<MappingCodesListRegistreDB> mappings) {
        return mappings.stream()
                .collect(Collectors.toMap(
                        MappingCodesListRegistreDB::getPoguesCodesListId,
                        MappingCodesListRegistreDB::getRegistreCodesListId
                ));
    }

    public SuggesterParametersType convertSuggesterParameters(Object suggesterParameters) {
        JSONSynonymsPreProcessor synonymsPreProcessor = new JSONSynonymsPreProcessor();
        String jsonIn = objectMapper.writeValueAsString(suggesterParameters);
        String jsonOut = synonymsPreProcessor.transformSuggesterParameters(jsonIn);
        return objectMapper.readValue(jsonOut, SuggesterParametersType.class);

    }
}
