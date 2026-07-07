package fr.insee.pogues.utils.model.cleaner;

import fr.insee.pogues.model.CodeList;
import fr.insee.pogues.model.CodeLists;
import fr.insee.pogues.model.Questionnaire;
import fr.insee.pogues.service.modelcleaning.cleaners.NomenclatureCleaner;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static fr.insee.pogues.utils.ModelCreatorUtils.*;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class NomenclatureCleanerTest {

    @Test
    @DisplayName("Should not remove classic questionnaire codeList")
    void should_not_remove_classic_codeList() {
        // Given
        Questionnaire questionnaire = new Questionnaire();
        CodeLists codeLists = new CodeLists();
        questionnaire.setCodeLists(codeLists);
        questionnaire.getCodeLists().getCodeList().add(initFakeCodeList("codeList1", "super code list 1"));
        questionnaire.getCodeLists().getCodeList().add(initFakeCodeList("codeList2", "super code list 2"));
        questionnaire.getCodeLists().getCodeList().add(initFakeCodeList("codeList3", "super code list 3"));

        // When
        new NomenclatureCleaner().apply(questionnaire);

        // Then
        assertThat(questionnaire.getCodeLists().getCodeList()).hasSize(3);
    }

    @Test
    @DisplayName("Should not remove used nomenclature in questionnaire")
    void should_not_remove_used_nomenclature() {
        // Given (all nomenclature are used)
        Questionnaire questionnaire = new Questionnaire();
        CodeLists codeLists = new CodeLists();
        questionnaire.setCodeLists(codeLists);
        questionnaire.getCodeLists().getCodeList().add(initFakeNomenclature("nomenclature1", "super nomenclature 1"));
        questionnaire.getCodeLists().getCodeList().add(initFakeNomenclature("nomenclature2", "super nomenclature 2"));
        questionnaire.getCodeLists().getCodeList().add(initFakeNomenclature("nomenclature3", "super nomenclature 3"));
        questionnaire.getChild().add(createQuestionWithCodeList("nomenclature1"));
        questionnaire.getChild().add(createQuestionWithCodeList("nomenclature2"));
        questionnaire.getChild().add(createQuestionWithCodeList("nomenclature3"));

        // When
        new NomenclatureCleaner().apply(questionnaire);

        // Then
        assertThat(questionnaire.getCodeLists().getCodeList()).hasSize(3);
        assertThat(questionnaire.getCodeLists().getCodeList().stream().map(CodeList::getId)).contains("nomenclature1", "nomenclature2", "nomenclature3");
    }

    @Test
    @DisplayName("Should remove unused nomenclature in questionnaire")
    void should_remove_unused_nomenclature() {
        // Given (some nomenclature are used)
        Questionnaire questionnaire = new Questionnaire();
        CodeLists codeLists = new CodeLists();
        questionnaire.setCodeLists(codeLists);
        questionnaire.getCodeLists().getCodeList().add(initFakeNomenclature("nomenclature1", "super nomenclature 1"));
        questionnaire.getCodeLists().getCodeList().add(initFakeNomenclature("nomenclature2", "super nomenclature 2"));
        questionnaire.getCodeLists().getCodeList().add(initFakeNomenclature("nomenclature3", "super nomenclature 3"));
        questionnaire.getChild().add(createQuestionWithCodeList("nomenclature2"));

        // When
        new NomenclatureCleaner().apply(questionnaire);

        // Then (only used nomenclature should stay i.e "nomenclature2"
        assertThat(questionnaire.getCodeLists().getCodeList()).hasSize(1);
        assertThat(questionnaire.getCodeLists().getCodeList().stream().map(CodeList::getId)).contains("nomenclature2");
    }
}
