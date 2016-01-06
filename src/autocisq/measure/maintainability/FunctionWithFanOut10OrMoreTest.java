package autocisq.measure.maintainability;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;

import autocisq.IssueFinder;
import autocisq.io.IOUtils;
import autocisq.models.Issue;

public class FunctionWithFanOut10OrMoreTest {

	private List<Issue> issues;
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
		issueFinder.putMeasure(new FunctionWithFanOut10OrMore());

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
		// System.out.println(this.constructorFanOut10);
		// System.out.println(this.methodFanOut12);
		// System.out.println(this.methodFanOut10);
		// System.out.println(this.functionFanOut12);
		// System.out.println(this.functionFanOut11);
		// System.out.println(this.functionFanOut10);
		// System.out.println(this.functionFanOut9);
	}

	@Test
	public void skipConstructorWithFanOut10() {
		this.issues = IssueFinder.getInstance().analyzeNode(this.constructorFanOut10, null, this.fileString);
		skipIssue();
	}
	
	@Test
	public void skipMethodWithFanOut12() {
		this.issues = IssueFinder.getInstance().analyzeNode(this.methodFanOut12, null, this.fileString);
		skipIssue();
	}
	
	@Test
	public void skipMethodWithFanOut10() {
		this.issues = IssueFinder.getInstance().analyzeNode(this.methodFanOut10, null, this.fileString);
		skipIssue();
	}
	
	@Test
	public void findFunctionWithFanOut12() {
		this.issues = IssueFinder.getInstance().analyzeNode(this.functionFanOut12, null, this.fileString);
		findIssue();
	}

	@Test
	public void findFunctionWithFanOut11() {
		this.issues = IssueFinder.getInstance().analyzeNode(this.functionFanOut11, null, this.fileString);
		findIssue();
	}

	@Test
	public void findFunctionWithFanOut10() {
		this.issues = IssueFinder.getInstance().analyzeNode(this.functionFanOut10, null, this.fileString);
		findIssue();
	}

	@Test
	public void skipFunctionWithFanOut9() {
		this.issues = IssueFinder.getInstance().analyzeNode(this.functionFanOut9, null, this.fileString);
		skipIssue();
	}
	
	private void findIssue() {
		assertTrue(this.issues.size() > 0);
		boolean found = false;
		for (Issue issue : this.issues) {
			if (issue.getType().equals("Function with fan-out of 10 or more")) {
				found = true;
			}
		}
		assertTrue(found);
	}
	
	private void skipIssue() {
		boolean found = false;
		for (Issue issue : this.issues) {
			if (issue.getType().equals("Function with fan-out of 10 or more")) {
				found = true;
			}
		}
		assertFalse(found);
	}

}
