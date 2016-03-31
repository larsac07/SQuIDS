package autocisq.measure.maintainability;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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

	// E.g. <method(), "package2.uri">
	private Map<Node, PackageCall> callMap;

	public CyclicCallBetweenPackages(Map<String, Object> settings) {
		super(settings);
		this.callMap = new HashMap<>();
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
					this.callMap.put(node, new PackageCall(fromPackage, toPackage));
					for (Node packageSkippingCall : this.callMap.keySet()) {
						PackageCall packageCall = this.callMap.get(packageSkippingCall);
						if (packageCall.getFromPackage().equals(toPackage)
								&& packageCall.getToPackage().equals(fromPackage)) {
							List<Issue> issues = new LinkedList<>();
							issues.add(new FileIssue(this, node, fileString));
							issues.add(new FileIssue(this, packageSkippingCall,
									JavaParserHelper.getNodeFileString(packageSkippingCall)));
							return issues;
						}
					}
				}
			}

		}
		return null;
	};

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

	/**
	 * The internal {@link PackageCall} class is a helper class to hold linkages
	 * between packages. E.g. "somepackage.subpackageA" refers to
	 * "somepackage.subpackageB".
	 *
	 * @author Lars A. V. Cabrera
	 *
	 */
	private class PackageCall {

		private String fromPackage;
		private String toPackage;

		public PackageCall(String fromPackage, String toPackage) {
			this.fromPackage = fromPackage;
			this.toPackage = toPackage;
		}

		public String getFromPackage() {
			return this.fromPackage;
		}

		public String getToPackage() {
			return this.toPackage;
		}

		@Override
		public String toString() {
			return this.fromPackage + " to " + this.toPackage;
		}
	}

}
