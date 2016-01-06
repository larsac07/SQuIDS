package autocisq.measure.maintainability;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.ModifierSet;

import autocisq.JavaParserHelper;
import autocisq.measure.Measure;
import autocisq.models.FileIssue;
import autocisq.models.Issue;

/**
 * The VariableDeclaredPublic class represents the CISQ Maintainability Measure
 * 10: # of variables declared public.
 *
 * It considers a variable as a public variable only if it is public and a
 * variable (not static and final at the same time). In other words, variables
 * declared as public static final are considered constants.
 *
 * @author Lars A. V. Cabrera
 *		
 */
public class VariableDeclaredPublic implements Measure {
	
	@Override
	public List<Issue> analyzeNode(Node node, String fileString) {
		List<Issue> issues = new ArrayList<>();

		if (node instanceof FieldDeclaration) {
			FieldDeclaration field = (FieldDeclaration) node;
			int modifiers = field.getModifiers();
			boolean isVariable = !ModifierSet.isStatic(modifiers) && !ModifierSet.isFinal(modifiers);
			boolean isPublic = ModifierSet.isPublic(modifiers);
			if (isVariable && isPublic) {
				int[] indexes = JavaParserHelper.columnsToIndexes(fileString, node.getBeginLine(), node.getEndLine(),
						node.getBeginColumn(), node.getEndColumn());
				issues.add(new FileIssue(node.getBeginLine(), indexes[0], indexes[1], "Variable declared public",
						node.toString(), node));
			}

		}

		return issues;
	}
	
}
