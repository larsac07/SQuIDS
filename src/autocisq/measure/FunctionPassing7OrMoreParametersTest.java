package autocisq.measure;

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

public class FunctionPassing7OrMoreParametersTest {
	
	private List<Issue> issues;
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
		issueFinder.putMeasure(new FunctionPassing7OrMoreParameters());
		
		File testFile = new File("res/test/Person.java");

		this.fileString = IOUtils.fileToString(testFile);

		CompilationUnit personCU = JavaParser.parse(testFile);

		this.constructor10Params = (ConstructorDeclaration) personCU.getTypes().get(0).getChildrenNodes().get(11);
		this.method10Params = (MethodDeclaration) personCU.getTypes().get(0).getChildrenNodes().get(12);
		this.method8Params = (MethodDeclaration) personCU.getTypes().get(0).getChildrenNodes().get(13);
		this.function10Params = (MethodDeclaration) personCU.getTypes().get(0).getChildrenNodes().get(14);
		this.function8Params = (MethodDeclaration) personCU.getTypes().get(0).getChildrenNodes().get(15);
		this.function7Params = (MethodDeclaration) personCU.getTypes().get(0).getChildrenNodes().get(16);
		this.function6Params = (MethodDeclaration) personCU.getTypes().get(0).getChildrenNodes().get(17);
	}
	
	@Test
	public void skipConstructorWith10Parameters() {
		this.issues = IssueFinder.getInstance().analyzeNode(this.constructor10Params, null, this.fileString);
		assertFalse(this.issues.size() > 0);
	}
	
	@Test
	public void skipMethodWith10Parameters() {
		this.issues = IssueFinder.getInstance().analyzeNode(this.method10Params, null, this.fileString);
		assertFalse(this.issues.size() > 0);
	}
	
	@Test
	public void skipMethodWith8Parameters() {
		this.issues = IssueFinder.getInstance().analyzeNode(this.method8Params, null, this.fileString);
		assertFalse(this.issues.size() > 0);
	}
	
	@Test
	public void findFunctionWith10Parameters() {
		this.issues = IssueFinder.getInstance().analyzeNode(this.function10Params, null, this.fileString);
		foundIssue();
	}

	@Test
	public void findFunctionWith8Parameters() {
		this.issues = IssueFinder.getInstance().analyzeNode(this.function8Params, null, this.fileString);
		foundIssue();
	}

	@Test
	public void findFunctionWith7Parameters() {
		this.issues = IssueFinder.getInstance().analyzeNode(this.function7Params, null, this.fileString);
		foundIssue();
	}

	@Test
	public void skipFunctionWith6Parameters() {
		this.issues = IssueFinder.getInstance().analyzeNode(this.function6Params, null, this.fileString);
		assertFalse(this.issues.size() > 0);
	}
	
	private void foundIssue() {
		assertTrue(this.issues.size() > 0);
		boolean found = false;
		for (Issue issue : this.issues) {
			if (issue.getType().equals("Function passing 7 or more parameters")) {
				found = true;
			}
		}
		assertTrue(found);
	}
	
}
