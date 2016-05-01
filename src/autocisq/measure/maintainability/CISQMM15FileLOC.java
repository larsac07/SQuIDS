package autocisq.measure.maintainability;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;

import autocisq.models.FileIssue;
import autocisq.models.Issue;

/**
 * The CISQMM15FileLOC class represents the CISQ Maintainability Measure 15: #
 * files > 1000 LOC.
 *
 * It counts all lines, except empty lines, directly from the source file.
 *
 * @author Lars A. V. Cabrera
 *
 */
public class CISQMM15FileLOC extends CISQMaintainabilityMeasure {

	public final static int THRESHOLD = 1000;
	public final static String ISSUE_TYPE = "CISQ MM15: More than " + THRESHOLD + " Lines of Code";
	private final static String COMMENTS = "^\\s*//" + "|" + "([^\"]/\\*\\*(?s:(?!\\*/).)*\\*/[^\"])";

	public CISQMM15FileLOC(Map<String, Object> settings) {
		super(settings);
	}

	@Override
	public List<Issue> analyzeNode(Node node, String fileString, List<CompilationUnit> compilationUnits) {
		List<Issue> issues = new LinkedList<>();
		if (node instanceof CompilationUnit) {
			int ploc = calculatePhysicalLOC(fileString);
			if (ploc > THRESHOLD) {
				issues.add(new FileIssue(this, node, fileString));
			}
		}
		return issues;
	}

	/**
	 * @param node
	 * @return
	 */
	public static int calculatePhysicalLOC(String fileString) {
		String nodeString = removeAllComments(fileString);
		String[] lines = nodeString.split("\r?\n|\r");
		int length = 0;
		for (String line : lines) {
			if (!line.isEmpty()) {
				length++;
			}
		}
		return length;
	}

	private static String removeAllComments(String fileString) {
		return fileString.replaceAll(COMMENTS, "");
	}

	@Override
	public String getMeasureElement() {
		return ISSUE_TYPE;
	}

}
