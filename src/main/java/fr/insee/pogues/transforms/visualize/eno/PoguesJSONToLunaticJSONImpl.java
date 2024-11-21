package fr.insee.pogues.transforms.visualize.eno;

import fr.insee.pogues.api.remote.eno.transforms.EnoClient;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Map;

import static fr.insee.pogues.utils.IOStreamsUtils.inputStream2String;
import static fr.insee.pogues.utils.IOStreamsUtils.string2BOAS;

@Service
@AllArgsConstructor
public class PoguesJSONToLunaticJSONImpl implements PoguesJSONToLunaticJSON {

    private EnoClient enoClient;

    @Override
    public ByteArrayOutputStream transform(InputStream inputStream, Map<String, Object> params, String surveyName) throws Exception {
        String inputAsString = inputStream2String(inputStream);
        String outputAsString = transform(inputAsString, params);
        return string2BOAS(outputAsString);
    }

    private String transform(String inputAsString, Map<String, Object> params) throws Exception {
        return enoClient.getPoguesJsonToLunaticJson(inputAsString, params);
    }
}
