package autocisq.measure.maintainability;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;

import autocisq.IssueFinder;
import autocisq.io.IOUtils;
import autocisq.measure.MeasureTest;

public class HardCodedLiteralTest extends MeasureTest {
	
	private FieldDeclaration fieldVariableInteger;
	private FieldDeclaration fieldStaticInteger;
	private FieldDeclaration fieldConstantInteger;
	private FieldDeclaration fieldVariableDouble;
	private FieldDeclaration fieldVariableString;
	private FieldDeclaration fieldVariableIntegerMinus1;
	private FieldDeclaration fieldVariableInteger0;
	private FieldDeclaration fieldVariableInteger1;
	private FieldDeclaration fieldVariableInteger2;
	private ExpressionStmt variableInteger;
	private ExpressionStmt staticInteger;
	private ExpressionStmt constantInteger;
	private ExpressionStmt variableDouble;
	private ExpressionStmt variableString;
	private ExpressionStmt variableIntegerMinus1;
	private ExpressionStmt variableInteger0;
	private ExpressionStmt variableInteger1;
	private ExpressionStmt variableInteger2;
	private ExpressionStmt assignVariableInteger;
	private ExpressionStmt assignStaticInteger;
	private ExpressionStmt assignConstantInteger;
	private ExpressionStmt assignVariableDouble;
	private ExpressionStmt assignVariableString;
	private ExpressionStmt assignVariableIntegerMinus1;
	private ExpressionStmt assignVariableInteger0;
	private ExpressionStmt assignVariableInteger1;
	private ExpressionStmt assignVariableInteger2;
	private ExpressionStmt assignFieldVariableInteger;
	private ExpressionStmt assignFieldStaticInteger;
	private ExpressionStmt assignFieldConstantInteger;
	private ExpressionStmt assignFieldVariableDouble;
	private ExpressionStmt assignFieldVariableString;
	private ExpressionStmt assignFieldVariableIntegerMinus1;
	private ExpressionStmt assignFieldVariableInteger0;
	private ExpressionStmt assignFieldVariableInteger1;
	private ExpressionStmt assignFieldVariableInteger2;

	@Before
	public void setUp() throws Exception {
		IssueFinder issueFinder = IssueFinder.getInstance();
		issueFinder.getMeasures().clear();
		issueFinder.putMeasure(new HardCodedLiteral());
		
		File testFile = new File("res/test/HardCodedLiterals.java");
		
		this.fileString = IOUtils.fileToString(testFile);
		
		CompilationUnit hclCU = JavaParser.parse(testFile);
		
		this.fieldVariableInteger = (FieldDeclaration) hclCU.getTypes().get(0).getChildrenNodes().get(0);
		this.fieldStaticInteger = (FieldDeclaration) hclCU.getTypes().get(0).getChildrenNodes().get(1);
		this.fieldConstantInteger = (FieldDeclaration) hclCU.getTypes().get(0).getChildrenNodes().get(2);
		this.fieldVariableDouble = (FieldDeclaration) hclCU.getTypes().get(0).getChildrenNodes().get(3);
		this.fieldVariableString = (FieldDeclaration) hclCU.getTypes().get(0).getChildrenNodes().get(4);
		this.fieldVariableIntegerMinus1 = (FieldDeclaration) hclCU.getTypes().get(0).getChildrenNodes().get(5);
		this.fieldVariableInteger0 = (FieldDeclaration) hclCU.getTypes().get(0).getChildrenNodes().get(6);
		this.fieldVariableInteger1 = (FieldDeclaration) hclCU.getTypes().get(0).getChildrenNodes().get(7);
		this.fieldVariableInteger2 = (FieldDeclaration) hclCU.getTypes().get(0).getChildrenNodes().get(8);
		
		MethodDeclaration methodA = (MethodDeclaration) hclCU.getTypes().get(0).getChildrenNodes().get(9);
		BlockStmt methodABlock = methodA.getBody();
		this.variableInteger = (ExpressionStmt) methodABlock.getChildrenNodes().get(0);
		this.staticInteger = (ExpressionStmt) methodABlock.getChildrenNodes().get(1);
		this.constantInteger = (ExpressionStmt) methodABlock.getChildrenNodes().get(2);
		this.variableDouble = (ExpressionStmt) methodABlock.getChildrenNodes().get(3);
		this.variableString = (ExpressionStmt) methodABlock.getChildrenNodes().get(4);
		this.variableIntegerMinus1 = (ExpressionStmt) methodABlock.getChildrenNodes().get(5);
		this.variableInteger0 = (ExpressionStmt) methodABlock.getChildrenNodes().get(6);
		this.variableInteger1 = (ExpressionStmt) methodABlock.getChildrenNodes().get(7);
		this.variableInteger2 = (ExpressionStmt) methodABlock.getChildrenNodes().get(8);
		
		this.assignVariableInteger = (ExpressionStmt) methodABlock.getChildrenNodes().get(9);
		this.assignStaticInteger = (ExpressionStmt) methodABlock.getChildrenNodes().get(10);
		this.assignConstantInteger = (ExpressionStmt) methodABlock.getChildrenNodes().get(11);
		this.assignVariableDouble = (ExpressionStmt) methodABlock.getChildrenNodes().get(12);
		this.assignVariableString = (ExpressionStmt) methodABlock.getChildrenNodes().get(13);
		this.assignVariableIntegerMinus1 = (ExpressionStmt) methodABlock.getChildrenNodes().get(14);
		this.assignVariableInteger0 = (ExpressionStmt) methodABlock.getChildrenNodes().get(14);
		this.assignVariableInteger1 = (ExpressionStmt) methodABlock.getChildrenNodes().get(15);
		this.assignVariableInteger2 = (ExpressionStmt) methodABlock.getChildrenNodes().get(16);
		
		MethodDeclaration methodB = (MethodDeclaration) hclCU.getTypes().get(0).getChildrenNodes().get(10);
		BlockStmt methodBBlock = methodB.getBody();
		this.assignFieldVariableInteger = (ExpressionStmt) methodBBlock.getChildrenNodes().get(0);
		this.assignFieldStaticInteger = (ExpressionStmt) methodBBlock.getChildrenNodes().get(1);
		this.assignFieldConstantInteger = (ExpressionStmt) methodBBlock.getChildrenNodes().get(2);
		this.assignFieldVariableDouble = (ExpressionStmt) methodBBlock.getChildrenNodes().get(3);
		this.assignFieldVariableString = (ExpressionStmt) methodBBlock.getChildrenNodes().get(4);
		this.assignFieldVariableIntegerMinus1 = (ExpressionStmt) methodBBlock.getChildrenNodes().get(5);
		this.assignFieldVariableInteger0 = (ExpressionStmt) methodBBlock.getChildrenNodes().get(6);
		this.assignFieldVariableInteger1 = (ExpressionStmt) methodBBlock.getChildrenNodes().get(7);
		this.assignFieldVariableInteger2 = (ExpressionStmt) methodBBlock.getChildrenNodes().get(8);
		
	}
	
	@Test
	public void findFieldVariableInteger() {
		findIssue(this.fieldVariableInteger);
	}
	
	@Test
	public void skipStaticField() {
		skipIssue(this.fieldStaticInteger);
	}

	@Test
	public void skipConstantField() {
		skipIssue(this.fieldConstantInteger);
	}
	
	@Test
	public void findFieldVariableDouble() {
		findIssue(this.fieldVariableDouble);
	}
	
	@Test
	public void findFieldVariableString() {
		findIssue(this.fieldVariableString);
	}

	@Test
	public void skipFieldVariableIntegerMinus1() {
		skipIssue(this.fieldVariableIntegerMinus1);
	}
	
	@Test
	public void skipFieldVariableInteger0() {
		skipIssue(this.fieldVariableInteger0);
	}
	
	@Test
	public void skipFieldVariableInteger1() {
		skipIssue(this.fieldVariableInteger1);
	}
	
	@Test
	public void skipFieldVariableInteger2() {
		skipIssue(this.fieldVariableInteger2);
	}
	
	@Test
	public void findVariableInteger() {
		findIssue(this.variableInteger);
	}
	
	@Test
	public void skipStatic() {
		skipIssue(this.staticInteger);
	}

	@Test
	public void skipConstant() {
		skipIssue(this.constantInteger);
	}
	
	@Test
	public void findVariableDouble() {
		findIssue(this.variableDouble);
	}
	
	@Test
	public void findVariableString() {
		findIssue(this.variableString);
	}

	@Test
	public void skipVariableIntegerMinus1() {
		skipIssue(this.variableIntegerMinus1);
	}
	
	@Test
	public void skipVariableInteger0() {
		skipIssue(this.variableInteger0);
	}
	
	@Test
	public void skipVariableInteger1() {
		skipIssue(this.variableInteger1);
	}
	
	@Test
	public void skipVariableInteger2() {
		skipIssue(this.variableInteger2);
	}

	@Test
	public void skipAssignVariableInteger() {
		skipIssue(this.assignVariableInteger);
	}
	
	@Test
	public void skipAssignStatic() {
		skipIssue(this.assignStaticInteger);
	}

	@Test
	public void skipAssignConstant() {
		skipIssue(this.assignConstantInteger);
	}
	
	@Test
	public void skipAssignVariableDouble() {
		skipIssue(this.assignVariableDouble);
	}
	
	@Test
	public void skipAssignVariableString() {
		skipIssue(this.assignVariableString);
	}

	@Test
	public void skipAssignVariableIntegerMinus1() {
		skipIssue(this.assignVariableIntegerMinus1);
	}
	
	@Test
	public void skipAssignVariableInteger0() {
		skipIssue(this.assignVariableInteger0);
	}
	
	@Test
	public void skipAssignVariableInteger1() {
		skipIssue(this.assignVariableInteger1);
	}
	
	@Test
	public void skipAssignVariableInteger2() {
		skipIssue(this.assignVariableInteger2);
	}
	
	@Test
	public void skipAssignFieldVariableInteger() {
		skipIssue(this.assignFieldVariableInteger);
	}
	
	@Test
	public void skipAssignStaticField() {
		skipIssue(this.assignFieldStaticInteger);
	}

	@Test
	public void skipAssignConstantField() {
		skipIssue(this.assignFieldConstantInteger);
	}
	
	@Test
	public void skipAssignFieldVariableDouble() {
		skipIssue(this.assignFieldVariableDouble);
	}
	
	@Test
	public void skipAssignFieldVariableString() {
		skipIssue(this.assignFieldVariableString);
	}

	@Test
	public void skipAssignFieldVariableIntegerMinus1() {
		skipIssue(this.assignFieldVariableIntegerMinus1);
	}
	
	@Test
	public void skipAssignFieldVariableInteger0() {
		skipIssue(this.assignFieldVariableInteger0);
	}
	
	@Test
	public void skipAssignFieldVariableInteger1() {
		skipIssue(this.assignFieldVariableInteger1);
	}
	
	@Test
	public void skipAssignFieldVariableInteger2() {
		skipIssue(this.assignFieldVariableInteger2);
	}
	
	@Override
	public String getIssueType() {
		return HardCodedLiteral.ISSUE_TYPE;
	}
	
}
