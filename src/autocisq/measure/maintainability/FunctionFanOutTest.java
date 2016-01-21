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

public class FunctionFanOutTest extends MeasureTest {

	private ConstructorDeclaration constructorFanOut10;
	private MethodDeclaration methodFanOut12;
	private MethodDeclaration methodFanOut10;
	private MethodDeclaration functionFanOut12;
	private MethodDeclaration functionFanOut11;
	private MethodDeclaration functionFanOut10;
	private MethodDeclaration functionFanOut9;
	private String fileString;

	@Before
	public void setUp() throws Exception {
		IssueFinder issueFinder = IssueFinder.getInstance();
		issueFinder.getMeasures().clear();
		issueFinder.putMeasure(new FunctionFanOut(new HashMap<>()));

		File testFile = new File("res/test/Person.java");
		
		this.fileString = IOUtils.fileToString(testFile);
		
		CompilationUnit personCU = JavaParser.parse(testFile);
		
		this.constructorFanOut10 = (ConstructorDeclaration) personCU.getTypes().get(0).getChildrenNodes().get(10);
		this.methodFanOut12 = (MethodDeclaration) personCU.getTypes().get(0).getChildrenNodes().get(17);
		this.methodFanOut10 = (MethodDeclaration) personCU.getTypes().get(0).getChildrenNodes().get(18);
		this.functionFanOut12 = (MethodDeclaration) personCU.getTypes().get(0).getChildrenNodes().get(19);
		this.functionFanOut11 = (MethodDeclaration) personCU.getTypes().get(0).getChildrenNodes().get(20);
		this.functionFanOut10 = (MethodDeclaration) personCU.getTypes().get(0).getChildrenNodes().get(21);
		this.functionFanOut9 = (MethodDeclaration) personCU.getTypes().get(0).getChildrenNodes().get(22);
	}

	@Test
	public void skipConstructorWithFanOut10() {
		skipIssue(this.constructorFanOut10, this.fileString);
	}
	
	@Test
	public void skipMethodWithFanOut12() {
		skipIssue(this.methodFanOut12, this.fileString);
	}
	
	@Test
	public void skipMethodWithFanOut10() {
		skipIssue(this.methodFanOut10, this.fileString);
	}
	
	@Test
	public void findFunctionWithFanOut12() {
		findIssue(this.functionFanOut12, this.fileString);
	}

	@Test
	public void findFunctionWithFanOut11() {
		findIssue(this.functionFanOut11, this.fileString);
	}

	@Test
	public void findFunctionWithFanOut10() {
		findIssue(this.functionFanOut10, this.fileString);
	}

	@Test
	public void skipFunctionWithFanOut9() {
		skipIssue(this.functionFanOut9, this.fileString);
	}

	@Override
	public String getIssueType() {
		return FunctionFanOut.ISSUE_TYPE;
	}
}
