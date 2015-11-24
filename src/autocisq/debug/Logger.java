package autocisq.debug;

import org.eclipse.core.resources.IResource;

public class Logger {

	private final static String NL = System.lineSeparator();

	public static void log(String message) {
		System.out.println(message);
	}

	public static void log(Object object) {
		log(object.toString());
	}

	public static void bug(String message) {
		System.err.println(message);
	}

	public static void bug(Object object) {
		System.err.println(object);
	}

	public static void cisqIssue(IResource file, int errorLineNumber, int startIndex, int endIndex,
			String problemArea) {
		bug("Found error in " + file.getLocation().toString() + " at line " + errorLineNumber + ":" + NL
				+ "Start index: " + startIndex + " End index: " + endIndex + NL + problemArea);
	}
}
