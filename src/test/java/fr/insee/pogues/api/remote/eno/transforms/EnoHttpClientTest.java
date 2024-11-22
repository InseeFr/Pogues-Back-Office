package fr.insee.pogues.api.remote.eno.transforms;

import fr.insee.pogues.webservice.model.EnoContext;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EnoHttpClientTest {

    @Nested
    class ContextParamTests {
        @Test
        void getContextParam_withContextKey() {
            Map<String, Object> params = Map.of("context", EnoContext.BUSINESS);
            EnoContext result =  EnoHttpClient.getContextParam(params);
            assertEquals(EnoContext.BUSINESS, result);
        }
        @Test
        void getContextParam_withoutContextKey() {
            Map<String, Object> params = new HashMap<>();
            EnoContext result = EnoHttpClient.getContextParam(params);
            assertEquals(EnoContext.DEFAULT, result);
        }
        @Test
        void getContextParam_withIncorrectContextKey() {
            Map<String, Object> params = Map.of("notAContext", "notAValue");
            EnoContext result = EnoHttpClient.getContextParam(params);
            assertEquals(EnoContext.DEFAULT, result);
        }
    }

    @Nested
    class ModeParamTests {
        @Test
        void getModeParam_withModeKey() {
            Map<String, Object> params = Map.of("mode", "CAPI");
            String mode = EnoHttpClient.getModeParam(params);
            assertEquals("CAPI", mode);
        }
        @Test
        void getModeParam_withoutModeKey() {
            Map<String, Object> params = new HashMap<>();
            assertThrows(IllegalStateException.class, () -> EnoHttpClient.getModeParam(params));
        }
    }

    @Nested
    class DsfrParamTests {
        @Test
        void getDsfrParam_true() {
            Map<String, Object> params = Map.of("dsfr", true);
            String value = EnoHttpClient.getDsfrParam(params);
            assertEquals("true", value);
        }
        @Test
        void getDsfrParam_false() {
            Map<String, Object> params = Map.of("dsfr", false);
            String value = EnoHttpClient.getDsfrParam(params);
            assertEquals("false", value);
        }
        @Test
        void getDsfrParam_defaultValue() {
            Map<String, Object> params = new HashMap<>();
            String value = EnoHttpClient.getDsfrParam(params);
            assertEquals("false", value);
        }
    }

}
