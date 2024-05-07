package fr.insee.pogues.webservice.rest;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/env")
@Tag(name = "Pogues Environment")
@Slf4j
public class PoguesEnvironment {

	@Autowired
	Environment env;

	@GetMapping("")
	@Operation(operationId = "getEnvironment", summary = "Get pogues back office environment")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Success"),
			@ApiResponse(responseCode = "401", description = "Unauthorized"),
			@ApiResponse(responseCode = "404", description = "Not found") })
	public ResponseEntity<Object> getEnvironment() throws Exception {
		try {
			JSONObject entity = new JSONObject();
			entity.put("Swagger Host", env.getProperty("application.host"));
			entity.put("Swagger Name", env.getProperty("application.name"));
			entity.put("Swagger Scheme", env.getProperty("application.scheme"));
			entity.put("Database", env.getProperty("fr.insee.pogues.persistence.database.host"));
			entity.put("Metadata services", env.getProperty("application.metadata.ddi-as"));
			entity.put("Eno Webservice", env.getProperty("application.eno.host"));
			entity.put("Stromae", env.getProperty("application.stromae.host"));
			entity.put("Stromae v2", env.getProperty("application.stromaev2.vis.host"));			
			entity.put("Stromae v3", env.getProperty("application.stromaev3.vis.host"));
			entity.put("Queen", env.getProperty("application.queen.vis.host"));
			return ResponseEntity.status(HttpStatus.OK).body(entity);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		}

	}
}
