package autocisq.debug;

import org.eclipse.core.resources.IResource;

import autocisq.ProjectIssue;
import autocisq.models.FileIssue;

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

	public static void logIssue(IResource file, FileIssue issue) {
		bug("Found " + issue.getType() + " error in " + file.getLocation().toString() + " at line "
				+ issue.getBeginLine() + ":" + NL + "Start index: " + issue.getStartIndex() + " End index: "
				+ issue.getEndIndex() + NL + issue.getProblemArea());
	}

	public static void logIssue(IResource file, ProjectIssue issue) {
		bug("Found " + issue.getType() + " error in project");
	}
}
