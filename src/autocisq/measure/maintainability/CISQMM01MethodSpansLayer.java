package autocisq.measure.maintainability;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;

import autocisq.JavaParserHelper;
import autocisq.models.FileIssue;
import autocisq.models.Issue;

public class CISQMM01MethodSpansLayer extends CISQMMLayerDependentMeasure {

	public final static String ISSUE_TYPE = "CISQ MM01: Function that spans layers";
	public final static int THRESHOLD = 1;

	private final static String MESSAGE = " spans these layers: ";

	private final static String DOT = ".";
	private final static String METHOD_REGEX = "[a-zA-Z_][a-zA-Z0-9_]*\\([^\\)]*\\)";

	public CISQMM01MethodSpansLayer(Map<String, Object> settings) {
		super(settings);
	}

	@Override
	public List<Issue> analyzeNode(Node node, String fileString, List<CompilationUnit> compilationUnits) {
		if (node instanceof MethodDeclaration) {
			String path = JavaParserHelper.getMemberPath(node);
			int layerAmount = 0;
			for (Set<String> layer : this.layers) {
				if (layer.contains(path)) {
					layerAmount++;
				}
			}
			if (layerAmount > 1) {
				List<Issue> issues = new LinkedList<>();
				String message = path + MESSAGE + layerAmount;
				issues.add(new FileIssue(this, node, fileString, message));
				return issues;
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	protected boolean isMethod(String string) {
		int lastDot = string.lastIndexOf(DOT);
		String lastPart = string.substring(lastDot + 1, string.length());
		return lastPart.matches(METHOD_REGEX);
	}

	@Override
	public String getMeasureElement() {
		return ISSUE_TYPE;
	}

}
