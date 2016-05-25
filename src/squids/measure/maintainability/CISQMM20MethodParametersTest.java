package squids.measure.maintainability;

import java.io.File;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;

import squids.io.IOUtils;
import squids.measure.MeasureTest;

public class CISQMM20MethodParametersTest extends MeasureTest {

	private ConstructorDeclaration constructor8Params;
	private ConstructorDeclaration constructor7Params;
	private ConstructorDeclaration constructor6Params;
	private MethodDeclaration function8Params;
	private MethodDeclaration function7Params;
	private MethodDeclaration function6Params;
	private String fileString;

	@Before
	public void setUp() throws Exception {
		this.issueFinder.getMeasures().clear();
		this.issueFinder.putMeasure(new CISQMM20MethodParameters(new HashMap<>()));

		File testFile = new File("res/test/PersonParams.java");

		this.fileString = IOUtils.fileToString(testFile);

		CompilationUnit personCU = JavaParser.parse(testFile);

		this.constructor8Params = (ConstructorDeclaration) personCU.getTypes().get(0).getChildrenNodes().get(8);
		this.constructor7Params = (ConstructorDeclaration) personCU.getTypes().get(0).getChildrenNodes().get(9);
		this.constructor6Params = (ConstructorDeclaration) personCU.getTypes().get(0).getChildrenNodes().get(10);
		this.function8Params = (MethodDeclaration) personCU.getTypes().get(0).getChildrenNodes().get(11);
		this.function7Params = (MethodDeclaration) personCU.getTypes().get(0).getChildrenNodes().get(12);
		this.function6Params = (MethodDeclaration) personCU.getTypes().get(0).getChildrenNodes().get(13);
	}

	@Test
	public void findConstructorWith8Parameters() {
		findIssue(this.constructor8Params, this.fileString);
	}

	@Test
	public void findConstructorWith7Parameters() {
		findIssue(this.constructor7Params, this.fileString);
	}

	@Test
	public void skipConstructorWith6Parameters() {
		skipIssue(this.constructor6Params, this.fileString);
	}

	@Test
	public void findMethodWith8Parameters() {
		findIssue(this.function8Params, this.fileString);
	}

	@Test
	public void findMethodWith7Parameters() {
		findIssue(this.function7Params, this.fileString);
	}

	@Test
	public void skipMethodWith6Parameters() {
		skipIssue(this.function6Params, this.fileString);
	}

	@Override
	public String getIssueType() {
		return CISQMM20MethodParameters.ISSUE_TYPE;
	}

}
