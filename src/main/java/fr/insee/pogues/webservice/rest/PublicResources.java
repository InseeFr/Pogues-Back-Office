package fr.insee.pogues.webservice.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.http.HttpStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api")
@Tag(name = "Pogues Public Resources")
public class PublicResources {
	
	final static Logger logger = LogManager.getLogger(PublicResources.class);
	
	@Value("${fr.insee.pogues.authentication}")
    String authentificationType;
	
	@Value("${fr.insee.pogues.search.disable:false}")
	boolean isSearchDisable;
	
	@GET
	@GetMapping("/init")
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(operationId = "getInit", summary = "Initial properties")
	public ResponseEntity<Object> getProperties() throws Exception {
		JSONObject props = new JSONObject();
		try {
			props.put("authType", authentificationType);
			props.put("isSearchDisable", isSearchDisable);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return ResponseEntity.status(HttpStatus.SC_OK).body(props.toString());
	}

}
