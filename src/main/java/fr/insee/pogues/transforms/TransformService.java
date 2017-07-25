package fr.insee.pogues.transforms;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by acordier on 20/07/17.
 */
public interface TransformService {
    void transform(InputStream input, OutputStream output) throws Exception;
}
