package autocisq.measure.reliability;

import java.io.File;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.CatchClause;

import autocisq.io.IOUtils;
import autocisq.measure.MeasureTest;

public class EmptyExceptionHandlingBlockTest extends MeasureTest {

	private CatchClause autoGenCatchClause;
	private CatchClause emptyCatchClause;
	private BlockStmt emptyFinallyBlock;
	private CatchClause nonEmptyCatchClause;
	private String fileString;

	@Before
	public void setUp() throws Exception {
		this.issueFinder.getMeasures().clear();
		this.issueFinder.putMeasure(new EmptyExceptionHandlingBlock(new HashMap<>()));

		File testFile = new File("res/test/EntropyManualCalculator.java");

		this.fileString = IOUtils.fileToString(testFile);

		CompilationUnit compilationUnit = JavaParser.parse(testFile);
		MethodDeclaration methodCalculateEntropy1 = (MethodDeclaration) compilationUnit.getTypes().get(0).getMembers()
				.get(2);
		MethodDeclaration methodCalculateEntropy2 = (MethodDeclaration) compilationUnit.getTypes().get(0).getMembers()
				.get(4);

		this.autoGenCatchClause = (CatchClause) methodCalculateEntropy1.getChildrenNodes().get(3).getChildrenNodes()
				.get(4).getChildrenNodes().get(1);
		this.emptyCatchClause = (CatchClause) methodCalculateEntropy1.getChildrenNodes().get(3).getChildrenNodes()
				.get(4).getChildrenNodes().get(2);
		this.emptyFinallyBlock = (BlockStmt) methodCalculateEntropy1.getChildrenNodes().get(3).getChildrenNodes().get(4)
				.getChildrenNodes().get(3);
		this.nonEmptyCatchClause = (CatchClause) methodCalculateEntropy2.getChildrenNodes().get(3).getChildrenNodes()
				.get(0).getChildrenNodes().get(1);
	}

	@Test
	public void findAutoGenCatchClause() {
		findIssue(this.autoGenCatchClause, this.fileString);
	}

	@Test
	public void findEmptyCatchClause() {
		findIssue(this.emptyCatchClause, this.fileString);
	}

	@Test
	public void findEmptyFinallyBlock() {
		findIssue(this.emptyFinallyBlock, this.fileString);
	}

	@Test
	public void skipNonEmptyCatchClause() {
		skipIssue(this.nonEmptyCatchClause, this.fileString);
	}

	@Override
	public String getIssueType() {
		return EmptyExceptionHandlingBlock.ISSUE_TYPE;
	}

}
