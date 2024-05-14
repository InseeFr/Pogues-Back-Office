package fr.insee.pogues.webservice.rest;

import fr.insee.pogues.search.model.DDIItem;
import fr.insee.pogues.search.model.DataCollectionContext;
import fr.insee.pogues.search.model.PoguesQuery;
import fr.insee.pogues.search.model.ResponseSearchItem;
import fr.insee.pogues.search.service.SearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/search")
@Tag(name = "Pogues Search")
@Slf4j
public class PoguesSearch {

    @Autowired
    SearchService searchService;

    @PostMapping("")
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
            @RequestParam("subgroupId") String subgroupId,
            @Parameter(description = "Search only items referring to study-unit id", required = false)
            @RequestParam("studyUnitId") String studyUnitId,
            @Parameter(description = "Search only items referring to data-collection id", required = false)
            @RequestParam("dataCollectionId") String dataCollectionId,
            PoguesQuery query,
            WebRequest request
            ) throws Exception {
        try {
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            request.getParameterMap().entrySet().stream().forEach(map -> {
               params.put(map.getKey(), Arrays.stream(map.getValue()).toList());
            });
            return searchService.searchByLabel(query, params);
        } catch(Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("series")
    @Operation(
    		summary = "Import indexes from Colectica",
            description = "This require a living instance of colectica aswell as a up and running elasticsearch cluster")
    public List<DDIItem> getSubGroups() throws Exception {
        try {
            return searchService.getSubGroups();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("series/{id}/operations")
    @Operation(
    		summary = "Get all study-units (operations) for a given sub-group (series)",
            description = "Retrieve all operations with a parent id matching the series id given as a path parameter")
    public List<DDIItem> getStudyUnits(
            @PathVariable(value = "id") String id
    ) throws Exception {
        try {
            return searchService.getStudyUnits(id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }
    
    
    @GetMapping("context/collection/{id}")
    @Operation(
    		summary = "Get data collection context (Sub-group id, StudyUnit id) for a given data collection",
            description = "Retrieve the context (Sub-group id, StudyUnit id) for a id given as a path parameter")
    public DataCollectionContext getDataCollectionContext(
    		@PathVariable(value = "id") String id
    ) throws Exception {
        try {
            return searchService.getDataCollectionContext(id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }
    

    @GetMapping("operations/{id}/collections")
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
            log.error(e.getMessage(), e);
            throw e;
        }
    }


}