package fr.insee.pogues.webservice.rest;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.insee.pogues.search.model.DDIItem;
import fr.insee.pogues.search.model.DataCollectionContext;
import fr.insee.pogues.search.model.PoguesQuery;
import fr.insee.pogues.search.model.ResponseSearchItem;
import fr.insee.pogues.search.service.SearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/search")
@Tag(name = "Pogues Search")
@SecurityRequirement(name = "bearerAuth")
public class PoguesSearch {

    static final Logger logger = LogManager.getLogger(PoguesSearch.class);

    @Autowired
    SearchService searchService;

    @PostMapping("")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    @Operation(
            summary = "Search Item",
            description = "Search the application index for item across types`"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "500", description = "Unexpected error"),
    })
    public List<ResponseSearchItem> search(
            @Parameter(description = "Search only items referring to sub-group id", required = false)
            @QueryParam("subgroupId") String subgroupId,
            @Parameter(description = "Search only items referring to study-unit id", required = false)
            @QueryParam("studyUnitId") String studyUnitId,
            @Parameter(description = "Search only items referring to data-collection id", required = false)
            @QueryParam("dataCollectionId") String dataCollectionId,
            PoguesQuery query,
            @Context UriInfo info
            ) throws Exception {
        try {
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            info.getQueryParameters().entrySet().stream().forEach(map -> {
                params.put(map.getKey(), map.getValue());
            });
            return searchService.searchByLabel(query, params);
        } catch(Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("series")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
    		summary = "Import indexes from Colectica",
            description = "This require a living instance of colectica aswell as a up and running elasticsearch cluster")
    public List<DDIItem> getSubGroups() throws Exception {
        try {
            return searchService.getSubGroups();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("series/{id}/operations")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
    		summary = "Get all study-units (operations) for a given sub-group (series)",
            description = "Retrieve all operations with a parent id matching the series id given as a path parameter")
    public List<DDIItem> getStudyUnits(
            @PathVariable(value = "id") String id
    ) throws Exception {
        try {
            return searchService.getStudyUnits(id);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
    }
    
    
    @GetMapping("context/collection/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
    		summary = "Get data collection context (Sub-group id, StudyUnit id) for a given data collection",
            description = "Retrieve the context (Sub-group id, StudyUnit id) for a id given as a path parameter")
    public DataCollectionContext getDataCollectionContext(
    		@PathVariable(value = "id") String id
    ) throws Exception {
        try {
            return searchService.getDataCollectionContext(id);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
    }
    

    @GetMapping("operations/{id}/collections")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
            summary = "Get all data collections for a given operation",
            description = "Retrieve all data collections with a parent id matching the operation id given as a path parameter"
    )
    public List<DDIItem> getDataCollections(
    		@PathVariable(value = "id") String id
    ) throws Exception {
        try {
            return searchService.getDataCollections(id);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
    }


}