package autocisq.models;

import autocisq.measure.Measure;

public class ProjectIssue extends Issue {

	public ProjectIssue(Measure measure) {
		super(measure);
	}

	@Override
	public String getID() {
		return getMeasureElement();
	}

}
