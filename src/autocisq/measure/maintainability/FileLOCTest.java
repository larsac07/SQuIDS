package autocisq.measure.maintainability;

import java.io.File;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;

import autocisq.IssueFinder;
import autocisq.io.IOUtils;
import autocisq.measure.MeasureTest;

public class FileLOCTest extends MeasureTest {
	private CompilationUnit cu1517;
	private CompilationUnit cu1001;
	private CompilationUnit cu1000;
	private String fileString1517;
	private String fileString1001;
	private String fileString1000;

	@Before
	public void setUp() throws Exception {
		IssueFinder issueFinder = IssueFinder.getInstance();
		issueFinder.getMeasures().clear();
		issueFinder.putMeasure(new FileLOC(new HashMap<>()));

		File testFile1517 = new File("res/test/DumpVisitor.java");
		File testFile1001 = new File("res/test/DumpVisitor1001.java");
		File testFile1000 = new File("res/test/DumpVisitor1000.java");

		this.fileString1517 = IOUtils.fileToString(testFile1517);
		this.fileString1001 = IOUtils.fileToString(testFile1001);
		this.fileString1000 = IOUtils.fileToString(testFile1000);

		this.cu1517 = JavaParser.parse(testFile1517);
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
	public void findFileWith1517Lines() {
		findIssue(this.cu1517, this.fileString1517);
	}

	@Override
	public String getIssueType() {
		return FileLOC.ISSUE_TYPE;
	}

}
