package autocisq.measure.maintainability;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;

import autocisq.JavaParserHelper;
import autocisq.measure.Measure;
import autocisq.models.FileIssue;
import autocisq.models.Issue;

/**
 * The MoreThan1000LOC class represents the CISQ Maintainability Measure 15: #
 * files > 1000 LOC.
 *
 * It counts all lines directly from the source file
 *
 * @author Lars A. V. Cabrera
 * 		
 */
public class MoreThan1000LOC implements Measure {
	
	@Override
	public List<Issue> analyzeNode(Node node, String fileString, List<CompilationUnit> compilationUnits,
			Map<String, Integer> layerMap) {
		List<Issue> issues = new LinkedList<>();
		if (node instanceof CompilationUnit) {
			String[] lines = fileString.split("\r?\n|\r");
			if (lines.length > 1000) {
				int[] indexes = JavaParserHelper.columnsToIndexes(fileString, node.getBeginLine(), node.getEndLine(),
						node.getBeginColumn(), node.getEndColumn());
				issues.add(new FileIssue(node.getBeginLine(), indexes[0], indexes[1], "More than 1000 Lines of Code",
						node.toString(), node));
			}
		}
		return issues;
	}
	
}
