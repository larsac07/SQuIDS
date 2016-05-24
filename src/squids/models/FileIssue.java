package squids.models;

import com.github.javaparser.ast.Node;

import squids.JavaParserHelper;
import squids.measure.Measure;

public class FileIssue extends Issue {

	private int beginLine;
	private int startIndex;
	private int endIndex;
	private String problemArea;
	private Node node;

	public FileIssue(int beginLine, int startIndex, int endIndex, Measure measure, String problemArea, Node node) {
		super(measure);
		this.beginLine = beginLine;
		this.startIndex = startIndex;
		this.endIndex = endIndex;
		this.problemArea = problemArea;
		this.node = node;
	}

	public FileIssue(Measure measure, Node node, String fileString) {
		this(measure, node, fileString, "");
	}

	public FileIssue(Measure measure, Node node, String fileString, String message) {
		super(measure, message);
		int[] indexes = JavaParserHelper.columnsToIndexes(fileString, node.getBeginLine(), node.getEndLine(),
				node.getBeginColumn(), node.getEndColumn());
		this.beginLine = node.getBeginLine();
		this.startIndex = indexes[0];
		this.endIndex = indexes[1];
		this.problemArea = node.toString();
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

	@Override
	public String getID() {
		return getMeasureElement() + this.node.toString() + this.beginLine + this.startIndex + this.endIndex;
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof FileIssue) {
			FileIssue otherIssue = (FileIssue) other;
			if (super.equals(otherIssue) && otherIssue.beginLine == this.beginLine
					&& otherIssue.startIndex == this.startIndex && otherIssue.endIndex == this.endIndex
					&& otherIssue.node.equals(this.node) && otherIssue.problemArea.equals(this.problemArea)) {
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
		return getMeasureElement() + " at line " + getBeginLine() + ": " + this.node.toString();
	}

}
