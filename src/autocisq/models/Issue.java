package autocisq.models;

public abstract class Issue {
	private String type;

	public Issue(String type) {
		this.type = type;
	}

	public String getType() {
		return this.type;
	}

	public abstract String getID();

	@Override
	public boolean equals(Object other) {
		if (other instanceof Issue) {
			Issue otherIssue = (Issue) other;
			if (otherIssue.getID().equals(getID()) && otherIssue.type.equals(this.type)) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	
	@Override
	public String toString() {
		return this.type;
	}
}
