package autocisq.measure.maintainability;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;

import autocisq.IssueFinder;
import autocisq.io.IOUtils;
import autocisq.models.Issue;

public class ContinueOrBreakOutsideSwitchTest {

	private String issueType;
	private List<Issue> issues;
	private MethodDeclaration nodeA;
	private MethodDeclaration nodeB;
	private MethodDeclaration nodeC;
	private MethodDeclaration nodeD;
	private String fileString;
	
	@Before
	public void setUp() throws Exception {
		this.issueType = "Continue or Break outside switch";
		IssueFinder issueFinder = IssueFinder.getInstance();
		issueFinder.getMeasures().clear();
		issueFinder.putMeasure(new ContinueOrBreakOutsideSwitch());
		
		File testFile = new File("res/test/ContinuesAndBreaks.java");
		
		this.fileString = IOUtils.fileToString(testFile);
		
		CompilationUnit supervisorCU = JavaParser.parse(testFile);
		
		this.nodeA = (MethodDeclaration) supervisorCU.getTypes().get(0).getChildrenNodes().get(0);
		this.nodeB = (MethodDeclaration) supervisorCU.getTypes().get(0).getChildrenNodes().get(1);
		this.nodeC = (MethodDeclaration) supervisorCU.getTypes().get(0).getChildrenNodes().get(2);
		this.nodeD = (MethodDeclaration) supervisorCU.getTypes().get(0).getChildrenNodes().get(3);
		
	}
	
	@Test
	public void findContinue() {
		findIssue(this.nodeA);
	}
	
	@Test
	public void findBreak() {
		findIssue(this.nodeB);
	}
	
	@Test
	public void skipContinueInsideSwitch() {
		skipIssue(this.nodeC);
	}
	
	@Test
	public void skipBreakInsideSwitch() {
		skipIssue(this.nodeD);
	}
	
	private void findIssue(Node nodeToAnalyze) {
		this.issues = IssueFinder.getInstance().analyzeNode(nodeToAnalyze, null, this.fileString);
		assertTrue(this.issues.size() > 0);
		boolean found = false;
		for (Issue issue : this.issues) {
			if (issue.getType().equals(this.issueType)) {
				found = true;
			}
		}
		assertTrue(found);
	}

	private void skipIssue(Node nodeToAnalyze) {
		this.issues = IssueFinder.getInstance().analyzeNode(nodeToAnalyze, null, this.fileString);
		boolean found = false;
		for (Issue issue : this.issues) {
			if (issue.getType().equals(this.issueType)) {
				found = true;
			}
		}
		assertFalse(found);
	}

}
