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
import autocisq.NoSuchAncestorFoundException;
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
public class ContinueOrBreakOutsideSwitch extends MaintainabilityMeasure {

	public ContinueOrBreakOutsideSwitch(Map<String, Object> settings) {
		super(settings);
	}

	public final static String ISSUE_TYPE = "Continue or Break outside switch";

	@Override
	public List<Issue> analyzeNode(Node node, String fileString, List<CompilationUnit> compilationUnits) {
		if (node instanceof ContinueStmt || node instanceof BreakStmt) {
			try {
				JavaParserHelper.findNodeAncestorOfType(node, SwitchStmt.class);
			} catch (NoSuchAncestorFoundException e) {
				List<Issue> issues = new ArrayList<>();
				issues.add(new FileIssue(this, node, fileString));
				return issues;
			}
		}
		return null;
	}

	@Override
	public String getMeasureElement() {
		return ISSUE_TYPE;
	}

}
