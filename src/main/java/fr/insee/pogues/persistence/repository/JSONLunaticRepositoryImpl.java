package fr.insee.pogues.persistence.repository;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.pogues.exception.PoguesException;
import fr.insee.pogues.persistence.exceptions.NonUniqueResultException;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.util.PGobject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import static fr.insee.pogues.utils.json.JSONFunctions.jsonStringtoJsonNode;

/**
 * Questionnaire Service Query for the Postgresql implementation to assume the persistance of questionnaires' JSON
 * Lunatic representation.
 */
@Service
@Slf4j
public class JSONLunaticRepositoryImpl implements JSONLunaticRepository {

    @Autowired
    JdbcTemplate jdbcTemplate;

    private static final String NOT_FOUND="Not found";

    /**
     * Fetch the JSON Lunatic representation of the questionnaire
     * @param id ID of the questionnaire to fetch
     * @return Description of the questionnaire in Lunatic JSON
     */
    public JsonNode getJsonLunaticByID(String id) throws Exception {
        try {
            String qString = "SELECT data_lunatic FROM visu_lunatic WHERE id=?";
            PGobject q = jdbcTemplate.queryForObject(qString, PGobject.class, id);
            return jsonStringtoJsonNode(q.toString());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    /**
     * Delete the JSON Lunatic representation of the questionnaire
     * @param id ID of the questionnaire to delete
     */
    public void deleteJsonLunaticByID(String id) throws Exception {
        String qString = "DELETE FROM visu_lunatic where id=?";
        int r = jdbcTemplate.update(qString, id);
        if(0 == r) {
            throw new PoguesException(404, NOT_FOUND, String.format("Entity with id %s not found", id));
        }
    }

    /**
     * Create a JSON Lunatic representation of a questionnaire
     * @param questionnaireLunatic JSON Lunatic description of a questionnaire to create
     */
    public void createJsonLunatic(JsonNode questionnaireLunatic) throws Exception {
        String qString =
                "INSERT INTO visu_lunatic (id, data_lunatic) VALUES (?, ?)";
        String id  = questionnaireLunatic.get("id").asText();
        if(null != getJsonLunaticByID(id)){
            throw new NonUniqueResultException("Entity already exists");
        }
        PGobject q = new PGobject();
        q.setType("json");
        q.setValue(questionnaireLunatic.toString());
        jdbcTemplate.update(qString, id, q);
    }

    /**
     * Update the JSON Lunatic representation of the questionnaire
     * @param id ID of the questionnaire to update
     * @param questionnaireLunatic New JSON Lunatic description of the questionnaire
     */
    public void updateJsonLunatic(String id, JsonNode questionnaireLunatic) throws Exception {
        String qString = "UPDATE visu_lunatic SET data_lunatic=? WHERE id=?";
        PGobject q = new PGobject();
        q.setType("json");
        q.setValue(questionnaireLunatic.toString());
        int r = jdbcTemplate.update(qString, q, id);
        if(0 == r) {
            throw new NonUniqueResultException("Entity already exists");
        }
    }
}
