package autocisq.measure.reliability;

import java.util.LinkedList;
import java.util.List;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.CatchClause;
import com.github.javaparser.ast.stmt.TryStmt;

import autocisq.JavaParserHelper;
import autocisq.measure.Measure;
import autocisq.models.FileIssue;
import autocisq.models.Issue;

/**
 * The EmptyExceptionHandlingBlock class represents the CISQ Reliability Measure
 * 1: # of exception handling blocks such as Catch and Finally blocks that are
 * empty.
 *
 * @author Lars A. V. Cabrera
 *
 */
public class EmptyExceptionHandlingBlock implements Measure {

	@Override
	public List<Issue> analyzeNode(Node node, String fileString) {
		List<Issue> issues = new LinkedList<>();
		if (node instanceof BlockStmt
				&& (node.getParentNode() instanceof TryStmt || node.getParentNode() instanceof CatchClause)) {
			BlockStmt blockStmt = (BlockStmt) node;
			checkEmptyBlockStmt(blockStmt, fileString, issues);
		}
		return issues;
	}

	/**
	 * Detects empty or generic catch blocks, and adds a marker to it
	 *
	 * @param blockStmt
	 *            - the catch clause to inspect
	 */
	public static List<Issue> checkEmptyBlockStmt(BlockStmt blockStmt, String fileAsString, List<Issue> issues) {
		if (issues == null) {
			issues = new LinkedList<>();
		}
		Node parent = blockStmt.getParentNode();
		if (blockStmt.getStmts().isEmpty() && parent instanceof CatchClause) {
			int[] indexes = JavaParserHelper.columnsToIndexes(fileAsString, parent.getBeginLine(), parent.getEndLine(),
					parent.getBeginColumn() - 14, parent.getEndColumn() - 14);
			issues.add(new FileIssue(parent.getBeginLine(), indexes[0], indexes[1], "Empty Catch Block",
					parent.toString(), parent));
		} else if (blockStmt.getStmts().size() == 1 && parent instanceof CatchClause
				&& blockStmt.getStmts().get(0).toString().equals("e.printStackTrace();")) {
			int[] indexes = JavaParserHelper.columnsToIndexes(fileAsString, parent.getBeginLine(), parent.getEndLine(),
					parent.getBeginColumn() - 14, parent.getEndColumn() - 14);
			issues.add(new FileIssue(parent.getBeginLine(), indexes[0], indexes[1], "Auto Generated Catch Block",
					parent.toString(), parent));
		} else if (blockStmt.getStmts().isEmpty() && blockStmt.getParentNode() instanceof TryStmt) {
			int[] indexes = JavaParserHelper.columnsToIndexes(fileAsString, blockStmt.getBeginLine(),
					blockStmt.getEndLine(), parent.getBeginColumn() - 14, parent.getEndColumn() - 14);
			issues.add(new FileIssue(blockStmt.getBeginLine(), indexes[0], indexes[1], "Empty Finally Block",
					blockStmt.toString(), blockStmt));
		}
		return issues;
	}
}
