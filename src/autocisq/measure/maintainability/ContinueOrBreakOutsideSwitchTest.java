package autocisq.measure.maintainability;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;

import autocisq.IssueFinder;
import autocisq.io.IOUtils;
import autocisq.measure.MeasureTest;

public class ContinueOrBreakOutsideSwitchTest extends MeasureTest {
	private MethodDeclaration nodeA;
	private MethodDeclaration nodeB;
	private MethodDeclaration nodeC;
	private MethodDeclaration nodeD;
	
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

}
