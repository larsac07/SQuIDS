class Class2 implements VoidVisitor<Object> {
	
	private boolean printComments;
	
	public Class1() {
		this(true);
	}
	
	public Class1(boolean printComments) {
		this.printComments = printComments;
	}
	
	protected static class SourcePrinter {

		private final String indentation;
		
		protected SourcePrinter(final String indentation) {
			this.indentation = indentation;
		}
		
		private int level = 0;
		
		private boolean indented = false;
		
		private final StringBuilder buf = new StringBuilder();
		
		public void indent() {
			this.level++;
		}
		
		public void unindent() {
			this.level--;
		}
		
		private void makeIndent() {
			makeIndent();
		}
	}
}