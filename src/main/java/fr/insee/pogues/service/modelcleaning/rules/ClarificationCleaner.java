package fr.insee.pogues.service.modelcleaning.rules;

import fr.insee.pogues.model.ComponentType;
import fr.insee.pogues.model.QuestionType;
import fr.insee.pogues.model.QuestionTypeEnum;
import fr.insee.pogues.model.Questionnaire;
import fr.insee.pogues.utils.model.cleaner.ModelCleaner;

import static fr.insee.pogues.service.modelcleaning.rules.ClarificationRules.limitToSingleClarification;

/**
 * This cleaner ensures that each question (QCU/QCM) keeps only a single clarification question,
 * and removes related FlowControls that target removed clarifications.
 */
public class ClarificationCleaner implements ModelCleaner {

    @Override
    public void apply(Questionnaire questionnaire) {singleClarification(questionnaire);}

    private void singleClarification(ComponentType poguesComponent){
        if(poguesComponent instanceof QuestionType question && (QuestionTypeEnum.MULTIPLE_CHOICE.equals(question.getQuestionType())
                || QuestionTypeEnum.SINGLE_CHOICE.equals(question.getQuestionType())) ){
            limitToSingleClarification(question);
        }
    }
}
