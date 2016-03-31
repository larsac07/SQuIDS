package autocisq.lexical;

public class TextOrJavaCode {

	private static Tokenizer tokenizer = null;

	public static void initializeTokenizer() {
		tokenizer = new Tokenizer();

		// key words
		String keyString = "abstract assert boolean break byte case catch "
				+ "char class const continue default do double else enum"
				+ " extends false final finally float for goto if implements "
				+ "import instanceof int interface long native new null "
				+ "package private protected public return short static "
				+ "strictfp super switch synchronized this throw throws true "
				+ "transient try void volatile while todo";
		String keyStr = keyString.replace(" ", "|");

		tokenizer.add(keyStr, 1);
		tokenizer.add("\\(|\\)|\\{|\\}|\\[|\\]|;|,|\\.|=|>|<|!|~|" + "\\?|:|==|<=|>=|!=|&&|\\|\\||\\+\\+|--|"
				+ "\\+|-|\\*|/|&|\\||\\^|%|\'|\"|\n|\r|\\$|\\#", 2);// separators,
																	// operators,etc
		tokenizer.add("[0-9]+", 3); // number
		tokenizer.add("[a-zA-Z_][a-zA-Z0-9_]*", 4);// identifier
		tokenizer.add("@", 4);
	}

	public static boolean isJava(String string) {
		String patternString = getPatternString(string);

		if (patternString.matches(".*444.*") || patternString.matches("4+")) {
			return false;
		} else {
			return true;
		}
	}

	public static String getPatternString(String string) {
		if (tokenizer == null) {
			initializeTokenizer();
		}
		tokenizer.tokenize(string);
		return tokenizer.getTokensString();
	}
}