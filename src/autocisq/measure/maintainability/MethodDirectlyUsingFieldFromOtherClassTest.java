package autocisq.measure.maintainability;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;

import autocisq.io.IOUtils;
import autocisq.measure.MeasureTest;
import autocisq.models.Issue;

public class MethodDirectlyUsingFieldFromOtherClassTest extends MeasureTest {

	private List<CompilationUnit> compilationUnits;
	private CompilationUnit cu;
	private MethodDeclaration methodMultipleDirectAccess;
	private MethodDeclaration methodNoDirectAccess;
	private MethodDeclaration methodSingleDirectAccess;
	private MethodDeclaration methodNonStaticAccess;
	private MethodDeclaration methodStaticAccess;
	private MethodDeclaration methodFinalAccess;
	private String fileString;

	@Before
	public void setUp() throws Exception {
		this.issueFinder.getMeasures().clear();
		this.issueFinder.putMeasure(new MethodDirectlyUsingFieldFromOtherClass(new HashMap<>()));

		File testFile = new File("res/test/Supervisor.java");

		this.fileString = IOUtils.fileToString(testFile);

		this.cu = JavaParser.parse(testFile);

		this.methodMultipleDirectAccess = (MethodDeclaration) this.cu.getTypes().get(0).getMembers().get(5);
		this.methodNoDirectAccess = (MethodDeclaration) this.cu.getTypes().get(0).getMembers().get(6);
		this.methodSingleDirectAccess = (MethodDeclaration) this.cu.getTypes().get(0).getMembers().get(7);
		this.methodNonStaticAccess = (MethodDeclaration) this.cu.getTypes().get(0).getMembers().get(11);
		this.methodStaticAccess = (MethodDeclaration) this.cu.getTypes().get(0).getMembers().get(12);
		this.methodFinalAccess = (MethodDeclaration) this.cu.getTypes().get(0).getMembers().get(13);

		this.compilationUnits = new ArrayList<>();
		this.compilationUnits.add(this.cu);
		this.compilationUnits.add(JavaParser.parse(new File("res/test/Person.java")));
		this.issueFinder.setCompilationUnits(this.compilationUnits);

	}

	@Test
	public void findMethodMultipleDirectAccess() {
		findIssue(this.methodMultipleDirectAccess, this.fileString);
	}

	@Test
	public void skipMethodNoDirectAccess() {
		skipIssue(this.methodNoDirectAccess, this.fileString);
	}

	@Test
	public void findMethodSingleDirectAccess() {
		findIssue(this.methodSingleDirectAccess, this.fileString);
	}

	@Test
	public void findNonStaticAccess() {
		findIssue(this.methodNonStaticAccess, this.fileString);
	}

	@Test
	public void findStaticAccess() {
		findIssue(this.methodStaticAccess, this.fileString);
	}

	@Test
	public void skipFinalAccess() {
		skipIssue(this.methodFinalAccess, this.fileString);
	}

	@Test
	public void markMethodOnlyOnce() {
		List<Issue> issues = this.issueFinder.analyzeNode(this.methodMultipleDirectAccess, null, this.fileString);
		assertEquals(1, issues.size());
	}

	@Override
	public String getIssueType() {
		return MethodDirectlyUsingFieldFromOtherClass.ISSUE_TYPE;
	}

}
