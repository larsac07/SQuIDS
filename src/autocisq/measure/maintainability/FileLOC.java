package autocisq.measure.maintainability;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;

import autocisq.measure.Measure;
import autocisq.models.FileIssue;
import autocisq.models.Issue;

/**
 * The FileLOC class represents the CISQ Maintainability Measure 15: # files >
 * 1000 LOC.
 *
 * It counts all lines, except empty lines, directly from the source file.
 *
 * @author Lars A. V. Cabrera
 *
 */
public class FileLOC extends Measure {

	public final static int THRESHOLD = 1000;
	public final static String ISSUE_TYPE = "More than " + THRESHOLD + " Lines of Code";

	public FileLOC(Map<String, Object> settings) {
		super(settings);
	}

	@Override
	public List<Issue> analyzeNode(Node node, String fileString, List<CompilationUnit> compilationUnits) {
		List<Issue> issues = new LinkedList<>();
		if (node instanceof CompilationUnit) {
			String[] lines = fileString.split("\r?\n|\r");
			int length = 0;
			for (String line : lines) {
				if (!line.isEmpty()) {
					length++;
				}
			}
			System.out.println(length);
			if (length > THRESHOLD) {
				issues.add(new FileIssue(ISSUE_TYPE, node, fileString));
			}
		}
		return issues;
	}

	@Override
	public String getIssueType() {
		return ISSUE_TYPE;
	}

}
