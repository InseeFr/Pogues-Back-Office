package fr.insee.pogues.search.model;

public class ResponseSearchItem {

	private String id;

	private String name;

	private String title;

	private String version;

	private String subgroupId;

	private String studyUnitId;

	private String dataCollectionId;

	private String type;

	public ResponseSearchItem() {
		super();
	}

	public ResponseSearchItem(String id, String title, String serie, String operation, String campaign) {

		this.id = id;
		this.name = id;
		this.version = "0";
		this.title = title;
		this.subgroupId = serie;
		this.studyUnitId = operation;
		this.dataCollectionId = campaign;
		this.type = "Instrument";

	}
	
	public ResponseSearchItem(String id, String title, String serie, String operation) {

		this.id = id;
		this.name = id;
		this.version = "0";
		this.title = title;
		this.subgroupId = serie;
		this.studyUnitId = operation;
		this.type = "CodeList";

	}
	

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getSubgroupId() {
		return subgroupId;
	}

	public void setSubgroupId(String subgroupId) {
		this.subgroupId = subgroupId;
	}

	public String getStudyUnitId() {
		return studyUnitId;
	}

	public void setStudyUnitId(String studyUnitId) {
		this.studyUnitId = studyUnitId;
	}

	public String getDataCollectionId() {
		return dataCollectionId;
	}

	public void setDataCollectionId(String dataCollectionId) {
		this.dataCollectionId = dataCollectionId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
