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
public class LayerSkippingCall extends Measure {
	
	public final static String ISSUE_TYPE = "Layer-Skipping Call";

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
					} catch (NoSuchAncestorFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					String methodClass = methodCompilationUnit.getPackage().getName() + "."
							+ methodCompilationUnit.getTypes().get(0).getName();
					String methodCallClass = methodCallCompilationUnit.getPackage().getName() + "."
							+ methodCallCompilationUnit.getTypes().get(0).getName();
							
					Integer methodLayer = this.layerMap.get(methodClass);
					Integer methodCallLayer = this.layerMap.get(methodCallClass);
					
					if (methodLayer != null) {
						if (Math.abs(methodLayer - methodCallLayer) > 1) {
							int[] indexes = JavaParserHelper.columnsToIndexes(fileString, node.getBeginLine(),
									node.getEndLine(), node.getBeginColumn(), node.getEndColumn());
							issues.add(new FileIssue(methodCall.getBeginLine(), indexes[0], indexes[1], ISSUE_TYPE,
									methodCall.toString(), methodCall));
						}
					}
				}
			}
		}
		return issues;
	}

	@Override
	public String getIssueType() {
		return ISSUE_TYPE;
	}
}
