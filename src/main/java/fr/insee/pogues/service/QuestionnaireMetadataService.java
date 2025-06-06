package fr.insee.pogues.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.insee.pogues.exception.QuestionnaireMetadataException;
import fr.insee.pogues.persistence.service.QuestionnairesService;
import fr.insee.pogues.transforms.visualize.PoguesJSONToPoguesXML;
import fr.insee.pogues.transforms.visualize.eno.PoguesXMLToDDI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static fr.insee.pogues.utils.json.JSONFunctions.objectNodeToPrettyJsonString;

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

    public void generateZip(String poguesId, OutputStream outputStream) throws QuestionnaireMetadataException {
        try {
            JsonNode jsonWithRef = questionnairesService.getQuestionnaireByIDWithReferences(poguesId);

            String jsonStr = objectNodeToPrettyJsonString(jsonWithRef);

            InputStream jsonStream = new ByteArrayInputStream(jsonStr.getBytes(StandardCharsets.UTF_8));

            ByteArrayOutputStream xmlStream = jsonToXml.transform(jsonStream, new HashMap<>(), poguesId);
            ByteArrayOutputStream ddiStream = xmlToDdi.transform(
                    new ByteArrayInputStream(xmlStream.toByteArray()),
                    new HashMap<>(),
                    poguesId
            );

            try (ZipOutputStream zos = new ZipOutputStream(outputStream)) {
                zos.putNextEntry(new ZipEntry("pogues_" + poguesId + ".json"));
                zos.write(jsonStr.getBytes(StandardCharsets.UTF_8));
                zos.closeEntry();

                zos.putNextEntry(new ZipEntry("ddi_" + poguesId + ".xml"));
                zos.write(ddiStream.toByteArray());
                zos.closeEntry();
            }

        } catch (Exception e) {
            throw new QuestionnaireMetadataException(500, "Error while generating the ZIP archive", e);
        }
    }
}

