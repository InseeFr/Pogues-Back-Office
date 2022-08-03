package fr.insee.pogues.webservice.rest;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import fr.insee.pogues.persistence.service.QuestionnairesService;
import fr.insee.pogues.transforms.PipeLine;
import fr.insee.pogues.transforms.Transformer;
import fr.insee.pogues.transforms.visualize.DDIToFO;
import fr.insee.pogues.transforms.visualize.DDIToFODT;
import fr.insee.pogues.transforms.visualize.DDIToLunaticJSON;
import fr.insee.pogues.transforms.visualize.DDIToXForms;
import fr.insee.pogues.transforms.visualize.FOToPDF;
import fr.insee.pogues.transforms.visualize.LunaticJSONToUriQueen;
import fr.insee.pogues.transforms.visualize.LunaticJSONToUriStromaeV2;
import fr.insee.pogues.transforms.visualize.PoguesJSONToPoguesXML;
import fr.insee.pogues.transforms.visualize.PoguesXMLToDDI;
import fr.insee.pogues.transforms.visualize.PoguesXMLToPoguesJSON;
import fr.insee.pogues.transforms.visualize.XFormsToURIStromaeV1;
import fr.insee.pogues.webservice.model.CaptureEnum;
import fr.insee.pogues.webservice.model.ColumnsEnum;
import fr.insee.pogues.webservice.model.OrientationEnum;
import fr.insee.pogues.webservice.model.StudyUnitEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Main WebService class of the PoguesBOOrchestrator
 *
 * @author I6VWID
 */
@RestController
@RequestMapping("/api/transform")
@Tag(name = "Pogues Transforms")
@SecurityRequirement(name = "bearerAuth")
public class PoguesTransforms {

	static final Logger logger = LogManager.getLogger(PoguesTransforms.class);

	@Autowired
	PoguesJSONToPoguesXML jsonToXML;

	@Autowired
	PoguesXMLToPoguesJSON xmlToJson;

	@Autowired
	PoguesXMLToDDI poguesXMLToDDI;

	@Autowired
	DDIToXForms ddiToXForm;

	@Autowired
	DDIToFODT ddiToOdt;
	
	@Autowired
	DDIToFO ddiToFo;
	
	@Autowired
	FOToPDF foToPdf;

	@Autowired
	XFormsToURIStromaeV1 xformToUri;
	
	@Autowired
	DDIToLunaticJSON ddiToLunaticJSON;
	
	@Autowired
	LunaticJSONToUriQueen lunaticJSONToUriQueen;
	
	@Autowired
	LunaticJSONToUriStromaeV2 lunaticJSONToUriStromaeV2;

	@Autowired
	QuestionnairesService questionnairesService;
	
	private static final String CONTENT_DISPOSITION = "Content-Disposition";

	@PostMapping(path = "visualize/{dataCollection}/{questionnaire}",
			consumes = MediaType.APPLICATION_JSON_VALUE)
	@Operation(
			summary = "Get visualization URI from JSON serialized Pogues entity", 
			description = "dataCollection MUST refer to the name attribute owned by the nested DataCollectionObject")
	@io.swagger.v3.oas.annotations.parameters.RequestBody(
			description = "JSON representation of the Pogues Model"
			)
	public ResponseEntity<StreamingResponseBody> visualizeFromBody(@RequestBody String request,
			@PathVariable(value = "dataCollection") String dataCollection,
			@PathVariable(value = "questionnaire") String questionnaire) throws Exception {
		PipeLine pipeline = new PipeLine();
		Map<String, Object> params = new HashMap<>();
		params.put("dataCollection", dataCollection.toLowerCase());
		params.put("questionnaire", questionnaire.toLowerCase());
		try {
			StreamingResponseBody stream = output -> {
				try {
					output.write(pipeline.from(new ByteArrayInputStream(request.getBytes(StandardCharsets.UTF_8)))
							.map(jsonToXML::transform, params, questionnaire.toLowerCase())
							.map(poguesXMLToDDI::transform, params, questionnaire.toLowerCase())
							.map(ddiToXForm::transform, params, questionnaire.toLowerCase())
							.map(xformToUri::transform, params, questionnaire.toLowerCase()).transform().getBytes());
				} catch (Exception e) {
					logger.error(e.getMessage());
					throw new PoguesException(500, e.getMessage(), null);
				}
			};
			return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.TEXT_PLAIN).body(stream);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
	}
	
	@PostMapping(path = "visualize-queen/{questionnaire}",
			consumes = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Get visualization URI Queen from JSON serialized Pogues entity", description = "Get visualization URI Queen from JSON serialized Pogues entity")
	public ResponseEntity<StreamingResponseBody> visualizeQueenFromBody(@RequestBody String request,
			@PathVariable(value = "questionnaire") String questionnaireName) throws Exception {
		PipeLine pipeline = new PipeLine();
		Map<String, Object> params = new HashMap<>();
		params.put("mode","CAPI");
		try {
			StreamingResponseBody stream = output -> {
				try {
					output.write(pipeline.from(new ByteArrayInputStream(request.getBytes(StandardCharsets.UTF_8)))
							.map(jsonToXML::transform, params, questionnaireName.toLowerCase())
							.map(poguesXMLToDDI::transform, params, questionnaireName.toLowerCase())
							.map(ddiToLunaticJSON::transform, params, questionnaireName.toLowerCase())
							.map(lunaticJSONToUriQueen::transform, params, questionnaireName.toLowerCase()).transform().getBytes());
				} catch (Exception e) {
					logger.error(e.getMessage());
					throw new PoguesException(500, e.getMessage(), null);
				}
			};
			return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.TEXT_PLAIN).body(stream);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
	}
	
	@PostMapping(path = "visualize-stromae-v2/{questionnaire}",
			consumes = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Get visualization URI Stromae V2 from JSON serialized Pogues entity", description = "Get visualization URI Stromae V2 from JSON serialized Pogues entity")
	public ResponseEntity<StreamingResponseBody> visualizeStromaeV2FromBody(@RequestBody String request,
			@PathVariable(value = "questionnaire") String questionnaireName) throws Exception {
		PipeLine pipeline = new PipeLine();
		Map<String, Object> params = new HashMap<>();
		params.put("questionnaire", questionnaireName.toLowerCase());
		try {
			StreamingResponseBody stream = output -> {
				try {
					output.write(pipeline.from(new ByteArrayInputStream(request.getBytes(StandardCharsets.UTF_8)))
							.map(jsonToXML::transform, params, questionnaireName.toLowerCase())
							.map(poguesXMLToDDI::transform, params, questionnaireName.toLowerCase())
							.map(ddiToLunaticJSON::transform, params, questionnaireName.toLowerCase())
							.map(lunaticJSONToUriStromaeV2::transform, params, questionnaireName.toLowerCase()).transform().getBytes());
				} catch (Exception e) {
					logger.error(e.getMessage());
					throw new PoguesException(500, e.getMessage(), null);
				}
			};
			return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.TEXT_PLAIN).body(stream);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
	}

	@PostMapping(path = "visualize-from-ddi/{dataCollection}/{questionnaire}",
			consumes = MediaType.APPLICATION_JSON_VALUE)
	@Operation(
			summary = "Get visualization URI from DDI questionnaire", 
			description = "dataCollection MUST refer to the name attribute owned by the nested DataCollectionObject")
	public ResponseEntity<StreamingResponseBody> visualizeFromDDIBody(@RequestBody  String request,
			@PathVariable(value = "dataCollection") String dataCollection,
			@PathVariable(value = "questionnaire") String questionnaire) throws Exception {
		PipeLine pipeline = new PipeLine();
		Map<String, Object> params = new HashMap<>();
		params.put("dataCollection", dataCollection.toLowerCase());
		params.put("questionnaire", questionnaire.toLowerCase());
		try {
			StreamingResponseBody stream = output -> {
				try {
					output.write(pipeline.from(new ByteArrayInputStream(request.getBytes(StandardCharsets.UTF_8)))
							.map(ddiToXForm::transform, params, questionnaire.toLowerCase())
							.map(xformToUri::transform, params, questionnaire.toLowerCase()).transform().getBytes());
				} catch (Exception e) {
					logger.error(e.getMessage());
					throw new PoguesException(500, e.getMessage(), null);
				}
			};
			return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.TEXT_PLAIN).body(stream);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
	}

	@PostMapping(path = "visualize-spec",
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_OCTET_STREAM_VALUE
			)
	@Operation(
			summary = "Get visualization spec from JSON serialized Pogues entity", hidden = true)
	public ResponseEntity<StreamingResponseBody> visualizeSpecFromBody(@RequestBody String request) throws Exception {
		PipeLine pipeline = new PipeLine();
		Map<String, Object> params = new HashMap<>();
		String questionnaireName = "spec";
		try {
			StreamingResponseBody stream = output -> {
				try {
					output.write(
							pipeline.from(new ByteArrayInputStream(request.getBytes(StandardCharsets.UTF_8)))
									.map(jsonToXML::transform, params, questionnaireName)
									.map(poguesXMLToDDI::transform, params, questionnaireName)
									.map(ddiToOdt::transform, params, questionnaireName).transform().getBytes());
				} catch (Exception e) {
					logger.error(e.getMessage());
					throw new PoguesException(500, e.getMessage(), null);
				}
			};

			return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_OCTET_STREAM)
					.header(CONTENT_DISPOSITION, "attachment; filename=form.fodt").body(stream);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
	}

	@PostMapping(path = "visualize-ddi",
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_OCTET_STREAM_VALUE
			)
	@Operation(summary = "Get visualization DDI file from JSON serialized Pogues entity")
	public ResponseEntity<StreamingResponseBody> visualizeDDIFromBody(@RequestBody String request) throws Exception {
		PipeLine pipeline = new PipeLine();
		Map<String, Object> params = new HashMap<>();
		String questionnaireName = "ddi";
		try {
			StreamingResponseBody stream = output -> {
				try {
					output.write(
							pipeline.from(new ByteArrayInputStream(request.getBytes(StandardCharsets.UTF_8))).map(jsonToXML::transform, params, questionnaireName)
									.map(poguesXMLToDDI::transform, params, questionnaireName).transform().getBytes());
				} catch (Exception e) {
					logger.error(e.getMessage());
					throw new PoguesException(500, e.getMessage(), null);
				}
			};

			return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_OCTET_STREAM)
					.header(CONTENT_DISPOSITION, "attachment; filename=form.xml").body(stream);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
	}

	@PostMapping(path = "visualize-pdf",
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_OCTET_STREAM_VALUE
			)
	@Operation(summary = "Get visualization PDF questionnaire from JSON serialized Pogues entity")
	public ResponseEntity<InputStreamResource> visualizePDFFromBody(@RequestBody String request) throws Exception {
		PipeLine pipeline = new PipeLine();
		Map<String, Object> params = new HashMap<>();
		String filePath = null;
		String questionnaireName = "pdf";
		try {
			filePath = pipeline.from(new ByteArrayInputStream(request.getBytes(StandardCharsets.UTF_8)))
					.map(jsonToXML::transform, params, questionnaireName)
					.map(poguesXMLToDDI::transform, params, questionnaireName)
					.map(ddiToFo::transform, params, questionnaireName)
					.map(foToPdf::transform, params, questionnaireName).transform();
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new PoguesException(500, e.getMessage(), null);
		}
		File file = new File(filePath);
		InputStream inputStream = new FileInputStream(file);
	    InputStreamResource inputStreamResource = new InputStreamResource(inputStream);
		return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_OCTET_STREAM)
				.header(CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"").body(inputStreamResource);

	}

	@PostMapping(path = "ddi2pdf",
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_OCTET_STREAM_VALUE
			)
	@Operation(summary = "Get visualization PDF questionnaire from DDI questionnaire")
	public ResponseEntity<InputStreamResource> ddi2pdfWithParamTest(@RequestBody String questDDI,
			@RequestParam(name = "columns") ColumnsEnum columns,
			@RequestParam(name = "orientation") OrientationEnum orientation,
			@RequestParam(name = "capture") CaptureEnum capture,
			@RequestParam(name = "studyunit") StudyUnitEnum studyUnit,
			@RequestParam(name = "timequestion") Boolean timequestion) throws Exception {
		PipeLine pipeline = new PipeLine();
		Map<String, Object> params = new HashMap<>();
		if (columns != null) {
			params.put("columns", columns.getNbcolumn());
		}
		if (orientation != null) {
			params.put("orientation", orientation.getOrientation());
		}
		if (capture != null) {
			params.put("capture", capture.getCapture());
		}
		if (studyUnit != null) {
			params.put("studyunit", studyUnit.getStudyUnit());
		}
		if (timequestion != null) {
			params.put("timequestion", timequestion.toString());
		}
		String filePath = null;
		String questionnaireName = "pdf";

		try {
			filePath = pipeline.from(new ByteArrayInputStream(questDDI.getBytes(StandardCharsets.UTF_8))).map(ddiToFo::transform, params, questionnaireName)
					.map(foToPdf::transform, params, questionnaireName)
					.transform();
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new PoguesException(500, e.getMessage(), null);
		}

		File file = new File(filePath);
		InputStream inputStream = new FileInputStream(file);
	    InputStreamResource inputStreamResource = new InputStreamResource(inputStream);
	    return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_OCTET_STREAM)
				.header(CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"").body(inputStreamResource);
	}
	
	@PostMapping(path = "fo2pdf",
			consumes = MediaType.APPLICATION_XML_VALUE,
			produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	@Operation(summary = "Get visualization PDF questionnaire from FO questionnaire")
	public ResponseEntity<InputStreamResource> fo2Pdf (@RequestBody String questFO) throws Exception{
		String filePath = null;
		String questionnaireName = "pdf";
		
		PipeLine pipeline = new PipeLine();
		Map<String, Object> params = new HashMap<>();
		try {
			filePath = pipeline.from(new ByteArrayInputStream(questFO.getBytes(StandardCharsets.UTF_8)))
					.map(foToPdf::transform, params, questionnaireName)
					.transform();;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
		
		File file = new File(filePath);
		InputStream inputStream = new FileInputStream(file);
	    InputStreamResource inputStreamResource = new InputStreamResource(inputStream);
		return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_OCTET_STREAM)
				.header(CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"").body(inputStreamResource);
	}

	@PostMapping(path = "json2xml",
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_XML_VALUE
			)
	@Operation(
			summary = "Get Pogues XML From Pogues JSON", 
			description = "Returns a serialized XML based on a JSON entity that must comply with Pogues data model")
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK"), 
			@ApiResponse(responseCode = "500", description = "Error") 
			})
	public ResponseEntity<StreamingResponseBody> json2XML(@RequestBody String questJson) throws Exception {
		String questionnaire = "xforms";
		try {
			return transform(new ByteArrayInputStream(questJson.getBytes(StandardCharsets.UTF_8)), jsonToXML, questionnaire, MediaType.APPLICATION_XML);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
	}

	@PostMapping(path = "xml2json",
			consumes = MediaType.APPLICATION_XML_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(
			summary = "Get Pogues JSON From Pogues XML", 
			description = "Returns a JSON entity that must comply with Pogues data model based on XML")
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK"), 
			@ApiResponse(responseCode = "500", description = "Error") })
	@ResponseBody
	public ResponseEntity<StreamingResponseBody> xml2Json(@RequestBody String questXML) throws Exception {
		String questionnaire = "xforms";
		try {
			return transform(new ByteArrayInputStream(questXML.getBytes(StandardCharsets.UTF_8)), xmlToJson, questionnaire, MediaType.APPLICATION_JSON);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
	}

	@PostMapping(path = "xform2uri/{dataCollection}/{questionnaire}",
			produces = MediaType.TEXT_PLAIN_VALUE)
	@Operation(
			summary = "Get Pogues visualization URI From Pogues XForm document", 
			description = "Returns the vizualisation URI of a form that was generated from XForm description found in body")
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK"), 
			@ApiResponse(responseCode = "500", description = "Error") })
	public String xForm2URI(@RequestBody String questXforms,
			@PathVariable(value = "dataCollection") String dataCollection,
			@PathVariable(value = "questionnaire") String questionnaire) throws Exception {
		try {
			Map<String, Object> params = new HashMap<>();
			params.put("dataCollection", dataCollection.toLowerCase());
			params.put("questionnaire", questionnaire.toLowerCase());
			return xformToUri.transform(questXforms, params, questionnaire);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
	}

	private ResponseEntity<StreamingResponseBody> transform(InputStream request, Transformer transformer, String questionnaire, MediaType type) {

		StreamingResponseBody stream = output -> {
			try {
				transformer.transform(request, output, null, questionnaire);
			} catch (Exception e) {
				e.printStackTrace();
				throw new PoguesException(500, "Transformation error", e.getMessage());
			}
		};
		return ResponseEntity.status(HttpStatus.OK).contentType(type).body(stream);
	}

}
