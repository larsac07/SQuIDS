package autocisq.measure.maintainability;

import static org.junit.Assert.fail;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;

import autocisq.IssueFinder;
import autocisq.io.IOUtils;
import autocisq.measure.MeasureTest;

public class ClassCouplingTest extends MeasureTest {

	private String fileString;
	private CompilationUnit testCU;
	private List<CompilationUnit> coupling6;
	private List<CompilationUnit> coupling7;
	private List<CompilationUnit> coupling8;

	private IssueFinder issueFinder;

	@Before
	public void setUp() throws Exception {
		this.issueFinder = IssueFinder.getInstance();
		this.issueFinder.getMeasures().clear();
		this.issueFinder.putMeasure(new ClassCoupling(new HashMap<>()));

		File class1 = new File("res/test/coupling/Class1.java");
		File class2 = new File("res/test/coupling/Class2.java");
		File class3 = new File("res/test/coupling/Class3.java");
		File class4 = new File("res/test/coupling/Class4.java");
		File class5 = new File("res/test/coupling/Class5.java");
		File class6 = new File("res/test/coupling/Class6.java");
		File class7 = new File("res/test/coupling/Class7.java");
		File class8 = new File("res/test/coupling/Class8.java");
		File class9 = new File("res/test/coupling/Class9.java");

		this.fileString = IOUtils.fileToString(class1);

		CompilationUnit class1CU = JavaParser.parse(class1);
		CompilationUnit class2CU = JavaParser.parse(class2);
		CompilationUnit class3CU = JavaParser.parse(class3);
		CompilationUnit class4CU = JavaParser.parse(class4);
		CompilationUnit class5CU = JavaParser.parse(class5);
		CompilationUnit class6CU = JavaParser.parse(class6);
		CompilationUnit class7CU = JavaParser.parse(class7);
		CompilationUnit class8CU = JavaParser.parse(class8);
		CompilationUnit class9CU = JavaParser.parse(class9);

		this.testCU = class1CU;

		this.coupling6 = new ArrayList<>();
		this.coupling6.add(class1CU);
		this.coupling6.add(class2CU);
		this.coupling6.add(class3CU);
		this.coupling6.add(class4CU);
		this.coupling6.add(class5CU);
		this.coupling6.add(class6CU);
		this.coupling6.add(class7CU);

		this.coupling7 = new ArrayList<>();
		this.coupling7.addAll(this.coupling6);
		this.coupling7.add(class8CU);

		this.coupling8 = new ArrayList<>();
		this.coupling8.addAll(this.coupling7);
		this.coupling8.add(class9CU);
	}

	@Test
	public void skipStaticAndOrFinalFields() {
		fail("Not implemented yet");
	}

	@Test
	public void findInstanceVariableCoupling() {
		fail("Not implemented yet");
	}

	@Test
	public void findMethodCoupling() {
		fail("Not implemented yet");
	}

	@Test
	public void skipCoupling6() {
		dryRun(this.coupling6);
		skipIssue(this.testCU, this.fileString);
	}

	@Test
	public void skipCoupling7() {
		dryRun(this.coupling7);
		skipIssue(this.testCU, this.fileString);
	}

	@Test
	public void findCoupling8() {
		dryRun(this.coupling8);
		findIssue(this.testCU, this.fileString);
	}

	private void dryRun(List<CompilationUnit> compilationUnits) {
		this.issueFinder.setCompilationUnits(compilationUnits);
		for (CompilationUnit cu : compilationUnits) {
			this.issueFinder.analyzeNode(cu, null, cu.toString());
		}
	}

	@Override
	public String getIssueType() {
		return ClassCoupling.ISSUE_TYPE;
	}

}