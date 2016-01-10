package autocisq.measure.maintainability;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.stmt.BreakStmt;
import com.github.javaparser.ast.stmt.ContinueStmt;
import com.github.javaparser.ast.stmt.SwitchStmt;

import autocisq.JavaParserHelper;
import autocisq.NoAncestorFoundException;
import autocisq.measure.Measure;
import autocisq.models.FileIssue;
import autocisq.models.Issue;

/**
 * The {@link ContinueOrBreakOutsideSwitch} class represents the CISQ
 * Maintainability measure 17: # of GO TOs, CONTINUE, and BREAK outside the
 * switch.
 *
 * While the goto keyword exists in Java, it is not used, and therefore ignored
 * in this measure.
 *
 * CONTINUEs and BREAKs are considered as outside a switch iff they do not have
 * an ancestor node which is an instance of {@link SwitchStmt}.
 *
 * @author Lars A. V. Cabrera
 *
 */
public class ContinueOrBreakOutsideSwitch implements Measure {
	
	public final static String ISSUE_TYPE = "Continue or Break outside switch";
	
	@Override
	public List<Issue> analyzeNode(Node node, String fileString, List<CompilationUnit> compilationUnits,
			Map<String, Integer> layerMap) {
		if (node instanceof ContinueStmt || node instanceof BreakStmt) {
			try {
				JavaParserHelper.findNodeAncestorOfType(node, SwitchStmt.class);
			} catch (NoAncestorFoundException e) {
				List<Issue> issues = new ArrayList<>();
				issues.add(new FileIssue(ISSUE_TYPE, node, fileString));
				return issues;
			}
		}
		return null;
	}
	
}
