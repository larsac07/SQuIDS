package autocisq.measure.maintainability;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.ModifierSet;
import com.github.javaparser.ast.expr.FieldAccessExpr;

import autocisq.JavaParserHelper;
import autocisq.NoSuchAncestorFoundException;
import autocisq.models.FileIssue;
import autocisq.models.Issue;

/**
 * The MethodDirectlyUsingFieldFromOtherClass class represents the CISQ
 * Maintainability Measure 9: # of methods that are directly using fields from
 * other classes.
 *
 * It considers a variable as a field from another class iff its declaration
 * cannot be found locally AND it is not a constant (not declared as final).
 *
 * @author Lars A. V. Cabrera
 *
 */
public class MethodDirectlyUsingFieldFromOtherClass extends TypeDependentMeasure {

	public final static String ISSUE_TYPE = "Method directly using field from other class";

	public MethodDirectlyUsingFieldFromOtherClass(Map<String, Object> settings) {
		super(settings);
	}

	@Override
	public List<Issue> analyzeNode(Node node, String fileString, List<CompilationUnit> compilationUnits) {
		super.analyzeNode(node, fileString, compilationUnits);

		if (node instanceof FieldAccessExpr) {
			FieldAccessExpr fieldAccessExpr = (FieldAccessExpr) node;
			String variableName = fieldAccessExpr.getScope().toString();
			String fieldName = fieldAccessExpr.getField();

			String type = variableToType(variableName);
			if (type != null) {
				String varNoArrayBrackets = withoutArrayBrackets(type);
				ClassOrInterfaceDeclaration fieldClass = JavaParserHelper
						.findClassOrInterfaceDeclaration(varNoArrayBrackets, compilationUnits);
				if (fieldClass != null) {
					try {
						ClassOrInterfaceDeclaration nodeClass = JavaParserHelper
								.findNodeClassOrInterfaceDeclaration(node);
						if (!fieldClass.equals(nodeClass)) {
							FieldDeclaration fieldDeclaration = JavaParserHelper.findFieldDeclarationTopDown(fieldName,
									fieldClass);
							if (fieldDeclaration != null) {
								if (isVariable(fieldDeclaration)) {
									List<Issue> issues = new ArrayList<>();
									issues.add(new FileIssue(this, node, fileString));
									return issues;
								}
							}
						}
					} catch (NoSuchAncestorFoundException e) {
						return null;
					}
				}
			}
		}
		return null;
	}

	public boolean isVariable(FieldDeclaration fieldDeclaration) {
		return !ModifierSet.isFinal(fieldDeclaration.getModifiers());
	}

	private static String withoutArrayBrackets(String string) {
		return string.replace("(\\[)|(\\])", "");
	}

	@Override
	public String getMeasureElement() {
		return ISSUE_TYPE;
	}
}
