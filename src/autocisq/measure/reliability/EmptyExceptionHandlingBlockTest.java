package autocisq.measure.reliability;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;

import autocisq.IssueFinder;
import autocisq.io.IOUtils;
import autocisq.models.Issue;

public class EmptyExceptionHandlingBlockTest {

	private List<Issue> issues;

	@Before
	public void setUp() throws Exception {
		IssueFinder issueFinder = IssueFinder.getInstance();
		issueFinder.putMeasure(new EmptyExceptionHandlingBlock());
		File testFile = new File("res/test/EntropyManualCalculator.java");

		String fileString = IOUtils.fileToString(testFile);

		CompilationUnit compilationUnit = JavaParser.parse(testFile);
		Node tryStmt = compilationUnit.getTypes().get(0).getMembers().get(2).getChildrenNodes().get(3)
				.getChildrenNodes().get(4);
		this.issues = issueFinder.analyzeNode(tryStmt, null, fileString);
	}

	@Test
	public void findEmptyCatchClause() {
		boolean found = false;
		for (Issue issue : this.issues) {
			if (issue.getType().equals("Empty Catch Block")) {
				found = true;
			}
			for (Issue otherIssue : this.issues) {
				if (issue.hashCode() != otherIssue.hashCode() && issue.equals(otherIssue)) {
					fail("Found duplicate issue " + otherIssue.toString());
				}
			}
		}
		assertTrue(found);
	}

	@Test
	public void findAutoGeneratedCatchClause() {
		boolean found = false;
		for (Issue issue : this.issues) {
			if (issue.getType().equals("Auto Generated Catch Block")) {
				found = true;
			}
			for (Issue otherIssue : this.issues) {
				if (issue.hashCode() != otherIssue.hashCode() && issue.equals(otherIssue)) {
					fail("Found duplicate issue " + otherIssue.toString());
				}
			}
		}
		assertTrue(found);
	}

	@Test
	public void findEmptyFinallyBlock() {
		boolean found = false;
		for (Issue issue : this.issues) {
			if (issue.getType().equals("Empty Finally Block")) {
				found = true;
			}
			for (Issue otherIssue : this.issues) {
				if (issue.hashCode() != otherIssue.hashCode() && issue.equals(otherIssue)) {
					fail("Found duplicate issue " + otherIssue.toString());
				}
			}
		}
		assertTrue(found);
	}

}