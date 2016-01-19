package autocisq.measure.maintainability;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;

import autocisq.measure.Measure;
import autocisq.models.FileIssue;
import autocisq.models.Issue;

/**
 * The {@link Function100DuplicateTokens} class represents the CISQ
 * Maintainability measure 4: # files that contain 100+ consecutive duplicate
 * tokens.
 *
 * It considers tokens as Java keywords, identifiers, separators, operators and
 * literals. In other words, comments and whitespace is not considered.
 *
 * A file is considered a {@link CompilationUnit}, and contains 100+ consecutive
 * duplicate tokens iff it has >= 100 consecutive tokens that have an equal list
 * of tokens in the same or another file.
 *
 * Due to performance efficiency, the duplicated area is not provided in the
 * returned issues, and the exact amounts of duplicate tokens is not provided
 * (i.e. the measure "stops counting" at 100).
 *
 * @author Lars A. V. Cabrera
 * 		
 */
public class Function100DuplicateTokens implements Measure {
	
	public final static String ISSUE_TYPE = "File with 100+ consecutive duplicate tokens";
	public final static int THRESHOLD = 100;
	
	private final static String KEYWORDS = "abstract|assert|boolean|break|byte|case|catch|char|class|const|continue|default|"
			+ "double|else|enum|extends|false|finally|final|float|for|goto|if|implements|import|instanceof|interface|int|long|"
			+ "native|new|null|package|private|protected|public|return|short|static|strictfp|super|switch|synchronized|this|"
			+ "throws|throw|true|transient|try|void|volatile|while|todo|do";
	private final static String SEPARATORS = ";|,|\\.|\\(|\\)|\\{\\|\\}|\\[|\\]";
	private final static String OPERATORS = "=|>|<|!|~|\\?|:|==|<=|>=|!=|&&|\\|\\||\\+\\+|--|\\+|-|\\*|/|&|\\||\\^|%|\\$|\\#";
	private final static String IDENTIFIERS = "[a-zA-Z][a-zA-Z0-9_]*";
	private final static String LITERALS = "[0-9]+(l|L)?\\.?([0-9]+((f|F)|(d|D)))?|\"^\"*\"";
	
	private Pattern pattern;
	private Map<CompilationUnit, List<String>> fileTokensMap;
	private List<CompilationUnit> markedCUs;
	
	public Function100DuplicateTokens() {
		this.pattern = Pattern
				.compile(KEYWORDS + "|" + SEPARATORS + "|" + OPERATORS + "|" + IDENTIFIERS + "|" + LITERALS);
		this.fileTokensMap = new HashMap<>();
		this.markedCUs = new LinkedList<>();
	}

	@Override
	public List<Issue> analyzeNode(Node node, String fileString, List<CompilationUnit> compilationUnits,
			Map<String, Integer> layerMap) {
		if (node instanceof CompilationUnit) {
			CompilationUnit compilationUnit = (CompilationUnit) node;
			Matcher matcher = this.pattern.matcher(node.toStringWithoutComments());
			List<String> tokens = new ArrayList<>();
			while (matcher.find()) {
				tokens.add(matcher.group());
			}
			System.out.println(compilationUnit.getTypes().get(0).getName() + " " + tokens.size() + " "
					+ fileString.split("\n").length + " " + tokens);
			if (tokens.size() >= THRESHOLD) {
				this.fileTokensMap.put(compilationUnit, tokens);
				List<CompilationUnit> cusWithDuplicates = getCusWithDuplicates(compilationUnit, tokens);
				if (!cusWithDuplicates.isEmpty()) {
					List<Issue> issues = new LinkedList<>();
					issues.add(mark(compilationUnit));
					for (CompilationUnit cuWithDuplicates : cusWithDuplicates) {
						if (!isMarked(cuWithDuplicates)) {
							issues.add(mark(cuWithDuplicates));
						}
					}
					return issues;
				}
			}
		}
		return null;
	}

	private Issue mark(CompilationUnit cuWithDuplicates) {
		this.markedCUs.add(cuWithDuplicates);
		return new FileIssue(ISSUE_TYPE, cuWithDuplicates, cuWithDuplicates.toString());
	}
	
	private boolean isMarked(CompilationUnit cu) {
		for (CompilationUnit markedCU : this.markedCUs) {
			if (cu.equals(markedCU)) {
				return true;
			}
		}
		return false;
	}
	
	private List<CompilationUnit> getCusWithDuplicates(CompilationUnit compilationUnit, List<String> tokens) {
		List<CompilationUnit> cusWithDuplicates = new LinkedList<>();
		for (CompilationUnit compilationUnit2 : this.fileTokensMap.keySet()) {
			List<String> tokens2 = this.fileTokensMap.get(compilationUnit2);
			if (!compilationUnit.equals(compilationUnit2)) {
				duplicates: for (int i = 0; i + THRESHOLD < tokens.size(); i++) {
					List<String> subList = tokens.subList(i, i + THRESHOLD);
					for (int j = 0; j + THRESHOLD < tokens2.size(); j++) {
						List<String> subList2 = tokens2.subList(j, j + THRESHOLD);
						if (subList.equals(subList2)) {
							cusWithDuplicates.add(compilationUnit2);
							break duplicates;
						}
					}
				}
			}
		}
		return cusWithDuplicates;
	}
	
}
