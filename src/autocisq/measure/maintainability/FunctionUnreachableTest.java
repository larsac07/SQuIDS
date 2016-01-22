package autocisq.measure.maintainability;

import java.io.File;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;

import autocisq.IssueFinder;
import autocisq.io.IOUtils;
import autocisq.measure.MeasureTest;

public class FunctionUnreachableTest extends MeasureTest {
	
	private MethodDeclaration methodPublicReferenced;
	private MethodDeclaration methodPublicUnreferenced;
	private MethodDeclaration methodPrivateReferenced;
	private MethodDeclaration methodPrivateUnreferenced;
	private MethodDeclaration methodInnerClassPublicReferenced;
	private MethodDeclaration methodInnerClassPublicUnreferenced;
	private MethodDeclaration methodInnerClassPrivateReferenced;
	private MethodDeclaration methodInnerClassPrivateUnreferenced;
	private CompilationUnit cu;
	private String fileString;
	private IssueFinder issueFinder;
	
	@Before
	public void setUp() throws Exception {
		this.issueFinder = IssueFinder.getInstance();
		this.issueFinder.getMeasures().clear();
		this.issueFinder.putMeasure(new FunctionUnreachable(new HashMap<>()));
		
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
	
	@Override
	public String getIssueType() {
		return FunctionUnreachable.ISSUE_TYPE;
	}

}
