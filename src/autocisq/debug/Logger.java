package autocisq.debug;

import org.eclipse.core.resources.IResource;

public class Logger {
	public static void log(String message) {
		System.out.println(message);
	}

	public static void log(Object object) {
		log(object.toString());
	}

	public static void cisqIssue(IResource file, int errorLineNumber, int startIndex, int endIndex,
			String problemArea) {
		log("Found error in " + file.getLocation().toString() + " at line " + errorLineNumber + ":");
		log("Start index: " + startIndex + " End index: " + endIndex);
		log(problemArea);
	}
}
