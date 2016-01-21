package autocisq.measure.maintainability;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.ModifierSet;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;

import autocisq.JavaParserHelper;
import autocisq.measure.Measure;
import autocisq.models.FileIssue;
import autocisq.models.Issue;

/**
 * The FunctionFanOut class represents the CISQ Maintainability Measure 11: # of
 * functions that have a fan-out ≥ 10.
 *
 * The fan-out is calculated by summing up the number of called functions
 * (methods) and the number of member variables set.
 *
 * It considers a method as a function iff it is static. In other words, it
 * ignores member functions (methods) and constructors.
 *
 * It does however not consider only static method calls as function calls, but
 * also non-static method calls.
 *
 *
 * @author Lars A. V. Cabrera
 *
 */
public class FunctionFanOut extends Measure {
	
	public final static int THRESHOLD = 10;
	public final static String ISSUE_TYPE = "Function with fan-out >= " + THRESHOLD;

	public FunctionFanOut(Map<String, Object> settings) {
		super(settings);
	}

	private String fileString = "";
	
	@Override
	public List<Issue> analyzeNode(Node node, String fileString, List<CompilationUnit> compilationUnits) {
		List<Issue> issues = new ArrayList<>();
		if (node instanceof MethodDeclaration) {
			MethodDeclaration methodDeclaration = (MethodDeclaration) node;
			boolean isFunction = ModifierSet.isStatic(methodDeclaration.getModifiers());
			if (isFunction) {
				int fanOut = calculateFanOut(node);
				if (fanOut >= THRESHOLD) {
					int[] indexes = JavaParserHelper.columnsToIndexes(this.fileString, node.getBeginLine(),
							node.getEndLine(), node.getBeginColumn(), node.getEndColumn());
					issues.add(new FileIssue(node.getBeginLine(), indexes[0], indexes[1], getIssueType(),
							node.toString(), node));
				}
			}
		}
		return issues;
	}

	private int calculateFanOut(Node node) {
		List<Issue> issues = new ArrayList<>();
		return calculateFanOut(node, issues);
	}

	private int calculateFanOut(Node node, List<Issue> issues) {
		int fanOut = 0;
		if (node instanceof AssignExpr) {
			AssignExpr assignExpr = (AssignExpr) node;
			if (assignExpr.getTarget() instanceof FieldAccessExpr) {
				fanOut++;
			}
		} else if (node instanceof MethodCallExpr) {
			fanOut++;
		}
		
		for (Node child : node.getChildrenNodes()) {
			fanOut += calculateFanOut(child, issues);
		}
		return fanOut;
	}
	
	@Override
	public String getIssueType() {
		return ISSUE_TYPE;
	}
	
}
