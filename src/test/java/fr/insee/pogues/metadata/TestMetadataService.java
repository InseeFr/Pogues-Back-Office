package fr.insee.pogues.metadata;

import fr.insee.pogues.metadata.mock.ColecticaMocks;
import fr.insee.pogues.metadata.repository.MetadataRepository;
import fr.insee.pogues.metadata.service.MetadataService;
import fr.insee.pogues.metadata.service.MetadataServiceImpl;
import fr.insee.pogues.metadata.utils.XpathProcessor;
import fr.insee.pogues.search.model.Family;
import fr.insee.pogues.search.model.Operation;
import fr.insee.pogues.search.model.Questionnaire;
import fr.insee.pogues.search.model.Series;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class TestMetadataService {

    @Mock
    MetadataRepository metadataRepository;

    @Mock
    XpathProcessor xpathProcessor;

    @InjectMocks
    MetadataService metadataService;

    JSONObject db;

    private static Stream<JSONObject> arrayToStream(Object jsonArray) {
        return StreamSupport.stream(((JSONArray) jsonArray).spliterator(), false);
    }

    @Before
    public void beforeEach() {
        metadataService = spy(new MetadataServiceImpl());
        db = ColecticaMocks.createDb();
        initMocks(this);
    }

    @Test
    public void getFamilyTest() throws Exception {
        mockFindByIdResponse("99c6b5c5-e591-4e64-af1b-f7e24970e20e");
        mockFindByIdResponse("391e505c-dc05-4042-8b9d-c602ff72690d");
        mockFindByIdResponse("bd18e047-560c-49ff-9c59-d620164e5f95");
        mockFindByIdResponse("c696f64b-ce55-4f6b-9f86-f84ee82ad685");
        mockFindByIdResponse("f03ea418-6f79-4cf8-8592-8546062016a9");
        mockFindByIdResponse("f6a921fb-421b-4bd7-a0c6-233d3665c2c2");
        mockFindByIdResponse("a18c2085-d44d-4544-a43a-c1b1499b5646");
        mockFindByIdResponse("9e7c9483-f8a7-4ea1-abe1-3c37e2688dc9");
        mockXpathProcessorQueries();
        Family family = metadataService.getFamily("391e505c-dc05-4042-8b9d-c602ff72690d");
        Assert.assertEquals("ANTIPOL", family.getLabel());
        Assert.assertEquals("391e505c-dc05-4042-8b9d-c602ff72690d", family.getId());
        Series series = family.getSeries().get(0);
        Assert.assertEquals("Investissements et dépenses courantes pour protéger l'environnement", series.getLabel());
        Assert.assertEquals("bd18e047-560c-49ff-9c59-d620164e5f95", series.getId());
        Operation operation = series.getOperations().get(0);
        Assert.assertEquals("Investissements et dépenses courantes pour protéger l'environnement 2016", operation.getLabel());
        Assert.assertEquals("a18c2085-d44d-4544-a43a-c1b1499b5646", operation.getId());
        Questionnaire questionnaire = operation.getQuestionnaires().get(0);
        Assert.assertEquals("Investissements et dépenses courantes pour protéger l'environnement 2016", questionnaire.getLabel());
        Assert.assertEquals("99c6b5c5-e591-4e64-af1b-f7e24970e20e", questionnaire.getId());
    }

    private void mockXpathProcessorQueries() throws Exception {

        Node fN = Mockito.mock(Node.class);
        NodeList familyList = new NodeList() {
            @Override
            public Node item(int i) {
                return fN;
            }

            @Override
            public int getLength() {
                return 1;
            }
        };
        Node sN = Mockito.mock(Node.class);
        NodeList seriesList = new NodeList() {
            @Override
            public Node item(int i) {
                return sN;
            }

            @Override
            public int getLength() {
                return 1;
            }
        };
        Node oN = Mockito.mock(Node.class);
        NodeList operationsList = new NodeList() {
            @Override
            public Node item(int i) {
                return oN;
            }

            @Override
            public int getLength() {
                return 1;
            }
        };
        Node qN = Mockito.mock(Node.class);
        NodeList questionnairesList = new NodeList() {
            @Override
            public Node item(int i) {
                return qN;
            }

            @Override
            public int getLength() {
                return 1;
            }
        };
        when(xpathProcessor.queryList((String) any(), eq("//*[local-name()='Group']")))
                .thenReturn(familyList);
        when(xpathProcessor.queryText(fN, "//*[local-name()='Citation']/*[local-name()='Title']/*[local-name()='String']/text()"))
                .thenReturn("ANTIPOL");
        when(xpathProcessor.queryList((Node) any(), eq(".//*[local-name()='SubGroupReference']")))
                .thenReturn(seriesList);
        when(xpathProcessor.queryText((Node) any(), eq(".//*[local-name()='SubGroup']/*[local-name()='Citation']/*[local-name()='Title']/*[local-name()='String']/text()")))
                .thenReturn("Investissements et dépenses courantes pour protéger l'environnement");
        when(xpathProcessor.queryText(sN, ".//*[local-name()='ID']/text()"))
                .thenReturn("bd18e047-560c-49ff-9c59-d620164e5f95");
        when(xpathProcessor.queryList((Node) any(), eq(".//*[local-name()='StudyUnitReference']")))
                .thenReturn(operationsList);
        when(xpathProcessor.queryText(oN, ".//*[local-name()='ID']/text()"))
                .thenReturn("a18c2085-d44d-4544-a43a-c1b1499b5646");
        when(xpathProcessor.queryText((Node) any(), eq(".//*[local-name()='StudyUnit']/*[local-name()='Citation']/*[local-name()='Title']/*[local-name()='String']/text()")))
                .thenReturn("Investissements et dépenses courantes pour protéger l'environnement 2016");
        when(xpathProcessor.queryList((Node) any(), eq(".//*[local-name()='DataCollectionReference']")))
                .thenReturn(questionnairesList);
        when(xpathProcessor.queryText(qN, ".//*[local-name()='ID']/text()"))
                .thenReturn("99c6b5c5-e591-4e64-af1b-f7e24970e20e");
        when(xpathProcessor.queryText((Node) any(), eq(".//*[local-name()='DataCollection']/*[local-name()='Label']/*[local-name()='Content']/text()")))
                .thenReturn("Investissements et dépenses courantes pour protéger l'environnement 2016");
    }

    private void mockFindByIdResponse(String id) throws Exception {
        when(metadataRepository.findById(id))
                .thenReturn(arrayToStream(db.get("items"))
                        .filter(item -> item.get("Identifier").toString().equals(id))
                        .findFirst()
                        .get());
    }
}
