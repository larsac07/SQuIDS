package autocisq.measure.maintainability;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.MethodCallExpr;

import autocisq.JavaParserHelper;
import autocisq.models.FileIssue;
import autocisq.models.Issue;

/**
 * The CISQMM02LayerSkippingCall class represents the CISQ Maintainability
 * Measure 2: # of layer-skipping calls.
 *
 * @author Lars A. V. Cabrera
 *
 */
public class CISQMM02LayerSkippingCall extends CISQMMLayerDependentMeasure {

	public final static String ISSUE_TYPE = "CISQ MM02: Layer-Skipping Call";

	public CISQMM02LayerSkippingCall(Map<String, Object> settings) {
		super(settings);
	}

	@Override
	public List<Issue> analyzeNode(Node node, String fileString, List<CompilationUnit> compilationUnits) {
		if (node instanceof MethodCallExpr) {
			MethodCallExpr methodCall = (MethodCallExpr) node;
			if (!JavaParserHelper.methodCallFromSameType(methodCall)) {
				CompilationUnit methodCompilationUnit = JavaParserHelper.findMethodCompilationUnit(methodCall,
						compilationUnits);
				if (methodCompilationUnit != null) {
					CompilationUnit methodCallCompilationUnit = JavaParserHelper.findNodeCompilationUnit(methodCall);

					String callingPackage = methodCompilationUnit.getPackage().getName().toString();
					String callingClass = methodCompilationUnit.getTypes().get(0).getName();
					String methodClass = callingPackage + "." + callingClass;

					String targetPackage = "";
					if (methodCallCompilationUnit.getPackage() != null) {
						targetPackage = methodCallCompilationUnit.getPackage().getName().toString();
					}
					String targetClass = methodCallCompilationUnit.getTypes().get(0).getName();
					String targetMethodClass = targetPackage + "." + targetClass;

					boolean layerSkippingCall = isLayerSkippingCall(methodClass, targetMethodClass);

					if (layerSkippingCall) {
						List<Issue> issues = new LinkedList<>();
						issues.add(new FileIssue(this, methodCall, fileString));
						return issues;
					}
				}
			}
		}
		return null;
	}

	/**
	 * @param methodClass
	 * @param targetMethodClass
	 * @return
	 */
	protected boolean isLayerSkippingCall(String methodClass, String targetMethodClass) {
		Set<Integer> methodLayers = new HashSet<>();
		for (int i = 0; i < this.layers.size(); i++) {
			Set<String> layerAssignments = this.layers.get(i);
			if (layerAssignments.contains(methodClass)) {
				methodLayers.add(i);
			}
		}

		for (int i = 0; i < this.layers.size(); i++) {
			Set<String> layerAssignments = this.layers.get(i);
			if (layerAssignments.contains(targetMethodClass)) {
				for (Integer methodLayer : methodLayers) {
					if (diff(methodLayer, i) > 1) {
						return true;
					}
				}
			}
		}
		return false;
	}

	protected int diff(int int1, int int2) {
		return Math.abs(int1 - int2);
	}

	@Override
	public String getMeasureElement() {
		return ISSUE_TYPE;
	}
}
