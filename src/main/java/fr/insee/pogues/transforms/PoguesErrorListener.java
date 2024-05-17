package fr.insee.pogues.transforms;

import lombok.extern.slf4j.Slf4j;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.TransformerException;

/**
 * Created by acordier on 19/07/17.
 */
@Slf4j
public class PoguesErrorListener implements ErrorListener {

    public void warning(TransformerException exception) throws TransformerException {
        log.warn(exception.getMessage());
    }

    public void error(TransformerException exception) throws TransformerException {
        log.error(exception.getMessage());
    }

    public void fatalError(TransformerException exception) throws TransformerException {
        log.error(exception.getMessageAndLocation()  );
    }
}
