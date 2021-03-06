package squids.measure.maintainability;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.stmt.BreakStmt;
import com.github.javaparser.ast.stmt.ContinueStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.SwitchStmt;

import squids.JavaParserHelper;
import squids.NoSuchAncestorFoundException;
import squids.models.FileIssue;
import squids.models.Issue;

/**
 * The {@link CISQMM17ContinueOrBreakOutsideSwitch} class represents the CISQ
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
public class CISQMM17ContinueOrBreakOutsideSwitch extends CISQMaintainabilityMeasure {

	public CISQMM17ContinueOrBreakOutsideSwitch(Map<String, Object> settings) {
		super(settings);
	}

	public final static String ISSUE_TYPE = "CISQ MM17: Continue or Break outside switch";

	@Override
	public List<Issue> analyzeNode(Node node, String fileString, List<CompilationUnit> compilationUnits) {
		if (node instanceof ContinueStmt || node instanceof BreakStmt) {
			try {
				Node ancestor = JavaParserHelper.findNodeAncestorOfType(node, SwitchStmt.class, IfStmt.class);
				if (ancestor instanceof IfStmt) {
					// The continue/break statement was inside an if-statement
					List<Issue> issues = new ArrayList<>();
					issues.add(new FileIssue(this, node, fileString));
					return issues;
				}
			} catch (NoSuchAncestorFoundException e) {
				// The continue/break statement was not inside a
				// switch-statement
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
