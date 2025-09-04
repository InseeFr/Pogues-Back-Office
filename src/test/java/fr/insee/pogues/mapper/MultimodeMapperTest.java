package fr.insee.pogues.mapper;

import fr.insee.pogues.model.*;
import fr.insee.pogues.model.dto.multimode.MultimodeDTO;
import fr.insee.pogues.model.dto.multimode.MultimodeItemDTO;
import fr.insee.pogues.model.dto.multimode.MultimodeRuleDTO;
import fr.insee.pogues.model.dto.multimode.MultimodeRuleNameDTOEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MultimodeMapperTest {

    @Test
    @DisplayName("Should convert DTO multimode into model")
    void toModel_success() {
        // Given a DTO multimode
        MultimodeDTO multimodeDTO = new MultimodeDTO(
            new MultimodeItemDTO(List.of(
                new MultimodeRuleDTO(MultimodeRuleNameDTOEnum.IS_MOVED, "nvl(HABITEZ_VOUS_ICI, true)")
            )),
            new MultimodeItemDTO(List.of(
                new MultimodeRuleDTO(MultimodeRuleNameDTOEnum.IS_MOVED, "nvl(PRENOM_HABITE_PLUS_LA, false)"),
                new MultimodeRuleDTO(MultimodeRuleNameDTOEnum.IS_MOVED, "nvl(fzqfqzgjh, false)"),
                new MultimodeRuleDTO(MultimodeRuleNameDTOEnum.IS_SPLIT, "nvl(PRENOM_HABITE_PLUS_LA, false)")
            ))
        );

        Multimode expected = new Multimode();
        Rule ruleQ = new Rule();
        ruleQ.setName(MultimodeRuleNameEnum.IS_MOVED);
        ruleQ.setValue("nvl(HABITEZ_VOUS_ICI, true)");
        ruleQ.setType(ValueTypeEnum.VTL);
        Rules rulesQ = new Rules();
        rulesQ.getRules().add(ruleQ);
        expected.setQuestionnaire(rulesQ);
        Rule ruleL1 = new Rule();
        ruleL1.setName(MultimodeRuleNameEnum.IS_MOVED);
        ruleL1.setValue("nvl(PRENOM_HABITE_PLUS_LA, false)");
        ruleL1.setType(ValueTypeEnum.VTL);
        Rule ruleL2 = new Rule();
        ruleL2.setName(MultimodeRuleNameEnum.IS_MOVED);
        ruleL2.setValue("nvl(fzqfqzgjh, false)");
        ruleL2.setType(ValueTypeEnum.VTL);
        Rule ruleL3 = new Rule();
        ruleL3.setName(MultimodeRuleNameEnum.IS_SPLIT);
        ruleL3.setValue("nvl(PRENOM_HABITE_PLUS_LA, false)");
        ruleL3.setType(ValueTypeEnum.VTL);
        Rules rulesL = new Rules();
        rulesL.getRules().addAll(List.of(ruleL1, ruleL2, ruleL3));
        expected.setLeaf(rulesL);

        // When we convert it to Pogues model
        Multimode res = MultimodeMapper.toModel(multimodeDTO);

        // It is correctly converted
        assertThat(res).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    @DisplayName("Should convert model multimode into DTO")
    void toDTO_success() {
        // Given a model multimode
        Multimode multimode = new Multimode();
        Rule ruleQ = new Rule();
        ruleQ.setName(MultimodeRuleNameEnum.IS_MOVED);
        ruleQ.setValue("nvl(HABITEZ_VOUS_ICI, true)");
        ruleQ.setType(ValueTypeEnum.VTL);
        Rules rulesQ = new Rules();
        rulesQ.getRules().add(ruleQ);
        multimode.setQuestionnaire(rulesQ);
        Rule ruleL1 = new Rule();
        ruleL1.setName(MultimodeRuleNameEnum.IS_MOVED);
        ruleL1.setValue("nvl(PRENOM_HABITE_PLUS_LA, false)");
        ruleL1.setType(ValueTypeEnum.VTL);
        Rule ruleL2 = new Rule();
        ruleL2.setName(MultimodeRuleNameEnum.IS_MOVED);
        ruleL2.setValue("nvl(fzqfqzgjh, false)");
        ruleL2.setType(ValueTypeEnum.VTL);
        Rule ruleL3 = new Rule();
        ruleL3.setName(MultimodeRuleNameEnum.IS_SPLIT);
        ruleL3.setValue("nvl(PRENOM_HABITE_PLUS_LA, false)");
        ruleL3.setType(ValueTypeEnum.VTL);
        Rules rulesL = new Rules();
        rulesL.getRules().addAll(List.of(ruleL1, ruleL2, ruleL3));
        multimode.setLeaf(rulesL);

        MultimodeDTO expected = new MultimodeDTO(
            new MultimodeItemDTO(List.of(
                new MultimodeRuleDTO(MultimodeRuleNameDTOEnum.IS_MOVED, "nvl(HABITEZ_VOUS_ICI, true)")
            )),
            new MultimodeItemDTO(List.of(
                new MultimodeRuleDTO(MultimodeRuleNameDTOEnum.IS_MOVED, "nvl(PRENOM_HABITE_PLUS_LA, false)"),
                new MultimodeRuleDTO(MultimodeRuleNameDTOEnum.IS_MOVED, "nvl(fzqfqzgjh, false)"),
                new MultimodeRuleDTO(MultimodeRuleNameDTOEnum.IS_SPLIT, "nvl(PRENOM_HABITE_PLUS_LA, false)")
            ))
        );

        // When we convert it to DTO
        MultimodeDTO res = MultimodeMapper.toDTO(multimode);

        // It is correctly converted
        assertThat(res).usingRecursiveComparison().isEqualTo(expected);
    }

}
