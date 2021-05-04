package fr.insee.pogues.webservice.rest;

import fr.insee.pogues.transforms.XpathToVtl;
import fr.insee.pogues.utils.vtl.Analyser;
import fr.insee.pogues.utils.vtl.AnalyserResult;
import io.swagger.annotations.*;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.nio.charset.StandardCharsets;

/**
 * Main WebService class of the PoguesBOOrchestrator
 *
 * @author I6VWID
 */
@Path("/utils")
@Api(value = "Pogues Utils")
public class PoguesUtils {

	final static Logger logger = LogManager.getLogger(PoguesUtils.class);


	@Autowired
	XpathToVtl xpathToVtl;

	@POST
	@Path("check-formula")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Check formula (vtl or xpath)")
	@ApiImplicitParams(value = {
			@ApiImplicitParam(name = "formula", value = "The formula to test (xpath or vtl)", paramType = "query", dataType = "string"),
			@ApiImplicitParam(name = "language", value = "language", paramType = "query", dataType = "string",allowableValues="VTL,XPATH", defaultValue = "XPATH")})
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Success"),
			@ApiResponse(code = 500, message = "Error")
	})
	public Response checkFormula(
			@Context final HttpServletRequest request,
			@ApiParam(value = "Questionnaire object") String jsonStringQuestionnaire
	) throws Exception {
		try {
			JSONParser jsonParser = new JSONParser();
			JSONObject jsonQuestionnaire = (JSONObject) jsonParser.parse(jsonStringQuestionnaire);

			String script = request.getParameter("formula");
			String language = request.getParameter("language");
			if(language.equals("XPATH")) script = xpathToVtl.transform(script);

			AnalyserResult analyserResult = Analyser.analyseExpression(script,jsonQuestionnaire);

			logger.info(String.format("Analyse %s : %s",script,analyserResult));
			return Response.status(Response.Status.OK).entity(analyserResult).build();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
	}
}
