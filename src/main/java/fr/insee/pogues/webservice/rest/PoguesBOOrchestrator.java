package fr.insee.pogues.webservice.rest;

import fr.insee.pogues.persistence.service.QuestionnairesService;
import fr.insee.pogues.transforms.*;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Main WebService class of the PoguesBOOrchestrator
 *
 * @author I6VWID
 */
@Path("/transform")
public class PoguesBOOrchestrator {

    final static Logger logger = Logger.getLogger(PoguesBOOrchestrator.class);

    @Autowired
    JSONToXML jsonToXML;

    @Autowired
    XMLToDDIImpl xmlToDDI;

    @Autowired
    DDIToXForm ddiToXForm;

    @Autowired
    XFormToURI xformToUri;

    @Autowired
    QuestionnairesService questionnairesService;

    /**
     * Dummy GET Helloworld used in unit tests
     *
     * @return "Hello world" as a String
     */
    @GET
    @Path("helloworld")
    public String helloworld() {
        return "Hello world";
    }

    @POST
    @Path("visualize/{name}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_XML)
    public Response visualizeFromBody(
            @Context final HttpServletRequest request,
            @PathParam(value = "name") String name
    ) throws Exception {
        PipeLine pipeline = new PipeLine();
        Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        try {
            StreamingOutput stream = output -> {
                try {
                    output.write(pipeline
                            .from(request.getInputStream())
                            .map(jsonToXML::transform, params)
                            .map(xmlToDDI::transform, params)
                            .map(ddiToXForm::transform, params)
                            .map(xformToUri::transform, params)
                            .transform()
                            .getBytes());
                } catch (Exception e) {
                    logger.error(e.getMessage());
                    throw new PoguesException(500, e.getMessage(), null);
                }
            };
            return Response.ok(stream).build();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @GET
    @Path("visualize/{id}")
    public Response visualizeFromId(
            @PathParam(value = "id") String id
    ) throws Exception {
        PipeLine pipeline = new PipeLine();
        InputStream input = null;
        Map<String, Object> params = new HashMap<>();
        try {
            JSONObject questionnaire = questionnairesService.getQuestionnaireByID(id);
            JSONObject survey = (JSONObject) questionnaire.get("survey");
            String name = survey.get("name").toString();
            params.put("name", name);
            input = new ByteArrayInputStream(questionnaire.toJSONString().getBytes(StandardCharsets.UTF_8));
            String uri = pipeline
                            .from(input)
                            .map(jsonToXML::transform, params)
                            .map(xmlToDDI::transform, params)
                            .map(ddiToXForm::transform, params)
                            .map(xformToUri::transform, params)
                            .transform();
            return Response.seeOther(URI.create(uri)).build();
        } catch (Exception e) {
            throw e;
        } finally {
            input.close();
        }
    }

}
