package fr.insee.pogues.webservice.rest;

import fr.insee.pogues.persistence.service.QuestionnairesService;
import fr.insee.pogues.transforms.*;
import io.swagger.annotations.*;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
public class PoguesTransforms {

    final static Logger logger = LogManager.getLogger(PoguesTransforms.class);

    @Autowired
    JSONToXML jsonToXML;

    @Autowired
    XMLToDDI xmlToDDI;

    @Autowired
    DDIToXForm ddiToXForm;

    @Autowired
    XFormToURI xformToUri;

    @Autowired
    QuestionnairesService questionnairesService;


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
        } catch(PoguesException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        } finally {
            input.close();
        }
    }


    @POST
    @Path("json2xml")
    @Produces(MediaType.APPLICATION_XML)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(
            value = "Get Pogues XML From Pogues JSON",
            notes = "Get Transformed XML document from Pogues JSON"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 500, message = "Error")
    })
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "poguesJson", value = "JSON representation of the Pogues Model", paramType = "body", dataType = "string")
    })
    public Response json2XML(@Context final HttpServletRequest request) throws Exception {
        try {
            return transform(request, jsonToXML);
        } catch(PoguesException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        }
    }


    @POST
    @Path("xml2ddi")
    @Produces(MediaType.APPLICATION_XML)
    @Consumes(MediaType.APPLICATION_XML)
    @ApiOperation(
            value = "Get DDI From Pogues XML",
            notes = "Get DDI representation of Pogues metadata from XML"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 500, message = "Error")
    })
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "poguesDdi", value = "XML representation of the Pogues Model", paramType = "body", dataType = "string")
    })
    public Response xml2DDi(@Context final HttpServletRequest request) throws Exception {
        try {
            return transform(request, xmlToDDI);
        } catch(PoguesException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        }
    }


    @POST
    @Path("ddi2xform")
    @Produces(MediaType.APPLICATION_XML)
    @Consumes(MediaType.APPLICATION_XML)
    @ApiOperation(
            value = "Get Pogues XForm From Pogues DDI metadata",
            notes = "Get Transformed XForm document from Pogues DDI metadata representation"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 500, message = "Error")
    })
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "poguesDDI", value = "DDI representation of the Pogues Model", paramType = "body", dataType = "string")
    })
    public Response ddi2XForm(@Context final HttpServletRequest request) throws Exception {
        try {
            return transform(request, ddiToXForm);
        } catch(PoguesException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        }
    }

    @POST
    @Path("xform2uri/{name}")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_XML)
    @ApiOperation(
            value = "Get Pogues visualization URI From Pogues XForm document",
            notes = "Get Transformed XForm document from Pogues DDI metadata representation"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 500, message = "Error")
    })
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "poguesXForm", value = "XForm document generated from Pogues DDI metadata", paramType = "body", dataType = "string")
    })
    public String xForm2URI(
            @Context final HttpServletRequest request,
            @PathParam(value = "name") String name
    ) throws Exception {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("name", name);
            String input = IOUtils.toString(request.getInputStream(), StandardCharsets.UTF_8);
            return xformToUri.transform(input, params);
        } catch(PoguesException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        }
    }


    private Response transform(HttpServletRequest request, Transformer transformer) throws Exception {
        try {
            StreamingOutput stream = output -> {
                try {
                    transformer.transform(request.getInputStream(), output, null);
                } catch (Exception e) {
                    logger.error(e.getMessage());
                    throw new PoguesException(500, e.getMessage(), null);
                }
            };
            return Response.ok(stream).build();
        } catch (Exception e) {
            throw e;
        }
    }

}
