package autocisq.measure;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import com.github.javaparser.ast.Node;

import autocisq.IssueFinder;
import autocisq.models.Issue;

public abstract class MeasureTest {
	
	protected List<Issue> issues;
	protected String fileString;
	
	public void findIssue(Node nodeToAnalyze) {
		this.issues = IssueFinder.getInstance().analyzeNode(nodeToAnalyze, null, this.fileString);
		assertTrue(this.issues.size() > 0);
		boolean found = false;
		for (Issue issue : this.issues) {
			if (issue.getType().equals(getIssueType())) {
				found = true;
			}
		}
		assertTrue(found);
	}
	
	public void skipIssue(Node nodeToAnalyze) {
		this.issues = IssueFinder.getInstance().analyzeNode(nodeToAnalyze, null, this.fileString);
		boolean found = false;
		for (Issue issue : this.issues) {
			if (issue.getType().equals(getIssueType())) {
				found = true;
			}
		}
		assertFalse(found);
	}

	public abstract String getIssueType();
}