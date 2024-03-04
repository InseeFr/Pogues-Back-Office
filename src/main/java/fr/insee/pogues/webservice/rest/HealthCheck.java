package fr.insee.pogues.webservice.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.insee.pogues.api.remote.eno.transforms.EnoClient;
import fr.insee.pogues.persistence.query.QuestionnairesServiceQuery;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.io.IOException;

@RestController
@RequestMapping("/api/healthcheck")
@Tag(name="Health Check")
public class HealthCheck {
	
	private final static Logger logger = LogManager.getLogger(HealthCheck.class);

	@Autowired
	EnoClient enoClient;

	@Autowired
    private QuestionnairesServiceQuery questionnaireServiceQuery;

	@Autowired
    HttpClientBuilder httpClientBuilder;
	
	@Value("${fr.insee.pogues.api.remote.metadata.url}")
    String ddiAccessServicesUrl;
	
	@Value("${fr.insee.pogues.api.remote.stromae.orbeon.host}")
    String orbeonHost;
	
	@Value("${fr.insee.pogues.api.remote.queen.vis.host}")
    String queenHost;
	
	@Value("${fr.insee.pogues.api.remote.stromae.host}")
	String stromaeDbHost;
	
	@GET
	@GetMapping("")
    @Produces(MediaType.TEXT_PLAIN)
    @Operation(
            summary = "Perform HealthCheck on Pogues environment",
            description = "This method will return the status of applications needed for Pogues"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "209", description = "Warning"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public ResponseEntity<Object> getHealthcheck() {
		boolean haveError=false;
		boolean haveWarning=false;
		String stateResult="";
		String errorMessage="Errors : \n";
		logger.debug("Begin healthCheck");
        try {
        	enoClient.getParameters();
        	stateResult = stateResult.concat(" - EnoWS : OK \n");	
        } catch (Exception e) {
        	haveWarning=true;
			logger.error(e.getMessage());
        	errorMessage = errorMessage.concat("- Eno API doesn't return default parameters \n");
			stateResult = stateResult.concat(" - EnoWS : KO \n");
        }
        try {
        	questionnaireServiceQuery.countQuestionnaires();
        	stateResult = stateResult.concat(" - DB PostgreSql : OK \n");	
        } catch (Exception e) {
        	haveError=true;
        	logger.error(e.getMessage());
        	errorMessage = errorMessage.concat("- Couldn't connect to Pogues database \n");
			stateResult = stateResult.concat(" - DB PostgreSql : KO \n");
        }
        try(CloseableHttpClient httpClient = httpClientBuilder.build()) {
            HttpGet get = new HttpGet(String.format("%s/%s", stromaeDbHost, "exist/rest/db/properties.xml"));
            get.setHeader("Content-type", "application/xml");
            httpClient.execute(get);
            stateResult = stateResult.concat(" - Stromae, DB Exist : OK \n");
        } catch (Exception e) {
        	haveWarning=true;
        	logger.error(e.getMessage());
        	errorMessage = errorMessage.concat("- Couldn't connect to Stromae Exist database \n");
			stateResult = stateResult.concat(" - Stromae, DB Exist : KO \n");
        }
        try {
            getCall(String.format("%s/%s", orbeonHost, "rmesstromae/fr/esa-dc-2018/simpsons/new?unite-enquete=123456789"));
            stateResult = stateResult.concat(" - Stromae, Orbeon : OK \n");
        } catch (Exception e) {
        	haveWarning=true;
        	logger.error(e.getMessage());
        	errorMessage = errorMessage.concat("- Couldn't connect to orbeon \n");
			stateResult = stateResult.concat(" - Stromae, Orbeon : KO \n");
        }
        try {
            getCall(String.format("%s/%s", ddiAccessServicesUrl, "env"));
            stateResult = stateResult.concat(" - DDI-Access-Services : OK \n");
        } catch (Exception e) {
        	haveError=true;
        	logger.error(e.getMessage());
        	errorMessage = errorMessage.concat("- DDI-Access-Services doesn't return environnement parameters \n");
			stateResult = stateResult.concat(" - DDI-Access-Services : KO \n");
        }
    	try {
            getCall(String.format("%s/%s", queenHost, "queen/index.html"));
            stateResult = stateResult.concat(" - Vizualisation Queen : OK \n");
        } catch (Exception e) {
        	haveWarning=true;
        	logger.error(e.getMessage());
        	errorMessage = errorMessage.concat("- Can't reach index.html on queen application\n");
			stateResult = stateResult.concat(" - Vizualisation Queen : KO \n");
        }
    	stateResult = stateResult.concat(" - Vizualisation Stromae V2 : to be implemented \n");
		logger.debug("HealthCheck complete");
		logger.debug(stateResult);

		if (!haveError && !haveWarning) {
			return ResponseEntity.status(HttpStatus.OK).body(stateResult);
		} else if (haveWarning && !haveError) {
			logger.warn(errorMessage);
			return ResponseEntity.status(209).body(stateResult.concat(errorMessage));
		} else {
			logger.error(errorMessage);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(stateResult.concat(errorMessage));
		}
    }

	private void getCall(String uri) throws IOException {
		try(CloseableHttpClient httpClient = httpClientBuilder.build()){
			HttpGet get = new HttpGet(uri);
			httpClient.execute(get);
		}
	}

}
