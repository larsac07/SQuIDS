package autocisq.measure;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;

import autocisq.JavaParserHelper;
import autocisq.models.FileIssue;
import autocisq.models.Issue;

public class FunctionPassing7OrMoreParameters implements Measure {

	@Override
	public List<Issue> analyzeNode(Node node, String fileString) {
		List<Issue> issues = new ArrayList<>();
		if (node instanceof MethodDeclaration) {
			MethodDeclaration methodDeclaration = (MethodDeclaration) node;
			List<Parameter> parameters = methodDeclaration.getParameters();
			if (parameters.size() >= 7) {
				int[] indexes = JavaParserHelper.columnsToIndexes(fileString, node.getBeginLine(), node.getEndLine(),
						node.getBeginColumn(), node.getEndColumn());
				issues.add(new FileIssue(node.getBeginLine(), indexes[0], indexes[1],
						"Function passing 7 or more parameters", node.toString(), node));
			}
		}
		return issues;
	}

}
