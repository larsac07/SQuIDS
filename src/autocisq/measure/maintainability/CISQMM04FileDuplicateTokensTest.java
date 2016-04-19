package autocisq.measure.maintainability;

import java.io.File;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;

import autocisq.io.IOUtils;
import autocisq.measure.MeasureTest;

public class CISQMM04FileDuplicateTokensTest extends MeasureTest {

	private CompilationUnit fileCU99;
	private CompilationUnit fileCU100;
	private String file99String;
	private String file100String;

	@Before
	public void setUp() throws Exception {
		this.issueFinder.getMeasures().clear();
		this.issueFinder.putMeasure(new CISQMM04FileDuplicateTokens(new HashMap<>()));

		File testFile99 = new File("res/test/duplicate/Class1.java");
		File testFile100 = new File("res/test/duplicate/Class2.java");

		this.file99String = IOUtils.fileToString(testFile99);
		this.file100String = IOUtils.fileToString(testFile100);

		this.fileCU99 = JavaParser.parse(testFile99);
		this.fileCU100 = JavaParser.parse(testFile100);
	}

	@Test
	public void skip99DuplicateTokens() {
		skipIssue(this.fileCU99, this.file99String);
	}

	@Test
	public void find100DuplicateTokens() {
		findIssue(this.fileCU100, this.file100String);
	}

	@Override
	public String getIssueType() {
		return CISQMM04FileDuplicateTokens.ISSUE_TYPE;
	}

}
