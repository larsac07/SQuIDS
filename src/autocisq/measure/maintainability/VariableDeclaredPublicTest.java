package autocisq.measure.maintainability;

import java.io.File;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.FieldDeclaration;

import autocisq.IssueFinder;
import autocisq.io.IOUtils;
import autocisq.measure.MeasureTest;

public class VariableDeclaredPublicTest extends MeasureTest {

	private FieldDeclaration publicConstant;
	private FieldDeclaration publicStaticVariable;
	private FieldDeclaration privateVariable;
	private FieldDeclaration publicVariable;
	private String fileString;

	@Before
	public void setUp() throws Exception {
		IssueFinder issueFinder = IssueFinder.getInstance();
		issueFinder.getMeasures().clear();
		issueFinder.putMeasure(new VariableDeclaredPublic(new HashMap<>()));
		
		File testFile = new File("res/test/layers/GUI.java");
		this.fileString = IOUtils.fileToString(testFile);

		CompilationUnit compilationUnit = JavaParser.parse(testFile);
		this.publicConstant = (FieldDeclaration) compilationUnit.getTypes().get(0).getChildrenNodes().get(1);
		this.publicStaticVariable = (FieldDeclaration) compilationUnit.getTypes().get(0).getChildrenNodes().get(2);
		this.privateVariable = (FieldDeclaration) compilationUnit.getTypes().get(0).getChildrenNodes().get(3);
		this.publicVariable = (FieldDeclaration) compilationUnit.getTypes().get(0).getChildrenNodes().get(7);
	}

	@Test
	public void skipPublicConstants() {
		skipIssue(this.publicConstant, this.fileString);
	}

	@Test
	public void skipPublicStaticVariable() {
		skipIssue(this.publicStaticVariable, this.fileString);
	}

	@Test
	public void skipPrivateVariable() {
		skipIssue(this.privateVariable, this.fileString);
	}

	@Test
	public void findPublicVariable() {
		findIssue(this.publicVariable, this.fileString);
	}

	@Override
	public String getIssueType() {
		return VariableDeclaredPublic.ISSUE_TYPE;
	}

}
