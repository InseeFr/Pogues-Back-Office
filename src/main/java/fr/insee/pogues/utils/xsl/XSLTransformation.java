package fr.insee.pogues.utils.xsl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.xml.transform.Transformer;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;

/**
 * Main Saxon Service used to perform XSLT transformations
 * 
 * @author I6VWID
 *
 */
public class XSLTransformation {

	final static Logger logger = LogManager.getLogger(XSLTransformation.class);

	/**
	 * Main Saxon transformation method
	 * 
	 * @param transformer
	 *            : The defined transformer with his embedded parameters
	 *            (defined in the other methods of this class)
	 * @param xmlInput
	 *            : The input xml file where the XSLT will be applied
	 * @param xmlOutput
	 *            : The output xml file after the transformation
	 * @throws Exception
	 *             : Mainly if the input/output files path are incorrect
	 */
	public void xslTransform(Transformer transformer, String xmlInput, String xmlOutput) throws Exception {
		logger.debug("Starting xsl transformation -Input : " + xmlInput + " -Output : " + xmlOutput);
		transformer.transform(new StreamSource(new File(xmlInput)), new StreamResult(new File(xmlOutput)));
	}

}
