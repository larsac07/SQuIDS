package autocisq.measure.maintainability;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;

import autocisq.IssueFinder;
import autocisq.io.IOUtils;
import autocisq.measure.MeasureTest;

public class FileDuplicateTokensTest extends MeasureTest {
	
	private CompilationUnit fileCU99;
	private CompilationUnit fileCU100;
	private CompilationUnit fileCU100Copy;
	private String file99String;
	private String file100String;
	private String file100CopyString;
	private IssueFinder issueFinder;

	@Before
	public void setUp() throws Exception {
		this.issueFinder = IssueFinder.getInstance();
		this.issueFinder.getMeasures().clear();
		this.issueFinder.putMeasure(new FileDuplicateTokens(new HashMap<>()));
		
		File testFile99 = new File("res/test/duplicate/Class1.java");
		File testFile100 = new File("res/test/duplicate/Class2.java");
		File testFile100Copy = new File("res/test/duplicate/Class3.java");
		
		this.file99String = IOUtils.fileToString(testFile99);
		this.file100String = IOUtils.fileToString(testFile100);
		this.file100CopyString = IOUtils.fileToString(testFile100Copy);
		
		this.fileCU99 = JavaParser.parse(testFile99);
		this.fileCU100 = JavaParser.parse(testFile100);
		this.fileCU100Copy = JavaParser.parse(testFile100Copy);
	}
	
	@Test
	public void skip99DuplicateTokens() {
		this.issueFinder.analyzeNode(this.fileCU100, this.issues, this.file100String);
		this.issueFinder.analyzeNode(this.fileCU100Copy, this.issues, this.file100CopyString);
		skipIssue(this.fileCU99, this.file99String);
	}
	
	@Test
	public void find100DuplicateTokens() {
		this.issueFinder.analyzeNode(this.fileCU100Copy, this.issues, this.file100CopyString);
		findIssue(this.fileCU100, this.file100String);
	}
	
	@Test
	public void markOncePerFile() {
		this.issueFinder.analyzeNode(this.fileCU99, this.issues, this.file99String);
		this.issueFinder.analyzeNode(this.fileCU100, this.issues, this.file100String);
		findIssue(this.fileCU100Copy, this.file100CopyString);
		assertTrue(this.issues.size() == 2);
	}

	@Override
	public String getIssueType() {
		return FileDuplicateTokens.ISSUE_TYPE;
	}
	
}
