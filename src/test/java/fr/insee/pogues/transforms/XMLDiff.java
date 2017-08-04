package fr.insee.pogues.transforms;

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
public class XMLDiff {

    private TransformService transformService;

    public XMLDiff(TransformService transformService) {
        this.transformService = transformService;
    }
    
    public Diff getDiff(InputStream input, String expectedFilePath) throws Exception {
        System.out.println(String.format("Diff input with %s", expectedFilePath));
        InputStream expected = null;
        OutputStream output = null;
        try {
            expected = XMLDiff.class.getClassLoader().getResourceAsStream(expectedFilePath);
            output = new ByteArrayOutputStream();
            transformService.transform(input, output);
            return DiffBuilder
                    .compare(Input.fromStream(expected))
                    .withTest(new String(((ByteArrayOutputStream) output).toByteArray(), "UTF-8"))
                    .ignoreWhitespace()
                    .normalizeWhitespace()
                    .checkForIdentical()
                    .build();
        } catch (Exception e) {
            throw e;
        } finally {
            close(input, expected, output);
        }
    }

    public Diff getDiff(String inputFilePath, String expectedFilePath) throws Exception {
        System.out.println(String.format("Diff %s with %s", inputFilePath, expectedFilePath));
        try {
            InputStream input = XMLDiff.class.getClassLoader().getResourceAsStream(inputFilePath);
            return getDiff(input, expectedFilePath);
        } catch (Exception e) {
            throw e;
        }
    }

    public Diff getDiff(String path) throws Exception {
        return getDiff(String.format("%s/in.xml", path), String.format("%s/out.xml", path));
    }


    private void close(InputStream input, InputStream expected, OutputStream output) throws Exception{
        try {
            if(null != input){
                input.close();
            }
            if(null != output){
                output.close();
            }
            if(null != expected){
                expected.close();
            }
        } catch (IOException e) {
            throw e;
        }
    }





}
