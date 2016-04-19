package autocisq.measure.maintainability;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.MethodCallExpr;

import autocisq.JavaParserHelper;
import autocisq.NoSuchAncestorFoundException;
import autocisq.models.FileIssue;
import autocisq.models.Issue;

/**
 * The LayerSkippingCall class represents the CISQ Maintainability Measure 2: #
 * of layer-skipping calls.
 *
 * @author Lars A. V. Cabrera
 *
 */
public class LayerSkippingCall extends MaintainabilityMeasure {

	public final static String ISSUE_TYPE = "CISQ MM02: Layer-Skipping Call";

	private Map<String, Integer> layerMap;

	@SuppressWarnings("unchecked")
	public LayerSkippingCall(Map<String, Object> settings) {
		super(settings);
		try {
			this.layerMap = (Map<String, Integer>) settings.get("layer_map");
			if (this.layerMap == null) {
				System.err.println(this.getClass().getSimpleName()
						+ " was provided an empty layer_map and will not work. Please provide a layer_map");
				this.layerMap = new HashMap<>();
			}
		} catch (NullPointerException | ClassCastException e) {
			this.layerMap = new HashMap<>();
			System.err.println(this.getClass().getSimpleName()
					+ " was not provided a layer_map and will not work. Please provide a layer_map");
			e.printStackTrace();
		}
	}

	@Override
	public List<Issue> analyzeNode(Node node, String fileString, List<CompilationUnit> compilationUnits) {
		List<Issue> issues = new LinkedList<>();
		if (node instanceof MethodCallExpr) {
			MethodCallExpr methodCall = (MethodCallExpr) node;
			if (!JavaParserHelper.methodCallFromSameType(methodCall)) {
				CompilationUnit methodCompilationUnit = JavaParserHelper.findMethodCompilationUnit(methodCall,
						compilationUnits);
				if (methodCompilationUnit != null) {
					CompilationUnit methodCallCompilationUnit = null;
					try {
						methodCallCompilationUnit = JavaParserHelper.findNodeCompilationUnit(methodCall);

						String callingPackage = methodCompilationUnit.getPackage().getName().toString();
						String callingClass = methodCompilationUnit.getTypes().get(0).getName();
						String methodClass = callingPackage + "." + callingClass;

						String targetPackage = "";
						if (methodCallCompilationUnit.getPackage() != null) {
							targetPackage = methodCallCompilationUnit.getPackage().getName().toString();
						}
						String targetClass = methodCallCompilationUnit.getTypes().get(0).getName();
						String targetMethodClass = targetPackage + "." + targetClass;

						Integer methodLayer = this.layerMap.get(methodClass);
						Integer methodCallLayer = this.layerMap.get(targetMethodClass);

						if (methodLayer != null) {
							if (Math.abs(methodLayer - methodCallLayer) > 1) {
								issues.add(new FileIssue(this, methodCall, fileString));
							}
						}
					} catch (NoSuchAncestorFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}
		}
		return issues;
	}

	@Override
	public String getMeasureElement() {
		return ISSUE_TYPE;
	}
}
