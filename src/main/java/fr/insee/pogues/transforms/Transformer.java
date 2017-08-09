package fr.insee.pogues.transforms;

import java.io.InputStream;
import java.io.OutputStream;

public interface TransformService {
    void transform(InputStream input, OutputStream output) throws Exception;
}
