package fr.insee.pogues.utils.xsl;

import org.apache.log4j.Logger;



/**
 * Service representing the ddi2fr ant target Creates the form.xhtml from the
 * -final.tmp file
 * 
 * @author I6VWID
 *
 */
public class DDI2Pogues {

	final static Logger logger = Logger.getLogger(DDI2Pogues.class);

	//private static XSLTransformation saxonService = new XSLTransformation();

	/**
	 * Main method of the ddi2pogues target
	 * 
	 * @param finalInput
	 *            : the -final tmp file used in input
	 * @param surveyName
	 *            : the name of the survey used to create the proper folder to
	 *            store the created outputs
	 * @return : the output form to be added in the WS Response
	 * @throws Exception
	 *             : XSL related exceptions
	 */
	public String ddi2frTarget(String finalInput, String surveyName) throws Exception {

//		logger.debug("DDI2FR Target : START");
//		logger.debug("Arguments : finalInput : " + finalInput + " surveyName " + surveyName);
//		String formNameFolder = null;
//		String outputBasicForm = null;
//
//		File f = new File(finalInput);
//
//		formNameFolder = FilenameUtils.getBaseName(f.getAbsolutePath());
//		formNameFolder = FilenameUtils.removeExtension(formNameFolder);
//		formNameFolder = formNameFolder.replace(XslParameters.TITLED_EXTENSION, "");
//
//		logger.debug("formNameFolder : " + formNameFolder);
//
//		outputBasicForm = Constants.TEMP_XFORMS_FOLDER + "/" + formNameFolder + "/" + Constants.BASIC_FORM_TMP_FILENAME;
//		logger.debug("Output folder for basic-form : " + outputBasicForm);
//
//		logger.debug("Ddi2fr part 1 : from -final to basic-form");
//		logger.debug("-Input : " + finalInput + " -Output : " + outputBasicForm + " -Stylesheet : "
//				+ Constants.TRANSFORMATIONS_DDI2FR_DDI2FR_XSL);
//		logger.debug("-Parameters : " + surveyName + " | " + formNameFolder + " | " + Constants.PROPERTIES_FILE);
//		saxonService.transformDdi2frBasicForm(finalInput, Constants.TRANSFORMATIONS_DDI2FR_DDI2FR_XSL, outputBasicForm,
//				surveyName, formNameFolder, Constants.PROPERTIES_FILE);
//
//		String outputForm = Constants.TARGET_FOLDER + "/" + surveyName + "/" + formNameFolder + "/form/form.xhtml";
//
//		logger.debug("Ddi2fr part 2 : from basic-form to form.xhtml");
//		logger.debug("-Input : " + outputBasicForm + " -Output : " + outputForm + " -Stylesheet : "
//				+ Constants.BROWSING_TEMPLATE_XSL);
//		logger.debug("-Parameters : " + surveyName + " | " + formNameFolder + " | " + Constants.PROPERTIES_FILE);
//		saxonService.transformDdi2frBasicForm(outputBasicForm, Constants.BROWSING_TEMPLATE_XSL, outputForm, surveyName,
//				formNameFolder, Constants.PROPERTIES_FILE);

		return "";
	}
}
