package fr.insee.pogues.utils.model;

import fr.insee.pogues.model.*;
import fr.insee.pogues.utils.model.question.Table;
import org.junit.jupiter.api.Test;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import static fr.insee.pogues.utils.Utils.findQuestionWithId;
import static fr.insee.pogues.utils.Utils.loadQuestionnaireFromResources;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TableTest {

    @Test
    void testGetUniqueResponseType() throws JAXBException, URISyntaxException, IOException {
        Questionnaire questionnaire = loadQuestionnaireFromResources("service/complexTableWithCodesLists.json");
        QuestionType table = findQuestionWithId(questionnaire, "m7d6ws56");

        List<ResponseType> uniqueResponseType = Table.getUniqueResponseType(table);
        List<DimensionType> measureDimensions = table.getResponseStructure().getDimension().stream()
                .filter(dimensionType -> DimensionTypeEnum.MEASURE.equals(dimensionType.getDimensionType()))
                .toList();
        assertEquals(2, uniqueResponseType.size());
        assertEquals(measureDimensions.size(), uniqueResponseType.size());
        assertEquals(DatatypeTypeEnum.TEXT, uniqueResponseType.get(0).getDatatype().getTypeName());
        assertEquals(VisualizationHintEnum.RADIO, uniqueResponseType.get(0).getDatatype().getVisualizationHint());
        assertEquals(DatatypeTypeEnum.NUMERIC, uniqueResponseType.get(1).getDatatype().getTypeName());
    }


    @Test
    void testGetUniqueResponseTypeWhenSeveralResponseTypeAreEquals() throws JAXBException, URISyntaxException, IOException {
        Questionnaire questionnaire = loadQuestionnaireFromResources("service/tableWithSameDataTypeFor2Measures.json");
        QuestionType table = findQuestionWithId(questionnaire, "m8fv4po6");

        List<ResponseType> uniqueResponseType = Table.getUniqueResponseType(table);List<DimensionType> measureDimensions = table.getResponseStructure().getDimension().stream()
                .filter(dimensionType -> DimensionTypeEnum.MEASURE.equals(dimensionType.getDimensionType()))
                .toList();
        assertEquals(3, uniqueResponseType.size());
        assertEquals(measureDimensions.size(), uniqueResponseType.size());
        assertEquals(DatatypeTypeEnum.TEXT, uniqueResponseType.get(0).getDatatype().getTypeName());
        assertEquals(VisualizationHintEnum.RADIO, uniqueResponseType.get(0).getDatatype().getVisualizationHint());
        assertEquals(DatatypeTypeEnum.NUMERIC, uniqueResponseType.get(1).getDatatype().getTypeName());
        assertEquals(DatatypeTypeEnum.TEXT, uniqueResponseType.get(2).getDatatype().getTypeName());
        assertEquals(VisualizationHintEnum.RADIO, uniqueResponseType.get(2).getDatatype().getVisualizationHint());
    }
}
