package fr.insee.pogues.webservice.rest;

import fr.insee.pogues.service.QuestionnaireMetadataService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/questionnaire")
@Tag(name = "3. Questionnaire Metadata Controller")
@Slf4j
public class QuestionnaireMetadataController {

    private final QuestionnaireMetadataService metadataService;

    public QuestionnaireMetadataController(QuestionnaireMetadataService metadataService) {
        this.metadataService = metadataService;
    }

}
