package autocisq.measure.maintainability;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.MethodCallExpr;

import autocisq.models.Issue;

/**
 * The {@link CyclicCallBetweenPackages} class represents the CISQ
 * Maintainability measure 13: # of cyclic calls between packages.
 *
 * @author Lars A. V. Cabrera
 *
 */
public class CyclicCallBetweenPackages extends TypeDependentMeasure {

	public final static String ISSUE_TYPE = "Cyclic call between packages";

	// E.g. <method(), "package2.uri">
	private Map<MethodCallExpr, String> callMap;

	public CyclicCallBetweenPackages(Map<String, Object> settings) {
		super(settings);
		this.callMap = new HashMap<>();
	}

	@Override
	public List<Issue> analyzeNode(Node node, String fileString, List<CompilationUnit> compilationUnits) {
		super.analyzeNode(node, fileString, compilationUnits);
		if (node instanceof MethodCallExpr) {
			MethodCallExpr methodCall = (MethodCallExpr) node;
			String type = variableToType(methodCall.getScope().toString());
			String typePackage = typeToPackage(type);
			if (typePackage != null) {
				System.out.println(typePackage + " " + methodCall);
				this.callMap.put(methodCall, typePackage);
				for (MethodCallExpr packageSkippingCall : this.callMap.keySet()) {
					// TODO get packageSkippingCall's package
					String callingPackageString = "";
					String calledPackageString = this.callMap.get(packageSkippingCall);
					// TODO find back-and-forth calls
				}
			}

		}
		return null;
	};

	@Override
	public String getIssueType() {
		return ISSUE_TYPE;
	}

}
