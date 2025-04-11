package fr.insee.pogues.utils.model.cleaner;

import fr.insee.pogues.model.Questionnaire;

/**
 * Interface: implementation should clean questionnaire model to have a correct one
 * It's usefull when modeling change and to fix old modeling
 */
public interface ModelCleaner {
    void apply(Questionnaire questionnaire);
}
