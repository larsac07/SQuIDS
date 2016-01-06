package autocisq.measure.maintainability;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.FieldDeclaration;

import autocisq.IssueFinder;
import autocisq.io.IOUtils;
import autocisq.models.Issue;

public class VariableDeclaredPublicTest {

	private List<Issue> issues;
	private FieldDeclaration publicConstant;
	private FieldDeclaration publicStaticVariable;
	private FieldDeclaration privateVariable;
	private FieldDeclaration publicVariable;
	private String fileString;

	@Before
	public void setUp() throws Exception {
		IssueFinder issueFinder = IssueFinder.getInstance();
		issueFinder.putMeasure(new VariableDeclaredPublic());
		File testFile = new File("res/test/layers/GUI.java");
		this.fileString = IOUtils.fileToString(testFile);

		CompilationUnit compilationUnit = JavaParser.parse(testFile);
		this.publicConstant = (FieldDeclaration) compilationUnit.getTypes().get(0).getChildrenNodes().get(1);
		this.publicStaticVariable = (FieldDeclaration) compilationUnit.getTypes().get(0).getChildrenNodes().get(2);
		this.privateVariable = (FieldDeclaration) compilationUnit.getTypes().get(0).getChildrenNodes().get(3);
		this.publicVariable = (FieldDeclaration) compilationUnit.getTypes().get(0).getChildrenNodes().get(7);
		System.out.println(this.publicConstant);
		System.out.println(this.publicStaticVariable);
		System.out.println(this.privateVariable);
		System.out.println(this.publicVariable);
	}

	@Test
	public void skipPublicConstants() {
		this.issues = IssueFinder.getInstance().analyzeNode(this.publicConstant, null, this.fileString);
		skipIssue();
	}

	@Test
	public void skipPublicStaticVariable() {
		this.issues = IssueFinder.getInstance().analyzeNode(this.publicStaticVariable, null, this.fileString);
		skipIssue();
	}

	@Test
	public void skipPrivateVariable() {
		this.issues = IssueFinder.getInstance().analyzeNode(this.privateVariable, null, this.fileString);
		skipIssue();
	}

	@Test
	public void findPublicVariable() {
		this.issues = IssueFinder.getInstance().analyzeNode(this.publicVariable, null, this.fileString);
		assertTrue(this.issues.size() > 0);
		boolean found = false;
		for (Issue issue : this.issues) {
			if (issue.getType().equals("Variable declared public")) {
				found = true;
			}
		}
		assertTrue(found);
	}
	
	private void skipIssue() {
		boolean found = false;
		for (Issue issue : this.issues) {
			if (issue.getType().equals("Function passing 7 or more parameters")) {
				found = true;
			}
		}
		assertFalse(found);
	}

}
