package squids.measure.maintainability;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.ModifierSet;
import com.github.javaparser.ast.body.VariableDeclarator;

import squids.models.FileIssue;
import squids.models.Issue;

/**
 * The CISQMM10VariableDeclaredPublic class represents the CISQ Maintainability Measure
 * 10: # of variables declared public.
 *
 * It considers a variable as a public variable only if it is public and a
 * variable (not final). In other words, variables declared as public static
 * final or public final are considered constants.
 *
 * @author Lars A. V. Cabrera
 *
 */
public class CISQMM10VariableDeclaredPublic extends CISQMaintainabilityMeasure {

	public final static String ISSUE_TYPE = "CISQ MM10: Variable declared public";

	public CISQMM10VariableDeclaredPublic(Map<String, Object> settings) {
		super(settings);
	}

	@Override
	public List<Issue> analyzeNode(Node node, String fileString, List<CompilationUnit> compilationUnits) {

		if (node instanceof FieldDeclaration) {
			FieldDeclaration field = (FieldDeclaration) node;
			int modifiers = field.getModifiers();
			boolean isFinal = ModifierSet.isFinal(modifiers);
			boolean isPublic = ModifierSet.isPublic(modifiers);
			if (!isFinal && isPublic) {
				List<Issue> issues = new ArrayList<>();
				for (VariableDeclarator variable : field.getVariables()) {
					issues.add(new FileIssue(this, variable, fileString));
				}
				return issues;
			}

		}
		return null;
	}

	@Override
	public String getMeasureElement() {
		return ISSUE_TYPE;
	}

}
