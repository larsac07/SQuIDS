package autocisq.measure.maintainability;

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

	@Override
	public List<Issue> analyzeNode(Node node, String fileString, List<CompilationUnit> compilationUnits,
			Map<String, Integer> layerMap) {
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
					} catch (NoAncestorFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					String methodClass = methodCompilationUnit.getPackage().getPackageName() + "."
							+ methodCompilationUnit.getTypes().get(0).getName();
					String methodCallClass = methodCallCompilationUnit.getPackage().getPackageName() + "."
							+ methodCallCompilationUnit.getTypes().get(0).getName();

					Integer methodLayer = layerMap.get(methodClass);
					Integer methodCallLayer = layerMap.get(methodCallClass);

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
