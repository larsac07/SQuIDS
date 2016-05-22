package autocisq.measure.maintainability;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.expr.Expression;

import autocisq.io.IOUtils;
import autocisq.measure.MeasureTest;

public class CISQMM05MethodUnreachableTest extends MeasureTest {

	private MethodDeclaration methodPublicReferenced;
	private MethodDeclaration methodPublicUnreferenced;
	private MethodDeclaration methodPrivateReferenced;
	private MethodDeclaration methodPrivateUnreferenced;
	private MethodDeclaration methodInnerClassPublicReferenced;
	private MethodDeclaration methodInnerClassPublicUnreferenced;
	private MethodDeclaration methodInnerClassPrivateReferenced;
	private MethodDeclaration methodInnerClassPrivateUnreferenced;
	private ConstructorDeclaration constructorPublicReferenced;
	private ConstructorDeclaration constructorPublicUnreferenced;
	private ConstructorDeclaration constructorPrivateReferenced;
	private ConstructorDeclaration constructorPrivateUnreferenced;
	private ConstructorDeclaration constructorInnerClassPublicReferenced;
	private ConstructorDeclaration constructorInnerClassPublicUnreferenced;
	private ConstructorDeclaration constructorInnerClassPrivateReferenced;
	private ConstructorDeclaration constructorInnerClassPrivateUnreferenced;
	private CompilationUnit cu;
	private String fileString;
	private CISQMM05MethodUnreachable dummy;

	@Before
	public void setUp() throws Exception {
		this.dummy = new CISQMM05MethodUnreachable(new HashMap<>());
		this.issueFinder.getMeasures().clear();
		this.issueFinder.putMeasure(new CISQMM05MethodUnreachable(new HashMap<>()));

		File file = new File("res/test/UnreachableFunction.java");

		this.fileString = IOUtils.fileToString(file);

		this.cu = JavaParser.parse(file);
		TypeDeclaration outerClass = this.cu.getTypes().get(0);
		TypeDeclaration innerClass = (TypeDeclaration) outerClass.getMembers().get(0);

		this.methodPublicReferenced = (MethodDeclaration) outerClass.getMembers().get(1);
		this.methodPublicUnreferenced = (MethodDeclaration) outerClass.getMembers().get(2);
		this.methodPrivateReferenced = (MethodDeclaration) outerClass.getMembers().get(3);
		this.methodPrivateUnreferenced = (MethodDeclaration) outerClass.getMembers().get(4);
		this.methodInnerClassPublicReferenced = (MethodDeclaration) innerClass.getMembers().get(0);
		this.methodInnerClassPublicUnreferenced = (MethodDeclaration) innerClass.getMembers().get(1);
		this.methodInnerClassPrivateReferenced = (MethodDeclaration) innerClass.getMembers().get(2);
		this.methodInnerClassPrivateUnreferenced = (MethodDeclaration) innerClass.getMembers().get(3);
		this.constructorPublicReferenced = (ConstructorDeclaration) outerClass.getMembers().get(5);
		this.constructorPublicUnreferenced = (ConstructorDeclaration) outerClass.getMembers().get(6);
		this.constructorPrivateReferenced = (ConstructorDeclaration) outerClass.getMembers().get(7);
		this.constructorPrivateUnreferenced = (ConstructorDeclaration) outerClass.getMembers().get(8);
		this.constructorInnerClassPublicReferenced = (ConstructorDeclaration) innerClass.getMembers().get(4);
		this.constructorInnerClassPublicUnreferenced = (ConstructorDeclaration) innerClass.getMembers().get(5);
		this.constructorInnerClassPrivateReferenced = (ConstructorDeclaration) innerClass.getMembers().get(6);
		this.constructorInnerClassPrivateUnreferenced = (ConstructorDeclaration) innerClass.getMembers().get(7);

		this.issueFinder.analyzeNode(this.cu, null, this.fileString);
	}

	@Test
	public void skipPublicReferencedMethod() {
		skipIssue(this.methodPublicReferenced, this.fileString);
	}

	@Test
	public void skipPublicUnreferencedMethod() {
		skipIssue(this.methodPublicUnreferenced, this.fileString);
	}

	@Test
	public void skipPrivateReferencedMethod() {
		skipIssue(this.methodPrivateReferenced, this.fileString);
	}

	@Test
	public void findPrivateUnreferencedMethod() {
		findIssue(this.methodPrivateUnreferenced, this.fileString);
	}

	@Test
	public void skipInnerClassPublicReferencedMethod() {
		skipIssue(this.methodInnerClassPublicReferenced, this.fileString);
	}

	@Test
	public void findInnerClassPublicUnreferencedMethod() {
		findIssue(this.methodInnerClassPublicUnreferenced, this.fileString);
	}

	@Test
	public void skipInnerClassPrivateReferencedMethod() {
		skipIssue(this.methodInnerClassPrivateReferenced, this.fileString);
	}

	@Test
	public void findInnerClassPrivateUnreferencedMethod() {
		findIssue(this.methodInnerClassPrivateUnreferenced, this.fileString);
	}

	@Test
	public void skipPublicReferencedConstructor() {
		skipIssue(this.constructorPublicReferenced, this.fileString);
	}

	@Test
	public void skipPublicUnreferencedConstructor() {
		skipIssue(this.constructorPublicUnreferenced, this.fileString);
	}

	@Test
	public void skipPrivateReferencedConstructor() {
		skipIssue(this.constructorPrivateReferenced, this.fileString);
	}

	@Test
	public void findPrivateUnreferencedConstructor() {
		findIssue(this.constructorPrivateUnreferenced, this.fileString);
	}

	@Test
	public void skipInnerClassPublicReferencedConstructor() {
		skipIssue(this.constructorInnerClassPublicReferenced, this.fileString);
	}

	@Test
	public void findInnerClassPublicUnreferencedConstructor() {
		findIssue(this.constructorInnerClassPublicUnreferenced, this.fileString);
	}

	@Test
	public void skipInnerClassPrivateReferencedConstructor() {
		skipIssue(this.constructorInnerClassPrivateReferenced, this.fileString);
	}

	@Test
	public void findInnerClassPrivateUnreferencedConstructor() {
		findIssue(this.constructorInnerClassPrivateUnreferenced, this.fileString);
	}

	@Test
	public void testGetVariableNameNotNullNameExpr() throws ParseException, IOException {
		CompilationUnit cu = JavaParser.parse(new File("res/test/HardCodedLiterals.java"));
		Expression scope = (Expression) cu.getTypes().get(0).getMembers().get(13).getChildrenNodes().get(1)
				.getChildrenNodes().get(0).getChildrenNodes().get(0).getChildrenNodes().get(0);
		assertNotNull(this.dummy.getVariableName(scope));
	}

	@Test
	public void testGetVariableNameNotNullFieldAccessExpr() throws ParseException, IOException {
		CompilationUnit cu = JavaParser.parse(new File("res/test/HardCodedLiterals.java"));
		Expression scope = (Expression) cu.getTypes().get(0).getMembers().get(13).getChildrenNodes().get(1)
				.getChildrenNodes().get(0).getChildrenNodes().get(0).getChildrenNodes().get(0);
		assertNotNull(this.dummy.getVariableName(scope));
	}

	@Test
	public void testGetVariableNullAssignExpr() throws ParseException, IOException {
		CompilationUnit cu = JavaParser.parse(new File("res/test/HardCodedLiterals.java"));
		Expression scope = (Expression) cu.getTypes().get(0).getMembers().get(13).getChildrenNodes().get(1)
				.getChildrenNodes().get(0).getChildrenNodes().get(0);
		assertNull(this.dummy.getVariableName(scope));
	}

	@Test
	public void testArgsToTypesEmptyArgs() {
		List<String> expected = new ArrayList<>();
		List<String> actual = this.dummy.argsToTypes(null, null);
		assertEquals(expected, actual);
	}

	@Override
	public String getIssueType() {
		return CISQMM05MethodUnreachable.ISSUE_TYPE;
	}

}
