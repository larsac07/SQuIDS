package autocisq.measure.maintainability;

import java.io.File;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.DoStmt;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.WhileStmt;

import autocisq.io.IOUtils;
import autocisq.measure.MeasureTest;

public class CISQMM16IndexModifiedWithinLoopTest extends MeasureTest {

	private String fileString;
	private CompilationUnit testCU;
	private ForStmt nonModifyingFor;
	private ForStmt modifyingFor;
	private WhileStmt modifyingWhile;
	private DoStmt modifyingDoWhile;

	@Before
	public void setUp() throws Exception {
		this.issueFinder.getMeasures().clear();
		this.issueFinder.putMeasure(new CISQMM16IndexModifiedWithinLoop(new HashMap<>()));

		File testFile = new File("res/test/IndexModifiedWithinLoop.java");

		this.fileString = IOUtils.fileToString(testFile);
		this.testCU = JavaParser.parse(testFile);

		MethodDeclaration method = (MethodDeclaration) this.testCU.getTypes().get(0).getMembers().get(2);
		this.nonModifyingFor = (ForStmt) method.getBody().getStmts().get(0);
		this.modifyingFor = (ForStmt) method.getBody().getStmts().get(1);
		this.modifyingWhile = (WhileStmt) method.getBody().getStmts().get(2);
		this.modifyingDoWhile = (DoStmt) method.getBody().getStmts().get(3);

		dryRun();
	}

	@Test
	public void skipNonModifyingLoop() {
		skipIssue(this.nonModifyingFor, this.fileString);
	}

	@Test
	public void findModifyingForLoop() {
		findIssue(this.modifyingFor, this.fileString);
	}

	@Test
	public void findModifyingWhileLoop() {
		findIssue(this.modifyingWhile, this.fileString);
	}

	@Test
	public void findModifyingDoWhileLoop() {
		findIssue(this.modifyingDoWhile, this.fileString);
	}

	private void dryRun() {
		this.issueFinder.analyzeNode(this.testCU, null, this.fileString);
	}

	@Override
	public String getIssueType() {
		return CISQMM16IndexModifiedWithinLoop.ISSUE_TYPE;
	}

}
