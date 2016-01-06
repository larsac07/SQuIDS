package autocisq.measure.maintainability;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.MethodCallExpr;

import autocisq.JavaParserHelper;
import autocisq.NoAncestorFoundException;
import autocisq.measure.Measure;
import autocisq.models.FileIssue;
import autocisq.models.Issue;

/**
 * The LayerSkippingCall class represents the CISQ Maintainability Measure 2: #
 * of layer-skipping calls.
 *
 * @author Lars A. V. Cabrera
 *
 */
public class LayerSkippingCall implements Measure {

	private List<CompilationUnit> compilationUnits;
	private Map<String, Integer> layerMap;

	/**
	 * Constructs a new LayerSkipppingCall measure with a list of compilation
	 * units in the project and a map of layers.
	 *
	 * If either values passed through the parameters are null, new are created.
	 *
	 * The required layerMap is a map of which compilation units are assigned to
	 * which layer. The compilation unit is represented as package name + class
	 * name, e.g. java.util.List, and the layer is represented as a simple
	 * integer, e.g. "layer 1" = 1.
	 *
	 * @param compilationUnits
	 *            - a list of all the CompilationUnit objects in the current
	 *            project.
	 * @param layerMap
	 *            - a map of which compilation units are assigned to which
	 *            layer.
	 */
	public LayerSkippingCall(List<CompilationUnit> compilationUnits, Map<String, Integer> layerMap) {
		if (compilationUnits == null) {
			compilationUnits = new LinkedList<>();
		}
		if (layerMap == null) {
			layerMap = new HashMap<>();
		}

		this.compilationUnits = compilationUnits;
		this.layerMap = layerMap;
	}

	@Override
	public List<Issue> analyzeNode(Node node, String fileString) {
		List<Issue> issues = new LinkedList<>();
		if (node instanceof MethodCallExpr) {
			MethodCallExpr methodCall = (MethodCallExpr) node;
			if (!JavaParserHelper.methodCallFromSameType(methodCall)) {
				CompilationUnit methodCompilationUnit = JavaParserHelper.findMethodCompilationUnit(methodCall,
						this.compilationUnits);
				if (methodCompilationUnit != null) {
					CompilationUnit methodCallCompilationUnit = null;
					try {
						methodCallCompilationUnit = JavaParserHelper.findNodeCompilationUnit(methodCall);
					} catch (NoAncestorFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					String methodClass = methodCompilationUnit.getPackage().getPackageName() + "."
							+ methodCompilationUnit.getTypes().get(0).getName();
					String methodCallClass = methodCallCompilationUnit.getPackage().getPackageName() + "."
							+ methodCallCompilationUnit.getTypes().get(0).getName();

					Integer methodLayer = this.layerMap.get(methodClass);
					Integer methodCallLayer = this.layerMap.get(methodCallClass);

					if (methodLayer != null) {
						if (Math.abs(methodLayer - methodCallLayer) > 1) {
							int[] indexes = JavaParserHelper.columnsToIndexes(fileString, node.getBeginLine(),
									node.getEndLine(), node.getBeginColumn(), node.getEndColumn());
							issues.add(new FileIssue(methodCall.getBeginLine(), indexes[0], indexes[1],
									"Layer-Skipping Call", methodCall.toString(), methodCall));
						}
					}
				}
			}
		}
		return issues;
	}
}
