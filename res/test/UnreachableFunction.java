
public class UnreachableFunction {
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
	}
	
	public void method(String a) { // Referenced
		InnerClass innerClass = new InnerClass();
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
	
}
