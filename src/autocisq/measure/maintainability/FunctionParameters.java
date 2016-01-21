package autocisq.measure.maintainability;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.ModifierSet;
import com.github.javaparser.ast.body.Parameter;

import autocisq.JavaParserHelper;
import autocisq.measure.Measure;
import autocisq.models.FileIssue;
import autocisq.models.Issue;

public class FunctionParameters extends Measure {

	public final static int THRESHOLD = 7;
	public final static String ISSUE_TYPE = "Function passing >= " + THRESHOLD + " parameters";
	
	public FunctionParameters(Map<String, Object> settings) {
		super(settings);
	}
	
	@Override
	public List<Issue> analyzeNode(Node node, String fileString, List<CompilationUnit> compilationUnits) {
		List<Issue> issues = new ArrayList<>();
		if (node instanceof MethodDeclaration) {

			MethodDeclaration methodDeclaration = (MethodDeclaration) node;
			List<Parameter> parameters = methodDeclaration.getParameters();
			int modifiers = methodDeclaration.getModifiers();
			boolean isFunction = ModifierSet.isStatic(modifiers);
			boolean meq7Params = parameters.size() >= 7;

			if (isFunction && meq7Params) {
				int[] indexes = JavaParserHelper.columnsToIndexes(fileString, node.getBeginLine(), node.getEndLine(),
						node.getBeginColumn(), node.getEndColumn());
				issues.add(new FileIssue(node.getBeginLine(), indexes[0], indexes[1], getIssueType(), node.toString(),
						node));
			}
		}
		return issues;
	}

	@Override
	public String getIssueType() {
		return ISSUE_TYPE;
	}
	
}
