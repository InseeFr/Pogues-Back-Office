package fr.insee.pogues.persistence.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import fr.insee.pogues.model.Questionnaire;
import fr.insee.pogues.persistence.query.QuestionnairesServiceQuery;

@Service
@Slf4j
public class VariablesServiceImpl implements VariablesService {

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Autowired QuestionnairesService questionnairesService;

	public VariablesServiceImpl() {}

	public VariablesServiceImpl(QuestionnairesService questionnairesService) {
		this.questionnairesService = questionnairesService;
	}

	public JSONArray getVariablesByQuestionnaireForPublicEnemy(String id){
		try {
			JSONObject questionnaire = questionnairesService.getQuestionnaireByIDWithReferences(id);
			// We test the existence of the questionnaire in repository
			if (questionnaire != null) {
				JSONObject variables = (JSONObject) questionnaire.get("Variables");
				return (JSONArray) variables.get("Variable");
			}
		} catch (Exception e) {
			log.error("Exception occurred when trying to get variables from questionnaire with id={}", id, e);
		}
		return null;
	}

	public String getVariablesByQuestionnaire(String id){
		StreamSource json = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			JSONObject questionnaire = questionnairesService.getQuestionnaireByID(id);
			// We test the existence of the questionnaire in repository
			if (questionnaire != null) {
				log.info("Deserializing questionnaire ");
				JAXBContext context = JAXBContext.newInstance(Questionnaire.class);
				Unmarshaller unmarshaller = context.createUnmarshaller();
				unmarshaller.setProperty(UnmarshallerProperties.MEDIA_TYPE, "application/json");
				unmarshaller.setProperty(UnmarshallerProperties.JSON_INCLUDE_ROOT, false);
				try(InputStream inQuestionnaire = new ByteArrayInputStream(questionnaire.toString().getBytes())){
					json = new StreamSource(inQuestionnaire);
					Questionnaire questionnaireJava = unmarshaller.unmarshal(json, Questionnaire.class).getValue();
					log.info("Questionnaire " + questionnaireJava.getId() + " successfully deserialized");
					log.info("Serializing variables for questionnaire {}", questionnaireJava.getId());
					JAXBContext context2 = JAXBContext.newInstance(Questionnaire.Variables.class);
					Marshaller marshaller = context2.createMarshaller();
					marshaller.setProperty(MarshallerProperties.MEDIA_TYPE, "application/json");
					// Set it to true if you need to include the JSON root element in the JSON output
					marshaller.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, true);
					// Set it to true if you need the JSON output to formatted
					marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
					// Marshal the questionnaire object to JSON and put the output in a string
					marshaller.marshal(questionnaireJava.getVariables(), baos);
				}
				return baos.toString(StandardCharsets.UTF_8);
			}
		} catch (Exception e) {
			log.error("Exception occurred when trying to get variables from questionnaire with id={}", id, e);
		} finally {
			IOUtils.closeQuietly(baos);
		}
		return null;
	}

}
