package autocisq.measure.maintainability;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;

import autocisq.models.FileIssue;
import autocisq.models.Issue;

public class CISQMM20MethodParameters extends CISQMMMaintainabilityMeasure {

	public final static int THRESHOLD = 7;
	public final static String ISSUE_TYPE = "CISQ MM20: Function passing >= " + THRESHOLD + " parameters";

	public CISQMM20MethodParameters(Map<String, Object> settings) {
		super(settings);
	}

	@Override
	public List<Issue> analyzeNode(Node node, String fileString, List<CompilationUnit> compilationUnits) {
		List<Issue> issues = new ArrayList<>();
		if (node instanceof MethodDeclaration || node instanceof ConstructorDeclaration) {
			List<Parameter> parameters;
			if (node instanceof MethodDeclaration) {
				parameters = ((MethodDeclaration) node).getParameters();
			} else {
				parameters = ((ConstructorDeclaration) node).getParameters();
			}

			if (parameters.size() >= THRESHOLD) {
				issues.add(new FileIssue(this, node, fileString));
			}
		}
		return issues;
	}

	@Override
	public String getMeasureElement() {
		return ISSUE_TYPE;
	}

}
