package fr.insee.pogues.metadata.service;

import fr.insee.pogues.metadata.model.ColecticaItem;
import fr.insee.pogues.metadata.model.ColecticaItemRefList;
import fr.insee.pogues.metadata.repository.GroupRepository;
import fr.insee.pogues.metadata.repository.MetadataRepository;
import fr.insee.pogues.metadata.utils.XpathProcessor;
import fr.insee.pogues.search.model.*;
import fr.insee.pogues.utils.ddi.DDIDocumentBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MetadataServiceImpl implements MetadataService {

    @Autowired
    MetadataRepository metadataRepository;

    @Autowired
    GroupRepository groupRepository;

    @Autowired
    XpathProcessor xpathProcessor;

    @Override
    public ColecticaItem getItem(String id) throws Exception {
        return metadataRepository.findById(id);
    }

    @Override
    public ColecticaItemRefList getChildrenRef(String id) throws Exception {
        return metadataRepository.getChildrenRef(id);
    }

    @Override
    public List<ColecticaItem> getItems(ColecticaItemRefList refs) throws Exception {
        return metadataRepository.getItems(refs);
    }

    @Override
    public String getDDIDocument(String id) throws Exception {
        List<ColecticaItem> items = getItems(getChildrenRef(id));
        Map<String, String> refs = items.stream()
                .collect(Collectors.toMap(ColecticaItem::getIdentifier, item -> {
                    try {
                       return xpathProcessor.queryString(item.getItem(), "/Fragment/*");
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }));
        return new DDIDocumentBuilder().build(id, refs).toString();
    }

    public Group getGroup(String id) throws Exception {
        Group group = new Group();
        String fragment = getItem(id).item;
        String groupExp = "//*[local-name()='Group']";
        String labelExp = "//*[local-name()='Citation']/*[local-name()='Title']/*[local-name()='String']/text()";
        Node rootNode = xpathProcessor.queryList(fragment, groupExp).item(0);
        group.setId(id);
        group.setLabel(xpathProcessor.queryText(rootNode, labelExp));
        group.setSeries(getSeries(rootNode, group));
        return group;
    }

    @Override
    public List<String> getGroupIds() throws Exception {
        return groupRepository.getRootIds();
    }

    private List<Series> getSeries(Node node, Group group) throws Exception {
        List<Series> seriesList = new ArrayList<>();
        String childExp = ".//*[local-name()='SubGroupReference']";
        NodeList children = xpathProcessor.queryList(node, childExp);
        for (int i = 0; i < children.getLength(); i++) {
            String id = xpathProcessor.queryText(children.item(i), ".//*[local-name()='ID']/text()");
            String fragment = getItem(id).item;
            Node child = xpathProcessor.toDocument(fragment);
            Series series = new Series();
            series.setId(id);
            series.setParent(group.getId());
            series.setLabel(xpathProcessor.queryText(child, ".//*[local-name()='SubGroup']/*[local-name()='Citation']/*[local-name()='Title']/*[local-name()='String']/text()"));
            series.setOperations(getOperations(child, series));
            seriesList.add(series);
        }
        return seriesList;
    }

    private List<Operation> getOperations(Node node, Series series) throws Exception {
        List<Operation> operations = new ArrayList<>();
        String childExp = ".//*[local-name()='StudyUnitReference']";
        NodeList children = xpathProcessor.queryList(node, childExp);
        for (int i = 0; i < children.getLength(); i++) {
            String id = xpathProcessor.queryText(children.item(i), ".//*[local-name()='ID']/text()");
            String fragment = getItem(id).item;
            Node child = xpathProcessor.toDocument(fragment);
            Operation operation = new Operation();
            operation.setId(id);
            operation.setParent(series.getId());
            operation.setLabel(xpathProcessor.queryText(child, ".//*[local-name()='StudyUnit']/*[local-name()='Citation']/*[local-name()='Title']/*[local-name()='String']/text()"));
            operation.setDataCollections(getDataCollections(child, operation));
            operations.add(operation);
        }
        return operations;
    }

    private List<DataCollection> getDataCollections(Node node, Operation operation) throws Exception {
        List<DataCollection> dataCollections = new ArrayList<>();
        String childExp = ".//*[local-name()='DataCollectionReference']";
        NodeList children = xpathProcessor.queryList(node, childExp);
        for (int i = 0; i < children.getLength(); i++) {
            String id = xpathProcessor.queryText(children.item(i), ".//*[local-name()='ID']/text()");
            String fragment = getItem(id).item;
            Node child = xpathProcessor.toDocument(fragment);
            DataCollection dataCollection = new DataCollection();
            dataCollection.setId(id);
            dataCollection.setParent(operation.getId());
            dataCollection.setLabel(xpathProcessor.queryText(child, ".//*[local-name()='DataCollection']/*[local-name()='Label']/*[local-name()='Content']/text()"));
            dataCollections.add(dataCollection);
        }
        return dataCollections;
    }

    private List<Questionnaire> getQuestionnaires(Node node, DataCollection dataCollection) throws Exception {
        List<Questionnaire> questionnaires = new ArrayList<>();
        String childExp = ".//*[local-name()='InstrumentSchemeReference']";
        NodeList children = xpathProcessor.queryList(node, childExp);
        for (int i = 0; i < children.getLength(); i++) {
            String id = xpathProcessor.queryText(children.item(i), ".//*[local-name()='ID']/text()");
            String fragment = getItem(id).item;
            Node child = xpathProcessor.toDocument(fragment);
            Questionnaire questionnaire = new Questionnaire();
            questionnaire.setId(id);
            questionnaire.setParent(dataCollection.getId());
            questionnaire.setLabel(xpathProcessor.queryText(child, ".//*[local-name()='DataCollection']/*[local-name()='Label']/*[local-name()='Content']/text()"));
            questionnaires.add(questionnaire);
        }
        return questionnaires;
    }
}
