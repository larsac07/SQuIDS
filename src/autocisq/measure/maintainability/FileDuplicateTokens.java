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
 * The {@link FileDuplicateTokens} class represents the CISQ Maintainability
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
public class FileDuplicateTokens extends MaintainabilityMeasure {

	public final static int THRESHOLD = 100;
	public final static String ISSUE_TYPE = "File with >= " + THRESHOLD + " consecutive duplicate tokens";

	private final static String KEYWORDS = "abstract|assert|boolean|break|byte|case|catch|char|class|const|continue|default|"
			+ "double|else|enum|extends|false|finally|final|float|for|goto|if|implements|import|instanceof|interface|int|long|"
			+ "native|new|null|package|private|protected|public|return|short|static|strictfp|super|switch|synchronized|this|"
			+ "throws|throw|true|transient|try|void|volatile|while|todo|do";
	private final static String SEPARATORS = ";|,|\\.|\\(|\\)|\\{|\\|\\}|\\[|\\]";
	private final static String OPERATORS = "~|\\?|:|==|=|<=|>=|>|<|!=|!|&&|\\|\\||\\+\\+|--|\\+|-|\\*|/|&|\\||\\^|%|\\$|\\#";
	private final static String IDENTIFIERS = "[a-zA-Z][a-zA-Z0-9_]*";
	private final static String LITERALS = "[0-9]+(l|L)?\\.?([0-9]+((f|F)|(d|D)))?|\"^\"*\"";

	private Pattern pattern;

	public FileDuplicateTokens(Map<String, Object> settings) {
		super(settings);
		this.pattern = Pattern
				.compile(KEYWORDS + "|" + SEPARATORS + "|" + OPERATORS + "|" + IDENTIFIERS + "|" + LITERALS);
	}

	@Override
	public List<Issue> analyzeNode(Node node, String fileString, List<CompilationUnit> compilationUnits) {
		if (node instanceof CompilationUnit) {
			Matcher matcher = this.pattern.matcher(node.toStringWithoutComments());
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
		for (int i1 = 0, e1 = i1 + THRESHOLD; i1 < tokens.size(); i1++, e1 = i1 + THRESHOLD) {
			for (int i2 = e1, e2 = i2 + THRESHOLD; i2 < tokens.size(); i2++, e2++) {
				if (i1 <= e2 && e1 <= i2 && e1 < tokens.size() && e2 < tokens.size()) {
					List<String> sublist1 = tokens.subList(i1, e1);
					List<String> sublist2 = tokens.subList(i2, e2);
					if (sublist1.equals(sublist2)) {
						List<Issue> issues = new ArrayList<>();
						issues.add(new FileIssue(this, node, String.join(" ", tokens)));
						return issues;
					}
				}
			}
		}
		return null;
	}

	@Override
	public String getMeasureElement() {
		return ISSUE_TYPE;
	}

}
