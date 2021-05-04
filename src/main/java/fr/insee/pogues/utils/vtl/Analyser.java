package fr.insee.pogues.utils.vtl;

import fr.insee.pogues.utils.json.JSONFunctions;
import org.json.simple.JSONObject;

import javax.script.*;
import java.util.List;

public class Analyser {

    public static AnalyserResult analyseExpression(String script, JSONObject questionnaire){
        List<Variable> variables = JSONFunctions.getVariablesListFromJsonQuestionnaire(questionnaire);
        Bindings bindings = BindingsUtils.createBindingsFromVariables(variables);
        return analyseExpression(script,bindings);
    };

    public static AnalyserResult analyseExpression(String script, Bindings bindings) {
        // for formula, we need to have "res:= xxx ;"
        script="res := "+script;
        if(!script.endsWith(";")) script+=";";

        ScriptEngine engine = new ScriptEngineManager().getEngineByName("vtl");

        ScriptContext context = engine.getContext();
        context.setBindings(bindings, ScriptContext.ENGINE_SCOPE);
        try {
            engine.eval(script);
        } catch (ScriptException e) {
            return new AnalyserResult(e.getMessage(),false);
        }
        return new AnalyserResult("ok !",true);
    }
}
