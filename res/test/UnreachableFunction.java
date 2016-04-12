
public class OuterClass {
	private class InnerClass {
		public void innerMethod(String a) { // Referenced
			
		}
		
		public void innerMethod(String a, int b) { // Unreferenced
			
		}
		
		private void innerMethod(String a, int b, double c) { // Referenced
			
		}
		
		private void innerMethod(String a, int b, double c, float e) { // Unreferenced
			innerMethod(a, b, c);
		}
		
		public InnerClass(String a) { // Referenced
			
		}
		
		public InnerClass(String a, int b) { // Unreferenced
			this(a, b, 0.5d);
		}
		
		private InnerClass(String a, int b, double c) { // Referenced
			
		}
		
		private InnerClass(String a, int b, double c, float e) { // Unreferenced
			
		}
	}
	
	public void method(String a) { // Referenced
		InnerClass innerClass = new InnerClass("Hello");
		innerClass.innerMethod(a);
	}
	
	public void method(String a, int b) { // Unreferenced
		method("a");
	}
	
	private void method(String a, int b, double c) { // Referenced
		
	}
	
	private void method(String a, int b, double c, float e) { // Unreferenced
		method(a, 2, c);
	}
	
	public OuterClass(String a) { // Referenced
		
	}
	
	public OuterClass(String a, int b) { // Unreferenced
		this(a, b, 0.5d);
	}
	
	private OuterClass(String a, int b, double c) { // Referenced
		
	}
	
	private OuterClass(String a, int b, double c, float e) { // Unreferenced
		
	}
}
