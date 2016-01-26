package autocisq.measure.maintainability;

import java.io.File;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.MethodCallExpr;

import autocisq.IssueFinder;
import autocisq.io.IOUtils;
import autocisq.measure.MeasureTest;

public class CyclicCallBetweenPackagesTest extends MeasureTest {

	private MethodCallExpr cyclicCall1;
	private MethodCallExpr cyclicCall2;
	private MethodCallExpr samePackageCall1;
	private MethodCallExpr samePackageCall2;
	private String fileStringClass1;
	private String fileStringClass2;
	private String fileStringClass3;
	private IssueFinder issueFinder;

	@Before
	public void setUp() throws Exception {
		this.issueFinder = IssueFinder.getInstance();
		this.issueFinder.getMeasures().clear();
		this.issueFinder.putMeasure(new CyclicCallBetweenPackages(new HashMap<>()));

		File class1 = new File("res/test/cyclic/Class1.java");
		File class2 = new File("res/test/cyclic/Class2.java");
		File class3 = new File("res/test/cyclic/Class3.java");

		this.fileStringClass1 = IOUtils.fileToString(class1);
		this.fileStringClass2 = IOUtils.fileToString(class2);
		this.fileStringClass3 = IOUtils.fileToString(class3);

		CompilationUnit class1CU = JavaParser.parse(class1);
		CompilationUnit class2CU = JavaParser.parse(class2);
		CompilationUnit class3CU = JavaParser.parse(class3);

		this.cyclicCall1 = (MethodCallExpr) class1CU.getTypes().get(0).getChildrenNodes().get(0).getChildrenNodes()
				.get(1).getChildrenNodes().get(1).getChildrenNodes().get(0);
		this.cyclicCall2 = (MethodCallExpr) class2CU.getTypes().get(0).getChildrenNodes().get(0).getChildrenNodes()
				.get(1).getChildrenNodes().get(1).getChildrenNodes().get(0);
		this.samePackageCall1 = (MethodCallExpr) class1CU.getTypes().get(0).getChildrenNodes().get(0).getChildrenNodes()
				.get(1).getChildrenNodes().get(3).getChildrenNodes().get(0);
		this.samePackageCall2 = (MethodCallExpr) class3CU.getTypes().get(0).getChildrenNodes().get(0).getChildrenNodes()
				.get(1).getChildrenNodes().get(1).getChildrenNodes().get(0);

		dryRun(class1CU, class2CU, class3CU);
	}

	@Test
	public void findCyclicCallDifferentPackage() {
		findIssue(this.cyclicCall2, this.fileStringClass2);
	}

	@Test
	public void skipCyclicCallSamePackage() {
		skipIssue(this.samePackageCall2, this.fileStringClass3);
	}

	private void dryRun(CompilationUnit... compilationUnits) {
		for (CompilationUnit cu : compilationUnits) {
			this.issueFinder.analyzeNode(cu, null, cu.toString());
		}
	}

	@Override
	public String getIssueType() {
		return CyclicCallBetweenPackages.ISSUE_TYPE;
	}

}
