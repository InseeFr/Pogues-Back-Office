package fr.insee.pogues.webservice.rest;

import fr.insee.pogues.search.model.PoguesHit;
import fr.insee.pogues.search.model.PoguesQuery;
import fr.insee.pogues.search.model.Questionnaire;
import fr.insee.pogues.search.service.SearchService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Path("/search")
@Api(value = "Pogues Search")
public class PoguesSearch {

    @Autowired
    SearchService searchService;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(
            value = "Search Item",
            notes = "Search the application index for item across types`"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Created"),
            @ApiResponse(code = 400, message = "Entity already exists")
    })
    public List<PoguesHit> searchQuestionnaire(PoguesQuery query) throws Exception {
        try {
            SearchResponse response = searchService.searchByLabel(query.getFilter(), query.getTypes().toArray(new String[query.getTypes().size()]));
            List<SearchHit> esHits = Arrays.asList(response.getHits().getHits());
            return esHits
                    .stream()
                    .map(hit -> new PoguesHit(
                            hit.getId(),
                            hit.getSource().get("label").toString(),
                            hit.getType()
                    ))
                    .collect(Collectors.toList());
        } catch(Exception e) {
            throw e;
        }
    }

    @POST
    @Path("questionnaire")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(
            value = "Index Questionnaire",
            notes = "Index a new `Questionnaire`"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Created"),
            @ApiResponse(code = 400, message = "Entity already exists")
    })
    public IndexResponse indexQuestionnaire(Questionnaire item) throws Exception {
        try {
            return searchService.save("questionnaire", item);
        } catch(Exception e) {
            e.printStackTrace();
            throw e;
        }
    }


}