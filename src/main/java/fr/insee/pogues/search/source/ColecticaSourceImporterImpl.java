package fr.insee.pogues.search.source;

import fr.insee.pogues.metadata.service.MetadataService;
import fr.insee.pogues.search.model.Family;
import fr.insee.pogues.search.model.Operation;
import fr.insee.pogues.search.model.Questionnaire;
import fr.insee.pogues.search.model.Series;
import fr.insee.pogues.search.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
public class ColecticaSourceImporterImpl implements ColecticaSourceImporter {

    @Autowired
    MetadataService metadataService;

    @Autowired
    SearchService searchService;

    List<String> groupIds;

    @PostConstruct
    public void setUp() throws Exception {
        try {
            groupIds = metadataService.getGroupIds();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }


    @Override
    public void source() throws Exception {
        for (String id : groupIds) {
            Family f = metadataService.getFamily(id);
            searchService.save("family", f);
            saveSeries(f.getSeries());
        }
    }

    public void saveSeries(List<Series> series) throws Exception {
        for (Series s : series) {
            searchService.save("series", s);
            saveOperations(s.getOperations());
        }
    }

    public void saveOperations(List<Operation> operations) throws Exception {
        for (Operation o : operations) {
            searchService.save("operation", o);
            saveQuestionnaires(o.getQuestionnaires());
        }
    }

    public void saveQuestionnaires(List<Questionnaire> questionnaires) throws Exception {
        for (Questionnaire q : questionnaires) {
            searchService.save("questionnaire", q);
        }
    }


}
