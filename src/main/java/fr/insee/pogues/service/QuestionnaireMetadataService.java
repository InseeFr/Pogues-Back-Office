package fr.insee.pogues.service;

import fr.insee.pogues.exception.PoguesException;
import fr.insee.pogues.mapper.CodesListMapper;
import fr.insee.pogues.model.Questionnaire;
import fr.insee.pogues.model.dto.nomenclatures.NomenclatureZipDto;
import fr.insee.pogues.persistence.service.QuestionnaireService;
import fr.insee.pogues.transforms.visualize.PoguesJSONToPoguesXML;
import fr.insee.pogues.transforms.visualize.eno.PoguesXMLToDDI;
import fr.insee.pogues.utils.PoguesSerializer;
import fr.insee.pogues.utils.model.ZipFile;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


@Service
@Slf4j
@AllArgsConstructor
public class QuestionnaireMetadataService {

    private final QuestionnaireService questionnaireService;
    private final NomenclatureService nomenclatureService;
    private final PoguesJSONToPoguesXML jsonToXml;
    private final PoguesXMLToDDI xmlToDdi;

    private static final ObjectMapper objectMapper = JsonMapper.builder().build();

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
            Questionnaire questionnaireWithRef = questionnaireService.getQuestionnaireModelByIDWithReferences(poguesId);

            List<NomenclatureZipDto> nomenclatureZipDtos = nomenclatureService.getQuestionnaireNomenclatures(questionnaireWithRef)
                    .stream()
                    .map(CodesListMapper::toNomenclatureZipDto)
                    .toList();

            byte[] jsonBytes = PoguesSerializer
                    .questionnaireJavaToString(questionnaireWithRef)
                    .getBytes(StandardCharsets.UTF_8);

            byte[] poguesXmlBytes = jsonToXml
                    .transform(new ByteArrayInputStream(jsonBytes), new HashMap<>(), poguesId)
                    .toByteArray();

            byte[] ddiBytes = xmlToDdi
                    .transform(new ByteArrayInputStream(poguesXmlBytes), new HashMap<>(), poguesId)
                    .toByteArray();

            byte[] nomenclaturesBytes = objectMapper
                    .writeValueAsString(nomenclatureZipDtos)
                    .getBytes(StandardCharsets.UTF_8);

            List<ZipFile> files = List.of(
                    new ZipFile("pogues_" + poguesId + ".json", jsonBytes),
                    new ZipFile("ddi_" + poguesId + ".xml", ddiBytes),
                    new ZipFile("nomenclatures.json", nomenclaturesBytes));

            generateZip(files, outputStream);

        } catch (Exception e) {
            throw new PoguesException(500, "Error while generating the ZIP archive", e.getMessage());
        }
    }
}


