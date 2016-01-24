package autocisq.measure.maintainability;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;

import autocisq.IssueFinder;
import autocisq.io.IOUtils;
import autocisq.measure.MeasureTest;

public class MethodDirectlyUsingFieldFromOtherClassTest extends MeasureTest {
	
	private List<CompilationUnit> compilationUnits;
	private MethodDeclaration methodMultipleDirectAccess;
	private MethodDeclaration methodNoDirectAccess;
	private MethodDeclaration methodSingleDirectAccess;
	private String fileString;

	@Before
	public void setUp() throws Exception {
		IssueFinder issueFinder = IssueFinder.getInstance();
		issueFinder.getMeasures().clear();
		issueFinder.putMeasure(new MethodDirectlyUsingFieldFromOtherClass(new HashMap<>()));
		
		File testFile = new File("res/test/Supervisor.java");
		
		this.fileString = IOUtils.fileToString(testFile);
		
		CompilationUnit supervisorCU = JavaParser.parse(testFile);
		
		this.methodMultipleDirectAccess = (MethodDeclaration) supervisorCU.getTypes().get(0).getChildrenNodes().get(5);
		this.methodNoDirectAccess = (MethodDeclaration) supervisorCU.getTypes().get(0).getChildrenNodes().get(6);
		this.methodSingleDirectAccess = (MethodDeclaration) supervisorCU.getTypes().get(0).getChildrenNodes().get(7);
		
		this.compilationUnits = new ArrayList<>();
		this.compilationUnits.add(supervisorCU);
		this.compilationUnits.add(JavaParser.parse(new File("res/test/Person.java")));
		issueFinder.setCompilationUnits(this.compilationUnits);
		
	}

	@Test
	public void findMethodMultipleDirectAccess() {
		findIssue(this.methodMultipleDirectAccess, this.fileString);
	}
	
	@Test
	public void skipMethodNoDirectAccess() {
		skipIssue(this.methodNoDirectAccess, this.fileString);
	}
	
	@Test
	public void findMethodSingleDirectAccess() {
		findIssue(this.methodSingleDirectAccess, this.fileString);
	}

	@Override
	public String getIssueType() {
		return MethodDirectlyUsingFieldFromOtherClass.ISSUE_TYPE;
	}
	
}
