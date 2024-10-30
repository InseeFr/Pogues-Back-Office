package fr.insee.pogues.api.remote.eno.transforms;

import fr.insee.pogues.webservice.model.StudyUnitEnum;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EnoClientImplTest {
    @Test
    void testWSPathWhenContextIsDefault() {

        // Call the method
        String wsPath = EnoClientImpl.buildWSPath(StudyUnitEnum.DEFAULT, "CAWI");

        // Check that the default context is being used
        assertEquals("questionnaire/DEFAULT/lunatic-json/CAWI", wsPath);
    }

    @ParameterizedTest
    @EnumSource(StudyUnitEnum.class) // Test with all values of StudyUnitEnum
    void testWSPathWithDifferentContexts(StudyUnitEnum context) {

        // Call the method
        String wsPath = EnoClientImpl.buildWSPath(context, "CAWI");

        // Check that the correct context is being used
        assertEquals("questionnaire/" + context + "/lunatic-json/CAWI", wsPath);
    }

    @Test
    void testGetContextParam_withContextKey() {
        Map<String, Object> params = new HashMap<>();
        params.put("context", StudyUnitEnum.BUSINESS);

        StudyUnitEnum result =  EnoClientImpl.getContextParam(params);

        assertEquals(StudyUnitEnum.BUSINESS, result);
    }

    @Test
    void testGetContextParam_withoutContextKey() {
        Map<String, Object> params = new HashMap<>();

        StudyUnitEnum result = EnoClientImpl.getContextParam(params);

        assertEquals(StudyUnitEnum.DEFAULT, result);
    }

    @Test
    void testGetContextParam_withIncorrectContextKey() {
        Map<String, Object> params = new HashMap<>();
        params.put("notAContext","notAValue");

        StudyUnitEnum result = EnoClientImpl.getContextParam(params);

        assertEquals(StudyUnitEnum.DEFAULT, result);
    }

    @Test
    void testGetContextParam_withNullKey() {
        Map<String, Object> params = new HashMap<>();
        params.put("context", null);

        StudyUnitEnum result =  EnoClientImpl.getContextParam(params);

        assertEquals(StudyUnitEnum.DEFAULT, result);
    }

}
