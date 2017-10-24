package fr.insee.pogues.webservice.rest;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import fr.insee.pogues.search.model.DDIItem;
import fr.insee.pogues.search.model.DataCollectionContext;
import fr.insee.pogues.search.model.PoguesQuery;
import fr.insee.pogues.search.model.ResponseSearchItem;
import fr.insee.pogues.search.service.SearchService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Component
@Path("/search")
@Api(value = "Pogues Search")
public class PoguesSearch {

    final static Logger logger = LogManager.getLogger(PoguesSearch.class);

    @Autowired
    SearchService searchService;

    @POST
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    @ApiOperation(
            value = "Search Item",
            notes = "Search the application index for item across types`"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 500, message = "Unexpected error")
    })
    public List<ResponseSearchItem> search(
            @ApiParam(value = "Search only items referring to sub-group id", required = false)
            @QueryParam("subgroupId") String subgroupId,
            @ApiParam(value = "Search only items referring to study-unit id", required = false)
            @QueryParam("studyUnitId") String studyUnitId,
            @ApiParam(value = "Search only items referring to data-collection id", required = false)
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

    @GET
    @Path("series")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Import indexes from Colectica",
            notes = "This require a living instance of colectica aswell as a up and running elasticsearch cluster",
            response = String.class)
    public List<DDIItem> getSubGroups() throws Exception {
        try {
            return searchService.getSubGroups();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
    }

    @GET
    @Path("series/{id}/operations")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Get all study-units (operations) for a given sub-group (series)",
            notes = "Retrieve all operations with a parent id matching the series id given as a path parameter",
            response = String.class)
    public List<DDIItem> getStudyUnits(
            @PathParam(value = "id") String id
    ) throws Exception {
        try {
            return searchService.getStudyUnits(id);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
    }
    
    
    @GET
    @Path("context/collection/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Get data collection context (Sub-group id, StudyUnit id) for a given data collection",
            notes = "Retrieve the context (Sub-group id, StudyUnit id) for a id given as a path parameter",
            response = String.class)
    public DataCollectionContext getDataCollectionContext(
            @PathParam(value = "id") String id
    ) throws Exception {
        try {
            return searchService.getDataCollectionContext(id);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
    }
    

    @GET
    @Path("operations/{id}/collections")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(
            value = "Get all data collections for a given operation",
            notes = "Retrieve all data collections with a parent id matching the operation id given as a path parameter"
    )
    public List<DDIItem> getDataCollections(
            @PathParam(value = "id") String id
    ) throws Exception {
        try {
            return searchService.getDataCollections(id);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
    }


}