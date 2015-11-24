package autocisq.junit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.stmt.CatchClause;

import autocisq.IssueFinder;
import autocisq.models.Issue;

public class IssueFinderTest {

	private String fileString = "";
	private File testFile;
	private List<String> lines = new ArrayList<>();
	private List<Issue> issues;

	public IssueFinderTest() {
		this.testFile = new File("res/test/EntropyManualCalculator.java");

		try {
			this.lines = Files.readAllLines(this.testFile.toPath());
		} catch (IOException e) {
			fail("Could not find file " + this.testFile.getAbsolutePath());
		}

		String nl = System.lineSeparator();
		for (String line : this.lines) {
			this.fileString += line + nl;
		}

		try {
			CompilationUnit compilationUnit = JavaParser.parse(this.testFile);
			Node tryStmt = compilationUnit.getTypes().get(0).getMembers().get(1).getChildrenNodes().get(3)
					.getChildrenNodes().get(4);
			this.issues = IssueFinder.analyzeNode(tryStmt, null, this.fileString);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Before
	public void setUp() throws Exception {

	}

	@Test
	public void testColumnsToIndexes() {
		int startLine = 1;
		int endLine = this.lines.size();
		int startExpected = 0;
		int endExpected = this.fileString.length();
		int[] indexes = IssueFinder.columnsToIndexes(this.fileString, startLine, endLine, 1, 2);
		assertEquals(startExpected, indexes[0]);
		assertEquals(endExpected, indexes[1]);
	}

	@Test
	public void testAnalyzeNode() {
		fail("Not yet implemented");
	}

	@Test
	public void testInspectCatchClause() {
		assertTrue(this.issues.size() == 2);
		assertEquals(this.issues.get(0).getBeginLine(), 28);
		assertEquals(this.issues.get(1).getBeginLine(), 31);
	}

	@Test
	public void findEmptyCatchClause() {
		boolean found = false;
		for (Issue issue : this.issues) {
			assertTrue(issue.getNode() instanceof CatchClause);
			if (issue.getType().equals("Empty Catch Block")) {
				found = true;
			}
		}
		assertTrue(found);
	}

	@Test
	public void findAutoGeneratedCatchClause() {
		boolean found = false;
		for (Issue issue : this.issues) {
			assertTrue(issue.getNode() instanceof CatchClause);
			if (issue.getType().equals("Auto Generated Catch Block")) {
				found = true;
			}
		}
		assertTrue(found);
	}
}
