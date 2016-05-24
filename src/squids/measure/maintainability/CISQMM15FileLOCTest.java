package squids.measure.maintainability;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;

import squids.io.IOUtils;
import squids.measure.MeasureTest;

public class CISQMM15FileLOCTest extends MeasureTest {
	private CompilationUnit cu1512;
	private CompilationUnit cu1001;
	private CompilationUnit cu1000;
	private String fileString1512;
	private String fileString1001;
	private String fileString1000;

	@Before
	public void setUp() throws Exception {
		this.issueFinder.getMeasures().clear();
		this.issueFinder.putMeasure(new CISQMM15FileLOC(new HashMap<>()));

		File testFile1512 = new File("res/test/DumpVisitor.java");
		File testFile1001 = new File("res/test/DumpVisitor1001.java");
		File testFile1000 = new File("res/test/DumpVisitor1000.java");

		this.fileString1512 = IOUtils.fileToString(testFile1512);
		this.fileString1001 = IOUtils.fileToString(testFile1001);
		this.fileString1000 = IOUtils.fileToString(testFile1000);

		this.cu1512 = JavaParser.parse(testFile1512);
		this.cu1001 = JavaParser.parse(testFile1001);
		this.cu1000 = JavaParser.parse(testFile1000);
	}

	@Test
	public void skipFileWith1000Lines() {
		skipIssue(this.cu1000, this.fileString1000);
	}

	@Test
	public void findFileWith1001Lines() {
		findIssue(this.cu1001, this.fileString1001);
	}

	@Test
	public void findFileWith1512Lines() {
		findIssue(this.cu1512, this.fileString1512);
	}

	@Test
	public void findExactly1000() {
		int expected = 1000;
		int actual = CISQMM15FileLOC.calculatePhysicalLOC(this.fileString1000);
		assertEquals(expected, actual);
	}

	@Test
	public void findExactly1001() {
		int expected = 1001;
		int actual = CISQMM15FileLOC.calculatePhysicalLOC(this.fileString1001);
		assertEquals(expected, actual);
	}

	@Test
	public void findExactly1512() {
		int expected = 1512;
		int actual = CISQMM15FileLOC.calculatePhysicalLOC(this.fileString1512);
		assertEquals(expected, actual);
	}

	@Override
	public String getIssueType() {
		return CISQMM15FileLOC.ISSUE_TYPE;
	}

}
