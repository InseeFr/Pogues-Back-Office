package fr.insee.pogues.webservice.rest;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Slf4j
@Tag(name = "1. Public Resources")
public class PublicResources {

	@Value("${feature.oidc.enabled}")
    boolean oidcEnabled;
	
	@Value("${fr.insee.pogues.search.disable:false}")
	boolean isSearchDisable;
	
	@GetMapping("/init")
	@Operation(operationId = "getInit", summary = "Initial properties")
	public ResponseEntity<ObjectNode> getProperties() {
		ObjectNode props = JsonNodeFactory.instance.objectNode();
		try {
			props.put("authType", oidcEnabled ? "OIDC" : "NONE");
			props.put("isSearchDisable", isSearchDisable);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return ResponseEntity.status(HttpStatus.OK).body(props);
	}

}
