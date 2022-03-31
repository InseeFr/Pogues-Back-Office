package fr.insee.pogues.metadata;

import static org.mockito.Mockito.spy;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import fr.insee.pogues.metadata.repository.MetadataRepository;
import fr.insee.pogues.metadata.service.MetadataService;
import fr.insee.pogues.metadata.service.MetadataServiceImpl;

class TestMetadataService {

    @Mock
    MetadataRepository metadataRepository;
    @InjectMocks
    MetadataService metadataService;

    private static Stream<JSONObject> arrayToStream(Object jsonArray) {
        return StreamSupport.stream(((JSONArray) jsonArray).spliterator(), false);
    }

    @BeforeEach
    public void beforeEach() {
        metadataService = spy(new MetadataServiceImpl());
        initMocks(this);
    }

    @Test
    void getGroupTest() throws Exception {
//        mockFindByIdResponse("99c6b5c5-e591-4e64-af1b-f7e24970e20e");
//        mockFindByIdResponse("391e505c-dc05-4042-8b9d-c602ff72690d");
//        mockFindByIdResponse("bd18e047-560c-49ff-9c59-d620164e5f95");
//        mockFindByIdResponse("c696f64b-ce55-4f6b-9f86-f84ee82ad685");
//        mockFindByIdResponse("f03ea418-6f79-4cf8-8592-8546062016a9");
//        mockFindByIdResponse("f6a921fb-421b-4bd7-a0c6-233d3665c2c2");
//        mockFindByIdResponse("a18c2085-d44d-4544-a43a-c1b1499b5646");
//        mockFindByIdResponse("9e7c9483-f8a7-4ea1-abe1-3c37e2688dc9");
//        mockXpathProcessorQueries();
//        Group group = metadataService.getGroup("391e505c-dc05-4042-8b9d-c602ff72690d");
//        Assert.assertEquals("ANTIPOL", group.getLabel());
//        Assert.assertEquals("391e505c-dc05-4042-8b9d-c602ff72690d", group.getId());
//        SubGroup subGroup = group.getSubGroups().get(0);
//        Assert.assertEquals("Investissements et dépenses courantes pour protéger l'environnement", subGroup.getLabel());
//        Assert.assertEquals("bd18e047-560c-49ff-9c59-d620164e5f95", subGroup.getId());
//        StudyUnit studyUnit = subGroup.getStudyUnits().get(0);
//        Assert.assertEquals("Investissements et dépenses courantes pour protéger l'environnement 2016", studyUnit.getLabel());
//        Assert.assertEquals("a18c2085-d44d-4544-a43a-c1b1499b5646", studyUnit.getId());
//        DataCollection dataCollection = studyUnit.getDataCollections().get(0);
//        Assert.assertEquals("Investissements et dépenses courantes pour protéger l'environnement 2016", dataCollection.getLabel());
//        Assert.assertEquals("99c6b5c5-e591-4e64-af1b-f7e24970e20e", dataCollection.getId());
    }

    private void mockXpathProcessorQueries() throws Exception {

//        Node fN = Mockito.mock(Node.class);
//        NodeList groupList = new NodeList() {
//            @Override
//            public Node item(int i) {
//                return fN;
//            }
//
//            @Override
//            public int getLength() {
//                return 1;
//            }
//        };
//        Node sN = Mockito.mock(Node.class);
//        NodeList subGroupList = new NodeList() {
//            @Override
//            public Node item(int i) {
//                return sN;
//            }
//
//            @Override
//            public int getLength() {
//                return 1;
//            }
//        };
//        Node dcN = Mockito.mock(Node.class);
//        NodeList dataCollectionList = new NodeList() {
//            @Override
//            public Node item(int i) {
//                return dcN;
//            }
//
//            @Override
//            public int getLength() {
//                return 1;
//            }
//        };
//        Node oN = Mockito.mock(Node.class);
//        NodeList studyUnitList = new NodeList() {
//            @Override
//            public Node item(int i) {
//                return oN;
//            }
//
//            @Override
//            public int getLength() {
//                return 1;
//            }
//        };
//        Node isN = Mockito.mock(Node.class);
//        NodeList instrumentSchemeList = new NodeList() {
//            @Override
//            public Node item(int i) {
//                return isN;
//            }
//
//            @Override
//            public int getLength() {
//                return 1;
//            }
//        };
//        Node iN = Mockito.mock(Node.class);
//        NodeList instrumentList = new NodeList() {
//            @Override
//            public Node item(int i) {
//                return iN;
//            }
//
//            @Override
//            public int getLength() {
//                return 1;
//            }
//        };
//        when(xpathProcessor.queryList((String) any(), eq("//*[local-name()='Group']")))
//                .thenReturn(groupList);
//        when(xpathProcessor.queryText(fN, "//*[local-name()='Citation']/*[local-name()='Title']/*[local-name()='String']/text()"))
//                .thenReturn("ANTIPOL");
//        when(xpathProcessor.queryList((Node) any(), eq(".//*[local-name()='SubGroupReference']")))
//                .thenReturn(subGroupList);
//        when(xpathProcessor.queryText((Node) any(), eq(".//*[local-name()='SubGroup']/*[local-name()='Citation']/*[local-name()='Title']/*[local-name()='String']/text()")))
//                .thenReturn("Investissements et dépenses courantes pour protéger l'environnement");
//        when(xpathProcessor.queryText(dcN, ".//*[local-name()='ID']/text()"))
//                .thenReturn("bd18e047-560c-49ff-9c59-d620164e5f95");
//        when(xpathProcessor.queryList((Node) any(), eq(".//*[local-name()='DataCollectionReference']")))
//                .thenReturn(dataCollectionList);
//        when(xpathProcessor.queryText((Node) any(), eq(".//*[local-name()='StudyUnit']/*[local-name()='Citation']/*[local-name()='Title']/*[local-name()='String']/text()")))
//                .thenReturn("Investissements et dépenses courantes pour protéger l'environnement 2016");
//        when(xpathProcessor.queryText(sN, ".//*[local-name()='ID']/text()"))
//                .thenReturn("bd18e047-560c-49ff-9c59-d620164e5f95");
//        when(xpathProcessor.queryList((Node) any(), eq(".//*[local-name()='StudyUnitReference']")))
//                .thenReturn(studyUnitList);
//        when(xpathProcessor.queryText(oN, ".//*[local-name()='ID']/text()"))
//                .thenReturn("a18c2085-d44d-4544-a43a-c1b1499b5646");
//        when(xpathProcessor.queryList((Node) any(), eq( ".//*[local-name()='InstrumentSchemeReference']")))
//                .thenReturn(instrumentSchemeList);
//        when(xpathProcessor.queryText(isN, ".//*[local-name()='ID']/text()"))
//                .thenReturn("a18c2085-d44d-4544-a43a-c1b1499b5646");
//        when(xpathProcessor.queryList((Node) any(), eq(".//*[local-name()='InstrumentReference']")))
//                .thenReturn(instrumentList);
//        when(xpathProcessor.queryText(iN, ".//*[local-name()='ID']/text()"))
//                .thenReturn("99c6b5c5-e591-4e64-af1b-f7e24970e20e");
//        when(xpathProcessor.queryText((Node) any(), eq(".//*[local-name()='Instrument']/*[local-name()='Label']/*[local-name()='Content']/text()")))
//                .thenReturn("Investissements et dépenses courantes pour protéger l'environnement 2016");
    }

}
