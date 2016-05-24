package squids.models;

import squids.measure.Measure;

public abstract class Issue {
	private String type;
	private String qualityCharacteristic;
	private String message;
	private Measure measure;

	public Issue(Measure measure) {
		this(measure, "");
	}

	public Issue(Measure measure, String message) {
		this.type = measure.getMeasureElement();
		this.qualityCharacteristic = measure.getQualityCharacteristic();
		this.message = message;
		this.measure = measure;
	}

	public String getMeasureElement() {
		return this.type;
	}

	public String getQualityCharacteristic() {
		return this.qualityCharacteristic;
	}

	public String getMessage() {
		return this.message;
	}

	public Measure getMeasure() {
		return this.measure;
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
