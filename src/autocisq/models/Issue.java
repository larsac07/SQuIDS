package autocisq.models;

public class Issue {
	private int beginLine;
	private int startIndex;
	private int endIndex;
	private String type;
	private String problemArea;

	public Issue(int beginLine, int startIndex, int endIndex, String type, String problemArea) {
		this.beginLine = beginLine;
		this.startIndex = startIndex;
		this.endIndex = endIndex;
		this.type = type;
		this.problemArea = problemArea;
	}

	public int getBeginLine() {
		return this.beginLine;
	}

	public int getStartIndex() {
		return this.startIndex;
	}

	public int getEndIndex() {
		return this.endIndex;
	}

	public String getType() {
		return this.type;
	}

	public String getProblemArea() {
		return this.problemArea;
	}
}
