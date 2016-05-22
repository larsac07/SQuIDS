package autocisq.measure.maintainability;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.ArrayAccessExpr;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.UnaryExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.DoStmt;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.ForeachStmt;
import com.github.javaparser.ast.stmt.WhileStmt;

import autocisq.JavaParserHelper;
import autocisq.NoSuchAncestorFoundException;
import autocisq.models.FileIssue;
import autocisq.models.Issue;

/**
 * The {@link CISQMM16IndexModifiedWithinLoop} class represents the CISQ
 * Maintainability measure 16: # of instances of indexes modified within its
 * loop.
 *
 * It considers indexes to be integer values used to access arrays.
 *
 * It considers loops to be instances of {@link ForStmt}, {@link ForeachStmt},
 * {@link WhileStmt} and {@link DoStmt}.
 *
 * It considers a modification of an index as a {@link UnaryExpr} or
 * {@link AssignExpr} with operators ("=", "+", "-", "*", "/", "+=", "-=", "/=",
 * "*=", "%=", "<<=" , ">>=", ">>>=", "++" and "--"). See
 * {@link com.github.javaparser.ast.expr.UnaryExpr.Operator} and
 * {@link com.github.javaparser.ast.expr.AssignExpr.Operator}.
 *
 * @author Lars A. V. Cabrera
 *
 */
public class CISQMM16IndexModifiedWithinLoop extends CISQMMTypeDependentMeasure {

	public final static String ISSUE_TYPE = "CISQ MM16: Index modified within loop";
	private final static String MESSAGE = " is an index variable modified within a loop";
	private Set<String> variablesModifiedInLoop;
	private Set<String> indexVariables;
	private Set<String> markedVariables;

	public CISQMM16IndexModifiedWithinLoop(Map<String, Object> settings) {
		super(settings);
		resetIndexVariables();
	}

	@Override
	public List<Issue> analyzeNode(Node node, String fileString, List<CompilationUnit> compilationUnits) {
		super.analyzeNode(node, fileString, compilationUnits);
		if (isLoop(node)) {
			resetIndexVariables();
		}
		if (insideLoop(node)) {
			if (isVariableModification(node)) {
				addVariableModifiedInLoop(node);
				return checkVariables(node, fileString);
			} else if (isIndexVariable(node)) {
				addIndexVariable(node);
				return checkVariables(node, fileString);
			}
		}
		return null;
	}

	private List<Issue> checkVariables(Node node, String fileString) {
		for (String variable : this.indexVariables) {
			if (this.variablesModifiedInLoop.contains(variable)) {
				if (!this.markedVariables.contains(variable)) {
					this.markedVariables.add(variable);
					String message = variable + MESSAGE;
					List<Issue> issues = new LinkedList<>();
					issues.add(new FileIssue(this, node, fileString, message));
					return issues;
				}
			}
		}
		return null;
	}

	/**
	 * Checks to see if a node is inside a loop.
	 *
	 * More specifically it tries to find a {@link ForStmt}, {@link ForeachStmt}
	 * , {@link WhileStmt} or {@link DoStmt} ancestor, and returns true if it
	 * does.
	 *
	 * @param node
	 *            - the node
	 * @return true if the node has a {@link ForStmt}, {@link ForeachStmt},
	 *         {@link WhileStmt} or {@link DoStmt} ancestor
	 */
	private boolean insideLoop(Node node) {
		// Make sure node is not inside a loop-statement's parameters
		if (node.getParentNode() != null && isLoop(node.getParentNode())) {
			return false;
		}
		try {
			// Make sure the node is inside the block itself, and not the
			// loop-statement's parameters
			BlockStmt block = (BlockStmt) JavaParserHelper.findNodeAncestorOfType(node, BlockStmt.class);
			JavaParserHelper.findNodeAncestorOfType(block, ForStmt.class, ForeachStmt.class, WhileStmt.class,
					DoStmt.class);
			// A loop-type ancestor is found
			return true;
		} catch (NoSuchAncestorFoundException e) {
			// No loop-type ancestors were found
			return false;
		}
	}

	private boolean isLoop(Node node) {
		return node instanceof ForStmt || node instanceof ForeachStmt || node instanceof WhileStmt
				|| node instanceof DoStmt;
	}

	/**
	 * Checks a node to see if it is a {@link UnaryExpr}, {@link AssignExpr} or
	 * {@link BinaryExpr} with altering or number-producing operators
	 *
	 * @param node
	 *            - the node
	 * @return true if the node is a variable modification expression
	 */
	protected boolean isVariableModification(Node node) {
		if (node instanceof UnaryExpr) {
			UnaryExpr.Operator op = ((UnaryExpr) node).getOperator();
			return op.equals(UnaryExpr.Operator.positive) || op.equals(UnaryExpr.Operator.negative)
					|| op.equals(UnaryExpr.Operator.posIncrement) || op.equals(UnaryExpr.Operator.posDecrement)
					|| op.equals(UnaryExpr.Operator.preIncrement) || op.equals(UnaryExpr.Operator.preDecrement);
		} else if (node instanceof AssignExpr) {
			AssignExpr.Operator op = ((AssignExpr) node).getOperator();
			return op.equals(AssignExpr.Operator.assign) || op.equals(AssignExpr.Operator.lShift)
					|| op.equals(AssignExpr.Operator.minus) || op.equals(AssignExpr.Operator.plus)
					|| op.equals(AssignExpr.Operator.rem) || op.equals(AssignExpr.Operator.rSignedShift)
					|| op.equals(AssignExpr.Operator.rUnsignedShift) || op.equals(AssignExpr.Operator.slash)
					|| op.equals(AssignExpr.Operator.star);
		} else if (node instanceof BinaryExpr) {
			BinaryExpr.Operator op = ((BinaryExpr) node).getOperator();
			return op.equals(BinaryExpr.Operator.plus) || op.equals(BinaryExpr.Operator.minus)
					|| op.equals(BinaryExpr.Operator.times) || op.equals(BinaryExpr.Operator.divide)
					|| op.equals(BinaryExpr.Operator.remainder) || op.equals(BinaryExpr.Operator.lShift)
					|| op.equals(BinaryExpr.Operator.rSignedShift) || op.equals(BinaryExpr.Operator.rUnsignedShift);
		}
		return false;
	}

	/**
	 * Add variable to set of variables which are modfied in loops
	 *
	 * @param node
	 *            - the node
	 */
	private void addVariableModifiedInLoop(Node node) {
		List<Expression> expressions = new LinkedList<>();
		if (node instanceof AssignExpr) {
			AssignExpr assign = (AssignExpr) node;
			expressions.add(assign.getTarget());
		} else if (node instanceof UnaryExpr) {
			UnaryExpr unary = (UnaryExpr) node;
			expressions.add(unary.getExpr());
		} else {
			BinaryExpr binary = (BinaryExpr) node;
			expressions.add(binary.getLeft());
			expressions.add(binary.getRight());
		}
		for (Expression expression : expressions) {
			String variable = getVariableName(expression);
			if (variable != null) {
				this.variablesModifiedInLoop.add(variable);
			}
		}
	}

	/**
	 * Checks to see if a node is an index variable. If the node is a variable
	 * reference of any kind (see
	 * {@link CISQMM16IndexModifiedWithinLoop#getVariableName(Node)}), and it
	 * has an ancestor of type {@link ArrayAccessExpr}, it is an index variable.
	 *
	 * @param node
	 *            - the node
	 * @return true if the node is a variable reference and has an
	 *         {@link ArrayAccessExpr} ancestor
	 */
	private boolean isIndexVariable(Node node) {
		String variableName = getVariableName(node);
		// If node is a variable reference of any kind
		if (variableName != null) {
			try {
				JavaParserHelper.findNodeAncestorOfType(node, ArrayAccessExpr.class);
				// No exception was caught, therefore node is part of
				// ArrayAccessExpr
				return true;
			} catch (NoSuchAncestorFoundException e) {
				// No ArrayAccessExpr ancestor was found, therefore node is not
				// part of any
				return false;
			}
		}
		// Node is not a variable reference
		return false;
	}

	private void addIndexVariable(Node node) {
		String variableName = getVariableName(node);
		if (variableName != null) {
			this.indexVariables.add(variableName);
		}
	}

	/**
	 * @param node
	 * @return
	 */
	private String getVariableName(Node node) {
		String variableName = null;
		if (node instanceof NameExpr) {
			variableName = ((NameExpr) node).getName();
		} else if (node instanceof FieldAccessExpr) {
			variableName = ((FieldAccessExpr) node).getField();
		}
		return variableName;
	}

	private void resetIndexVariables() {
		this.variablesModifiedInLoop = new HashSet<>();
		this.indexVariables = new HashSet<>();
		this.markedVariables = new HashSet<>();
	}

	@Override
	public String getMeasureElement() {
		return ISSUE_TYPE;
	}

}
