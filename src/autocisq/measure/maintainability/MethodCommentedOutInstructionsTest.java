package autocisq.measure.maintainability;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;

import autocisq.io.IOUtils;
import autocisq.measure.MeasureTest;

public class MethodCommentedOutInstructionsTest extends MeasureTest {

	private final static double threshold = MethodCommentedOutInstructions.THRESHOLD;
	private MethodDeclaration methodNoCOI;
	private MethodDeclaration method36COI;
	private MethodDeclaration methodOverThreshold;
	private MethodDeclaration methodAtThreshold;
	private MethodDeclaration methodUnderThreshold;
	private String fileString;

	@Before
	public void setUp() throws Exception {
		this.issueFinder.getMeasures().clear();
		this.issueFinder.putMeasure(new MethodCommentedOutInstructions(new HashMap<>()));

		File testFile = new File("res/test/CommentedOutInstructions.java");

		this.fileString = IOUtils.fileToString(testFile);

		CompilationUnit coiCU = JavaParser.parse(testFile);

		this.methodNoCOI = (MethodDeclaration) coiCU.getTypes().get(0).getChildrenNodes().get(3);
		this.method36COI = (MethodDeclaration) coiCU.getTypes().get(0).getChildrenNodes().get(4);
		this.methodOverThreshold = (MethodDeclaration) coiCU.getTypes().get(0).getChildrenNodes().get(5);
		this.methodAtThreshold = (MethodDeclaration) coiCU.getTypes().get(0).getChildrenNodes().get(6);
		this.methodUnderThreshold = (MethodDeclaration) coiCU.getTypes().get(0).getChildrenNodes().get(7);

	}

	@Test
	public void testCountInstructions() {
		int expected = 36;
		int actual = MethodCommentedOutInstructions.countInstructions(this.methodNoCOI);
		assertEquals(expected, actual);
	}

	@Test
	public void testCountCommentedOutInstructions() {
		int expected = 36;
		int actual = MethodCommentedOutInstructions.countCommentedOutInstructions(this.method36COI);
		assertEquals(expected, actual);
	}

	@Test
	public void moreThanTwoPercentCommentedOutInstructions() {
		int instructions = MethodCommentedOutInstructions.countInstructions(this.methodOverThreshold);
		int commOutInstructions = MethodCommentedOutInstructions
				.countCommentedOutInstructions(this.methodOverThreshold);
		double result = (double) commOutInstructions / (instructions + commOutInstructions);
		assertTrue("Expected " + result + " to be > " + threshold, result > threshold);
	}

	@Test
	public void twoPercentCommentedOutInstructions() {
		int instructions = MethodCommentedOutInstructions.countInstructions(this.methodAtThreshold);
		int commOutInstructions = MethodCommentedOutInstructions.countCommentedOutInstructions(this.methodAtThreshold);
		double result = (double) commOutInstructions / (instructions + commOutInstructions);
		assertEquals("Expected " + result + " to be == " + threshold, threshold, result, 0.0000001d);
	}

	@Test
	public void lessThanTwoPercentCommentedOutInstructions() {
		int instructions = MethodCommentedOutInstructions.countInstructions(this.methodUnderThreshold);
		int commOutInstructions = MethodCommentedOutInstructions
				.countCommentedOutInstructions(this.methodUnderThreshold);
		double result = (double) commOutInstructions / (instructions + commOutInstructions);
		assertTrue("Expected " + result + " to be < " + threshold, result < threshold);
	}

	@Test
	public void findMethodMoreThanTwoPercent() {
		findIssue(this.methodOverThreshold, this.fileString);
	}

	@Test
	public void skipMethodTwoPercent() {
		skipIssue(this.methodAtThreshold, this.fileString);
	}

	@Test
	public void skipMethodLessThanTwoPercent() {
		skipIssue(this.methodUnderThreshold, this.fileString);
	}

	@Override
	public String getIssueType() {
		return MethodCommentedOutInstructions.ISSUE_TYPE;
	}

}
