package fr.insee.pogues.service.modelcleaning.cleaners;

import fr.insee.pogues.model.CodeList;
import fr.insee.pogues.model.Questionnaire;
import fr.insee.pogues.service.modelcleaning.ModelCleaner;
import fr.insee.pogues.utils.model.CodesList;


/**
 * This cleaner removes the references to unused nomenclatures in a questionnaire.
 */
public class NomenclatureCleaner implements ModelCleaner {

    @Override
    public void apply(Questionnaire questionnaire) {
        if (questionnaire.getCodeLists() == null || questionnaire.getCodeLists().getCodeList() == null) return;
        questionnaire.getCodeLists().getCodeList().removeIf(codeList -> isUnusedNomenclature(questionnaire, codeList));
    }

    private static boolean isUnusedNomenclature(Questionnaire questionnaire, CodeList nomenclature) {
        return CodesList.isNomenclatureCodeList(nomenclature) && CodesList.getListOfQuestionIdWhereCodesListIsUsed(questionnaire, nomenclature.getId()).isEmpty();
    }
}
