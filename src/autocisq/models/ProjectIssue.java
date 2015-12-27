package autocisq.models;

public class ProjectIssue extends Issue {

	public ProjectIssue(String type) {
		super(type);
	}

	@Override
	public String getID() {
		return getType();
	}

}
