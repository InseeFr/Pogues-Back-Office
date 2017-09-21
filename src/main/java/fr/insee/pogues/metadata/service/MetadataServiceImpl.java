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
        ResourcePackage resourcePackage = getResourcePackage("5696739a-751c-4603-a0bc-e4c05bd41c83");
        return new DDIDocumentBuilder()
                .buildResourcePackageDocument(resourcePackage.getId(), resourcePackage.getReferences())
                .buildItemDocument(id, refs)
                .build()
                .toString();
    }

    public ResourcePackage getResourcePackage(String groupId) throws Exception {
        String fragment = getItem(groupId).item;
        String resourcePackageExp = ".//*[local-name()='ResourcePackageReference']";
        Node rootNode = xpathProcessor.queryList(fragment, resourcePackageExp).item(0);
        String id = xpathProcessor.queryText(rootNode, ".//*[local-name()='ID']/text()");
        ResourcePackage resourcePackage = new ResourcePackage(id);
        List<ColecticaItem> items = getItems(getChildrenRef(id));
        Map<String, String> refs = items.stream()
                .collect(Collectors.toMap(ColecticaItem::getIdentifier, item -> {
                    try {
                        return xpathProcessor.queryString(item.getItem(), "/Fragment/*");
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }));
        resourcePackage.setReferences(refs);
        return resourcePackage;
    }

    public Group getGroup(String id) throws Exception {
        Group group = new Group();
        String fragment = getItem(id).item;
        String groupExp = "//*[local-name()='Group']";
        String labelExp = "//*[local-name()='Citation']/*[local-name()='Title']/*[local-name()='String']/text()";
        Node rootNode = xpathProcessor.queryList(fragment, groupExp).item(0);
        group.setId(id);
        group.setLabel(xpathProcessor.queryText(rootNode, labelExp));
        group.setSubGroups(getSubGroups(rootNode, group));
        return group;
    }

    @Override
    public List<String> getGroupIds() throws Exception {
        return groupRepository.getRootIds();
    }

    private List<SubGroup> getSubGroups(Node node, Group group) throws Exception {
        List<SubGroup> subGroups = new ArrayList<>();
        String childExp = ".//*[local-name()='SubGroupReference']";
        NodeList children = xpathProcessor.queryList(node, childExp);
        for (int i = 0; i < children.getLength(); i++) {
            String id = xpathProcessor.queryText(children.item(i), ".//*[local-name()='ID']/text()");
            String fragment = getItem(id).item;
            Node child = xpathProcessor.toDocument(fragment);
            SubGroup subGroup = new SubGroup();
            subGroup.setId(id);
            subGroup.setParent(group.getId());
            subGroup.setLabel(xpathProcessor.queryText(child, ".//*[local-name()='SubGroup']/*[local-name()='Citation']/*[local-name()='Title']/*[local-name()='String']/text()"));
            subGroup.setStudyUnits(getStudyUnits(child, subGroup));
            subGroups.add(subGroup);
        }
        return subGroups;
    }

    private List<StudyUnit> getStudyUnits(Node node, SubGroup subGroup) throws Exception {
        List<StudyUnit> studyUnits = new ArrayList<>();
        String childExp = ".//*[local-name()='StudyUnitReference']";
        NodeList children = xpathProcessor.queryList(node, childExp);
        for (int i = 0; i < children.getLength(); i++) {
            String id = xpathProcessor.queryText(children.item(i), ".//*[local-name()='ID']/text()");
            String fragment = getItem(id).item;
            Node child = xpathProcessor.toDocument(fragment);
            StudyUnit studyUnit = new StudyUnit();
            studyUnit.setSubGroupId(id);
            studyUnit.setId(id);
            studyUnit.setParent(subGroup.getId());
            studyUnit.setLabel(xpathProcessor.queryText(child, ".//*[local-name()='StudyUnit']/*[local-name()='Citation']/*[local-name()='Title']/*[local-name()='String']/text()"));
            studyUnit.setDataCollections(getDataCollections(child, studyUnit, subGroup));
            studyUnits.add(studyUnit);
        }
        return studyUnits;
    }

    private List<DataCollection> getDataCollections(Node node, StudyUnit studyUnit, SubGroup subGroup) throws Exception {
        List<DataCollection> dataCollections = new ArrayList<>();
        String childExp = ".//*[local-name()='DataCollectionReference']";
        NodeList children = xpathProcessor.queryList(node, childExp);
        for (int i = 0; i < children.getLength(); i++) {
            String id = xpathProcessor.queryText(children.item(i), ".//*[local-name()='ID']/text()");
            String fragment = getItem(id).item;
            Node child = xpathProcessor.toDocument(fragment);
            DataCollection dataCollection = new DataCollection();
            dataCollection.setId(id);
            dataCollection.setSubGroupId(subGroup.getId());
            dataCollection.setStudyUnitId(studyUnit.getId());
            dataCollection.setParent(studyUnit.getId());
            dataCollection.setLabel(xpathProcessor.queryText(child, ".//*[local-name()='DataCollection']/*[local-name()='Label']/*[local-name()='Content']/text()"));
            dataCollections.add(dataCollection);
            dataCollection.setInstrumentSchemes(getInstrumentSchemes(child, dataCollection, studyUnit, subGroup));
        }
        return dataCollections;
    }

    private List<InstrumentScheme> getInstrumentSchemes(Node node, DataCollection dataCollection, StudyUnit studyUnit, SubGroup subGroup) throws Exception {
        List<InstrumentScheme>  instrumentSchemes= new ArrayList<>();
        String childExp = ".//*[local-name()='InstrumentSchemeReference']";
        NodeList children = xpathProcessor.queryList(node, childExp);
        for (int i = 0; i < children.getLength(); i++) {
            String id = xpathProcessor.queryText(children.item(i), ".//*[local-name()='ID']/text()");
            String fragment = getItem(id).item;
            Node child = xpathProcessor.toDocument(fragment);
            InstrumentScheme instrumentScheme = new InstrumentScheme();
            instrumentScheme.setId(id);
            instrumentScheme.setParent(dataCollection.getId());
            instrumentSchemes.add(instrumentScheme);
            instrumentScheme.setInstruments(getInstruments(child, instrumentScheme, dataCollection, studyUnit, subGroup));
        }
        return instrumentSchemes;
    }

    private List<Instrument> getInstruments(Node node, InstrumentScheme instrumentScheme, DataCollection dataCollection, StudyUnit studyUnit, SubGroup subGroup) throws Exception {
        List<Instrument> instruments = new ArrayList<>();
        String childExp = ".//*[local-name()='InstrumentReference']";
        NodeList children = xpathProcessor.queryList(node, childExp);
        for (int i = 0; i < children.getLength(); i++) {
            String id = xpathProcessor.queryText(children.item(i), ".//*[local-name()='ID']/text()");
            String fragment = getItem(id).item;
            Node child = xpathProcessor.toDocument(fragment);
            Instrument instrument = new Instrument();
            instrument.setId(id);
            instrument.setParent(instrumentScheme.getId());
            instrument.setDataCollectionId(dataCollection.getId());
            instrument.setSubGroupId(subGroup.getId());
            instrument.setStudyUnitId(studyUnit.getId());
            instrument.setLabel(xpathProcessor.queryText(child, ".//*[local-name()='Instrument']/*[local-name()='Label']/*[local-name()='Content']/text()"));
            instruments.add(instrument);
        }
        return instruments;
    }
}
