package fr.insee.pogues.transforms;

import org.springframework.beans.factory.annotation.Autowired;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.builder.Input;
import org.xmlunit.diff.Diff;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by acordier on 20/07/17.
 */
public class XmlDiff {

    private static TransformService transformService  = new PoguesTransformService();

    public static Diff getDiff(String inputFilePath, String expectedOutputFilePath) throws Exception {
        InputStream input = null, expectedOutput = null;
        OutputStream output = null;
        System.out.println(String.format("Diff %s with %s", inputFilePath, expectedOutputFilePath));
        try {
            expectedOutput = XmlDiff.class.getClassLoader().getResourceAsStream(expectedOutputFilePath);
            input = XmlDiff.class.getClassLoader().getResourceAsStream(inputFilePath);
            output = new ByteArrayOutputStream();
            transformService.transform(input, output);
            return DiffBuilder
                    .compare(Input.fromStream(expectedOutput))
                    .withTest(new String(((ByteArrayOutputStream) output).toByteArray()))
                    .checkForIdentical()
                    .ignoreWhitespace()
                    .build();
        } catch (Exception e) {
            throw e;
        } finally {
            try {
                if(null != input){
                    input.close();
                }
                if(null != output){
                    output.close();
                }
                if(null != expectedOutput){
                    expectedOutput.close();
                }
            } catch (IOException e) {
                throw e;
            }
        }
    }

    public static Diff getDiff(String path) throws Exception {
        return getDiff(String.format("%s/in.xml", path), String.format("%s/out.xml", path));
    }

    @Autowired
    public void setTransformService(TransformService service) {
        transformService = service;
    }

}
