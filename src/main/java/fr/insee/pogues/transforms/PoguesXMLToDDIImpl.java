package fr.insee.pogues.transforms;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import fr.insee.eno.GenerationService;
import fr.insee.eno.generation.PoguesXML2DDIGenerator;
import fr.insee.eno.postprocessing.DDIPostprocessor;
import fr.insee.eno.preprocessing.PoguesXMLPreprocessor;

/**
 * Created by I6VWID 12/01/18.
 */
@Service
public class PoguesXMLToDDIImpl implements PoguesXMLToDDI {

	@Override
    public void transform(InputStream input, OutputStream output, Map<String, Object> params) throws Exception {
        if (null == input) {
            throw new NullPointerException("Null input");
        }
        if (null == output) {
            throw new NullPointerException("Null output");
        }
        String xForm = transform(input, params);
        output.write(xForm.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String transform(InputStream input, Map<String, Object> params) throws Exception {
        if (null == input) {
            throw new NullPointerException("Null input");
        }
        File enoInput;
        enoInput = File.createTempFile("eno", ".xml");
        FileUtils.copyInputStreamToFile(input, enoInput);
        return transform(enoInput, params);
    }

    @Override
    public String transform(String input, Map<String, Object> params) throws Exception {
        File enoInput;
        if (null == input) {
            throw new NullPointerException("Null input");
        }
        enoInput = File.createTempFile("eno", ".xml");
        FileUtils.writeStringToFile(enoInput, input, StandardCharsets.UTF_8);
        return transform(enoInput, params);
    }

    private String transform(File file, Map<String, Object> params) throws Exception {
        try {
            File output;
            GenerationService genService = new GenerationService(new PoguesXMLPreprocessor(), new PoguesXML2DDIGenerator(), new DDIPostprocessor());
            output = genService.generateQuestionnaire(file, null);
            return FileUtils.readFileToString(output, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new Exception(String.format("%s:%s", getClass().getName(), e.getMessage()));
        }
    }

}
