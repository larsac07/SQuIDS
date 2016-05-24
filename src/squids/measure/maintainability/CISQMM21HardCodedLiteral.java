package squids.measure.maintainability;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ModifierSet;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.LiteralExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;

import squids.JavaParserHelper;
import squids.NoSuchAncestorFoundException;
import squids.models.FileIssue;
import squids.models.Issue;

/**
 * The {@link CISQMM21HardCodedLiteral} class represents the CISQ Maintainability
 * measure 21: # of hard coded literals except (-1, 0, 1, 2, or literals
 * initializing static or constant variables).
 *
 * It considers a hard coded literal as any instance of
 * {@link StringLiteralExpr} or its subclasses which is assigned to a
 * {@link VariableDeclarator} which does not have the static or final modifier.
 *
 * @author Lars A. V. Cabrera
 *
 */
public class CISQMM21HardCodedLiteral extends CISQMMTypeDependentMeasure {

	public CISQMM21HardCodedLiteral(Map<String, Object> settings) {
		super(settings);
	}

	public final static int MIN_INTEGER_LITERAL = -1;
	public final static int MAX_INTEGER_LITERAL = 2;
	public final static String ISSUE_TYPE = "CISQ MM21: Non-valid, hard coded literal";

	@Override
	public List<Issue> analyzeNode(Node node, String fileString, List<CompilationUnit> compilationUnits) {
		super.analyzeNode(node, fileString, compilationUnits);
		if (node instanceof StringLiteralExpr) {
			StringLiteralExpr literalExpr = (StringLiteralExpr) node;
			// Do not bother with empty strings, e.g. "".
			if (literalExpr.getValue().isEmpty()) {
				return null;
			}
			// Check if integer
			if (literalExpr instanceof IntegerLiteralExpr) {
				IntegerLiteralExpr intLitExpr = (IntegerLiteralExpr) literalExpr;
				try {
					int value = Integer.parseInt(intLitExpr.getValue());
					// If integer is -1, 0, 1 or 2, no issue is reported
					if (isWithinThreshold(value)) {
						return null;
					}
				} catch (NumberFormatException e) {
					return null;
				}
			}

			boolean isNonStaticAndNonFinal = isNonStaticAndNonFinalVariable(literalExpr);

			// Only bother with non-static and non-final variables
			if (isNonStaticAndNonFinal) {
				List<Issue> issues = new ArrayList<>();
				issues.add(new FileIssue(this, node, fileString));
				return issues;
			}
		}
		return null;
	}

	public static boolean isWithinThreshold(int value) {
		if (value < MIN_INTEGER_LITERAL || value > MAX_INTEGER_LITERAL) {
			return false;
		} else {
			return true;
		}
	}

	private boolean isNonStaticAndNonFinalVariable(LiteralExpr literalExpr) {
		int modifiers = 0;
		String varName = "";
		try {
			VariableDeclarator varDecl = (VariableDeclarator) JavaParserHelper.findNodeAncestorOfType(literalExpr,
					VariableDeclarator.class);
			varName = varDecl.getId().getName();
		} catch (NoSuchAncestorFoundException e1) {
			return true;
		}
		Integer modifiersObject = getVariableModifiers(varName);
		if (modifiersObject != null) {
			modifiers = modifiersObject;
		}
		boolean isStatic = ModifierSet.isStatic(modifiers);
		boolean isFinal = ModifierSet.isFinal(modifiers);
		return !(isStatic || isFinal);
	}

	@Override
	public String getMeasureElement() {
		return ISSUE_TYPE;
	}
}
