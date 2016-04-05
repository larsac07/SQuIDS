package autocisq.measure.maintainability;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;

import autocisq.JavaParserHelper;
import autocisq.NoSuchAncestorFoundException;
import autocisq.models.FileIssue;
import autocisq.models.Issue;

/**
 * The {@link CyclicCallBetweenPackages} class represents the CISQ
 * Maintainability measure 13: # of cyclic calls between packages.
 *
 * It considers a cyclic calls as instances where a class A from package A calls
 * to variables or methods in a class B from package B, and class B calls back
 * to variables or methods in class A.
 *
 * It considers a call to a variable or method in another class as a
 * {@link MethodCallExpr} or {@link FieldAccessExpr} which refers to a class
 * with a different package signature.
 *
 * @author Lars A. V. Cabrera
 *
 */
public class CyclicCallBetweenPackages extends TypeDependentMeasure {

	public final static String ISSUE_TYPE = "Cyclic call between packages";

	// E.g. <"package1", "package2">
	private Map<String, Set<String>> callMap;
	private String path;

	public CyclicCallBetweenPackages(Map<String, Object> settings) {
		super(settings);
		this.callMap = new HashMap<>();
		this.path = "";
	}

	@Override
	public List<Issue> analyzeNode(Node node, String fileString, List<CompilationUnit> compilationUnits) {
		super.analyzeNode(node, fileString, compilationUnits);
		if (node instanceof MethodCallExpr || node instanceof FieldAccessExpr) {
			Expression scope;
			if (node instanceof MethodCallExpr) {
				MethodCallExpr methodCall = (MethodCallExpr) node;
				scope = methodCall.getScope();
			} else {
				FieldAccessExpr fieldAccessExpr = (FieldAccessExpr) node;
				scope = fieldAccessExpr.getScope();
			}
			if (scope != null) {
				String type = variableToType(scope.toString());
				String toPackage = typeToPackage(type);
				if (toPackage != null) {
					PackageDeclaration nodePackage = getNodePackage(node);
					String fromPackage = "";
					if (nodePackage != null) {
						fromPackage = nodePackage.getName().toString();
					}
					Set<String> packageCalls = this.callMap.get(fromPackage);
					if (packageCalls == null) {
						packageCalls = new HashSet<>();
					}
					packageCalls.add(toPackage);
					this.callMap.put(fromPackage, packageCalls);
					if (cyclicCall(fromPackage, fromPackage, toPackage, fromPackage)) {
						List<Issue> issues = new LinkedList<>();
						issues.add(new FileIssue(this, node, fileString));
						return issues;
					}
				}
			}

		}
		return null;
	};

	private boolean cyclicCall(String originalPackage, String fromPackage, String toPackage, String path) {
		Set<String> calledPackages = this.callMap.get(toPackage);
		if (calledPackages != null) {
			for (String calledPackage : calledPackages) {
				String newPath = path + " -> " + calledPackage;
				if (calledPackage.equals(originalPackage)) {
					this.path = newPath;
					return true;
				} else if (cyclicCall(originalPackage, toPackage, calledPackage, newPath)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Returns the {@link PackageDeclaration} of the {@link CompilationUnit}
	 * ancestor of a node.
	 *
	 * @param node
	 *            - the node to get the package for
	 * @return the {@link PackageDeclaration} of the {@link CompilationUnit}
	 *         ancestor of the node
	 */
	private PackageDeclaration getNodePackage(Node node) {
		try {
			CompilationUnit cu = JavaParserHelper.findNodeCompilationUnit(node);
			return cu.getPackage();
		} catch (NoSuchAncestorFoundException e) {
			return null;
		}
	}

	@Override
	public String getMeasureElement() {
		return ISSUE_TYPE;
	}

}
