package fr.insee.pogues.webservice.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import fr.insee.pogues.configuration.auth.UserProvider;
import fr.insee.pogues.configuration.auth.user.User;
import fr.insee.pogues.configuration.properties.ApplicationProperties;
import fr.insee.pogues.persistence.service.QuestionnairesService;
import fr.insee.pogues.persistence.service.VariablesService;
import fr.insee.pogues.utils.suggester.SuggesterVisuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * WebService class for the Instrument Persistence
 * 
 * See the swagger documentation for this service :
 * http://inseefr.github.io/Pogues/en/remote-apis/swagger.html
 * 
 * @author I6VWID
 * 
 *         schemes: - http
 * 
 *         consumes: - application/json
 * 
 *         produces: - application/json
 *
 */
@RestController
@RequestMapping("/api/persistence")
@Tag(name = "2. Questionnaire Controller")
@Slf4j
public class QuestionnaireController {

	@Autowired
	private ApplicationProperties applicationProperties;

    @Autowired
	private QuestionnairesService questionnaireService;
    
    @Autowired
	private VariablesService variablesService;

	@Autowired
	private SuggesterVisuService suggesterVisuService;

	@Autowired
	private UserProvider userProvider;

	private static final String IDQUESTIONNAIRE_PATTERN="[a-zA-Z0-9]*";
	public static final String BAD_REQUEST = "Bad Request";
    private static final String MESSAGE_INVALID_IDENTIFIER = "Identifier %s is invalid";

	/**
	 * @param id: the id of questionnaire
	 * @param references (false by default): this param indicates if you want the complete questionnaire
	 *           A questionnaire may be "contain" other questionnaires. These questionnaires appear as references.
	 *           This end-point makes it possible to obtain the complete questionnaire, by replacing the references with the complete questionnaires.
	 * @return the json representation of questionnaire (and potentially its references according to references param)
	 * @throws Exception
	 */
	@GetMapping("questionnaire/{id}")
    @Operation(
			operationId  = "getQuestionnaires",
	        summary = "Get questionnaire",
            description = "Gets the questionnaire with id {id}"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })	
	public ResponseEntity<Object> getQuestionnaire(
			@PathVariable(value = "id") String id,
			@RequestParam(name = "references", defaultValue = "false") Boolean references
	) throws Exception {
		JsonNode result = references ?
					questionnaireService.getQuestionnaireByIDWithReferences(id) :
					questionnaireService.getQuestionnaireByID(id);
			return ResponseEntity.status(HttpStatus.OK).body(result);
	}
	
    @GetMapping("questionnaire/json-lunatic/{id}")
    @Operation(
			operationId = "getJsonLunatic",
	        summary = "Get questionnaire",
            description = "Gets the questionnaire with id JsonLunatic {id}"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
	public ResponseEntity<Object> getJsonLunatic(
			@PathVariable(value = "id") String id
	) throws Exception {
		JsonNode result = questionnaireService.getJsonLunaticByID(id);
		return ResponseEntity.status(HttpStatus.OK).body(result);

	}

    @GetMapping("questionnaires/search")
    @Operation(
    		operationId = "searchQuestionnaires",
            summary = "Search questionnaires",
            description = "Search questionnaires matching query params"
    )
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Success"),
			@ApiResponse(responseCode = "400", description = "Bad request")
	})
    public ResponseEntity<Object> searchQuestionnaires(
            @RequestParam("owner") String owner
    ) throws Exception {
		List<JsonNode> questionnaires = new ArrayList<>();
		if(null != owner){
			questionnaires.addAll(questionnaireService.getQuestionnairesByOwner(owner));
		}
		return ResponseEntity.status(HttpStatus.OK).body(questionnaires);
    }

	@GetMapping("questionnaires/search/meta")
	@Operation(
			operationId = "searchQuestionnairesMetadata",
	        summary = "Get questionnaires' metadata",
            description = "Get questionnaires' metadata matching query params"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
	public ResponseEntity<Object> getQuestionnairesMetadata(
            @RequestParam("owner") String owner
	) throws Exception {
		List<JsonNode> questionnairesMetadata = new ArrayList<>();
		if(null != owner){
			questionnairesMetadata.addAll(questionnaireService.getQuestionnairesMetadata(owner));
		}
		return ResponseEntity.status(HttpStatus.OK).body(questionnairesMetadata);

	}
	
	@GetMapping("questionnaires/stamps")
	@Operation(
			operationId = "searchQuestionnairesStamps",
	        summary = "Get stamps in database",
            description = "Get stamps with at least one questionnaire saved in database"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
	public ResponseEntity<Object> getQuestionnaireStamps() throws Exception {
		List<JsonNode> questionnairesStamps = new ArrayList<>();
		questionnairesStamps.addAll(questionnaireService.getQuestionnairesStamps());
		return ResponseEntity.status(HttpStatus.OK).body(questionnairesStamps);
	}
	

	@DeleteMapping("questionnaire/{id}")
	@Operation(
			operationId = "deleteQuestionnaire",
	        summary = "Delete questionnaire",
            description = "Delete questionnaire with id {id}"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "No content"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
	public ResponseEntity<Object> deleteQuestionnaire(Authentication auth,
			@PathVariable(value = "id") String id
	) throws Exception {
		questionnaireService.deleteQuestionnaireByID(id);
		User user = userProvider.getUser(auth);
		log.info("Questionnaire {} deleted by {}", id, user.getName());
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	@GetMapping("questionnaire/{id}/variables")
	@Operation(
			operationId  = "getQuestionnaireVariables",
			summary = "Get the variables of a questionnaire, used for pogues frontend",
			description = "Gets the variables with questionnaire id {id}",
			responses = {
					@ApiResponse(content = @Content(mediaType = "application/json"))}
	)
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Success"),
			@ApiResponse(responseCode = "404", description = "Not found")
	})
	public ResponseEntity<JsonNode> getQuestionnaireVariables(
			@PathVariable(value = "id") String id
	) throws Exception {
		JsonNode result = variablesService.getVariablesByQuestionnaire(id);
		return ResponseEntity.status(HttpStatus.OK).body(result);
	}

	@GetMapping("questionnaire/{id}/vars")
	@Operation(
			operationId  = "getQuestionnaireVars",
			summary = "Get the variables of a questionnaire",
			description = "Gets the variables with questionnaire id {id}",
			responses = {
					@ApiResponse(content = @Content(mediaType = "application/json"))}
	)
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Success"),
			@ApiResponse(responseCode = "404", description = "Not found")
	})
	public ResponseEntity<ArrayNode> getVariables(
			@PathVariable(value = "id") String id
	) throws Exception {
		ArrayNode result = variablesService.getVariablesByQuestionnaireForPublicEnemy(id);
		return ResponseEntity.status(HttpStatus.OK).body(result);
	}
	
	@DeleteMapping("questionnaire/json-lunatic/{id}")
	@Operation(
			operationId = "deleteJsonLunatic",
	        summary = "Delete Json Lunatic of a questionnaire",
            description = "Delete the Json Lunatic representation of a  questionnaire with id {id}"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "No content"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
//	@OwnerRestricted
	public ResponseEntity<Object> deleteJsonLunatic(
			@PathVariable(value = "id") String id
	) throws Exception {
		questionnaireService.deleteJsonLunaticByID(id);
		log.info("Questionnaire {} deleted", id);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	@GetMapping("questionnaires")
	@Operation(
			operationId = "getQuestionnaireList",
	        summary = "Get questionnaires",
            description = "Gets the `QuestionnaireList` object"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
	public ResponseEntity<Object> getQuestionnaireList() throws Exception {
		List<JsonNode> questionnaires = questionnaireService.getQuestionnaireList();
		return ResponseEntity.status(HttpStatus.OK).body(questionnaires);
	}
	
	@PutMapping("questionnaire/{id}")
	@Operation(
			operationId = "updateQuestionnaire",
	        summary = "Update questionnaire",
            description = "Update a `Questionnaire` object with id {id}"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
	public ResponseEntity<Object> updateQuestionnaire(
			@PathVariable(value = "id") String id,
			@RequestBody JsonNode jsonContent
	) throws Exception {
		if (id.matches(IDQUESTIONNAIRE_PATTERN)) {
			questionnaireService.updateQuestionnaire(id, jsonContent);
			log.info("Questionnaire {} updated", id);
		} else {
			throw new PoguesException(400, BAD_REQUEST,String.format(MESSAGE_INVALID_IDENTIFIER,id));
		}
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
	
	@PutMapping("questionnaire/json-lunatic/{id}")
	@Operation(
			operationId = "updateJsonLunatic",
	        summary = "Update Json Lunatic",
            description = "Update Json Lunatic of a `Questionnaire` object with id {id}"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
	public ResponseEntity<Object> updateJsonLunatic(
			@PathVariable(value = "id") String id,
			@RequestBody JsonNode jsonLunatic
	) throws Exception {
		questionnaireService.updateJsonLunatic(id, jsonLunatic);
		log.info("Json Lunatic of questionnaire {} updated", id);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	@PostMapping("questionnaires")
	@Operation(
			operationId = "createQuestionnaire",
	        summary = "Create Questionnaire",
            description = "Creates a new `Questionnaire`"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created"),
            @ApiResponse(responseCode = "400", description = "Entity already exists")
    })
	public ResponseEntity<Object> createQuestionnaire(
			@RequestBody JsonNode jsonContent
	) throws Exception {
		String id = jsonContent.get("id").asText();
		if (id.matches(IDQUESTIONNAIRE_PATTERN)) {
			questionnaireService.createQuestionnaire(jsonContent);
			String uriQuestionnaire = String.format("%s://%s/api/persistence/questionnaire/%s",
					applicationProperties.scheme(),
					applicationProperties.host(),
					id);
			log.debug("New questionnaire created , uri : {}",uriQuestionnaire);
			return ResponseEntity.status(HttpStatus.CREATED).header("Location", uriQuestionnaire).build();
    	} else {
    		throw new PoguesException(400,BAD_REQUEST,String.format(MESSAGE_INVALID_IDENTIFIER,id));
    	}
	}
	
	@PostMapping("questionnaires/json-lunatic")
	@Operation(
			operationId = "createJsonLunatic",
	        summary = "Create Json Lunatic of questionnaire",
            description = "Creates a new Json Lunatic entry"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created"),
            @ApiResponse(responseCode = "400", description = "Entity already exists")
    })
	public ResponseEntity<Object> createJsonLunatic(
			@RequestBody JsonNode jsonContent
	) throws Exception {
		String id = jsonContent.get("id").asText();
		if (id.matches(IDQUESTIONNAIRE_PATTERN)) {
			questionnaireService.createJsonLunatic(jsonContent);
			String uriJsonLunaticQuestionnaire = String.format("%s://%s/api/persistence/questionnaire/json-lunatic/%s",
					applicationProperties.scheme(),
					applicationProperties.host(),
					id);
			log.debug("New Json Lunatic created , uri : {}", uriJsonLunaticQuestionnaire);
			return ResponseEntity.status(HttpStatus.CREATED).header("Location", uriJsonLunaticQuestionnaire).build();
		} else {
			throw new PoguesException(400,BAD_REQUEST,String.format(MESSAGE_INVALID_IDENTIFIER,id));
		}
	}

	@GetMapping("questionnaire/{id}/nomenclatures")
	@Operation(
			operationId  = "getNomenclaturesUrls",
			summary = "Get object representation of id:url for suggester of a questionnaire",
			description = "Gets the suggesters with questionnaire id {id}",
			responses = {
					@ApiResponse(content = @Content(mediaType = "application/json"))}
	)
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Success"),
			@ApiResponse(responseCode = "404", description = "Not found")
	})
	public ResponseEntity<JsonNode> getNomenclaturesUrls(
			@PathVariable(value = "id") String id
	) throws Exception {
		JsonNode jsonPoguesQuestionnaire = questionnaireService.getQuestionnaireByIDWithReferences(id);
		List<String> nomenclaturesIds = suggesterVisuService.getNomenclatureIdsFromQuestionnaire(String.valueOf(jsonPoguesQuestionnaire));
		JsonNode nomenclaturesUrls = suggesterVisuService.createJsonNomenclaturesForVisu(nomenclaturesIds);
		return ResponseEntity.status(HttpStatus.OK).body(nomenclaturesUrls);
	}
}
