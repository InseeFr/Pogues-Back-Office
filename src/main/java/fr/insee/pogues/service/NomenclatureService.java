package fr.insee.pogues.service;

import fr.insee.pogues.client.surveyregistry.SurveyRegistryClient;
import fr.insee.pogues.exception.PoguesException;
import fr.insee.pogues.mapper.CodesListMapper;
import fr.insee.pogues.model.CodeList;
import fr.insee.pogues.model.Questionnaire;
import fr.insee.pogues.model.dto.nomenclatures.ExtendedNomenclatureDTO;
import fr.insee.pogues.model.dto.nomenclatures.NomenclatureDTO;
import fr.insee.pogues.model.dto.nomenclatures.NomenclatureUrlDTO;
import fr.insee.pogues.persistence.service.IQuestionnaireService;
import fr.insee.pogues.persistence.service.VersionService;
import fr.insee.pogues.utils.model.CodesList;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.util.List;
import java.util.UUID;

import static fr.insee.pogues.utils.model.CodesList.getListOfQuestionNameWhereCodesListIsUsed;

@Service
@Slf4j
@AllArgsConstructor
public class NomenclatureService {

    private final IQuestionnaireService questionnaireService;
    private final VersionService versionService;
    private final SuggesterVisuService suggesterVisuService;
    private final SurveyRegistryClient surveyRegistryRestClient;

    /**
     * Fetch the nomenclatures of a questionnaire.
     * @param questionnaireId ID of the questionnaire to fetch the nomenclatures from
     * @throws Exception Could not read the questionnaire from the DB
     * @throws PoguesException 404 questionnaire not found
     */
    public List<ExtendedNomenclatureDTO> getQuestionnaireNomenclatures(String questionnaireId) throws Exception {
        Questionnaire questionnaire = questionnaireService.getQuestionnaireModelByID(questionnaireId);
        List<CodeList> nomenclatures = getQuestionnaireNomenclatures(questionnaire);
        return computeNomenclatureDTO(nomenclatures, questionnaire);
    }

    /**
     * Fetch the nomenclatures of a questionnaire's version.
     * @param versionId ID of the questionnaire's version to fetch the nomenclatures from
     * @throws Exception Could not read the questionnaire from the DB
     * @throws PoguesException 404 questionnaire not found
     */
    public List<ExtendedNomenclatureDTO> getVersionNomenclatures(UUID versionId) throws Exception {
        Questionnaire questionnaire = versionService.getVersionDataQuestionnaireModelByVersionId(versionId);
        List<CodeList> nomenclatures = getQuestionnaireNomenclatures(questionnaire);
        return computeNomenclatureDTO(nomenclatures, questionnaire);
    }

    public List<NomenclatureUrlDTO> getNomenclaturesUrls(String questionnaireId) throws Exception {
        return suggesterVisuService.computeNomenclaturesUrls(questionnaireId);
    }

    /**
     * Return the nomenclatures of a questionnaire.
     * @param questionnaire Questionnaire in the Pogues model format.
     */
    public List<CodeList> getQuestionnaireNomenclatures(Questionnaire questionnaire) {
        return questionnaire.getCodeLists().getCodeList().stream()
                .filter(CodesList::isNomenclatureCodeList)
                .toList();
    }

    private List<ExtendedNomenclatureDTO> computeNomenclatureDTO(List<CodeList> nomenclatures, Questionnaire questionnaire) {
        return nomenclatures.stream()
                .map(CodesListMapper::toNomenclatureDTO)
                .map(nomenclature -> new ExtendedNomenclatureDTO(
                        nomenclature,
                        getListOfQuestionNameWhereCodesListIsUsed(questionnaire, nomenclature.getId())
                )).toList();
    }

    /**
     * Fetch the nomenclatures that can be used by the users in the questionnaire.
     * Should include suggesterParameters
     * @throws HttpClientErrorException Could not get it from the API because of a client error
     * @throws HttpServerErrorException Could not get it from the API because of a server error
     */
    public List<NomenclatureDTO> getAllNomenclatures() throws HttpClientErrorException, HttpServerErrorException {
        return surveyRegistryRestClient.getNomenclatures();
    }
}
