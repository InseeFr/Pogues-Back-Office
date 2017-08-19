package fr.insee.pogues.transforms;

import fr.insee.eno.GenerationService;
import fr.insee.eno.generation.DDI2FRGenerator;
import fr.insee.eno.postprocessing.NoopPostprocessor;
import fr.insee.eno.preprocessing.DDIPreprocessor;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
public class DDIToXFormImpl implements DDIToXForm {

    @Override
    public void transform(InputStream input, OutputStream output, Map<String, Object> params) throws Exception {

    }

    @Override
    public String transform(InputStream input, Map<String, Object> params) throws Exception {
        return null;
    }

    @Override
    public String transform(String input, Map<String, Object> params) throws Exception {
        File enoInput = null;
        try {
            enoInput = File.createTempFile("eno", ".xml");
            FileUtils.writeStringToFile(enoInput, input, StandardCharsets.UTF_8);
            return transform(enoInput, params);
        } catch(Exception e) {
            throw e;
        }
    }

    private String transform(File file, Map<String, Object> params) throws Exception {
        File output = null;
        try {
            GenerationService genService = new GenerationService(new DDIPreprocessor(), new DDI2FRGenerator(), new NoopPostprocessor());
            output = genService.generateQuestionnaire(file,null);
            String response = FileUtils.readFileToString(output, Charset.forName("UTF-8"));
            return response;
        } catch(Exception e){
            throw e;
        }
    }
}
