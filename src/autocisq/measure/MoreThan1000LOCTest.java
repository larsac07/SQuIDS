package autocisq.measure;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;

import autocisq.IssueFinder;
import autocisq.io.IOUtils;
import autocisq.models.Issue;

public class MoreThan1000LOCTest {

	private List<Issue> issues;
	private MoreThan1000LOC moreThan1000LOC = new MoreThan1000LOC();
	private CompilationUnit cu1702;
	private CompilationUnit cu1001;
	private CompilationUnit cu1000;
	private String fileString1702;
	private String fileString1001;
	private String fileString1000;
	
	@Before
	public void setUp() throws Exception {
		IssueFinder issueFinder = IssueFinder.getInstance();
		if (issueFinder.getMeasures().isEmpty()) {
			issueFinder.getMeasures().add(this.moreThan1000LOC);
		}
		File testFile1702 = new File("res/test/DumpVisitor.java");
		File testFile1001 = new File("res/test/DumpVisitor1001.java");
		File testFile1000 = new File("res/test/DumpVisitor1000.java");

		this.fileString1702 = IOUtils.fileToString(testFile1702);
		this.fileString1001 = IOUtils.fileToString(testFile1001);
		this.fileString1000 = IOUtils.fileToString(testFile1000);

		this.cu1702 = JavaParser.parse(testFile1702);
		this.cu1001 = JavaParser.parse(testFile1001);
		this.cu1000 = JavaParser.parse(testFile1000);
	}
	
	@Test
	public void skipFileWith1000Lines() {
		this.issues = IssueFinder.getInstance().analyzeNode(this.cu1000, null, this.fileString1000);
		assertFalse(this.issues.size() > 0);
	}

	@Test
	public void findFileWith1001Lines() {
		this.issues = IssueFinder.getInstance().analyzeNode(this.cu1001, null, this.fileString1001);
		assertTrue(this.issues.size() > 0);
		boolean found = false;
		for (Issue issue : this.issues) {
			if (issue.getType().equals("More than 1000 Lines of Code")) {
				found = true;
			}
		}
		assertTrue(found);
	}

	@Test
	public void findFileWith1702Lines() {
		this.issues = IssueFinder.getInstance().analyzeNode(this.cu1702, null, this.fileString1702);
		assertTrue(this.issues.size() > 0);
		boolean found = false;
		for (Issue issue : this.issues) {
			if (issue.getType().equals("More than 1000 Lines of Code")) {
				found = true;
			}
		}
		assertTrue(found);
	}
	
}
