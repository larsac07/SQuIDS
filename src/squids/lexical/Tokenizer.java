package squids.lexical;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tokenizer {
	private class TokenInfo {
		public final Pattern regex;
		public final int token;

		public TokenInfo(Pattern regex, int token) {
			super();
			this.regex = regex;
			this.token = token;
		}

		@Override
		public String toString() {
			return this.token + ": " + this.regex.toString();
		}
	}

	public class Token {
		public final int token;
		public final String sequence;

		public Token(int token, String sequence) {
			super();
			this.token = token;
			this.sequence = sequence;
		}

		@Override
		public String toString() {
			return this.token + ": " + this.sequence;
		}

	}

	private LinkedList<TokenInfo> tokenInfos;
	private LinkedList<Token> tokens;

	public Tokenizer() {
		this.tokenInfos = new LinkedList<TokenInfo>();
		this.tokens = new LinkedList<Token>();
	}

	public void add(String regex, int token) {
		this.tokenInfos.add(new TokenInfo(Pattern.compile("^(" + regex + ")"), token));
	}

	public void tokenize(String str) {
		String s = str.trim();
		this.tokens.clear();
		while (!s.equals("")) {
			boolean match = false;
			for (TokenInfo info : this.tokenInfos) {
				Matcher m = info.regex.matcher(s);
				if (m.find()) {
					match = true;
					String tok = m.group().trim();
					s = m.replaceFirst("").trim();
					this.tokens.add(new Token(info.token, tok));
					break;
				}
			}
			if (!match) {
				this.tokens.clear();
				return;
			}

		}
	}

	public synchronized String getTokensString() {
		StringBuilder sb = new StringBuilder();
		if (this.tokens != null) {
			for (Tokenizer.Token tok : this.tokens) {
				if (tok != null) {
					sb.append(tok.token);
				}
			}
		}

		return sb.toString();
	}

	public synchronized int[] getTokenIDs() {
		int[] tokens = new int[this.tokens.size()];
		if (this.tokens != null) {
			for (int i = 0; i < this.tokens.size(); i++) {
				Tokenizer.Token tok = this.tokens.get(i);
				tokens[i] = tok.token;
			}
		}

		return tokens;
	}

	public synchronized List<Token> getTokens() {
		return this.tokens;
	}
}