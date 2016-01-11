
public class HardCodedLiterals {
	private int variableInteger = 5;
	private static int staticInteger = 5;
	private final static int CONSTANT_INTEGER = 5;
	private double variableDouble = 5.5d;
	private String variableString = "something";
	private int variableIntegerMinus1 = -1;
	private int variableInteger0 = 0;
	private int variableInteger1 = 1;
	private int variableInteger2 = 2;
	
	public void methodA() {
		int variableInteger = 5;
		static int staticInteger = 5;
		final static int CONSTANT_INTEGER = 5;
		double variableDouble = 5.5d;
		String variableString = "something";
		int variableIntegerMinus1 = -1;
		int variableInteger0 = 0;
		int variableInteger1 = 1;
		int variableInteger2 = 2;
		
		variableInteger = 5;
		staticInteger = 5;
		CONSTANT_INTEGER = 5;
		variableDouble = 5.5d;
		variableString = "something";
		variableIntegerMinus1 = -1;
		variableInteger0 = 0;
		variableInteger1 = 1;
		variableInteger2 = 2;
	}
	
	public void methodB() {
		variableInteger = 5;
		staticInteger = 5;
		CONSTANT_INTEGER = 5;
		variableDouble = 5.5d;
		variableString = "something";
		variableIntegerMinus1 = -1;
		variableInteger0 = 0;
		variableInteger1 = 1;
		variableInteger2 = 2;
	}
}
