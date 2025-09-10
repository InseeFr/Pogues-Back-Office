package fr.insee.pogues.mapper;


import fr.insee.pogues.model.*;
import fr.insee.pogues.model.dto.multimode.MultimodeDTO;
import fr.insee.pogues.model.dto.multimode.MultimodeItemDTO;
import fr.insee.pogues.model.dto.multimode.MultimodeRuleDTO;
import fr.insee.pogues.model.dto.multimode.MultimodeRuleNameDTOEnum;

import java.util.List;

public class MultimodeMapper {

    /**
     * Compute the multimode into its Pogues model.
     * @param multimodeDTO Multimode to convert
     */
    public static Multimode toModel(MultimodeDTO multimodeDTO) {
        Multimode multimode = new Multimode();
        Rules leafRules = new Rules();
        if (multimodeDTO.getLeaf() != null) {
            leafRules.getRules().addAll(toModel(multimodeDTO.getLeaf().getRules()));
            multimode.setLeaf(leafRules);
        }
        Rules questionnaireRules = new Rules();
        if (multimodeDTO.getQuestionnaire() != null) {
            questionnaireRules.getRules().addAll(toModel(multimodeDTO.getQuestionnaire().getRules()));
            multimode.setQuestionnaire(questionnaireRules);
        }
        return multimode;
    }

    private static List<Rule> toModel(List<MultimodeRuleDTO> rulesDTO) {
        return rulesDTO.stream().map(MultimodeMapper::toModel).toList();
    }

    private static Rule toModel(MultimodeRuleDTO ruleDTO) {
        Rule rule = new Rule();
        rule.setType(ValueTypeEnum.VTL);
        rule.setValue(ruleDTO.getValue());
        rule.setName(toModel(ruleDTO.getName()));
        return rule;
    }

    private static MultimodeRuleNameEnum toModel(MultimodeRuleNameDTOEnum name) {
        return switch (name) {
            case MultimodeRuleNameDTOEnum.IS_MOVED -> MultimodeRuleNameEnum.IS_MOVED;
            case MultimodeRuleNameDTOEnum.IS_SPLIT -> MultimodeRuleNameEnum.IS_SPLIT;
        };
    }

    /**
     * Compute the articulation into its DTO
     * @param multimode Multimode to convert
     */
    public static MultimodeDTO toDTO(Multimode multimode) {
        if (multimode == null) return new MultimodeDTO();
        
        return new MultimodeDTO(
            new MultimodeItemDTO(toDTO(multimode.getQuestionnaire().getRules())),
                new MultimodeItemDTO(toDTO(multimode.getLeaf().getRules())));
    }

    private static List<MultimodeRuleDTO> toDTO(List<Rule> rules) {
        return rules.stream().map(MultimodeMapper::toDTO).toList();
    }

    private static MultimodeRuleDTO toDTO(Rule rule) {
        return new MultimodeRuleDTO(toDTO(rule.getName()), rule.getValue());
    }

    private static MultimodeRuleNameDTOEnum toDTO(MultimodeRuleNameEnum name) {
        return switch (name) {
            case MultimodeRuleNameEnum.IS_MOVED -> MultimodeRuleNameDTOEnum.IS_MOVED;
            case MultimodeRuleNameEnum.IS_SPLIT -> MultimodeRuleNameDTOEnum.IS_SPLIT;
        };
    }

}