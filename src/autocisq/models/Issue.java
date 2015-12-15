package autocisq.models;

public abstract class Issue {
	private String type;

	public Issue(String type) {
		this.type = type;
	}

	public String getType() {
		return this.type;
	}
}
