package autocisq.measure.maintainability;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;

import autocisq.IssueFinder;
import autocisq.io.IOUtils;
import autocisq.models.Issue;

public class FunctionCommentedOutInstructionsTest {

	private final static double threshold = FunctionCommentedOutInstructions.threshold;
	private List<Issue> issues;
	private MethodDeclaration functionClean;
	private MethodDeclaration function36COI;
	private MethodDeclaration functionOverThreshold;
	private MethodDeclaration functionAtThreshold;
	private MethodDeclaration functionUnderThreshold;
	private MethodDeclaration methodOverThreshold;
	private MethodDeclaration methodAtThreshold;
	private MethodDeclaration methodUnderThreshold;
	private String fileString;
	
	@Before
	public void setUp() throws Exception {
		IssueFinder issueFinder = IssueFinder.getInstance();
		issueFinder.getMeasures().clear();
		issueFinder.putMeasure(new FunctionCommentedOutInstructions(new HashMap<>()));

		File testFile = new File("res/test/CommentedOutInstructions.java");

		this.fileString = IOUtils.fileToString(testFile);

		CompilationUnit supervisorCU = JavaParser.parse(testFile);

		this.functionClean = (MethodDeclaration) supervisorCU.getTypes().get(0).getChildrenNodes().get(3);
		this.function36COI = (MethodDeclaration) supervisorCU.getTypes().get(0).getChildrenNodes().get(4);
		this.functionOverThreshold = (MethodDeclaration) supervisorCU.getTypes().get(0).getChildrenNodes().get(5);
		this.functionAtThreshold = (MethodDeclaration) supervisorCU.getTypes().get(0).getChildrenNodes().get(6);
		this.functionUnderThreshold = (MethodDeclaration) supervisorCU.getTypes().get(0).getChildrenNodes().get(7);
		this.methodOverThreshold = (MethodDeclaration) supervisorCU.getTypes().get(0).getChildrenNodes().get(10);
		this.methodAtThreshold = (MethodDeclaration) supervisorCU.getTypes().get(0).getChildrenNodes().get(11);
		this.methodUnderThreshold = (MethodDeclaration) supervisorCU.getTypes().get(0).getChildrenNodes().get(12);

	}
	
	@Test
	public void testCountInstructions() {
		int expected = 36;
		int actual = FunctionCommentedOutInstructions.countInstructions(this.functionClean);
		assertEquals(expected, actual);
	}

	@Test
	public void testCountCommentedOutInstructions() {
		int expected = 36;
		int actual = FunctionCommentedOutInstructions.countCommentedOutInstructions(this.function36COI);
		assertEquals(expected, actual);
	}
	
	@Test
	public void moreThanTwoPercentCommentedOutInstructions() {
		int instructions = FunctionCommentedOutInstructions.countInstructions(this.functionOverThreshold);
		int commOutInstructions = FunctionCommentedOutInstructions
				.countCommentedOutInstructions(this.functionOverThreshold);
		double result = (double) commOutInstructions / (instructions + commOutInstructions);
		assertTrue("Expected " + result + " to be > " + threshold, result > threshold);
	}

	@Test
	public void twoPercentCommentedOutInstructions() {
		int instructions = FunctionCommentedOutInstructions.countInstructions(this.functionAtThreshold);
		int commOutInstructions = FunctionCommentedOutInstructions
				.countCommentedOutInstructions(this.functionAtThreshold);
		double result = (double) commOutInstructions / (instructions + commOutInstructions);
		assertEquals("Expected " + result + " to be == " + threshold, threshold, result, 0.0000001d);
	}

	@Test
	public void lessThanTwoPercentCommentedOutInstructions() {
		int instructions = FunctionCommentedOutInstructions.countInstructions(this.functionUnderThreshold);
		int commOutInstructions = FunctionCommentedOutInstructions
				.countCommentedOutInstructions(this.functionUnderThreshold);
		double result = (double) commOutInstructions / (instructions + commOutInstructions);
		assertTrue("Expected " + result + " to be < " + threshold, result < threshold);
	}
	
	@Test
	public void findFunctionMoreThanTwoPercent() {
		this.issues = IssueFinder.getInstance().analyzeNode(this.functionOverThreshold, null, this.fileString);
		findIssue();
	}

	@Test
	public void skipFunctionTwoPercent() {
		this.issues = IssueFinder.getInstance().analyzeNode(this.functionAtThreshold, null, this.fileString);
		skipIssue();
	}
	
	@Test
	public void skipFunctionLessThanTwoPercent() {
		this.issues = IssueFinder.getInstance().analyzeNode(this.functionUnderThreshold, null, this.fileString);
		skipIssue();
	}

	@Test
	public void findMethodMoreThanTwoPercent() {
		this.issues = IssueFinder.getInstance().analyzeNode(this.methodOverThreshold, null, this.fileString);
		findIssue();
	}

	@Test
	public void skipMethodTwoPercent() {
		this.issues = IssueFinder.getInstance().analyzeNode(this.methodAtThreshold, null, this.fileString);
		skipIssue();
	}
	
	@Test
	public void skipMethodLessThanTwoPercent() {
		this.issues = IssueFinder.getInstance().analyzeNode(this.methodUnderThreshold, null, this.fileString);
		skipIssue();
	}
	
	private void findIssue() {
		assertTrue(this.issues.size() > 0);
		boolean found = false;
		for (Issue issue : this.issues) {
			if (issue.getType().equals("Function with > 2% commented out instructions")) {
				found = true;
			}
		}
		assertTrue(found);
	}

	private void skipIssue() {
		boolean found = false;
		for (Issue issue : this.issues) {
			if (issue.getType().equals("Function with > 2% commented out instructions")) {
				found = true;
			}
		}
		assertFalse(found);
	}

}
