package squids.lexical;

public class TextOrJavaCode {

	private Tokenizer tokenizer = null;

	private void initializeTokenizer() {
		this.tokenizer = new Tokenizer();

		// key words
		String keyString = "abstract assert boolean break byte case catch "
				+ "char class const continue default do double else enum"
				+ " extends false final finally float for goto if implements "
				+ "import instanceof int interface long native new null "
				+ "package private protected public return short static "
				+ "strictfp super switch synchronized this throw throws true "
				+ "transient try void volatile while todo";
		String keyStr = keyString.replace(" ", "|");

		this.tokenizer.add(keyStr, 1);
		this.tokenizer.add(
				";|,|\\.|\\(|\\)|\\{|\\|\\}|\\[|\\]|~|\\?|:|==|=|<=|>=|>|<|!=|!|&&|\\|\\||\\+\\+|--|\\+|-|\\*|/|&|\\||\\^|%|\\$|\\#",
				2);// separators,
		// operators,etc
		this.tokenizer.add("[0-9]+", 3); // number
		this.tokenizer.add("[a-zA-Z_][a-zA-Z0-9_]*", 4);// identifier
		this.tokenizer.add("@", 4);
	}

	public boolean isJava(String string) {
		String patternString = getPatternString(string);

		if (patternString.matches(".*444.*") || patternString.matches("4+")) {
			return false;
		} else {
			return true;
		}
	}

	private String getPatternString(String string) {
		if (this.tokenizer == null) {
			initializeTokenizer();
		}
		this.tokenizer.tokenize(string);
		return this.tokenizer.getTokensString();
	}
}