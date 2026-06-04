package fr.insee.pogues.service;

import fr.insee.pogues.model.CodeList;
import fr.insee.pogues.model.Questionnaire;
import fr.insee.pogues.persistence.service.QuestionnaireService;
import fr.insee.pogues.transforms.visualize.PoguesJSONToPoguesXML;
import fr.insee.pogues.transforms.visualize.eno.PoguesXMLToDDI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class QuestionnaireMetadataServiceTest {

    private QuestionnaireMetadataService service;

    private QuestionnaireService questionnaireService;
    private NomenclatureService nomenclatureService;
    private PoguesJSONToPoguesXML jsonToXml;
    private PoguesXMLToDDI xmlToDdi;

    @BeforeEach
    void setup() {
        questionnaireService = mock(QuestionnaireService.class);
        nomenclatureService = mock(NomenclatureService.class);
        jsonToXml = mock(PoguesJSONToPoguesXML.class);
        xmlToDdi = mock(PoguesXMLToDDI.class);
        service = new QuestionnaireMetadataService(questionnaireService, nomenclatureService, jsonToXml, xmlToDdi);
    }

    @Test
    void shouldGenerateZipSuccessfully() throws Exception {

        // Given
        String dummyJson = "{\"foo\":\"bar\"}";
        JsonNode jsonNode = JsonMapper.builder().build().readTree(dummyJson);

        CodeList dummyCodeList = new CodeList();
        dummyCodeList.setId("h-f");
        dummyCodeList.setLabel("Homme-Femme");

        ByteArrayOutputStream xmlStream = new ByteArrayOutputStream();
        xmlStream.write("<questionnaire/>".getBytes(StandardCharsets.UTF_8));

        ByteArrayOutputStream ddiStream = new ByteArrayOutputStream();
        ddiStream.write("<ddi/>".getBytes(StandardCharsets.UTF_8));

        List<CodeList> nomenclatures = List.of(dummyCodeList);

        String poguesId = "test-id";
        when(questionnaireService.getQuestionnaireByIDWithReferences(poguesId)).thenReturn(jsonNode);
        when(nomenclatureService.getQuestionnaireNomenclatures(any(Questionnaire.class))).thenReturn(nomenclatures);
        when(jsonToXml.transform(any(InputStream.class), anyMap(), eq(poguesId))).thenReturn(xmlStream);
        when(xmlToDdi.transform(any(InputStream.class), anyMap(), eq(poguesId))).thenReturn(ddiStream);

        // When
        ByteArrayOutputStream outputZip = new ByteArrayOutputStream();
        service.generateZip(poguesId, outputZip);

        // Then
        List<String> zipEntryNames = new ArrayList<>();
        try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(outputZip.toByteArray()))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                zipEntryNames.add(entry.getName());
            }
        }

        assert outputZip.size() > 0;
        assertEquals(3, zipEntryNames.size());
        assertTrue(zipEntryNames.contains("pogues_" + poguesId + ".json"));
        assertTrue(zipEntryNames.contains("ddi_" + poguesId + ".xml"));
        assertTrue(zipEntryNames.contains("nomenclatures.json"));
    }
}
