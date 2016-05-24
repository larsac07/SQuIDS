package squids.models;

import squids.measure.Measure;

public class ProjectIssue extends Issue {

	public ProjectIssue(Measure measure) {
		this(measure, "");
	}

	public ProjectIssue(Measure measure, String message) {
		super(measure, message);
	}

	@Override
	public String getID() {
		return getMeasureElement();
	}

}
