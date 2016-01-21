package autocisq.measure.maintainability;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;

import autocisq.IssueFinder;
import autocisq.io.IOUtils;
import autocisq.measure.MeasureTest;

public class FunctionCyclomaticComplexityTest extends MeasureTest {

	private ConstructorDeclaration constructorCC10;
	private MethodDeclaration functionCC9;
	private MethodDeclaration functionCC10;
	private MethodDeclaration functionCC11;
	private MethodDeclaration functionAllCases;
	private String fileString;
	
	@Before
	public void setUp() throws Exception {
		IssueFinder issueFinder = IssueFinder.getInstance();
		issueFinder.getMeasures().clear();
		issueFinder.putMeasure(new FunctionCyclomaticComplexity(new HashMap<>()));

		File file = new File("res/test/CyclomaticComplexity.java");

		this.fileString = IOUtils.fileToString(file);

		CompilationUnit cu = JavaParser.parse(file);
		
		this.constructorCC10 = (ConstructorDeclaration) cu.getTypes().get(0).getMembers().get(0);
		this.functionCC9 = (MethodDeclaration) cu.getTypes().get(0).getMembers().get(1);
		this.functionCC10 = (MethodDeclaration) cu.getTypes().get(0).getMembers().get(2);
		this.functionCC11 = (MethodDeclaration) cu.getTypes().get(0).getMembers().get(3);
		this.functionAllCases = (MethodDeclaration) cu.getTypes().get(0).getMembers().get(4);

	}

	@Test
	public void findConstructorCC10() {
		findIssue(this.constructorCC10, this.fileString);
	}

	@Test
	public void skipFunctionCC9() {
		skipIssue(this.functionCC9, this.fileString);
	}
	
	@Test
	public void findFunctionCC10() {
		findIssue(this.functionCC10, this.fileString);
	}
	
	@Test
	public void findFunctionCC11() {
		findIssue(this.functionCC11, this.fileString);
	}

	@Test
	public void findAllCases() {
		List<Node> controlFlowStatements = findControlFlowStatements(this.functionAllCases);
		final int expected = 11;
		int actual = controlFlowStatements.size();
		assertEquals(expected, actual);
	}
	
	private static List<Node> findControlFlowStatements(Node node) {
		List<Node> controlFlowStatements = new LinkedList<>();
		if (FunctionCyclomaticComplexity.isControlFlowStmt(node)) {
			controlFlowStatements.add(node);
		}
		for (Node child : node.getChildrenNodes()) {
			controlFlowStatements.addAll(findControlFlowStatements(child));
		}
		return controlFlowStatements;
	}

	@Override
	public String getIssueType() {
		return FunctionCyclomaticComplexity.ISSUE_TYPE;
	}

}
