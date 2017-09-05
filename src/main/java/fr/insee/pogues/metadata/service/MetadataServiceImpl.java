package fr.insee.pogues.metadata.service;

import fr.insee.pogues.metadata.repository.FamilyRepository;
import fr.insee.pogues.metadata.repository.MetadataRepository;
import fr.insee.pogues.metadata.utils.XpathProcessor;
import fr.insee.pogues.search.model.Family;
import fr.insee.pogues.search.model.Operation;
import fr.insee.pogues.search.model.Questionnaire;
import fr.insee.pogues.search.model.Series;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

@Service
public class MetadataServiceImpl implements MetadataService {

    @Autowired
    MetadataRepository metadataRepository;

    @Autowired
    FamilyRepository familyRepository;

    @Autowired
    XpathProcessor xpathProcessor;

    @Override
    public JSONObject getItem(String id) throws Exception {
        try {
            return metadataRepository.findById(id);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public Family getFamily(String id) throws Exception {
        Family family = new Family();
        String fragment = getItem(id).get("Item").toString();
        String groupExp = "//*[local-name()='Group']";
        String labelExp = "//*[local-name()='Citation']/*[local-name()='Title']/*[local-name()='String']/text()";
        Node rootNode = xpathProcessor.queryList(fragment, groupExp).item(0);
        family.setId(id);
        family.setLabel(xpathProcessor.queryText(rootNode, labelExp));
        family.setSeries(getSeries(rootNode, family));
        return family;
    }

    @Override
    public List<String> getFamilyIds() throws Exception {
        return familyRepository.getRootIds();
    }

    public List<Series> getSeries(Node node, Family family) throws Exception {
        List<Series> seriesList = new ArrayList<>();
        String childExp = ".//*[local-name()='SubGroupReference']";
        NodeList children = xpathProcessor.queryList(node, childExp);
        for (int i = 0; i < children.getLength(); i++) {
            String id = xpathProcessor.queryText(children.item(i), ".//*[local-name()='ID']/text()");
            String fragment = getItem(id).get("Item").toString();
            Node child = xpathProcessor.toDocument(fragment);
            Series series = new Series();
            series.setId(id);
            series.setParent(family.getId());
            series.setLabel(xpathProcessor.queryText(child, ".//*[local-name()='SubGroup']/*[local-name()='Citation']/*[local-name()='Title']/*[local-name()='String']/text()"));
            series.setOperations(getOperations(child, series));
            seriesList.add(series);
        }
        return seriesList;
    }

    public List<Operation> getOperations(Node node, Series series) throws Exception {
        List<Operation> operations = new ArrayList<>();
        String childExp = ".//*[local-name()='StudyUnitReference']";
        NodeList children = xpathProcessor.queryList(node, childExp);
        for (int i = 0; i < children.getLength(); i++) {
            String id = xpathProcessor.queryText(children.item(i), ".//*[local-name()='ID']/text()");
            String fragment = getItem(id).get("Item").toString();
            Node child = xpathProcessor.toDocument(fragment);
            Operation operation = new Operation();
            operation.setId(id);
            operation.setParent(series.getId());
            operation.setLabel(xpathProcessor.queryText(child, ".//*[local-name()='StudyUnit']/*[local-name()='Citation']/*[local-name()='Title']/*[local-name()='String']/text()"));
            operation.setQuestionnaires(getQuestionnaires(child, operation));
            operations.add(operation);
        }
        return operations;
    }

    public List<Questionnaire> getQuestionnaires(Node node, Operation operation) throws Exception {
        List<Questionnaire> questionnaires = new ArrayList<>();
        String childExp = ".//*[local-name()='DataCollectionReference']";
        NodeList children = xpathProcessor.queryList(node, childExp);
        for (int i = 0; i < children.getLength(); i++) {
            String id = xpathProcessor.queryText(children.item(i), ".//*[local-name()='ID']/text()");
            String fragment = getItem(id).get("Item").toString();
            Node child = xpathProcessor.toDocument(fragment);
            Questionnaire questionnaire = new Questionnaire();
            questionnaire.setId(id);
            questionnaire.setParent(operation.getId());
            questionnaire.setLabel(xpathProcessor.queryText(child, ".//*[local-name()='DataCollection']/*[local-name()='Label']/*[local-name()='Content']/text()"));
            questionnaires.add(questionnaire);
        }
        return questionnaires;
    }
}
