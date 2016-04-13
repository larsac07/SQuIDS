package autocisq.measure.maintainability;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;

import autocisq.io.IOUtils;
import autocisq.measure.MeasureTest;

public class ClassTooManyChildrenTest extends MeasureTest {

	private List<String> fileStrings;
	private List<CompilationUnit> children9;
	private List<CompilationUnit> children10;
	private List<CompilationUnit> children11;
	private List<CompilationUnit> children9WithOtherClass;
	private CompilationUnit superClassCU;
	private String fileStringSuperClass;

	@Before
	public void setUp() throws Exception {
		this.issueFinder.getMeasures().clear();
		this.issueFinder.putMeasure(new ClassTooManyChildren(new HashMap<>()));

		File superClass = new File("res/test/inheritance/SuperClass.java");
		File subClass1 = new File("res/test/inheritance/SubClass1.java");
		File subClass2 = new File("res/test/inheritance/SubClass2.java");
		File subClass3 = new File("res/test/inheritance/SubClass3.java");
		File subClass4 = new File("res/test/inheritance/SubClass4.java");
		File subClass5 = new File("res/test/inheritance/SubClass5.java");
		File subClass6 = new File("res/test/inheritance/SubClass6.java");
		File subClass7 = new File("res/test/inheritance/SubClass7.java");
		File subClass8 = new File("res/test/inheritance/SubClass8.java");
		File subClass9 = new File("res/test/inheritance/SubClass9.java");
		File subClass10 = new File("res/test/inheritance/SubClass10.java");
		File subClass11 = new File("res/test/inheritance/SubClass11.java");
		File subClassOther = new File("res/test/inheritance/SubClassOther.java");

		this.fileStringSuperClass = IOUtils.fileToString(superClass);
		String fileStringSubClass1 = IOUtils.fileToString(subClass1);
		String fileStringSubClass2 = IOUtils.fileToString(subClass2);
		String fileStringSubClass3 = IOUtils.fileToString(subClass3);
		String fileStringSubClass4 = IOUtils.fileToString(subClass4);
		String fileStringSubClass5 = IOUtils.fileToString(subClass5);
		String fileStringSubClass6 = IOUtils.fileToString(subClass6);
		String fileStringSubClass7 = IOUtils.fileToString(subClass7);
		String fileStringSubClass8 = IOUtils.fileToString(subClass8);
		String fileStringSubClass9 = IOUtils.fileToString(subClass9);
		String fileStringSubClass10 = IOUtils.fileToString(subClass10);
		String fileStringSubClass11 = IOUtils.fileToString(subClass11);
		String fileStringSubClassOther = IOUtils.fileToString(subClassOther);

		this.fileStrings = new ArrayList<>();
		this.fileStrings.add(this.fileStringSuperClass);
		this.fileStrings.add(fileStringSubClass1);
		this.fileStrings.add(fileStringSubClass2);
		this.fileStrings.add(fileStringSubClass3);
		this.fileStrings.add(fileStringSubClass4);
		this.fileStrings.add(fileStringSubClass5);
		this.fileStrings.add(fileStringSubClass6);
		this.fileStrings.add(fileStringSubClass7);
		this.fileStrings.add(fileStringSubClass8);
		this.fileStrings.add(fileStringSubClass9);
		this.fileStrings.add(fileStringSubClass10);
		this.fileStrings.add(fileStringSubClass11);
		this.fileStrings.add(fileStringSubClassOther);

		this.superClassCU = JavaParser.parse(superClass);
		CompilationUnit subClass1CU = JavaParser.parse(subClass1);
		CompilationUnit subClass2CU = JavaParser.parse(subClass2);
		CompilationUnit subClass3CU = JavaParser.parse(subClass3);
		CompilationUnit subClass4CU = JavaParser.parse(subClass4);
		CompilationUnit subClass5CU = JavaParser.parse(subClass5);
		CompilationUnit subClass6CU = JavaParser.parse(subClass6);
		CompilationUnit subClass7CU = JavaParser.parse(subClass7);
		CompilationUnit subClass8CU = JavaParser.parse(subClass8);
		CompilationUnit subClass9CU = JavaParser.parse(subClass9);
		CompilationUnit subClass10CU = JavaParser.parse(subClass10);
		CompilationUnit subClass11CU = JavaParser.parse(subClass11);
		CompilationUnit subClassOtherCU = JavaParser.parse(subClassOther);

		this.children9 = new ArrayList<>();
		this.children9.add(this.superClassCU);
		this.children9.add(subClass1CU);
		this.children9.add(subClass2CU);
		this.children9.add(subClass3CU);
		this.children9.add(subClass4CU);
		this.children9.add(subClass5CU);
		this.children9.add(subClass6CU);
		this.children9.add(subClass7CU);
		this.children9.add(subClass8CU);
		this.children9.add(subClass9CU);

		this.children10 = new ArrayList<>();
		this.children10.addAll(this.children9);
		this.children10.add(subClass10CU);

		this.children11 = new ArrayList<>();
		this.children11.addAll(this.children10);
		this.children11.add(subClass11CU);

		this.children9WithOtherClass = new ArrayList<>();
		this.children9WithOtherClass.addAll(this.children9);
		this.children9WithOtherClass.add(subClassOtherCU);

	}

	@Test
	public void skip9Children() {
		this.issueFinder.setCompilationUnits(this.children9);
		skipIssue(this.superClassCU, this.fileStringSuperClass);
	}

	@Test
	public void find10Children() {
		this.issueFinder.setCompilationUnits(this.children10);
		findIssue(this.superClassCU, this.fileStringSuperClass);
	}

	@Test
	public void find11Children() {
		this.issueFinder.setCompilationUnits(this.children11);
		findIssue(this.superClassCU, this.fileStringSuperClass);
	}

	@Override
	public String getIssueType() {
		return ClassTooManyChildren.ISSUE_TYPE;
	}

}
