package squids.measure;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import com.github.javaparser.ast.Node;

import squids.IssueFinder;
import squids.models.Issue;

public abstract class MeasureTest {

	protected List<Issue> issues;
	protected IssueFinder issueFinder;

	protected MeasureTest() {
		this.issueFinder = new IssueFinder();
	}

	public void findIssue(String message, Node nodeToAnalyze, String fileString) {
		try {
			this.issues = this.issueFinder.analyzeNode(nodeToAnalyze, null, fileString);
		} catch (RuntimeException e) {
			fail();
		}
		assertNotNull(this.issues);
		assertTrue(this.issues.size() > 0);
		boolean found = false;
		for (Issue issue : this.issues) {
			if (issue.getMeasureElement().equals(getIssueType())) {
				found = true;
			}
		}
		assertTrue(message, found);
	}

	public void findIssue(Node nodeToAnalyze, String fileString) {
		findIssue(null, nodeToAnalyze, fileString);
	}

	public void skipIssue(String message, Node nodeToAnalyze, String fileString) {
		this.issues = this.issueFinder.analyzeNode(nodeToAnalyze, null, fileString);
		boolean found = false;
		for (Issue issue : this.issues) {
			if (issue.getMeasureElement().equals(getIssueType())) {
				found = true;
			}
		}
		assertFalse(message, found);
	}

	public void skipIssue(Node nodeToAnalyze, String fileString) {
		skipIssue(null, nodeToAnalyze, fileString);
	}

	public abstract String getIssueType();
}
