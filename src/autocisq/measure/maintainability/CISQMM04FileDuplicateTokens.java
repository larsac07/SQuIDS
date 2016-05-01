package autocisq.measure.maintainability;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;

import autocisq.models.FileIssue;
import autocisq.models.Issue;

/**
 * The {@link CISQMM04FileDuplicateTokens} class represents the CISQ Maintainability
 * measure 4: # files that contain 100+ consecutive duplicate tokens.
 *
 * It considers tokens as Java keywords, identifiers, separators, operators and
 * literals. In other words, comments and whitespace is not considered.
 *
 * A file is considered a {@link CompilationUnit}, and contains 100+ consecutive
 * duplicate tokens iff it has >= 100 consecutive tokens that have an equal list
 * of tokens in the same file.
 *
 * Due to performance efficiency, the exact amount of consecutive duplicate
 * tokens is not provided (i.e. the measure "stops counting" at 100).
 *
 * @author Lars A. V. Cabrera
 *
 */
public class CISQMM04FileDuplicateTokens extends CISQMaintainabilityMeasure {

	public final static int THRESHOLD = 100;
	public final static String ISSUE_TYPE = "CISQ MM04: File with >= " + THRESHOLD + " consecutive duplicate tokens";

	private final static String SEPARATORS = ";|,|\\.|\\(|\\)|\\{|\\|\\}|\\[|\\]";
	private final static String OPERATORS = "~|\\?|:|==|=|<=|>=|>|<|!=|!|&&|\\|\\||\\+\\+|--|\\+|-|\\*|/|&|\\||\\^|%|\\$|\\#";
	private final static String IDENTIFIERS = "[a-zA-Z_][a-zA-Z0-9_]*";
	private final static String LITERALS = "[0-9]+(l|L)?\\.?([0-9]+((f|F)|(d|D)))?|\"^\"*\"";

	private Pattern pattern;

	public CISQMM04FileDuplicateTokens(Map<String, Object> settings) {
		super(settings);
		this.pattern = Pattern.compile(SEPARATORS + "|" + OPERATORS + "|" + IDENTIFIERS + "|" + LITERALS);
	}

	@Override
	public List<Issue> analyzeNode(Node node, String fileString, List<CompilationUnit> compilationUnits) {
		if (node instanceof CompilationUnit) {
			Matcher matcher = this.pattern.matcher(node.toStringWithoutComments());
			// ArrayList because of use of index in listPartsEqual
			List<String> tokens = new ArrayList<>();
			while (matcher.find()) {
				tokens.add(matcher.group());
			}
			// If there are at least two 100-token lists to compare
			if (tokens.size() >= THRESHOLD * 2) {
				return findDuplicates(node, tokens);
			}
		}
		return null;
	}

	/**
	 * @param node
	 * @param tokens
	 */
	private List<Issue> findDuplicates(Node node, List<String> tokens) {
		int size = tokens.size();
		for (int i1 = 0, e1 = i1 + THRESHOLD; i1 < size; i1++, e1 = i1 + THRESHOLD) {
			for (int i2 = e1, e2 = i2 + THRESHOLD; i2 < size; i2++, e2++) {
				if (i1 <= e2 && e1 <= i2 && e1 < size && e2 < size) {
					if (listPartsEqual(tokens, i1, e1, i2, e2)) {
						List<Issue> issues = new ArrayList<>();
						issues.add(new FileIssue(this, node, String.join(" ", tokens)));
						return issues;
					}
				}
			}
		}
		return null;
	}

	/**
	 * Custom equals-function of list parts, instead of sublisting one list and
	 * comparing them.
	 *
	 * @param list
	 *            - the list to check
	 * @param from1
	 *            - the start index of the first part to check
	 * @param to1
	 *            - the end index of the first part to check
	 * @param from2
	 *            - the start index of the second part to check
	 * @param to2
	 *            - the end index of the second part to check
	 * @return true if the two parts of the list are equal
	 */
	private boolean listPartsEqual(List<String> list, int from1, int to1, int from2, int to2) {
		for (int x = from1, y = from2; x < to1 && y < to2; x++, y++) {
			if (!list.get(x).equals(list.get(y))) {
				return false;
			}
		}
		return true;
	}

	@Override
	public String getMeasureElement() {
		return ISSUE_TYPE;
	}

}
