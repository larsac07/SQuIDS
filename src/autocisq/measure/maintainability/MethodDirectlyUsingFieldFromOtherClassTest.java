package autocisq.measure.maintainability;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;

import autocisq.IssueFinder;
import autocisq.io.IOUtils;
import autocisq.models.Issue;

public class MethodDirectlyUsingFieldFromOtherClassTest {
	
	private List<Issue> issues;
	private List<CompilationUnit> compilationUnits;
	private MethodDeclaration methodMultipleDirectAccess;
	private MethodDeclaration methodNoDirectAccess;
	private MethodDeclaration methodSingleDirectAccess;
	private MethodDeclaration functionMultipleDirectAccess;
	private MethodDeclaration functionNoDirectAccess;
	private MethodDeclaration functionSingleDirectAccess;
	private String fileString;

	@Before
	public void setUp() throws Exception {
		IssueFinder issueFinder = IssueFinder.getInstance();
		issueFinder.getMeasures().clear();
		issueFinder.putMeasure(new MethodDirectlyUsingFieldFromOtherClass());
		
		File testFile = new File("res/test/Supervisor.java");
		
		this.fileString = IOUtils.fileToString(testFile);
		
		CompilationUnit supervisorCU = JavaParser.parse(testFile);
		
		this.methodMultipleDirectAccess = (MethodDeclaration) supervisorCU.getTypes().get(0).getChildrenNodes().get(5);
		this.methodNoDirectAccess = (MethodDeclaration) supervisorCU.getTypes().get(0).getChildrenNodes().get(6);
		this.methodSingleDirectAccess = (MethodDeclaration) supervisorCU.getTypes().get(0).getChildrenNodes().get(7);
		this.functionMultipleDirectAccess = (MethodDeclaration) supervisorCU.getTypes().get(0).getChildrenNodes()
				.get(8);
		this.functionNoDirectAccess = (MethodDeclaration) supervisorCU.getTypes().get(0).getChildrenNodes().get(9);
		this.functionSingleDirectAccess = (MethodDeclaration) supervisorCU.getTypes().get(0).getChildrenNodes().get(10);
		
		this.compilationUnits = new ArrayList<>();
		this.compilationUnits.add(supervisorCU);
		this.compilationUnits.add(JavaParser.parse(new File("res/test/Person.java")));
		issueFinder.setCompilationUnits(this.compilationUnits);
		
	}

	@Test
	public void findMethodMultipleDirectAccess() {
		this.issues = IssueFinder.getInstance().analyzeNode(this.methodMultipleDirectAccess, null, this.fileString);
		findIssue();
	}
	
	@Test
	public void skipMethodNoDirectAccess() {
		this.issues = IssueFinder.getInstance().analyzeNode(this.methodNoDirectAccess, null, this.fileString);
		skipIssue();
	}
	
	@Test
	public void findMethodSingleDirectAccess() {
		this.issues = IssueFinder.getInstance().analyzeNode(this.methodSingleDirectAccess, null, this.fileString);
		findIssue();
	}
	
	@Test
	public void skipFunctionMultipleDirectAccess() {
		this.issues = IssueFinder.getInstance().analyzeNode(this.functionMultipleDirectAccess, null, this.fileString);
		skipIssue();
	}
	
	@Test
	public void skipFunctionNoDirectAccess() {
		this.issues = IssueFinder.getInstance().analyzeNode(this.functionNoDirectAccess, null, this.fileString);
		skipIssue();
	}
	
	@Test
	public void skipFunctionSingleDirectAccess() {
		this.issues = IssueFinder.getInstance().analyzeNode(this.functionSingleDirectAccess, null, this.fileString);
		skipIssue();
	}

	private void findIssue() {
		assertTrue(this.issues.size() > 0);
		boolean found = false;
		for (Issue issue : this.issues) {
			if (issue.getType().equals("Method directly using field from other class")) {
				found = true;
			}
		}
		assertTrue(found);
	}

	private void skipIssue() {
		boolean found = false;
		for (Issue issue : this.issues) {
			if (issue.getType().equals("Method directly using field from other class")) {
				found = true;
			}
		}
		assertFalse(found);
	}
	
}
