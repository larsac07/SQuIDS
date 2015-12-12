package autocisq.junit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.CatchClause;

import autocisq.IssueFinder;
import autocisq.NoAncestorFoundException;
import autocisq.models.Issue;

public class IssueFinderTest {

	private String fileString = "";
	private File testFile;
	private File layerTestFile;
	private List<File> layerTestFiles;
	private List<String> lines = new ArrayList<>();
	private List<Issue> issues;
	private CompilationUnit compilationUnit;
	private CompilationUnit layerCompilationUnit;
	private CatchClause catchClause;
	private LinkedHashMap<String, String> layerMap;
	private IssueFinder issueFinder;

	@Before
	public void setUp() throws Exception {
		this.issueFinder = IssueFinder.getInstance();
		this.testFile = new File("res/test/EntropyManualCalculator.java");
		this.layerTestFile = new File("res/test/layers/Parser.java");
		this.layerTestFiles = new LinkedList<>();
		this.layerTestFiles.add(new File("res/test/layers/GUI.java"));
		this.layerTestFiles.add(new File("res/test/layers/GUIUtils.java"));
		this.layerTestFiles.add(new File("res/test/layers/SettingsGUI.java"));
		this.layerTestFiles.add(new File("res/test/layers/ThemeManager.java"));
		this.layerTestFiles.add(new File("res/test/layers/TsvToHtml.java"));
		this.layerTestFiles.add(new File("res/test/layers/Parser.java"));
		this.layerCompilationUnit = JavaParser.parse(this.layerTestFile);

		this.lines = Files.readAllLines(this.testFile.toPath());

		String nl = System.lineSeparator();
		for (String line : this.lines) {
			this.fileString += line + nl;
		}

		this.compilationUnit = JavaParser.parse(this.testFile);
		Node tryStmt = this.compilationUnit.getTypes().get(0).getMembers().get(2).getChildrenNodes().get(3)
				.getChildrenNodes().get(4);
		this.catchClause = (CatchClause) tryStmt.getChildrenNodes().get(2);
		this.issues = this.issueFinder.analyzeNode(tryStmt, null, this.fileString);

		String layer1 = "Layer 1";
		String layer2 = "Layer 2";
		String layer3 = "Layer 3";

		this.layerMap = new LinkedHashMap<>();
		this.layerMap.put("no.uib.lca092.rtms.gui.GUI", layer1);
		this.layerMap.put("no.uib.lca092.rtms.gui.GUIUtils", layer1);
		this.layerMap.put("no.uib.lca092.rtms.gui.SettingsGUI", layer1);
		this.layerMap.put("no.uib.lca092.rtms.gui.ThemeManager", layer1);
		this.layerMap.put("no.uib.lca092.rtms.TsvToHtml", layer2);
		this.layerMap.put("no.uib.lca092.rtms.io.Parser", layer3);
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
		List<Issue> expected = this.issues;
		List<Issue> actual = this.issueFinder.analyzeNode(this.compilationUnit, expected, this.fileString);
		assertEquals(expected, actual);
	}

	@Test
	public void testCheckEmptyBlockStmt() {
		assertTrue(this.issues.size() == 3);
		assertEquals(33, this.issues.get(0).getBeginLine());
		assertEquals(36, this.issues.get(1).getBeginLine());
		assertEquals(38, this.issues.get(2).getBeginLine());
	}

	@Test
	public void findEmptyCatchClause() {
		boolean found = false;
		for (Issue issue : this.issues) {
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
			if (issue.getType().equals("Auto Generated Catch Block")) {
				found = true;
			}
		}
		assertTrue(found);
	}

	@Test
	public void findEmptyFinallyBlock() {
		boolean found = false;
		for (Issue issue : this.issues) {
			if (issue.getType().equals("Empty Finally Block")) {
				found = true;
			}
		}
		assertTrue(found);
	}

	@Test
	public void placeMarkerCorrectly() {
		int beginLine = this.catchClause.getBeginLine();
		int endLine = this.catchClause.getEndLine();
		int startColumn = this.catchClause.getBeginColumn() - 14;
		int endColumn = this.catchClause.getEndColumn() - 14;
		int[] indexes = IssueFinder.columnsToIndexes(this.fileString, beginLine, endLine, startColumn, endColumn);
		Issue regexIssue = IssueFinder.analyzeRegex(this.fileString).get(0);
		int expectedBeginLine = regexIssue.getBeginLine();
		int expectedStartIndex = regexIssue.getStartIndex();
		int expectedEndIndex = regexIssue.getEndIndex();

		assertEquals("Begin line difference:" + Math.abs(expectedBeginLine - beginLine), expectedBeginLine, beginLine);
		assertEquals("Start index difference:" + Math.abs(expectedStartIndex - indexes[0]), expectedStartIndex,
				indexes[0]);
		assertEquals("End index difference:" + Math.abs(expectedEndIndex - indexes[1]), expectedEndIndex, indexes[1]);
	}

	@Test
	public void findClassOfMethodCall() {
		this.issueFinder.findIssues(this.layerTestFiles);
		Node methodCall = this.layerCompilationUnit.getTypes().get(0).getMembers().get(9).getChildrenNodes().get(2)
				.getChildrenNodes().get(1).getChildrenNodes().get(0);

		CompilationUnit expected = this.issueFinder.getJavaResources().get(0).getCompilationUnit();
		CompilationUnit actual = this.issueFinder.findMethodCompilationUnit((MethodCallExpr) methodCall);

		assertEquals(expected, actual);
	}

	@Test
	public void findClassOfStaticMethodCall() {
		this.issueFinder.findIssues(this.layerTestFiles);
		Node staticMethodCall = this.layerCompilationUnit.getTypes().get(0).getMembers().get(9).getChildrenNodes()
				.get(2).getChildrenNodes().get(0).getChildrenNodes().get(0).getChildrenNodes().get(1).getChildrenNodes()
				.get(1);

		CompilationUnit expected = this.issueFinder.getJavaResources().get(0).getCompilationUnit();
		CompilationUnit actual = this.issueFinder.findMethodCompilationUnit((MethodCallExpr) staticMethodCall);

		assertEquals(expected, actual);
	}

	@Test
	public void testFindNodeAncestorOfType() throws NoAncestorFoundException {
		Node node = this.layerCompilationUnit.getTypes().get(0).getMembers().get(9).getChildrenNodes().get(2)
				.getChildrenNodes().get(1).getChildrenNodes().get(0);
		MethodDeclaration expected = (MethodDeclaration) this.layerCompilationUnit.getTypes().get(0).getMembers()
				.get(9);
		Node actual = IssueFinder.findNodeAncestorOfType(node, MethodDeclaration.class);

		assertEquals(expected.getClass(), actual.getClass());
		assertEquals(expected, actual);
	}

	@Test
	public void findLayerSkippingCalls() {
		Map<File, List<Issue>> layerIssuesMap = IssueFinder.getInstance().findIssues(this.layerTestFiles);

		boolean found = false;
		for (List<Issue> fileIssues : layerIssuesMap.values()) {
			for (Issue issue : fileIssues) {
				if (issue.getType().equals("Layer-Skipping Call")) {
					System.out.println(issue.getProblemArea());
					System.out.println();
					found = true;
				}
			}
		}
		assertTrue(found);
	}

}
