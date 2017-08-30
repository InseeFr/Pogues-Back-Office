package fr.insee.pogues.webservice.rest;

import fr.insee.pogues.search.model.PoguesItem;
import fr.insee.pogues.search.service.SearchService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

@Component
@Path("/search")
@Api(value = "Pogues Search")
public class PoguesSearch {

    @Autowired
    SearchService searchService;

    @POST
    @Path("/questionnaire")
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(
            value = "Index Questionnaire",
            notes = "Index a new `Questionnaire`"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Created"),
            @ApiResponse(code = 400, message = "Entity already exists")
    })
    public void indexQuestionnaire(PoguesItem item) throws Exception {
        try {
            searchService.save("questionnaire", item);
        } catch(Exception e) {

        }
    }


}