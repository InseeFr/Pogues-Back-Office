package fr.insee.pogues.utils.vtl;


import fr.insee.pogues.utils.json.JSONFunctions;
import org.apache.commons.io.FileUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Assert;
import org.junit.Test;

import javax.script.Bindings;
import javax.script.SimpleBindings;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TestAnalyser {

    @Test
    public void testSimpleAnalyser() {
        Map<String, Object> variables = new HashMap<>();
        variables.put("a", 1);
        Bindings bindings = new SimpleBindings(variables);
        String scriptSuccess = "1 + a";
        String scriptFailed = "1 + \"a\"";
        AnalyserResult resultSuccess = Analyser.analyseExpression(scriptSuccess, bindings);
        AnalyserResult resultFailed = Analyser.analyseExpression(scriptFailed, bindings);
        Assert.assertTrue(resultSuccess.isValid());
        Assert.assertFalse(resultFailed.isValid());
    }

    @Test
    public void testFormulaWithQuestionnaire() throws IOException, ParseException {
        JSONParser jsonParser = new JSONParser();
        String jsonString = FileUtils.readFileToString(new File("src/test/resources/vtl/questionnaire-simpsons.json"), StandardCharsets.UTF_8);
        JSONObject questionnaire = (JSONObject) jsonParser.parse(jsonString);
        List<Variable> variables = JSONFunctions.getVariablesListFromJsonQuestionnaire(questionnaire);
        List<Variable> numericVariables = variables.stream().filter(variable -> variable.getType().equals("NUMERIC")).collect(Collectors.toList());
        List<Variable> textVariables = variables.stream().filter(variable -> variable.getType().equals("TEXT")).collect(Collectors.toList());
        Bindings bindings = BindingsUtils.createBindingsFromVariables(variables);

        String scriptToTest = "PERCENTAGE_EXPENSES11 + PERCENTAGE_EXPENSES21";
        AnalyserResult result = Analyser.analyseExpression(scriptToTest,bindings);
        Assert.assertTrue(result.isValid());

        String smartScriptSuccess = String.format("2 * %s / %s",
                numericVariables.get(0).getName(),
                numericVariables.get(1).getName());
        AnalyserResult resultSuccess = Analyser.analyseExpression(smartScriptSuccess,bindings);

        String smartScriptFailed = String.format("2 * %s / %s",
                numericVariables.get(0).getName(),
                textVariables.get(1).getName());
        AnalyserResult resultFailed = Analyser.analyseExpression(smartScriptFailed,bindings);

        Assert.assertTrue(resultSuccess.isValid());
        Assert.assertFalse(resultFailed.isValid());
    }
}
