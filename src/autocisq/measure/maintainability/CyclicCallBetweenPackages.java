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
	private Set<String> markedCyclicPackageCalls;
	private Set<String> passedPackages;

	public CyclicCallBetweenPackages(Map<String, Object> settings) {
		super(settings);
		this.callMap = new HashMap<>();
		this.markedCyclicPackageCalls = new HashSet<>();
		this.passedPackages = new HashSet<>();
	}

	@Override
	public List<Issue> analyzeNode(Node node, String fileString, List<CompilationUnit> compilationUnits) {

		// Let superclass handle storage of variables
		super.analyzeNode(node, fileString, compilationUnits);

		// MethodCallExpr or FieldAccessExpr are the node types which may call
		// other classes
		if (node instanceof MethodCallExpr || node instanceof FieldAccessExpr) {
			Expression scope = getCallScope(node);

			// If scope is null, method is internal
			if (scope != null) {
				String toPackage = typeToPackage(variableToType(scope.toString()));

				// If toPackage is null, the called class is in the same package
				// as the calling class
				if (toPackage != null) {
					PackageDeclaration nodePackage = getNodePackage(node);
					String fromPackage = "";
					if (nodePackage != null) {
						fromPackage = nodePackage.getName().toString();
					}
					return cyclicPackageCall(node, fileString, toPackage, fromPackage);
				} else {
					// The called class is in the same package as the calling
					// class
					return null;
				}
			} else {
				// The called method is internal
				return null;
			}
		} else {
			// The node is not of interest for this measure
			return null;
		}
	}

	/**
	 * Checks if a package call is cyclic, and returns an issue if it is.
	 *
	 * @param node
	 *            - the calling node
	 * @param fileString
	 *            - the string of the node's containing file
	 * @param fromPackage
	 *            - the called package
	 * @param toPackage
	 *            - the calling package
	 * @return a list containing one issue if a cyclic call is found, or zero
	 *         issues if not
	 */
	private List<Issue> cyclicPackageCall(Node node, String fileString, String fromPackage, String toPackage) {
		// If package call is known to be cyclic, simply report
		// issue
		if (markedCyclicPackageCall(fromPackage, toPackage)) {
			return createIssue(node, fileString);
		} else { // If not, check if it is
			markPackageCall(toPackage, fromPackage);
			if (cyclicPackageCall(fromPackage, fromPackage, toPackage)) {
				markCyclicPackageCall(fromPackage, toPackage);
				return createIssue(node, fileString);
			} else {
				return null;
			}
		}
	}

	/**
	 * Recursive method to find a cyclic calling path between packages.
	 *
	 * @param fromPackage
	 *            - the calling package
	 * @param toPackage
	 *            - the called package.
	 * @return true if a cyclic path between packages is found, false if not
	 */
	private boolean cyclicPackageCall(String originalPackage, String fromPackage, String toPackage) {
		Set<String> calledPackages = this.callMap.get(toPackage);
		if (calledPackages != null) {
			for (String calledPackage : calledPackages) {
				if (calledPackage.equals(originalPackage)) {
					return true;
				} else {
					// Only continue the path if the calledPackage has not yet
					// been passed
					if (this.passedPackages.add(calledPackage)) {
						if (cyclicPackageCall(originalPackage, toPackage, calledPackage)) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	/**
	 * @param node
	 * @return
	 */
	private Expression getCallScope(Node node) {
		Expression scope;
		if (node instanceof MethodCallExpr) {
			MethodCallExpr methodCall = (MethodCallExpr) node;
			scope = methodCall.getScope();
		} else {
			FieldAccessExpr fieldAccessExpr = (FieldAccessExpr) node;
			scope = fieldAccessExpr.getScope();
		}
		return scope;
	}

	/**
	 * @param toPackage
	 * @param fromPackage
	 */
	private void markPackageCall(String toPackage, String fromPackage) {
		Set<String> packageCalls = this.callMap.get(fromPackage);
		if (packageCalls == null) {
			packageCalls = new HashSet<>();
		}
		packageCalls.add(toPackage);
		this.callMap.put(fromPackage, packageCalls);
	}

	/**
	 * @param node
	 * @param fileString
	 * @return
	 */
	private List<Issue> createIssue(Node node, String fileString) {
		List<Issue> issues = new LinkedList<>();
		issues.add(new FileIssue(this, node, fileString));
		return issues;
	}

	private boolean markedCyclicPackageCall(String fromPackage, String toPackage) {
		return this.markedCyclicPackageCalls.contains(fromToPackage(fromPackage, toPackage));
	}

	private void markCyclicPackageCall(String fromPackage, String toPackage) {
		this.markedCyclicPackageCalls.add(fromToPackage(fromPackage, toPackage));
	}

	private String fromToPackage(String fromPackage, String toPackage) {
		return fromPackage + "->" + toPackage;
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
