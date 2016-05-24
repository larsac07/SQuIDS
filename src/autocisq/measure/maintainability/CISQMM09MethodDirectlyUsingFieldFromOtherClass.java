package autocisq.measure.maintainability;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.ModifierSet;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.NameExpr;

import autocisq.JavaParserHelper;
import autocisq.NoSuchAncestorFoundException;
import autocisq.models.FileIssue;
import autocisq.models.Issue;

/**
 * The CISQMM09MethodDirectlyUsingFieldFromOtherClass class represents the CISQ
 * Maintainability Measure 9: # of methods that are directly using fields from
 * other classes.
 *
 * It considers a variable as a field from another class iff its declaration
 * cannot be found locally AND it is not a constant (not declared as final).
 *
 * @author Lars A. V. Cabrera
 *
 */
public class CISQMM09MethodDirectlyUsingFieldFromOtherClass extends CISQMMTypeDependentMeasure {

	public final static String ISSUE_TYPE = "CISQ MM09: Method directly using field from other class";
	private Set<String> markedMethods;

	public CISQMM09MethodDirectlyUsingFieldFromOtherClass(Map<String, Object> settings) {
		super(settings);
		this.markedMethods = new HashSet<>();
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
							FieldDeclaration fieldDeclaration = JavaParserHelper.findFieldDeclarationInType(fieldName,
									fieldClass);
							if (fieldDeclaration != null) {
								if (isVariable(fieldDeclaration)) {
									MethodDeclaration methodDeclaration = (MethodDeclaration) JavaParserHelper
											.findNodeAncestorOfType(node, MethodDeclaration.class);
									if (!isMarked(methodDeclaration)) {
										markMethod(methodDeclaration);
										List<Issue> issues = new ArrayList<>();
										NameExpr methodHeader = JavaParserHelper.getNameExpr(methodDeclaration);
										issues.add(new FileIssue(this, methodHeader, fileString));
										return issues;
									}
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

	private boolean isMarked(MethodDeclaration methodDeclaration) {
		return this.markedMethods.contains(createMethodID(methodDeclaration));
	}

	private void markMethod(MethodDeclaration methodDeclaration) {
		this.markedMethods.add(createMethodID(methodDeclaration));
	}

	/**
	 * @param methodDeclaration
	 * @return
	 * @throws NoSuchAncestorFoundException
	 */
	private String createMethodID(MethodDeclaration methodDeclaration) {
		try {
			ClassOrInterfaceDeclaration methodClass = JavaParserHelper
					.findNodeClassOrInterfaceDeclaration(methodDeclaration);
			CompilationUnit cu = JavaParserHelper.findNodeCompilationUnit(methodClass);
			PackageDeclaration packageDeclaration = cu.getPackage();
			String methodID = "";
			if (packageDeclaration != null) {
				methodID += packageDeclaration.getName() + ".";
			}
			methodID += methodClass.getName() + ".";
			methodID += methodDeclaration.getName();
			return methodID;
		} catch (NoSuchAncestorFoundException e) {
			return methodDeclaration.getName();
		}
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
