package autocisq.measure.maintainability;

import java.io.File;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;

import autocisq.IssueFinder;
import autocisq.io.IOUtils;
import autocisq.measure.MeasureTest;

public class FunctionParametersTest extends MeasureTest {
	
	private ConstructorDeclaration constructor10Params;
	private MethodDeclaration method10Params;
	private MethodDeclaration method8Params;
	private MethodDeclaration function10Params;
	private MethodDeclaration function8Params;
	private MethodDeclaration function7Params;
	private MethodDeclaration function6Params;
	private String fileString;
	
	@Before
	public void setUp() throws Exception {
		IssueFinder issueFinder = IssueFinder.getInstance();
		issueFinder.getMeasures().clear();
		issueFinder.putMeasure(new FunctionParameters(new HashMap<>()));
		
		File testFile = new File("res/test/Person.java");

		this.fileString = IOUtils.fileToString(testFile);

		CompilationUnit personCU = JavaParser.parse(testFile);

		this.constructor10Params = (ConstructorDeclaration) personCU.getTypes().get(0).getChildrenNodes().get(10);
		this.method10Params = (MethodDeclaration) personCU.getTypes().get(0).getChildrenNodes().get(11);
		this.method8Params = (MethodDeclaration) personCU.getTypes().get(0).getChildrenNodes().get(12);
		this.function10Params = (MethodDeclaration) personCU.getTypes().get(0).getChildrenNodes().get(13);
		this.function8Params = (MethodDeclaration) personCU.getTypes().get(0).getChildrenNodes().get(14);
		this.function7Params = (MethodDeclaration) personCU.getTypes().get(0).getChildrenNodes().get(15);
		this.function6Params = (MethodDeclaration) personCU.getTypes().get(0).getChildrenNodes().get(16);
	}
	
	@Test
	public void skipConstructorWith10Parameters() {
		skipIssue(this.constructor10Params, this.fileString);
	}
	
	@Test
	public void skipMethodWith10Parameters() {
		skipIssue(this.method10Params, this.fileString);
	}
	
	@Test
	public void skipMethodWith8Parameters() {
		skipIssue(this.method8Params, this.fileString);
	}
	
	@Test
	public void findFunctionWith10Parameters() {
		findIssue(this.function10Params, this.fileString);
	}

	@Test
	public void findFunctionWith8Parameters() {
		findIssue(this.function8Params, this.fileString);
	}

	@Test
	public void findFunctionWith7Parameters() {
		findIssue(this.function7Params, this.fileString);
	}

	@Test
	public void skipFunctionWith6Parameters() {
		skipIssue(this.function6Params, this.fileString);
	}
	
	@Override
	public String getIssueType() {
		return FunctionParameters.ISSUE_TYPE;
	}
	
}
