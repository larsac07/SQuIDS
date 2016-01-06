package autocisq.measure;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.ModifierSet;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;

import autocisq.JavaParserHelper;
import autocisq.models.FileIssue;
import autocisq.models.Issue;

/**
 * The FunctionWithFanOut10OrMore class represents the CISQ Maintainability
 * Measure 11: # of functions that have a fan-out ≥ 10.
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
public class FunctionWithFanOut10OrMore implements Measure {

	private String fileString = "";
	int counter = 0;
	
	@Override
	public List<Issue> analyzeNode(Node node, String fileString) {
		List<Issue> issues = new ArrayList<>();
		this.counter = 0;
		if (node instanceof MethodDeclaration) {
			MethodDeclaration methodDeclaration = (MethodDeclaration) node;
			boolean isFunction = ModifierSet.isStatic(methodDeclaration.getModifiers());
			if (isFunction) {
				issues = calculateFanOut(node);
			}
		}
		return issues;
	}

	private List<Issue> calculateFanOut(Node node) {
		List<Issue> issues = new ArrayList<>();
		return calculateFanOut(node, issues);
	}

	private List<Issue> calculateFanOut(Node node, List<Issue> issues) {
		if (node instanceof AssignExpr) {
			AssignExpr assignExpr = (AssignExpr) node;
			if (assignExpr.getTarget() instanceof FieldAccessExpr) {
				this.counter++;
			}
		} else if (node instanceof MethodCallExpr) {
			this.counter++;
		}
		if (this.counter >= 10) {
			int[] indexes = JavaParserHelper.columnsToIndexes(this.fileString, node.getBeginLine(), node.getEndLine(),
					node.getBeginColumn(), node.getEndColumn());
			issues.add(new FileIssue(node.getBeginLine(), indexes[0], indexes[1], "Function with fan-out of 10 or more",
					node.toString(), node));
			return issues;
		}
		
		for (Node child : node.getChildrenNodes()) {
			calculateFanOut(child, issues);
		}
		return issues;
	}
	
}
