package autocisq.measure.maintainability;

import java.io.File;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;

import autocisq.io.IOUtils;
import autocisq.measure.MeasureTest;

public class CISQMM17ContinueOrBreakOutsideSwitchTest extends MeasureTest {
	private MethodDeclaration nodeA;
	private MethodDeclaration nodeB;
	private MethodDeclaration nodeC;
	private MethodDeclaration nodeD;
	private MethodDeclaration nodeE;
	private MethodDeclaration nodeF;
	private String fileString;

	@Before
	public void setUp() throws Exception {
		this.issueFinder.getMeasures().clear();
		this.issueFinder.putMeasure(new CISQMM17ContinueOrBreakOutsideSwitch(new HashMap<>()));

		File testFile = new File("res/test/ContinuesAndBreaks.java");

		this.fileString = IOUtils.fileToString(testFile);

		CompilationUnit cabCU = JavaParser.parse(testFile);

		this.nodeA = (MethodDeclaration) cabCU.getTypes().get(0).getChildrenNodes().get(0);
		this.nodeB = (MethodDeclaration) cabCU.getTypes().get(0).getChildrenNodes().get(1);
		this.nodeC = (MethodDeclaration) cabCU.getTypes().get(0).getChildrenNodes().get(2);
		this.nodeD = (MethodDeclaration) cabCU.getTypes().get(0).getChildrenNodes().get(3);
		this.nodeE = (MethodDeclaration) cabCU.getTypes().get(0).getChildrenNodes().get(4);
		this.nodeF = (MethodDeclaration) cabCU.getTypes().get(0).getChildrenNodes().get(5);

	}

	@Test
	public void findContinue() {
		findIssue(this.nodeA, this.fileString);
	}

	@Test
	public void findBreak() {
		findIssue(this.nodeB, this.fileString);
	}

	@Test
	public void findContinueInsideIfInsideSwitch() {
		findIssue(this.nodeC, this.fileString);
	}

	@Test
	public void findBreakInsideIfInsideSwitch() {
		findIssue(this.nodeD, this.fileString);
	}

	@Test
	public void skipContinueInsideSwitch() {
		skipIssue(this.nodeE, this.fileString);
	}

	@Test
	public void skipBreakInsideSwitch() {
		skipIssue(this.nodeF, this.fileString);
	}

	@Override
	public String getIssueType() {
		return CISQMM17ContinueOrBreakOutsideSwitch.ISSUE_TYPE;
	}

}
