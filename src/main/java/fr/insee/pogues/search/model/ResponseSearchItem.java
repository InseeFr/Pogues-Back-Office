package fr.insee.pogues.search.model;

public class ResponseSearchItem {

	private String id;

	private String name;

	private String title;

	private String version;

	private String serie;

	private String operation;

	private String campaign;

	public ResponseSearchItem() {
		super();
	}

	public ResponseSearchItem(String id, String title, String serie, String operation, String campaign) {

		this.id = id;
		this.name = id;
		this.version = "0";
		this.title = title;
		this.serie = serie;
		this.operation = operation;
		this.campaign = campaign;

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

	public String getSerie() {
		return serie;
	}

	public void setSerie(String serie) {
		this.serie = serie;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getCampaign() {
		return campaign;
	}

	public void setCampaign(String campaign) {
		this.campaign = campaign;
	}

}
