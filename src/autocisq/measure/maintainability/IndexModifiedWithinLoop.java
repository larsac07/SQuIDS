package autocisq.measure.maintainability;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.UnaryExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.DoStmt;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.WhileStmt;

import autocisq.JavaParserHelper;
import autocisq.NoSuchAncestorFoundException;
import autocisq.NoSuchDescendantFoundException;
import autocisq.models.FileIssue;
import autocisq.models.Issue;

/**
 * The {@link IndexModifiedWithinLoop} class represents the CISQ Maintainability
 * measure 16: # of instances of indexes modified within its loop.
 *
 * It considers indexes to be numerical values which are used by a loop to
 * determine when to stop.
 *
 * It considers relevant loops to be {@link ForStmt}, and {@link WhileStmt} and
 * {@link DoStmt} which contain numeric comparisons in their condition
 * parameter.
 *
 * It considers a modification of an index as a {@link UnaryExpr} or
 * {@link AssignExpr} with assigning operators ("=", "+=", "-=", "/=", "*=",
 * "%=", "<<=", ">>=", ">>>=", "++" and "--"). See
 * {@link com.github.javaparser.ast.expr.UnaryExpr.Operator} and
 * {@link com.github.javaparser.ast.expr.AssignExpr.Operator}.
 *
 * @author Lars A. V. Cabrera
 *
 */
public class IndexModifiedWithinLoop extends TypeDependentMeasure {

	public final static String ISSUE_TYPE = "Index modified within loop";
	private Set<String> indexVariables;
	private String fileString;

	public IndexModifiedWithinLoop(Map<String, Object> settings) {
		super(settings);
		this.indexVariables = new HashSet<>();
	}

	@Override
	public List<Issue> analyzeNode(Node node, String fileString, List<CompilationUnit> compilationUnits) {
		super.analyzeNode(node, fileString, compilationUnits);
		this.fileString = fileString;
		if (isIndexLoop(node)) {
			return analyzeLoop(node);
		} else if (isVariableModification(node)) {
			return analyzeVariableModification(node);
		}
		return null;
	}

	private List<Issue> analyzeVariableModification(Node node) {
		if (insideLoop(node)) {
			Expression expr;
			if (node instanceof AssignExpr) {
				AssignExpr assign = (AssignExpr) node;
				expr = assign.getTarget();
			} else {
				UnaryExpr unary = (UnaryExpr) node;
				expr = unary.getExpr();
			}
			String variable = null;
			if (expr instanceof NameExpr) {
				variable = ((NameExpr) expr).getName();
			} else if (expr instanceof FieldAccessExpr) {
				variable = ((FieldAccessExpr) expr).getField();
			}
			if (variable != null) {
				if (this.indexVariables.contains(variable)) {
					List<Issue> issues = new LinkedList<>();
					issues.add(new FileIssue(ISSUE_TYPE, expr, this.fileString));
					return issues;
				}
			}
		}
		return null;
	}

	private boolean insideLoop(Node node) {
		if (node.getParentNode() != null && isIndexLoop(node.getParentNode())) {
			return false;
		}
		try {
			Node block = JavaParserHelper.findNodeAncestorOfType(node, BlockStmt.class);
			JavaParserHelper.findNodeAncestorOfType(block, ForStmt.class, WhileStmt.class, DoStmt.class);
			return true;
		} catch (NoSuchAncestorFoundException e) {
			return false;
		}
	}

	/**
	 * Determines if a node is a loop which may depend on an index (
	 * {@link ForStmt}, {@link WhileStmt} or {@link DoStmt}).
	 *
	 * @param node
	 *            - the node to check
	 * @return true if the node is a loop which may depend on an index
	 */
	private boolean isIndexLoop(Node node) {
		return node instanceof ForStmt || node instanceof WhileStmt || node instanceof DoStmt;
	}

	private List<Issue> analyzeLoop(Node node) {
		Expression condition;
		if (node instanceof ForStmt) {
			ForStmt forStmt = (ForStmt) node;
			condition = forStmt.getCompare();
		} else if (node instanceof WhileStmt) {
			WhileStmt whileStmt = (WhileStmt) node;
			condition = whileStmt.getCondition();
		} else {
			DoStmt doStmt = (DoStmt) node;
			condition = doStmt.getCondition();
		}
		if (condition instanceof BinaryExpr) {
			return storeIndexVariables(condition);
		} else {
			return null;
		}
	}

	private List<Issue> storeIndexVariables(Expression condition) {
		try {
			List<Node> binaryExprs = JavaParserHelper.findNodeDescendantsOfType(condition, BinaryExpr.class);
			for (Node binaryExprNode : binaryExprs) {
				BinaryExpr binaryExpr = (BinaryExpr) binaryExprNode;
				if (isComparing(binaryExpr)) {
					List<Node> variables = JavaParserHelper.findNodeDescendantsOfType(binaryExpr, NameExpr.class,
							FieldAccessExpr.class);
					for (Node variableNode : variables) {
						String variable;
						if (variableNode instanceof NameExpr) {
							variable = ((NameExpr) variableNode).getName();
						} else {
							variable = ((FieldAccessExpr) variableNode).getField();
						}
						if (variableNode.getParentNode() instanceof BinaryExpr
								&& isComparing((BinaryExpr) variableNode.getParentNode())) {
							this.indexVariables.add(variable);
						}
					}
				}
			}
		} catch (NoSuchDescendantFoundException e) {
			return null;
		}
		return null;
	}

	private boolean isComparing(BinaryExpr binaryExpr) {
		BinaryExpr.Operator op = binaryExpr.getOperator();
		return op.equals(BinaryExpr.Operator.equals) || op.equals(BinaryExpr.Operator.greater)
				|| op.equals(BinaryExpr.Operator.greaterEquals) || op.equals(BinaryExpr.Operator.less)
				|| op.equals(BinaryExpr.Operator.lessEquals) || op.equals(BinaryExpr.Operator.notEquals);
	}

	private boolean isVariableModification(Node node) {
		if (node instanceof UnaryExpr) {
			UnaryExpr.Operator op = ((UnaryExpr) node).getOperator();
			return op.equals(UnaryExpr.Operator.posDecrement) || op.equals(UnaryExpr.Operator.posIncrement)
					|| op.equals(UnaryExpr.Operator.preDecrement) || op.equals(UnaryExpr.Operator.preIncrement);
		} else if (node instanceof AssignExpr) {
			AssignExpr.Operator op = ((AssignExpr) node).getOperator();
			return op.equals(AssignExpr.Operator.assign) || op.equals(AssignExpr.Operator.lShift)
					|| op.equals(AssignExpr.Operator.minus) || op.equals(AssignExpr.Operator.plus)
					|| op.equals(AssignExpr.Operator.rem) || op.equals(AssignExpr.Operator.rSignedShift)
					|| op.equals(AssignExpr.Operator.rUnsignedShift) || op.equals(AssignExpr.Operator.slash)
					|| op.equals(AssignExpr.Operator.star);
		}
		return false;
	}

	@Override
	public String getIssueType() {
		return ISSUE_TYPE;
	}

}
