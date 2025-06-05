package fr.insee.pogues.service;

import fr.insee.pogues.persistence.service.QuestionnairesService;
import fr.insee.pogues.transforms.visualize.PoguesJSONToPoguesXML;
import fr.insee.pogues.transforms.visualize.eno.PoguesXMLToDDI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class QuestionnaireMetadataService {

    private final QuestionnairesService questionnairesService;
    private final PoguesJSONToPoguesXML jsonToXml;
    private final PoguesXMLToDDI xmlToDdi;

    public QuestionnaireMetadataService(
            QuestionnairesService questionnairesService,
            PoguesJSONToPoguesXML jsonToXml,
            PoguesXMLToDDI xmlToDdi
    ) {
        this.questionnairesService = questionnairesService;
        this.jsonToXml = jsonToXml;
        this.xmlToDdi = xmlToDdi;
    }
}
