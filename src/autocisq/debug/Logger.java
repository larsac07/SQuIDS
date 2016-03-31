package autocisq.debug;

import org.eclipse.core.resources.IResource;

import autocisq.models.FileIssue;
import autocisq.models.ProjectIssue;

public class Logger {

	private final static String NL = System.lineSeparator();

	public static void log(Object object) {
		System.out.println(object);
	}

	public static void bug(Object object) {
		System.err.println(object);
	}

	public static void logIssue(IResource file, FileIssue issue) {
		log("Found " + issue.getMeasureElement() + " error in " + file.getLocation().toString() + " at line "
				+ issue.getBeginLine() + ":" + NL + "Start index: " + issue.getStartIndex() + " End index: "
				+ issue.getEndIndex() + NL + issue.getProblemArea());
	}

	public static void logIssue(IResource file, ProjectIssue issue) {
		log("Found " + issue.getMeasureElement() + " error in project");
	}
}
