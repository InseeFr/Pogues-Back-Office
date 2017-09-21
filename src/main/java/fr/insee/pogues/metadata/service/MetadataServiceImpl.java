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
    public List<String> getGroupIds() throws Exception {
        return groupRepository.getRootIds();
    }

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

    public PoguesItem getDDIRoot(String id) throws Exception {
        PoguesItem ddiRoot = new PoguesItem();
        String fragment = getItem(id).item;
        String rootExp = "//*[local-name()='DDIInstance']";
        String labelExp = "//*[local-name()='Citation']/*[local-name()='Title']/*[local-name()='String']/text()";
        Node rootNode = xpathProcessor.queryList(fragment, rootExp).item(0);
        ddiRoot.setId(id);
        ddiRoot.setLabel(xpathProcessor.queryText(rootNode, labelExp));
        ddiRoot.setResourcePackageId(getResourcePackageId(rootNode));
        ddiRoot.setChildren(getGroups(rootNode, ddiRoot));
        return ddiRoot;
    }

    public String getResourcePackageId(Node rootNode) throws Exception {
        String childExp = ".//*[local-name()='ResourcePackageReference']";
        Node rpNode = xpathProcessor.queryList(rootNode, childExp).item(0);
        return xpathProcessor.queryText(rpNode, ".//*[local-name()='ID']/text()");
    }

    public List<PoguesItem> getGroups(Node node, PoguesItem ddiRoot) throws Exception {
        List<PoguesItem> groups = new ArrayList<>();
        String childExp = ".//*[local-name()='GroupReference']";
        NodeList children = xpathProcessor.queryList(node, childExp);
        for (int i = 0; i < children.getLength(); i++) {
            String id = xpathProcessor.queryText(children.item(i), ".//*[local-name()='ID']/text()");
            String fragment = getItem(id).item;
            Node child = xpathProcessor.toDocument(fragment);
            PoguesItem group = new PoguesItem();
            group.setId(id);
            group.setLabel(xpathProcessor.queryText(child, ".//*[local-name()='Group']/*[local-name()='Citation']/*[local-name()='Title']/*[local-name()='String']/text()"));
            group.setParent(ddiRoot.getId());
            group.setResourcePackageId(ddiRoot.getResourcePackageId());
            group.setChildren(getSubGroups(child, group));
            groups.add(group);
        }
        return groups;
    }

    private List<PoguesItem> getSubGroups(Node node, PoguesItem group) throws Exception {
        List<PoguesItem> subGroups = new ArrayList<>();
        String childExp = ".//*[local-name()='SubGroupReference']";
        NodeList children = xpathProcessor.queryList(node, childExp);
        for (int i = 0; i < children.getLength(); i++) {
            String id = xpathProcessor.queryText(children.item(i), ".//*[local-name()='ID']/text()");
            String fragment = getItem(id).item;
            Node child = xpathProcessor.toDocument(fragment);
            PoguesItem subGroup = new PoguesItem();
            subGroup.setGroupId(group.getId());
            subGroup.setId(id);
            subGroup.setSubGroupId(id);
            subGroup.setResourcePackageId(group.getResourcePackageId());
            subGroup.setParent(group.getId());
            subGroup.setLabel(xpathProcessor.queryText(child, ".//*[local-name()='SubGroup']/*[local-name()='Citation']/*[local-name()='Title']/*[local-name()='String']/text()"));
            subGroup.setChildren(getStudyUnits(child, subGroup));
            subGroups.add(subGroup);
        }
        return subGroups;
    }

    private List<PoguesItem> getStudyUnits(Node node, PoguesItem subGroup) throws Exception {
        List<PoguesItem> studyUnits = new ArrayList<>();
        String childExp = ".//*[local-name()='StudyUnitReference']";
        NodeList children = xpathProcessor.queryList(node, childExp);
        for (int i = 0; i < children.getLength(); i++) {
            String id = xpathProcessor.queryText(children.item(i), ".//*[local-name()='ID']/text()");
            String fragment = getItem(id).item;
            Node child = xpathProcessor.toDocument(fragment);
            PoguesItem studyUnit = new PoguesItem();
            studyUnit.setGroupId(subGroup.getGroupId());
            studyUnit.setSubGroupId(id);
            studyUnit.setId(id);
            studyUnit.setStudyUnitId(id);
            studyUnit.setResourcePackageId(subGroup.getResourcePackageId());
            studyUnit.setParent(subGroup.getId());
            studyUnit.setLabel(xpathProcessor.queryText(child, ".//*[local-name()='StudyUnit']/*[local-name()='Citation']/*[local-name()='Title']/*[local-name()='String']/text()"));
            studyUnit.setChildren(getDataCollections(child, studyUnit));
            studyUnits.add(studyUnit);
        }
        return studyUnits;
    }

    private List<PoguesItem> getDataCollections(Node node, PoguesItem studyUnit) throws Exception {
        List<PoguesItem> dataCollections = new ArrayList<>();
        String childExp = ".//*[local-name()='DataCollectionReference']";
        NodeList children = xpathProcessor.queryList(node, childExp);
        for (int i = 0; i < children.getLength(); i++) {
            String id = xpathProcessor.queryText(children.item(i), ".//*[local-name()='ID']/text()");
            String fragment = getItem(id).item;
            Node child = xpathProcessor.toDocument(fragment);
            PoguesItem dataCollection = new PoguesItem();
            dataCollection.setId(id);
            dataCollection.setDataCollectionId(id);
            dataCollection.setGroupId(studyUnit.getId());
            dataCollection.setSubGroupId(studyUnit.getId());
            dataCollection.setStudyUnitId(studyUnit.getId());
            dataCollection.setResourcePackageId(studyUnit.getResourcePackageId());
            dataCollection.setParent(studyUnit.getId());
            dataCollection.setLabel(xpathProcessor.queryText(child, ".//*[local-name()='DataCollection']/*[local-name()='Label']/*[local-name()='Content']/text()"));
            dataCollections.add(dataCollection);
            dataCollection.setChildren(getInstrumentSchemes(child, dataCollection));
        }
        return dataCollections;
    }

    private List<PoguesItem> getInstrumentSchemes(Node node, PoguesItem dataCollection) throws Exception {
        List<PoguesItem>  instrumentSchemes= new ArrayList<>();
        String childExp = ".//*[local-name()='InstrumentSchemeReference']";
        NodeList children = xpathProcessor.queryList(node, childExp);
        for (int i = 0; i < children.getLength(); i++) {
            String id = xpathProcessor.queryText(children.item(i), ".//*[local-name()='ID']/text()");
            String fragment = getItem(id).item;
            Node child = xpathProcessor.toDocument(fragment);
            PoguesItem instrumentScheme = new PoguesItem();
            instrumentScheme.setId(id);
            instrumentScheme.setParent(dataCollection.getId());
            instrumentScheme.setGroupId(dataCollection.getGroupId());
            instrumentScheme.setSubGroupId(dataCollection.getSubGroupId());
            instrumentScheme.setStudyUnitId(dataCollection.getStudyUnitId());
            instrumentScheme.setDataCollectionId(dataCollection.getId());
            instrumentScheme.setResourcePackageId(dataCollection.getResourcePackageId());
            instrumentSchemes.add(instrumentScheme);
            instrumentScheme.setChildren(getInstruments(child, instrumentScheme));
        }
        return instrumentSchemes;
    }

    private List<PoguesItem> getInstruments(Node node, PoguesItem instrumentScheme) throws Exception {
        List<PoguesItem> instruments = new ArrayList<>();
        String childExp = ".//*[local-name()='InstrumentReference']";
        NodeList children = xpathProcessor.queryList(node, childExp);
        for (int i = 0; i < children.getLength(); i++) {
            String id = xpathProcessor.queryText(children.item(i), ".//*[local-name()='ID']/text()");
            String fragment = getItem(id).item;
            Node child = xpathProcessor.toDocument(fragment);
            PoguesItem instrument = new PoguesItem();
            instrument.setId(id);
            instrument.setParent(instrumentScheme.getId());
            instrument.setDataCollectionId(instrumentScheme.getDataCollectionId());
            instrument.setStudyUnitId(instrument.getStudyUnitId());
            instrument.setSubGroupId(instrumentScheme.getSubGroupId());
            instrument.setGroupId(instrumentScheme.getGroupId());
            instrument.setResourcePackageId(instrumentScheme.getResourcePackageId());
            instrument.setLabel(xpathProcessor.queryText(child, ".//*[local-name()='Instrument']/*[local-name()='Label']/*[local-name()='Content']/text()"));
            instruments.add(instrument);
        }
        return instruments;
    }

    @Override
    public String getDDIDocument(String itemId, String resourcePackageId) throws Exception {
        List<ColecticaItem> items = getItems(getChildrenRef(itemId));
        Map<String, String> refs = items.stream()
                .filter(item -> null != item)
                .collect(Collectors.toMap(ColecticaItem::getIdentifier, item -> {
                    try {
                        return xpathProcessor.queryString(item.getItem(), "/Fragment/*");
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }));
        ResourcePackage resourcePackage = getResourcePackage(resourcePackageId);
        return new DDIDocumentBuilder()
                .buildResourcePackageDocument(resourcePackage.getId(), resourcePackage.getReferences())
                .buildItemDocument(itemId, refs)
                .build()
                .toString();
    }

    public ResourcePackage getResourcePackage(String id) throws Exception {
        ResourcePackage resourcePackage = new ResourcePackage(id);
        List<ColecticaItem> items = getItems(getChildrenRef(id));
        Map<String, String> refs = items.stream()
                .filter(item -> null != item)
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
}
