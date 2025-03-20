package fr.insee.pogues.utils;

import fr.insee.pogues.model.ComponentType;
import fr.insee.pogues.model.QuestionType;
import fr.insee.pogues.model.Questionnaire;
import fr.insee.pogues.model.SequenceType;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

import static fr.insee.pogues.utils.json.JSONFunctions.jsonStringtoJsonNode;

public class Utils {

    public static Questionnaire loadQuestionnaireFromResources(String uriResources) throws URISyntaxException, IOException, JAXBException {
        URL url = Utils.class.getClassLoader().getResource(uriResources);
        String stringQuestionnaire = Files.readString(Path.of(url.toURI()));
        return PoguesDeserializer.questionnaireToJavaObject(jsonStringtoJsonNode(stringQuestionnaire));
    }

    public static QuestionType findQuestionWithId(Questionnaire questionnaire, String questionId){
        List<ComponentType> allComponents = questionnaire.getChild().stream()
                .map(c -> {
                    if(c.getClass().equals(SequenceType.class)){
                        return ((SequenceType) c).getChild();
                    }
                    return List.of(c);
                })
                .flatMap(Collection::stream)
                .toList();
        return (QuestionType) allComponents.stream().filter(c->questionId.equals(c.getId())).findFirst().get();
    }
}
