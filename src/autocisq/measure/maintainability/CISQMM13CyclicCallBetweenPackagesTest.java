package autocisq.measure.maintainability;

import java.io.File;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.MethodCallExpr;

import autocisq.io.IOUtils;
import autocisq.measure.MeasureTest;

public class CISQMM13CyclicCallBetweenPackagesTest extends MeasureTest {

	private MethodCallExpr reflexiveCall1;
	private MethodCallExpr reflexiveCall2;
	private MethodCallExpr cyclicCallStep1;
	private MethodCallExpr cyclicCallStep2;
	private MethodCallExpr cyclicCallStep3;
	private MethodCallExpr samePackageCall1;
	private MethodCallExpr samePackageCall2;
	private CompilationUnit class1CU;
	private CompilationUnit class2CU;
	private CompilationUnit class3CU;
	private CompilationUnit class4CU;
	private String fileStringClass1;
	private String fileStringClass2;
	private String fileStringClass3;
	private String fileStringClass4;

	@Before
	public void setUp() throws Exception {
		this.issueFinder.getMeasures().clear();
		this.issueFinder.putMeasure(new CISQMM13CyclicCallBetweenPackages(new HashMap<>()));

		File class1 = new File("res/test/cyclic/Class1.java");
		File class2 = new File("res/test/cyclic/Class2.java");
		File class3 = new File("res/test/cyclic/Class3.java");
		File class4 = new File("res/test/cyclic/Class4.java");

		this.fileStringClass1 = IOUtils.fileToString(class1);
		this.fileStringClass2 = IOUtils.fileToString(class2);
		this.fileStringClass3 = IOUtils.fileToString(class3);
		this.fileStringClass4 = IOUtils.fileToString(class4);

		this.class1CU = JavaParser.parse(class1);
		this.class2CU = JavaParser.parse(class2);
		this.class3CU = JavaParser.parse(class3);
		this.class4CU = JavaParser.parse(class4);

		this.reflexiveCall1 = (MethodCallExpr) this.class1CU.getTypes().get(0).getChildrenNodes().get(0)
				.getChildrenNodes().get(1).getChildrenNodes().get(1).getChildrenNodes().get(0);
		this.reflexiveCall2 = (MethodCallExpr) this.class2CU.getTypes().get(0).getChildrenNodes().get(0)
				.getChildrenNodes().get(1).getChildrenNodes().get(1).getChildrenNodes().get(0);
		this.cyclicCallStep1 = (MethodCallExpr) this.class1CU.getTypes().get(0).getChildrenNodes().get(0)
				.getChildrenNodes().get(1).getChildrenNodes().get(5).getChildrenNodes().get(0);
		this.cyclicCallStep2 = (MethodCallExpr) this.class4CU.getTypes().get(0).getChildrenNodes().get(0)
				.getChildrenNodes().get(1).getChildrenNodes().get(1).getChildrenNodes().get(0);
		this.cyclicCallStep3 = this.reflexiveCall2;
		this.samePackageCall1 = (MethodCallExpr) this.class1CU.getTypes().get(0).getChildrenNodes().get(0)
				.getChildrenNodes().get(1).getChildrenNodes().get(3).getChildrenNodes().get(0);
		this.samePackageCall2 = (MethodCallExpr) this.class3CU.getTypes().get(0).getChildrenNodes().get(0)
				.getChildrenNodes().get(1).getChildrenNodes().get(1).getChildrenNodes().get(0);
	}

	@Test
	public void findReflexiveCallDifferentPackage() {
		analyzeClasses(this.class2CU, this.class1CU);
		findIssue(this.reflexiveCall1, this.fileStringClass1);
		analyzeClasses(this.class1CU, this.class2CU);
		findIssue(this.reflexiveCall2, this.fileStringClass2);
	}

	@Test
	public void skipReflexiveCallSamePackage() {
		analyzeClasses(this.class3CU, this.class1CU);
		skipIssue(this.samePackageCall1, this.fileStringClass1);
		analyzeClasses(this.class1CU, this.class3CU);
		skipIssue(this.samePackageCall2, this.fileStringClass3);
	}

	/**
	 * Class 1 calls class 4.
	 *
	 * Class 4 calls class 2.
	 *
	 * Class 2 calls class 1.
	 */
	@Test
	public void findCyclicCallDifferentPackage() {
		analyzeClasses(this.class2CU, this.class4CU, this.class1CU);
		findIssue(this.cyclicCallStep1, this.fileStringClass1);
		analyzeClasses(this.class1CU, this.class2CU, this.class4CU);
		findIssue(this.cyclicCallStep2, this.fileStringClass4);
		analyzeClasses(this.class1CU, this.class4CU, this.class2CU);
		findIssue(this.cyclicCallStep3, this.fileStringClass1);
	}

	private void analyzeClasses(CompilationUnit... compilationUnits) {
		for (CompilationUnit cu : compilationUnits) {
			this.issueFinder.analyzeNode(cu, null, cu.toString());
		}
	}

	@Override
	public String getIssueType() {
		return CISQMM13CyclicCallBetweenPackages.ISSUE_TYPE;
	}

}
