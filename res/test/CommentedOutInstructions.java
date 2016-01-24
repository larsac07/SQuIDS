
public class CommentedOutInstructions {
	private int a;
	private String b;
	
	public CommentedOutInstructions(int a, String b)  {
		this.a = a;
		this.b = b;
	}
	
	public void methodA(int x) {
		a = x;
		int j = 2;
		String s = "Hello";
		b = s = "OK then";
		
		for(int i = 0; i < 20; i++) {
			a++;
		}
		
		while(true) {
			x++;
			if(x == a) {
				System.out.println("much amaze");
				break;
			}
		}
		
		someFunction(someOtherFunction(anotherFunction(++a)));
	}
	
	public void methodB(int x) {
		a = x;
		int j = 2;
		String s = "Hello";
		b = s = "OK then";
		
		for(int i = 0; i < 20; i++) {
			a++;
		}
		
		// hello my lovelies
//		a = x;
//		int j = 2;
//		String s = "Hello";
		// Dis no code man;
//		b = s = "OK then";
//		
//		for(int i = 0; i < 20; i++) {
//			a++;
//		}
//		
//		while(true) {
//			x++;
//			if(x == a) {
//				System.out.println("much amaze");
//				break;
//			}
//		}
//		
//		someFunction(someOtherFunction(anotherFunction(++a)));
		
		while(true) {
			x++;
			if(x == a) {
				System.out.println("much amaze");
				break;
			}
		}
		
		someFunction(someOtherFunction(anotherFunction(++a)));
	}
	
	public void methodC(int x) {
		a = x;
		int j = 2;
		
		// hello my lovelies
		a = x;
		int j;
		j = 2;
//		int z;
		String s = "Hello";
		// Dis no code man;
		b = s = "OK then";
		
		for(int i = 0; i < 20; i++) {
			a++;
		}
		
		while(true) {
			x++;
			if(x == a) {
				break;
			}
		}
		
		someFunction();
		
		while(true) {
			if(x == a) {
				System.out.println("much amaze");
				break;
			}
		}
		
		someFunction(someOtherFunction(anotherFunction(++a)));
	}
	
	public void methodD(int x) {
		a = x;
		int j = 2;
		
		// hello my lovelies
		a = x;
		int j;
		j = 2;
		int z;
//		int z;
		String s = "Hello";
		// Dis no code man;
		b = s = "OK then";
		
		for(int i = 0; i < 20; i++) {
			a++;
		}
		
		while(true) {
			x++;
			if(x == a) {
				break;
			}
		}
		
		someFunction();
		
		while(true) {
			if(x == a) {
				System.out.println("much amaze");
				break;
			}
		}
		
		someFunction(someOtherFunction(anotherFunction(++a)));
	}
	
	public void methodE(int x) {
		a = x;
		int j = 2;
		
		// hello my lovelies
		a = x;
		int j;
		j = 2;
		int z;
		int f;
//		int z;
		String s = "Hello";
		// Dis no code man;
		b = s = "OK then";
		
		for(int i = 0; i < 20; i++) {
			a++;
		}
		
		while(true) {
			x++;
			if(x == a) {
				break;
			}
		}
		
		someFunction();
		
		while(true) {
			if(x == a) {
				System.out.println("much amaze");
				break;
			}
		}
		
		someFunction(someOtherFunction(anotherFunction(++a)));
	}
}
