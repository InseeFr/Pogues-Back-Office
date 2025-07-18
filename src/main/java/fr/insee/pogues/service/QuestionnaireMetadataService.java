package fr.insee.pogues.service;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.pogues.exception.PoguesException;
import fr.insee.pogues.persistence.service.QuestionnaireService;
import fr.insee.pogues.transforms.visualize.PoguesJSONToPoguesXML;
import fr.insee.pogues.transforms.visualize.eno.PoguesXMLToDDI;
import fr.insee.pogues.utils.model.ZipFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static fr.insee.pogues.utils.json.JSONFunctions.objectNodeToPrettyJsonString;

@Service
@Slf4j
public class QuestionnaireMetadataService {

    private final QuestionnaireService questionnaireService;
    private final PoguesJSONToPoguesXML jsonToXml;
    private final PoguesXMLToDDI xmlToDdi;

    public QuestionnaireMetadataService(
            QuestionnaireService questionnaireService,
            PoguesJSONToPoguesXML jsonToXml,
            PoguesXMLToDDI xmlToDdi
    ) {
        this.questionnaireService = questionnaireService;
        this.jsonToXml = jsonToXml;
        this.xmlToDdi = xmlToDdi;
    }

    public void generateZip(List<ZipFile> files, OutputStream outputStream) throws IOException {
        try (ZipOutputStream zos = new ZipOutputStream(outputStream)) {
            for (ZipFile file : files) {
                zos.putNextEntry(new ZipEntry(file.getEntryName()));
                zos.write(file.getContent());
                zos.closeEntry();
            }
        }
    }

    public void generateZip(String poguesId, OutputStream outputStream) throws PoguesException {
        try {
            JsonNode jsonWithRef = questionnaireService.getQuestionnaireByIDWithReferences(poguesId);
            String jsonStr = objectNodeToPrettyJsonString(jsonWithRef);
            byte[] jsonBytes = jsonStr.getBytes(StandardCharsets.UTF_8);

            ByteArrayOutputStream xmlStream = jsonToXml.transform(
                    new ByteArrayInputStream(jsonBytes), new HashMap<>(), poguesId);

            ByteArrayOutputStream ddiStream = xmlToDdi.transform(
                    new ByteArrayInputStream(xmlStream.toByteArray()), new HashMap<>(), poguesId);

            List<ZipFile> files = List.of(
                    new ZipFile("pogues_" + poguesId + ".json", jsonBytes),
                    new ZipFile("ddi_" + poguesId + ".xml", ddiStream.toByteArray())
            );

            generateZip(files, outputStream);

        } catch (Exception e) {
            throw new PoguesException(500, "Error while generating the ZIP archive", e.getMessage());
        }
    }
}


