package fr.insee.pogues.transforms;

import java.io.InputStream;
import java.io.OutputStream;

public interface Transformer {
    void transform(InputStream input, OutputStream output) throws Exception;
    String transform(InputStream input) throws Exception;
    String transform(String input) throws Exception;

}
