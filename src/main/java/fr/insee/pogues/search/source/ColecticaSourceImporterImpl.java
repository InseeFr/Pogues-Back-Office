package fr.insee.pogues.search.source;

import fr.insee.pogues.metadata.service.MetadataService;
import fr.insee.pogues.search.model.*;
import fr.insee.pogues.search.service.SearchService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
public class ColecticaSourceImporterImpl implements ColecticaSourceImporter {

    private final static Logger logger = LogManager.getLogger(ColecticaSourceImporter.class);

    @Autowired
    MetadataService metadataService;

    @Autowired
    SearchService searchService;

    List<String> rootIds;

    @PostConstruct
    public void setUp() throws Exception {
        try {
            rootIds = metadataService.getGroupIds();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }


    @Override
    public void source() throws Exception {
        for (String id : rootIds) {
            logger.debug("Getting data from colectica API for root id " + id);
            PoguesItem r = metadataService.getDDIRoot(id);
            logger.debug("Root contains " + r.getChildren().size() + " groups");
            for(PoguesItem g: r.getChildren()) {
                System.out.println(g);
                searchService.save("group", g);
                saveSeries(g.getChildren());
            }
        }
    }

    public void saveSeries(List<PoguesItem> subGroups) throws Exception {
        for (PoguesItem s : subGroups) {
            searchService.save("sub-group", s);
            saveOperations(s.getChildren());
        }
    }

    public void saveOperations(List<PoguesItem> studyUnits) throws Exception {
        for (PoguesItem o : studyUnits) {
            searchService.save("study-unit", o);
            saveDataCollections(o.getChildren());
        }
    }

    public void saveDataCollections(List<PoguesItem> dataCollections) throws Exception {
        for (PoguesItem dc : dataCollections) {
            searchService.save("data-collection", dc);
            saveInstrumentSchemes(dc.getChildren());
        }
    }

    public void saveInstrumentSchemes(List<PoguesItem> instrumentSchemes) throws Exception {
        for (PoguesItem i : instrumentSchemes) {
            searchService.save("instrument-scheme", i);
            saveQuestionnaires(i.getChildren());
        }
    }

    public void saveQuestionnaires(List<PoguesItem> instruments) throws Exception {
        for (PoguesItem q : instruments) {
            searchService.save("instrument", q);
        }
    }


}
