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

public class CyclicCallBetweenPackagesTest extends MeasureTest {

	private MethodCallExpr cyclicCall1;
	private MethodCallExpr cyclicCall2;
	private MethodCallExpr samePackageCall1;
	private MethodCallExpr samePackageCall2;
	private CompilationUnit class1CU;
	private CompilationUnit class2CU;
	private CompilationUnit class3CU;
	private String fileStringClass1;
	private String fileStringClass2;
	private String fileStringClass3;

	@Before
	public void setUp() throws Exception {
		this.issueFinder.getMeasures().clear();
		this.issueFinder.putMeasure(new CyclicCallBetweenPackages(new HashMap<>()));

		File class1 = new File("res/test/cyclic/Class1.java");
		File class2 = new File("res/test/cyclic/Class2.java");
		File class3 = new File("res/test/cyclic/Class3.java");

		this.fileStringClass1 = IOUtils.fileToString(class1);
		this.fileStringClass2 = IOUtils.fileToString(class2);
		this.fileStringClass3 = IOUtils.fileToString(class3);

		this.class1CU = JavaParser.parse(class1);
		this.class2CU = JavaParser.parse(class2);
		this.class3CU = JavaParser.parse(class3);

		this.cyclicCall1 = (MethodCallExpr) this.class1CU.getTypes().get(0).getChildrenNodes().get(0).getChildrenNodes()
				.get(1).getChildrenNodes().get(1).getChildrenNodes().get(0);
		this.cyclicCall2 = (MethodCallExpr) this.class2CU.getTypes().get(0).getChildrenNodes().get(0).getChildrenNodes()
				.get(1).getChildrenNodes().get(1).getChildrenNodes().get(0);
		this.samePackageCall1 = (MethodCallExpr) this.class1CU.getTypes().get(0).getChildrenNodes().get(0)
				.getChildrenNodes().get(1).getChildrenNodes().get(3).getChildrenNodes().get(0);
		this.samePackageCall2 = (MethodCallExpr) this.class3CU.getTypes().get(0).getChildrenNodes().get(0)
				.getChildrenNodes().get(1).getChildrenNodes().get(1).getChildrenNodes().get(0);

	}

	@Test
	public void findCyclicCallDifferentPackage() {
		analyzeClasses(this.class2CU, this.class1CU);
		findIssue(this.cyclicCall1, this.fileStringClass1);
		analyzeClasses(this.class1CU, this.class2CU);
		findIssue(this.cyclicCall2, this.fileStringClass2);
	}

	@Test
	public void skipCyclicCallSamePackage() {
		analyzeClasses(this.class3CU, this.class1CU);
		skipIssue(this.samePackageCall1, this.fileStringClass1);
		analyzeClasses(this.class1CU, this.class3CU);
		skipIssue(this.samePackageCall2, this.fileStringClass3);
	}

	private void analyzeClasses(CompilationUnit... compilationUnits) {
		for (CompilationUnit cu : compilationUnits) {
			this.issueFinder.analyzeNode(cu, null, cu.toString());
		}
	}

	@Override
	public String getIssueType() {
		return CyclicCallBetweenPackages.ISSUE_TYPE;
	}

}
