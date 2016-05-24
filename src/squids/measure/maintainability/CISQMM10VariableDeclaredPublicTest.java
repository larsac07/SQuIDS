package squids.measure.maintainability;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.FieldDeclaration;

import squids.io.IOUtils;
import squids.measure.MeasureTest;
import squids.models.Issue;

public class CISQMM10VariableDeclaredPublicTest extends MeasureTest {

	private FieldDeclaration publicConstant;
	private FieldDeclaration publicStaticVariable;
	private FieldDeclaration privateVariable;
	private FieldDeclaration publicVariable;
	private FieldDeclaration multipleVariablesInSameField;
	private String fileString;

	@Before
	public void setUp() throws Exception {
		this.issueFinder.getMeasures().clear();
		this.issueFinder.putMeasure(new CISQMM10VariableDeclaredPublic(new HashMap<>()));

		File testFile = new File("res/test/layers/GUI.java");
		this.fileString = IOUtils.fileToString(testFile);

		CompilationUnit compilationUnit = JavaParser.parse(testFile);
		this.publicConstant = (FieldDeclaration) compilationUnit.getTypes().get(0).getChildrenNodes().get(1);
		this.publicStaticVariable = (FieldDeclaration) compilationUnit.getTypes().get(0).getChildrenNodes().get(2);
		this.privateVariable = (FieldDeclaration) compilationUnit.getTypes().get(0).getChildrenNodes().get(3);
		this.publicVariable = (FieldDeclaration) compilationUnit.getTypes().get(0).getChildrenNodes().get(7);
		this.multipleVariablesInSameField = (FieldDeclaration) compilationUnit.getTypes().get(0).getMembers().get(30);
	}

	@Test
	public void skipPublicConstants() {
		skipIssue(this.publicConstant, this.fileString);
	}

	@Test
	public void findPublicStaticVariable() {
		findIssue(this.publicStaticVariable, this.fileString);
	}

	@Test
	public void skipPrivateVariable() {
		skipIssue(this.privateVariable, this.fileString);
	}

	@Test
	public void findPublicVariable() {
		findIssue(this.publicVariable, this.fileString);
	}

	@Test
	public void findMultipleVariablesWithSameFieldDeclaration() {
		List<Issue> issues = this.issueFinder.analyzeNode(this.multipleVariablesInSameField, null, this.fileString);
		assertEquals(3, issues.size());
		for (Issue issue : issues) {
			assertEquals(getIssueType(), issue.getMeasureElement());
		}
	}

	@Override
	public String getIssueType() {
		return CISQMM10VariableDeclaredPublic.ISSUE_TYPE;
	}

}
