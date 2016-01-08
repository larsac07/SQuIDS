package autocisq.measure.maintainability;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.ModifierSet;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.type.Type;

import autocisq.JavaParserHelper;
import autocisq.NoAncestorFoundException;
import autocisq.measure.Measure;
import autocisq.models.FileIssue;
import autocisq.models.Issue;

/**
 * The MethodDirectlyUsingFieldFromOtherClass class represents the CISQ
 * Maintainability Measure 9: # of methods that are directly using fields from
 * other classes.
 *
 * It considers a method as a method iff it is non-static, i.e. member
 * functions.
 *
 * It considers a variable as a field from another class iff its declaration
 * cannot be found locally AND it is not a constant (not declared as static
 * final).
 *
 * @author Lars A. V. Cabrera
 *		
 */
public class MethodDirectlyUsingFieldFromOtherClass implements Measure {
	
	@Override
	public List<Issue> analyzeNode(Node node, String fileString, List<CompilationUnit> compilationUnits,
			Map<String, Integer> layerMap) {
		List<Issue> issues = new ArrayList<>();

		if (node instanceof FieldAccessExpr) {
			try {
				MethodDeclaration enclosingMethod = (MethodDeclaration) JavaParserHelper.findNodeAncestorOfType(node,
						MethodDeclaration.class);
				boolean enclosingMethodStatic = ModifierSet.isStatic(enclosingMethod.getModifiers());
				if (!enclosingMethodStatic) {
					
					FieldAccessExpr fieldAccessExpr = (FieldAccessExpr) node;
					String variableName = fieldAccessExpr.getScope().toString();
					String fieldName = fieldAccessExpr.getField();
					List<Type> types = JavaParserHelper.findVariableTypeBottomUp(variableName, node);
					
					for (Type variableType : types) {
						
						CompilationUnit fieldClass = JavaParserHelper.findCompilationUnit(variableType.toString(),
								compilationUnits);
						if (!fieldClass.equals(JavaParserHelper.findNodeCompilationUnit(node))) {
							FieldDeclaration fieldDeclaration = JavaParserHelper.findFieldDeclarationTopDown(fieldName,
									fieldClass);
							if (fieldDeclaration != null) {
								boolean isStatic = ModifierSet.isStatic(fieldDeclaration.getModifiers());
								boolean isFinal = ModifierSet.isFinal(fieldDeclaration.getModifiers());
								boolean isVariable = !(isStatic || isFinal);

								if (isVariable) {
									int[] indexes = JavaParserHelper.columnsToIndexes(fileString, node.getBeginLine(),
											node.getEndLine(), node.getBeginColumn(), node.getEndColumn());
									issues.add(new FileIssue(node.getBeginLine(), indexes[0], indexes[1],
											"Method directly using field from other class", node.toString(), node));
								}
							}
						}
					}
				}
			} catch (NoAncestorFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return issues;
	}
}
