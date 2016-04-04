package autocisq.measure.maintainability;

import java.io.File;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;

import autocisq.io.IOUtils;
import autocisq.measure.MeasureTest;

public class MethodFanOutTest extends MeasureTest {

	private ConstructorDeclaration constructorFanOut10;
	private MethodDeclaration functionFanOut12;
	private MethodDeclaration functionFanOut11;
	private MethodDeclaration functionFanOut10;
	private MethodDeclaration functionFanOut9;
	private String fileString;

	@Before
	public void setUp() throws Exception {
		this.issueFinder.getMeasures().clear();
		this.issueFinder.putMeasure(new MethodFanOut(new HashMap<>()));

		File testFile = new File("res/test/Person.java");

		this.fileString = IOUtils.fileToString(testFile);

		CompilationUnit personCU = JavaParser.parse(testFile);

		this.constructorFanOut10 = (ConstructorDeclaration) personCU.getTypes().get(0).getChildrenNodes().get(10);
		this.functionFanOut12 = (MethodDeclaration) personCU.getTypes().get(0).getChildrenNodes().get(19);
		this.functionFanOut11 = (MethodDeclaration) personCU.getTypes().get(0).getChildrenNodes().get(20);
		this.functionFanOut10 = (MethodDeclaration) personCU.getTypes().get(0).getChildrenNodes().get(21);
		this.functionFanOut9 = (MethodDeclaration) personCU.getTypes().get(0).getChildrenNodes().get(22);
	}

	@Test
	public void findConstructorWithFanOut10() {
		findIssue(this.constructorFanOut10, this.fileString);
	}

	@Test
	public void findMethodWithFanOut12() {
		findIssue(this.functionFanOut12, this.fileString);
	}

	@Test
	public void findMethodWithFanOut11() {
		findIssue(this.functionFanOut11, this.fileString);
	}

	@Test
	public void findMethodWithFanOut10() {
		findIssue(this.functionFanOut10, this.fileString);
	}

	@Test
	public void skipMethodWithFanOut9() {
		skipIssue(this.functionFanOut9, this.fileString);
	}

	@Override
	public String getIssueType() {
		return MethodFanOut.ISSUE_TYPE;
	}
}
