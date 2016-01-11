package autocisq.measure.maintainability;

import java.util.List;
import java.util.Map;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.LiteralExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;

import autocisq.measure.Measure;
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
public class HardCodedLiteral implements Measure {
	
	public final static String ISSUE_TYPE = "Non-valid, hard coded literal";
	
	@Override
	public List<Issue> analyzeNode(Node node, String fileString, List<CompilationUnit> compilationUnits,
			Map<String, Integer> layerMap) {
		if (node instanceof LiteralExpr) {
			if (node.getParentNode() instanceof AssignExpr) {
				AssignExpr assignExpr = (AssignExpr) node.getParentNode();
				try {
					int value = Integer.parseInt(assignExpr.getValue().toString());
					if (value < -1 || value > 2) {
						Expression target = assignExpr.getTarget();
						System.out.println(target.getClass().getSimpleName());
					}
				} catch (NumberFormatException e) {
					return null;
				}
			}
		}
		return null;
	}
	
}
