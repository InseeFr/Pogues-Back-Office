package fr.insee.pogues.utils;

import fr.insee.pogues.model.*;
import fr.insee.pogues.webservice.model.dtd.variables.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

import static fr.insee.pogues.utils.VariablesConverter.toDTO;
import static fr.insee.pogues.utils.VariablesConverter.toModel;
import static org.assertj.core.api.Assertions.assertThat;

class VariablesConverterTest {

    @Test
    @DisplayName("Should convert DTO collected variable into model")
    void toModel_success_collectedVariable() throws Exception {
        // Given a DTO collected variable
        VariableDTODatatype datatypeDTO = new VariableDTODatatype(VariableDTODatatypeTypeEnum.BOOLEAN, null, null, null, null, null, null, null);
        VariableDTO variableDTO = new VariableDTO("var-id", "MY_VAR", "a simple var", VariableDTOTypeEnum.COLLECTED, "my-scope", null, datatypeDTO);

        VariableType expected = new CollectedVariableType();
        expected.setId("var-id");
        expected.setName("MY_VAR");
        expected.setLabel("a simple var");
        expected.setScope("my-scope");
        BooleanDatatypeType expectedDatatype = new BooleanDatatypeType();
        expectedDatatype.setTypeName(DatatypeTypeEnum.BOOLEAN);
        expected.setDatatype(expectedDatatype);

        // When we convert it to Pogues model
        VariableType res = toModel(variableDTO);

        // It is correctly converted
        assertThat(res).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    @DisplayName("Should convert DTO external variable into model")
    void toModel_success_externalVariable() throws Exception {
        // Given a DTO external variable
        VariableDTODatatype datatypeDTO = new VariableDTODatatype(VariableDTODatatypeTypeEnum.BOOLEAN, null, null, null, null, null, null, null);
        VariableDTO variableDTO = new VariableDTO("var-id", "MY_VAR", "a simple var", VariableDTOTypeEnum.EXTERNAL, "my-scope", null, datatypeDTO);

        VariableType expected = new ExternalVariableType();
        expected.setId("var-id");
        expected.setName("MY_VAR");
        expected.setLabel("a simple var");
        expected.setScope("my-scope");
        BooleanDatatypeType expectedDatatype = new BooleanDatatypeType();
        expectedDatatype.setTypeName(DatatypeTypeEnum.BOOLEAN);
        expected.setDatatype(expectedDatatype);

        // When we convert it to Pogues model
        VariableType res = toModel(variableDTO);

        // It is correctly converted
        assertThat(res).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    @DisplayName("Should convert DTO calculated variable into model")
    void toModel_success_calculatedVariable() throws Exception {
        // Given a DTO calculated variable
        VariableDTODatatype datatypeDTO = new VariableDTODatatype(VariableDTODatatypeTypeEnum.BOOLEAN, null, null, null, null, null, null, null);
        VariableDTO variableDTO = new VariableDTO("var-id", "MY_VAR", "a simple var", VariableDTOTypeEnum.CALCULATED, "my-scope", "1+2", datatypeDTO);

        CalculatedVariableType expected = new CalculatedVariableType();
        expected.setId("var-id");
        expected.setName("MY_VAR");
        expected.setLabel("a simple var");
        expected.setScope("my-scope");
        ExpressionType formula = new ExpressionType();
        formula.setValue("1+2");
        expected.setFormula(formula);
        BooleanDatatypeType expectedDatatype = new BooleanDatatypeType();
        expectedDatatype.setTypeName(DatatypeTypeEnum.BOOLEAN);
        expected.setDatatype(expectedDatatype);

        // When we convert it to Pogues model
        VariableType res = toModel(variableDTO);

        // It is correctly converted
        assertThat(res).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    @DisplayName("Should convert DTO boolean datatype into model")
    void toModel_success_booleanDatatype() throws Exception {
        // Given a DTO variable with a boolean datatype
        VariableDTODatatype datatypeDTO = new VariableDTODatatype(VariableDTODatatypeTypeEnum.BOOLEAN, null, null, null, null, null, null, null);
        VariableDTO variableDTO = new VariableDTO(null, null, null, VariableDTOTypeEnum.COLLECTED, null, null, datatypeDTO);

        VariableType expected = new CollectedVariableType();
        BooleanDatatypeType expectedDatatype = new BooleanDatatypeType();
        expectedDatatype.setTypeName(DatatypeTypeEnum.BOOLEAN);
        expected.setDatatype(expectedDatatype);

        // When we convert it to Pogues model
        VariableType res = toModel(variableDTO);

        // It is correctly converted
        assertThat(res).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    @DisplayName("Should convert DTO boolean datatype into model")
    void toModel_success_dateDatatype() throws Exception {
        // Given a DTO variable with a date datatype
        VariableDTODatatype datatypeDTO = new VariableDTODatatype(VariableDTODatatypeTypeEnum.DATE, VariableDTODatatypeFormatEnum.DATE_YEAR_MONTH, "2000-01", "2019-12", null, null, null, null);
        VariableDTO variableDTO = new VariableDTO(null, null, null, VariableDTOTypeEnum.COLLECTED, null, null, datatypeDTO);

        VariableType expected = new CollectedVariableType();
        DateDatatypeType expectedDatatype = new DateDatatypeType();
        expectedDatatype.setTypeName(DatatypeTypeEnum.DATE);
        expectedDatatype.setFormat(DateFormatEnum.YYYY_MM);
        expectedDatatype.setMinimum("2000-01");
        expectedDatatype.setMaximum("2019-12");
        expected.setDatatype(expectedDatatype);

        // When we convert it to Pogues model
        VariableType res = toModel(variableDTO);

        // It is correctly converted
        assertThat(res).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    @DisplayName("Should convert DTO duration datatype into model")
    void toModel_success_durationDatatype() throws Exception {
        // Given a DTO variable with a duration datatype
        VariableDTODatatype datatypeDTO = new VariableDTODatatype(VariableDTODatatypeTypeEnum.DURATION, VariableDTODatatypeFormatEnum.DURATION_MINUTE_SECOND, "PT1H1M", "PT99H59M", null, null, null, null);
        VariableDTO variableDTO = new VariableDTO(null, null, null, VariableDTOTypeEnum.COLLECTED, null, null, datatypeDTO);

        VariableType expected = new CollectedVariableType();
        DurationDatatypeType expectedDatatype = new DurationDatatypeType();
        expectedDatatype.setTypeName(DatatypeTypeEnum.DURATION);
        expectedDatatype.setFormat("PTnHnM");
        expectedDatatype.setMinimum("PT1H1M");
        expectedDatatype.setMaximum("PT99H59M");
        expected.setDatatype(expectedDatatype);

        // When we convert it to Pogues model
        VariableType res = toModel(variableDTO);

        // It is correctly converted
        assertThat(res).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    @DisplayName("Should convert DTO numeric datatype into model")
    void toModel_success_numericDatatype() throws Exception {
        // Given a DTO variable with a numeric datatype
        VariableDTODatatype datatypeDTO = new VariableDTODatatype(VariableDTODatatypeTypeEnum.NUMERIC, null, 3.23, 42, 2, true, "€", null);
        VariableDTO variableDTO = new VariableDTO(null, null, null, VariableDTOTypeEnum.COLLECTED, null, null, datatypeDTO);

        VariableType expected = new CollectedVariableType();
        NumericDatatypeType expectedDatatype = new NumericDatatypeType();
        expectedDatatype.setTypeName(DatatypeTypeEnum.NUMERIC);
        expectedDatatype.setMinimum(BigDecimal.valueOf(3.23));
        expectedDatatype.setMaximum(BigDecimal.valueOf(42));
        expectedDatatype.setDecimals(BigInteger.valueOf(2));
        expectedDatatype.setIsDynamicUnit(true);
        expectedDatatype.setUnit("€");
        expected.setDatatype(expectedDatatype);

        // When we convert it to Pogues model
        VariableType res = toModel(variableDTO);

        // It is correctly converted
        assertThat(res).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    @DisplayName("Should convert DTO text datatype into model")
    void toModel_success_textDatatype() throws Exception {
        // Given a DTO variable with a text datatype
        VariableDTODatatype datatypeDTO = new VariableDTODatatype(VariableDTODatatypeTypeEnum.TEXT, null, null, null, null, null, null, 202);
        VariableDTO variableDTO = new VariableDTO(null, null, null, VariableDTOTypeEnum.COLLECTED, null, null, datatypeDTO);

        VariableType expected = new CollectedVariableType();
        TextDatatypeType expectedDatatype = new TextDatatypeType();
        expectedDatatype.setTypeName(DatatypeTypeEnum.TEXT);
        expectedDatatype.setMaxLength(BigInteger.valueOf(202));
        expected.setDatatype(expectedDatatype);

        // When we convert it to Pogues model
        VariableType res = toModel(variableDTO);

        // It is correctly converted
        assertThat(res).usingRecursiveComparison().isEqualTo(expected);
    }
    @Test
    @DisplayName("Should convert model collected variable into DTO")
    void toDTO_success_collectedVariable() throws Exception {
        // Given a model collected variable
        VariableType variableModel = new CollectedVariableType();
        variableModel.setId("var-id");
        variableModel.setName("MY_VAR");
        variableModel.setLabel("a simple var");
        variableModel.setScope("my-scope");
        BooleanDatatypeType datatypeModel = new BooleanDatatypeType();
        datatypeModel.setTypeName(DatatypeTypeEnum.BOOLEAN);
        variableModel.setDatatype(datatypeModel);

        VariableDTODatatype expectedDatatype = new VariableDTODatatype(VariableDTODatatypeTypeEnum.BOOLEAN, null, null, null, null, null, null, null);
        VariableDTO expected = new VariableDTO("var-id", "MY_VAR", "a simple var", VariableDTOTypeEnum.COLLECTED, "my-scope", null, expectedDatatype);

        // When we convert it to DTO
        VariableDTO res = toDTO(variableModel);

        // It is correctly converted
        assertThat(res).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    @DisplayName("Should convert model external variable into DTO")
    void toDTO_success_externalVariable() throws Exception {
        // Given a model external variable
        VariableType variableModel = new ExternalVariableType();
        variableModel.setId("var-id");
        variableModel.setName("MY_VAR");
        variableModel.setLabel("a simple var");
        variableModel.setScope("my-scope");
        BooleanDatatypeType datatypeModel = new BooleanDatatypeType();
        datatypeModel.setTypeName(DatatypeTypeEnum.BOOLEAN);
        variableModel.setDatatype(datatypeModel);

        VariableDTODatatype expectedDatatype = new VariableDTODatatype(VariableDTODatatypeTypeEnum.BOOLEAN, null, null, null, null, null, null, null);
        VariableDTO expected = new VariableDTO("var-id", "MY_VAR", "a simple var", VariableDTOTypeEnum.EXTERNAL, "my-scope", null, expectedDatatype);

        // When we convert it to DTO
        VariableDTO res = toDTO(variableModel);

        // It is correctly converted
        assertThat(res).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    @DisplayName("Should convert model calculated variable into DTO")
    void toDTO_success_calculatedVariable() throws Exception {
        // Given a model calculated variable
        CalculatedVariableType variableModel = new CalculatedVariableType();
        variableModel.setId("var-id");
        variableModel.setName("MY_VAR");
        variableModel.setLabel("a simple var");
        variableModel.setScope("my-scope");
        ExpressionType formula = new ExpressionType();
        formula.setValue("1+2");
        variableModel.setFormula(formula);
        BooleanDatatypeType datatypeModel = new BooleanDatatypeType();
        datatypeModel.setTypeName(DatatypeTypeEnum.BOOLEAN);
        variableModel.setDatatype(datatypeModel);

        VariableDTODatatype expectedDatatype = new VariableDTODatatype(VariableDTODatatypeTypeEnum.BOOLEAN, null, null, null, null, null, null, null);
        VariableDTO expected = new VariableDTO("var-id", "MY_VAR", "a simple var", VariableDTOTypeEnum.CALCULATED, "my-scope", "1+2", expectedDatatype);

        // When we convert it to DTO
        VariableDTO res = toDTO(variableModel);

        // It is correctly converted
        assertThat(res).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    @DisplayName("Should convert model boolean datatype into DTO")
    void toDTO_success_booleanDatatype() throws Exception {
        // Given a model variable with a boolean datatype
        VariableType variableModel = new CollectedVariableType();
        BooleanDatatypeType datatypeModel = new BooleanDatatypeType();
        datatypeModel.setTypeName(DatatypeTypeEnum.BOOLEAN);
        variableModel.setDatatype(datatypeModel);

        VariableDTODatatype expectedDatatype = new VariableDTODatatype(VariableDTODatatypeTypeEnum.BOOLEAN, null, null, null, null, null, null, null);
        VariableDTO expected = new VariableDTO(null, null, null, VariableDTOTypeEnum.COLLECTED, null, null, expectedDatatype);

        // When we convert it to DTO
        VariableDTO res = toDTO(variableModel);

        // It is correctly converted
        assertThat(res).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    @DisplayName("Should convert model boolean datatype into DTO")
    void toDTO_success_dateDatatype() throws Exception {
        // Given a model variable with a date datatype
        VariableType variableModel = new CollectedVariableType();
        DateDatatypeType datatypeModel = new DateDatatypeType();
        datatypeModel.setTypeName(DatatypeTypeEnum.DATE);
        datatypeModel.setFormat(DateFormatEnum.YYYY_MM);
        datatypeModel.setMinimum("2000-01");
        datatypeModel.setMaximum("2019-12");
        variableModel.setDatatype(datatypeModel);

        VariableDTODatatype expectedDatatype = new VariableDTODatatype(VariableDTODatatypeTypeEnum.DATE, VariableDTODatatypeFormatEnum.DATE_YEAR_MONTH, "2000-01", "2019-12", null, null, null, null);
        VariableDTO expected = new VariableDTO(null, null, null, VariableDTOTypeEnum.COLLECTED, null, null, expectedDatatype);

        // When we convert it to DTO
        VariableDTO res = toDTO(variableModel);

        // It is correctly converted
        assertThat(res).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    @DisplayName("Should convert model duration datatype into DTO")
    void toDTO_success_durationDatatype() throws Exception {
        // Given a model variable with a duration datatype
        VariableType variableModel = new CollectedVariableType();
        DurationDatatypeType datatypeModel = new DurationDatatypeType();
        datatypeModel.setTypeName(DatatypeTypeEnum.DURATION);
        datatypeModel.setFormat("PTnHnM");
        datatypeModel.setMinimum("PT1H1M");
        datatypeModel.setMaximum("PT99H59M");
        variableModel.setDatatype(datatypeModel);

        VariableDTODatatype expectedDatatype = new VariableDTODatatype(VariableDTODatatypeTypeEnum.DURATION, VariableDTODatatypeFormatEnum.DURATION_MINUTE_SECOND, "PT1H1M", "PT99H59M", null, null, null, null);
        VariableDTO expected = new VariableDTO(null, null, null, VariableDTOTypeEnum.COLLECTED, null, null, expectedDatatype);

        // When we convert it to DTO
        VariableDTO res = toDTO(variableModel);

        // It is correctly converted
        assertThat(res).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    @DisplayName("Should convert model numeric datatype into DTO")
    void toDTO_success_numericDatatype() throws Exception {
        // Given a model variable with a numeric datatype
        VariableType variableModel = new CollectedVariableType();
        NumericDatatypeType datatypeModel = new NumericDatatypeType();
        datatypeModel.setTypeName(DatatypeTypeEnum.NUMERIC);
        datatypeModel.setMinimum(BigDecimal.valueOf(3.23));
        datatypeModel.setMaximum(BigDecimal.valueOf(42));
        datatypeModel.setDecimals(BigInteger.valueOf(2));
        datatypeModel.setIsDynamicUnit(true);
        datatypeModel.setUnit("€");
        variableModel.setDatatype(datatypeModel);

        VariableDTODatatype expectedDatatype = new VariableDTODatatype(VariableDTODatatypeTypeEnum.NUMERIC, null, 3.23, 42.0, 2, true, "€", null);
        VariableDTO expected = new VariableDTO(null, null, null, VariableDTOTypeEnum.COLLECTED, null, null, expectedDatatype);

        // When we convert it to DTO
        VariableDTO res = toDTO(variableModel);

        // It is correctly converted
        assertThat(res).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    @DisplayName("Should convert model text datatype into DTO")
    void toDTO_success_textDatatype() throws Exception {
        // Given a model variable with a text datatype
        VariableType variableModel = new CollectedVariableType();
        TextDatatypeType datatypeModel = new TextDatatypeType();
        datatypeModel.setTypeName(DatatypeTypeEnum.TEXT);
        datatypeModel.setMaxLength(BigInteger.valueOf(202));
        variableModel.setDatatype(datatypeModel);

        VariableDTODatatype expectedDatatype = new VariableDTODatatype(VariableDTODatatypeTypeEnum.TEXT, null, null, null, null, null, null, 202);
        VariableDTO expected = new VariableDTO(null, null, null, VariableDTOTypeEnum.COLLECTED, null, null, expectedDatatype);

        // When we convert it to DTO
        VariableDTO res = toDTO(variableModel);

        // It is correctly converted
        assertThat(res).usingRecursiveComparison().isEqualTo(expected);
    }

}
