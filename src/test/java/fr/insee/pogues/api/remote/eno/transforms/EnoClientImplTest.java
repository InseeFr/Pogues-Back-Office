package fr.insee.pogues.api.remote.eno.transforms;

import fr.insee.pogues.webservice.model.StudyUnitEnum;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mockito;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.Map;

public class EnoClientImplTest {
    @Test
    void testWSPathWhenContextIsMissing() {
        // Mock de WebClient
        WebClient webClient = Mockito.mock(WebClient.class);

        // Création de l'instance d'EnoClientImpl avec WebClient mocké
        EnoClientImpl client = new EnoClientImpl(webClient);

        // Paramètres d'entrée
        Map<String, Object> params = new HashMap<>();
        params.put("mode", "CAWI"); // Mode explicite

        // Appel de la méthode
        String wsPath = client.buildWSPath(client.getContextParam(params), "CAWI");

        // Vérification que le contexte par défaut est utilisé
        assertEquals("questionnaire/DEFAULT/lunatic-json/CAWI", wsPath);
    }

    @ParameterizedTest
    @EnumSource(StudyUnitEnum.class) // Test avec toutes les valeurs de StudyUnitEnum
    void testWSPathWithDifferentContexts(StudyUnitEnum context) {
        // Mock de WebClient
        WebClient webClient = Mockito.mock(WebClient.class);

        // Création de l'instance d'EnoClientImpl avec WebClient mocké
        EnoClientImpl client = new EnoClientImpl(webClient);

        // Paramètres d'entrée
        Map<String, Object> params = new HashMap<>();
        params.put("mode", "CAWI"); // Mode explicite
        params.put("context", context); // Contexte à tester

        // Appel de la méthode
        String wsPath = client.buildWSPath(client.getContextParam(params), "CAWI");

        // Vérification que le contexte correct est utilisé
        assertEquals("questionnaire/" + context + "/lunatic-json/CAWI", wsPath);
    }

}
