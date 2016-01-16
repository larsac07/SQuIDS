package autocisq.measure.maintainability;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.ModifierSet;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.type.Type;

import autocisq.JavaParserHelper;
import autocisq.NoSuchAncestorFoundException;
import autocisq.NoSuchVariableException;
import autocisq.measure.Measure;
import autocisq.models.FileIssue;
import autocisq.models.Issue;

/**
 * The MethodDirectlyUsingFieldFromOtherClass class represents the CISQ
 * Maintainability Measure 9: # of methods that are directly using fields from
 * other classes.
 *
 * It considers a variable as a field from another class iff its declaration
 * cannot be found locally AND it is not a constant (not declared as static
 * final).
 *
 * @author Lars A. V. Cabrera
 *
 */
public class MethodDirectlyUsingFieldFromOtherClass implements Measure {

	public final static String ISSUE_TYPE = "Method directly using field from other class";
	
	@Override
	public List<Issue> analyzeNode(Node node, String fileString, List<CompilationUnit> compilationUnits,
			Map<String, Integer> layerMap) {
		List<Issue> issues = new ArrayList<>();
		
		if (node instanceof FieldAccessExpr) {
			try {
				FieldAccessExpr fieldAccessExpr = (FieldAccessExpr) node;
				String variableName = fieldAccessExpr.getScope().toString();
				String fieldName = fieldAccessExpr.getField();
				List<Type> types = JavaParserHelper.findVariableTypeBottomUp(variableName, node);
				
				for (Type variableType : types) {
					
					String varNoArrayBrackets = withoutArrayBrackets(variableType.toString());
					CompilationUnit fieldClass = JavaParserHelper.findCompilationUnit(varNoArrayBrackets,
							compilationUnits);
					CompilationUnit nodeCompilationUnit = JavaParserHelper.findNodeCompilationUnit(node);
					if (nodeCompilationUnit != null && fieldClass != null && !fieldClass.equals(nodeCompilationUnit)) {
						FieldDeclaration fieldDeclaration = JavaParserHelper.findFieldDeclarationTopDown(fieldName,
								fieldClass);
						if (fieldDeclaration != null) {
							if (isVariable(fieldDeclaration)) {
								issues.add(new FileIssue(ISSUE_TYPE, node, fileString));
							}
						}
					}
				}
			} catch (NoSuchAncestorFoundException e) {
				return issues;
			} catch (NoSuchVariableException e) {
				return issues;
			}
		}
		
		return issues;
	}

	public boolean isVariable(FieldDeclaration fieldDeclaration) {
		boolean isStatic = ModifierSet.isStatic(fieldDeclaration.getModifiers());
		boolean isFinal = ModifierSet.isFinal(fieldDeclaration.getModifiers());
		boolean isVariable = !(isStatic || isFinal);
		return isVariable;
	}

	private static String withoutArrayBrackets(String string) {
		return string.replace("(\\[)|(\\])", "");
	}
}
