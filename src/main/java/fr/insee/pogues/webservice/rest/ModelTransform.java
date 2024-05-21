package fr.insee.pogues.webservice.rest;

import fr.insee.pogues.transforms.PipeLine;
import fr.insee.pogues.transforms.visualize.*;
import fr.insee.pogues.transforms.visualize.eno.DDIToFO;
import fr.insee.pogues.transforms.visualize.eno.DDIToFODT;
import fr.insee.pogues.transforms.visualize.eno.PoguesXMLToDDI;
import fr.insee.pogues.webservice.model.CaptureEnum;
import fr.insee.pogues.webservice.model.ColumnsEnum;
import fr.insee.pogues.webservice.model.OrientationEnum;
import fr.insee.pogues.webservice.model.StudyUnitEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static fr.insee.pogues.utils.IOStreamsUtils.string2InputStream;

/**
 * Main WebService class of the PoguesBOOrchestrator
 *
 * @author I6VWID
 */
@RestController
@RequestMapping("/api/transform")
@Tag(name = "4. Model transformation")
@Slf4j
public class ModelTransform {

	@Autowired
	PoguesJSONToPoguesXML jsonToXML;

	@Autowired
	PoguesXMLToPoguesJSON xmlToJson;

	@Autowired
	PoguesXMLToDDI poguesXMLToDDI;
	@Autowired
	DDIToFODT ddiToOdt;

	@Autowired
	DDIToFO ddiToFo;

	@Autowired
	FOToPDF foToPdf;

	@Autowired
	PoguesJSONToPoguesJSONDeref jsonToJsonDeref;

	private static final String CONTENT_DISPOSITION = HttpHeaders.CONTENT_DISPOSITION;


	@PostMapping(path = "visualize-spec", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	@Operation(summary = "Get visualization spec from JSON serialized Pogues entity", hidden = true)
	public ResponseEntity<StreamingResponseBody> visualizeSpecFromBody(@RequestBody String request,
			@RequestParam(name = "references", defaultValue = "false") Boolean ref) throws Exception {
		PipeLine pipeline = new PipeLine();
		Map<String, Object> params = new HashMap<>();
		params.put("needDeref", ref);
		String questionnaireName = "spec";
		try {
			StreamingResponseBody stream = output -> {
				try {
					output.write(
							pipeline.from(new ByteArrayInputStream(request.getBytes(StandardCharsets.UTF_8)))
									.map(jsonToJsonDeref::transform, params, questionnaireName)
									.map(jsonToXML::transform, params, questionnaireName)
									.map(poguesXMLToDDI::transform, params, questionnaireName)
									.map(ddiToOdt::transform, params, questionnaireName).transform().toByteArray());
				} catch (Exception e) {
					log.error(e.getCause().getMessage());
					throw new PoguesException(500, e.getCause().getClass().getSimpleName(), e.getCause().getMessage());
				}
			};

			return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_OCTET_STREAM)
					.header(CONTENT_DISPOSITION, "attachment; filename=form.fodt").body(stream);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		}
	}

	@PostMapping(path = "visualize-ddi", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	@Operation(summary = "Get visualization DDI file from JSON serialized Pogues entity")
	public ResponseEntity<StreamingResponseBody> visualizeDDIFromBody(@RequestBody String request,
			@RequestParam(name = "references", defaultValue = "false") Boolean ref) throws Exception {
		PipeLine pipeline = new PipeLine();
		Map<String, Object> params = new HashMap<>();
		params.put("needDeref", ref);
		String questionnaireName = "ddi";
		try {
			StreamingResponseBody stream = output -> {
				try {
					output.write(
							pipeline.from(new ByteArrayInputStream(request.getBytes(StandardCharsets.UTF_8)))
									.map(jsonToJsonDeref::transform, params, questionnaireName)
									.map(jsonToXML::transform, params, questionnaireName)
									.map(poguesXMLToDDI::transform, params, questionnaireName).transform().toByteArray());
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			};

			return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_OCTET_STREAM)
					.header(CONTENT_DISPOSITION, "attachment; filename=form.xml").body(stream);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		}
	}

	@PostMapping(path = "visualize-pdf", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	@Operation(summary = "Get visualization PDF questionnaire from JSON serialized Pogues entity")
	public ResponseEntity<StreamingResponseBody> visualizePDFFromBody(@RequestBody String request,
			@RequestParam(name = "references", defaultValue = "false") Boolean ref) {
		PipeLine pipeline = new PipeLine();
		Map<String, Object> params = new HashMap<>();
		params.put("needDeref", ref);
		String questionnaireName = "pdf";

		StreamingResponseBody stream = output -> {
            try {
                output.write(pipeline.from(new ByteArrayInputStream(request.getBytes(StandardCharsets.UTF_8)))
                        .map(jsonToJsonDeref::transform, params, questionnaireName)
                        .map(jsonToXML::transform, params, questionnaireName)
                        .map(poguesXMLToDDI::transform, params, questionnaireName)
                        .map(ddiToFo::transform, params, questionnaireName)
                        .map(foToPdf::transform, params, questionnaireName).transform().toByteArray());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };

		return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_PDF)
				.header(CONTENT_DISPOSITION, "attachment; filename=form.pdf").body(stream);

	}

	@PostMapping(path = "ddi2pdf", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	@Operation(summary = "Get visualization PDF questionnaire from DDI questionnaire")
	public ResponseEntity<StreamingResponseBody> ddi2pdfWithParamTest(@RequestBody String questDDI,
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
		String questionnaireName = "pdf";

		StreamingResponseBody stream = output -> {
            try {
                output.write(pipeline.from(new ByteArrayInputStream(questDDI.getBytes(StandardCharsets.UTF_8)))
                        .map(ddiToFo::transform, params, questionnaireName)
                        .map(foToPdf::transform, params, questionnaireName)
                        .transform().toByteArray());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
		return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_OCTET_STREAM)
				.header(CONTENT_DISPOSITION, "attachment; filename=\"form.pdf\"")
				.body(stream);
	}

	@PostMapping(path = "fo2pdf", consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	@Operation(summary = "Get visualization PDF questionnaire from FO questionnaire")
	public ResponseEntity<StreamingResponseBody> fo2Pdf(@RequestBody String questFO) throws Exception {
		ByteArrayOutputStream boas = null;
		String questionnaireName = "pdf";

		PipeLine pipeline = new PipeLine();
		Map<String, Object> params = new HashMap<>();
		StreamingResponseBody stream = output -> {

            try {
                output.write(pipeline.from(new ByteArrayInputStream(questFO.getBytes(StandardCharsets.UTF_8)))
                        .map(foToPdf::transform, params, questionnaireName)
                        .transform().toByteArray());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
		return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_OCTET_STREAM)
				.header(CONTENT_DISPOSITION, "attachment; filename=\"form.pdf\"")
				.body(stream);
	}

	@PostMapping(path = "json2xml", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
	@Operation(summary = "Get Pogues XML From Pogues JSON", description = "Returns a serialized XML based on a JSON entity that must comply with Pogues data model")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK"),
			@ApiResponse(responseCode = "500", description = "Error")
	})
	public ResponseEntity<StreamingResponseBody> json2XML(@RequestBody String questJson) throws Exception {
		String questionnaire = "xforms";
		return transform(new ByteArrayInputStream(questJson.getBytes(StandardCharsets.UTF_8)), jsonToXML,
				questionnaire, MediaType.APPLICATION_XML);
	}

	@PostMapping(path = "xml2json", consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Get Pogues JSON From Pogues XML", description = "Returns a JSON entity that must comply with Pogues data model based on XML")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK"),
			@ApiResponse(responseCode = "500", description = "Error") })
	@ResponseBody
	public ResponseEntity<StreamingResponseBody> xml2Json(@RequestBody String questXML) throws Exception {
		String questionnaire = "xforms";
		return transform(new ByteArrayInputStream(questXML.getBytes(StandardCharsets.UTF_8)), xmlToJson,
				questionnaire, MediaType.APPLICATION_JSON);
	}

	@PostMapping(path = "json/dereferenced", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Get Pogues JSON complete from Pogues JSON with references", description = "Returns a JSON entity that must comply with Pogues data model based on XML without any references to other questionnaires")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK"),
			@ApiResponse(responseCode = "500", description = "Error") })
	@ResponseBody
	public ResponseEntity<String> jsonRef2JsonDeref(@RequestBody String questJson) throws Exception {
		ByteArrayOutputStream result = jsonToJsonDeref.transform(string2InputStream(questJson), Map.of("needDeref", true), null);
		return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(result.toString(StandardCharsets.UTF_8));
	}

	private ResponseEntity<StreamingResponseBody> transform(InputStream request, ModelTransformer transformer,
			String questionnaire, MediaType type) {
		StreamingResponseBody stream = output -> {
            try {
                output.write(transformer.transform(request, null, questionnaire).toByteArray());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
		return ResponseEntity.status(HttpStatus.OK).contentType(type).body(stream);
	}

}
