package squids;

public class NoSuchVariableException extends Exception {

	/**
	 *
	 */
	private static final long serialVersionUID = -475549252714245243L;
	private String variableName;

	public NoSuchVariableException(String message, String variableName) {
		super(message);
		
	}
	
	public String getVariableName() {
		return this.variableName;
	}
	
	@Override
	public String toString() {
		return "Variable name: " + this.variableName + super.toString();
	}
}
