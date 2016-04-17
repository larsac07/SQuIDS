package autocisq.measure.maintainability;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.ConditionalExpr;
import com.github.javaparser.ast.stmt.CatchClause;
import com.github.javaparser.ast.stmt.DoStmt;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.ForeachStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.SwitchEntryStmt;
import com.github.javaparser.ast.stmt.WhileStmt;

import autocisq.models.FileIssue;
import autocisq.models.Issue;

/**
 * The {@link MethodCyclomaticComplexity} class represents the CISQ
 * Maintainability measure 18: # functions with cyclomatic complexity ≥ a
 * language specific threshold.
 *
 * The threshold is set to 10.
 *
 * It considers a function as any method (static or not) or constructor.
 *
 * @author Lars A. V. Cabrera
 *
 */
public class MethodCyclomaticComplexity extends MaintainabilityMeasure {

	public final static int THRESHOLD = 10;
	public final static String ISSUE_TYPE = "Function with Cyclomatic Complexity >= " + THRESHOLD;

	private Node methodOrConstructor;
	private Set<Node> blamedMethodsAndConstructors;
	private int count;

	/**
	 * Creates a new {@link MethodCyclomaticComplexity} measure and assumes a
	 * starting cyclomatic complexity value of 1.
	 *
	 * @param settings
	 *            - not required
	 */
	public MethodCyclomaticComplexity(Map<String, Object> settings) {
		super(settings);
		reset(null);
	}

	/**
	 * Checks if a node is a control flow statement and counts it if it is.
	 * Resets if a {@link ConstructorDeclaration} or {@link MethodDeclaration}
	 * is found (performs Cyclomatic Complexity (CC) calculation per function).
	 *
	 * If a function has a CC value above the threshold, an issue is returned.
	 *
	 * @return a list containing 1 {@link FileIssue} element if an issue was
	 *         found, null if not
	 */
	@Override
	public List<Issue> analyzeNode(Node node, String fileString, List<CompilationUnit> compilationUnits) {
		if (node instanceof ConstructorDeclaration || node instanceof MethodDeclaration) {
			reset(node);
		} else if (isControlFlowStmt(node)) {
			this.count++;
		}
		if (this.methodOrConstructor == null) {
			// If node does not belong to a method or constructor
			// (e.g. initialization block).
			return null;
		} else if (this.count >= THRESHOLD && !this.blamedMethodsAndConstructors.contains(this.methodOrConstructor)) {
			this.blamedMethodsAndConstructors.add(this.methodOrConstructor);
			List<Issue> issues = new LinkedList<>();
			issues.add(new FileIssue(this, getMethodOrConstructorSignature(this.methodOrConstructor), fileString));
			return issues;
		} else {
			return null;
		}
	}

	private Node getMethodOrConstructorSignature(Node node) {
		if (node instanceof ConstructorDeclaration) {
			return ((ConstructorDeclaration) node).getNameExpr();
		} else if (node instanceof MethodDeclaration) {
			return ((MethodDeclaration) node).getNameExpr();
		} else {
			// If node is neither a constructor or method, simply return the
			// node itself
			return node;
		}
	}

	@Override
	public String getMeasureElement() {
		return ISSUE_TYPE;
	}

	public boolean isControlFlowStmt(Node node) {
		if (node instanceof IfStmt || node instanceof ConditionalExpr || node instanceof SwitchEntryStmt
				|| node instanceof CatchClause || node instanceof ForStmt || node instanceof ForeachStmt
				|| node instanceof WhileStmt || node instanceof DoStmt) {
			return true;
		} else if (node instanceof BinaryExpr) {
			BinaryExpr binaryExpr = (BinaryExpr) node;
			BinaryExpr.Operator op = binaryExpr.getOperator();
			if (op == BinaryExpr.Operator.and || op == BinaryExpr.Operator.or) {
				return true;
			}
		}
		return false;
	}

	private void reset(Node nodeToBlame) {
		this.methodOrConstructor = nodeToBlame;
		this.blamedMethodsAndConstructors = new HashSet<>();
		this.count = 1;
	}

}
