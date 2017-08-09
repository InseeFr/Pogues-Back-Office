package fr.insee.pogues.transforms;

import fr.insee.eno.GenerationService;
import fr.insee.eno.generation.DDI2FRGenerator;
import fr.insee.eno.postprocessing.NoopPostprocessor;
import fr.insee.eno.preprocessing.DDIPreprocessor;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.Charset;

@Service
public class DDIToXFormImpl implements DDIToXForm {

    @Override
    public void transform(InputStream input, OutputStream output) throws Exception {

    }

    @Override
    public String transform(InputStream input) throws Exception {
        return null;
    }

    @Override
    public String transform(String input) throws Exception {
        File enoInput = null;
        PrintStream printStream = null;
        try {
            enoInput = File.createTempFile("eno", ".tmp");
            printStream = new PrintStream(new FileOutputStream(enoInput));
            printStream.print(input);
            printStream.flush();
            return transform(enoInput);
        } catch(Exception e) {
            throw e;
        } finally {
            FileUtils.deleteQuietly(enoInput);
            printStream.close();
        }
    }

    private String transform(File file) throws Exception {
        File output = null;
        try {
            GenerationService genService = new GenerationService(new DDIPreprocessor(), new DDI2FRGenerator(), new NoopPostprocessor());
            output = genService.generateQuestionnaire(file,null);
            return FileUtils.readFileToString(output, Charset.forName("UTF-8"));
        } catch(Exception e){
            throw e;
        } finally {
            FileUtils.deleteQuietly(output);
        }
    }
}
