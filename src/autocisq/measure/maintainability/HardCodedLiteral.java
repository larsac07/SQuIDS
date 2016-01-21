package autocisq.measure.maintainability;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.ModifierSet;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.LiteralExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;

import autocisq.JavaParserHelper;
import autocisq.NoSuchAncestorFoundException;
import autocisq.measure.Measure;
import autocisq.models.FileIssue;
import autocisq.models.Issue;

/**
 * The {@link HardCodedLiteral} class represents the CISQ Maintainability
 * measure 21: # of hard coded literals except (-1, 0, 1, 2, or literals
 * initializing static or constant variables).
 *
 * It considers a hard coded literal as any instance of {@link LiteralExpr} or
 * its subclasses which is assigned to a {@link VariableDeclarationExpr} or
 * {@link FieldDeclaration} which does not have the static modifier.
 *
 * @author Lars A. V. Cabrera
 *
 */
public class HardCodedLiteral extends Measure {
	
	public HardCodedLiteral(Map<String, Object> settings) {
		super(settings);
	}
	
	public final static int MIN_INTEGER_LITERAL = -1;
	public final static int MAX_INTEGER_LITERAL = 2;
	public final static String ISSUE_TYPE = "Non-valid, hard coded literal";
	
	@Override
	public List<Issue> analyzeNode(Node node, String fileString, List<CompilationUnit> compilationUnits) {
		if (node instanceof LiteralExpr) {
			LiteralExpr literalExpr = (LiteralExpr) node;
			if (literalExpr instanceof IntegerLiteralExpr) {
				IntegerLiteralExpr intLitExpr = (IntegerLiteralExpr) literalExpr;
				int value = Integer.parseInt(intLitExpr.getValue());
				if (isWithinThreshold(value)) {
					return null;
				}
			}
			
			boolean isNonStatic = isNonStaticVariable(literalExpr);
			
			if (isNonStatic) {
				List<Issue> issues = new ArrayList<>();
				issues.add(new FileIssue(ISSUE_TYPE, node, fileString));
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
	
	private boolean isNonStaticVariable(LiteralExpr literalExpr) {
		boolean isNonStaticVariable = false;
		try {
			VariableDeclarationExpr variableDeclarationExpr = (VariableDeclarationExpr) JavaParserHelper
					.findNodeAncestorOfType(literalExpr, VariableDeclarationExpr.class);
			return !ModifierSet.isStatic(variableDeclarationExpr.getModifiers());
		} catch (NoSuchAncestorFoundException e) {
			isNonStaticVariable = false;
		}
		try {
			FieldDeclaration fieldDeclaration = (FieldDeclaration) JavaParserHelper.findNodeAncestorOfType(literalExpr,
					FieldDeclaration.class);
			return !ModifierSet.isStatic(fieldDeclaration.getModifiers());
		} catch (NoSuchAncestorFoundException e1) {
			isNonStaticVariable = false;
		}
		return isNonStaticVariable;
	}
	
	@Override
	public String getIssueType() {
		return ISSUE_TYPE;
	}
}
