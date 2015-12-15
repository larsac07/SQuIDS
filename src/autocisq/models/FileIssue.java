package autocisq.models;

import com.github.javaparser.ast.Node;

public class FileIssue extends Issue {

	private int beginLine;
	private int startIndex;
	private int endIndex;
	private String problemArea;
	private Node node;

	public FileIssue(int beginLine, int startIndex, int endIndex, String type, String problemArea, Node node) {
		super(type);
		this.beginLine = beginLine;
		this.startIndex = startIndex;
		this.endIndex = endIndex;
		this.problemArea = problemArea;
		this.node = node;
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

	public String getProblemArea() {
		return this.problemArea;
	}

	public Node getNode() {
		return this.node;
	}

}
