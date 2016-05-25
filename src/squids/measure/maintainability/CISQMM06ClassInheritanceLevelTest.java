package squids.measure.maintainability;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;

import squids.io.IOUtils;
import squids.measure.MeasureTest;

/**
 * Unit test class for {@link CISQMM06ClassInheritanceLevel}
 *
 * @author Lars A. V. Cabrera
 */
public class CISQMM06ClassInheritanceLevelTest extends MeasureTest {

	private CompilationUnit classIL6;
	private CompilationUnit classIL7;
	private CompilationUnit classIL8;
	private String classIL6String;
	private String classIL7String;
	private String classIL8String;

	@Before
	public void setUp() throws Exception {
		this.issueFinder.getMeasures().clear();
		this.issueFinder.putMeasure(new CISQMM06ClassInheritanceLevel(new HashMap<>()));

		File class1 = new File("res/test/inheritance/levels/Class1.java");
		File class2 = new File("res/test/inheritance/levels/Class2.java");
		File class3 = new File("res/test/inheritance/levels/Class3.java");
		File class4 = new File("res/test/inheritance/levels/Class4.java");
		File class5 = new File("res/test/inheritance/levels/Class5.java");
		File class6 = new File("res/test/inheritance/levels/Class6.java");
		File class7 = new File("res/test/inheritance/levels/Class7.java");
		File class8 = new File("res/test/inheritance/levels/Class8.java");
		File class9 = new File("res/test/inheritance/levels/Class9.java");

		CompilationUnit class1CU = JavaParser.parse(class1);
		CompilationUnit class2CU = JavaParser.parse(class2);
		CompilationUnit class3CU = JavaParser.parse(class3);
		CompilationUnit class4CU = JavaParser.parse(class4);
		CompilationUnit class5CU = JavaParser.parse(class5);
		CompilationUnit class6CU = JavaParser.parse(class6);
		CompilationUnit class7CU = JavaParser.parse(class7);
		CompilationUnit class8CU = JavaParser.parse(class8);
		CompilationUnit class9CU = JavaParser.parse(class9);

		List<CompilationUnit> compilationUnits = new ArrayList<>();
		compilationUnits.add(class1CU);
		compilationUnits.add(class2CU);
		compilationUnits.add(class3CU);
		compilationUnits.add(class4CU);
		compilationUnits.add(class5CU);
		compilationUnits.add(class6CU);
		compilationUnits.add(class7CU);
		compilationUnits.add(class8CU);
		compilationUnits.add(class9CU);

		this.classIL6String = IOUtils.fileToString(class7);
		this.classIL7String = IOUtils.fileToString(class8);
		this.classIL8String = IOUtils.fileToString(class9);

		this.classIL6 = class7CU;
		this.classIL7 = class8CU;
		this.classIL8 = class9CU;

		this.issueFinder.setCompilationUnits(compilationUnits);
	}

	/**
	 * Let classes with inheritance level of 6 pass (6 < threshold).
	 *
	 * Uses
	 * {@link MeasureTest#skipIssue(com.github.javaparser.ast.Node, String)}
	 */
	@Test
	public void skipClassIL6() {
		skipIssue(this.classIL6, this.classIL6String);
	}

	/**
	 * Report a problem for classes with inheritance level 7 (7 == threshold).
	 *
	 * Uses
	 * {@link MeasureTest#findIssue(com.github.javaparser.ast.Node, String)}
	 */
	@Test
	public void findClassIL7() {
		findIssue(this.classIL7, this.classIL7String);
	}

	/**
	 * Report a problem for classes with inheritance level 8 (8 > threshold).
	 *
	 * Uses
	 * {@link MeasureTest#findIssue(com.github.javaparser.ast.Node, String)}
	 */
	@Test
	public void findClassIL8() {
		findIssue(this.classIL8, this.classIL8String);
	}

	/**
	 * Used in superclass {@link MeasureTest} to determine if the correct issue
	 * was found
	 * {@link MeasureTest#findIssue(com.github.javaparser.ast.Node, String)} and
	 * {@link MeasureTest#skipIssue(com.github.javaparser.ast.Node, String)}
	 */
	@Override
	public String getIssueType() {
		return CISQMM06ClassInheritanceLevel.ISSUE_TYPE;
	}
}