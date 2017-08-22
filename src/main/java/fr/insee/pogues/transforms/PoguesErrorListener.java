package fr.insee.pogues.transforms;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.TransformerException;

/**
 * Created by acordier on 19/07/17.
 */

public class PoguesErrorListener implements ErrorListener {

    final static Logger logger = LogManager.getLogger(PoguesErrorListener.class);

    public void warning(TransformerException exception) throws TransformerException {
        logger.warn(exception.getMessage());
    }

    public void error(TransformerException exception) throws TransformerException {
        logger.error(exception.getMessage());
    }

    public void fatalError(TransformerException exception) throws TransformerException {
        logger.fatal(exception.getMessageAndLocation()  );
    }
}
