package fr.insee.pogues.controller;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/env")
@Tag(name = "1. Public Resources")
@Slf4j
public class PoguesEnvironment {

	@Autowired
	Environment env;

	@GetMapping("")
	@Operation(operationId = "getEnvironment", summary = "Get pogues back office environment")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Success"),
			@ApiResponse(responseCode = "401", description = "Unauthorized"),
			@ApiResponse(responseCode = "404", description = "Not found") })
	public ResponseEntity<ObjectNode> getEnvironment() throws Exception {
		try {
			ObjectNode entity = JsonNodeFactory.instance.objectNode();

			entity.put("Eno Webservice", env.getProperty("application.eno.host"));

			ObjectNode visu = JsonNodeFactory.instance.objectNode();
			visu.put("Stromae v1", env.getProperty("application.stromae.orbeon.host"));
			visu.put("Exist DB", env.getProperty("application.stromae.host"));
			visu.put("Stromae v2", env.getProperty("application.stromaev2.vis.host"));
			visu.put("Stromae v3", env.getProperty("application.stromaev3.vis.host"));
			visu.put("Queen", env.getProperty("application.queen.vis.host"));
			visu.put("API Questionnaire", env.getProperty("application.api.nomenclatures"));
			entity.put("Visualization",visu);

			ObjectNode external = JsonNodeFactory.instance.objectNode();
			external.put("Metadata services - DDI.AS",env.getProperty("application.metadata.ddi-as"));
			external.put("Metadata services - Magma",env.getProperty("application.metadata.magma"));
			entity.put("External Services",external);

			ObjectNode auth = JsonNodeFactory.instance.objectNode();
			auth.put("enabled",env.getProperty("feature.oidc.enabled"));
			auth.put("server",env.getProperty("feature.oidc.auth-server-url"));
			auth.put("realm",env.getProperty("feature.oidc.realm"));
			entity.put("Authentication", auth);
			return ResponseEntity.status(HttpStatus.OK).body(entity);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		}

	}
}
