package autocisq;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.CoreException;

import autocisq.debug.Logger;
import autocisq.io.EclipseFiles;

public abstract class IssueFinder {

	public static void findIssues(IWorkspace workspace) {
		IProject[] projects = workspace.getRoot().getProjects();
		for (IProject project : projects) {
			List<IFile> files;
			try {
				files = EclipseFiles.getFiles(project, "java", null);
				for (IFile file : files) {
					file.deleteMarkers("AutoCISQ.javaqualityissue", true, IResource.DEPTH_INFINITE);
					try {
						List<String> lines = Files.readAllLines(file.getLocation().toFile().toPath());
						String fileString = "";
						String nl = System.lineSeparator();
						for (String line : lines) {
							fileString += line + nl;
						}

						// Pattern for finding multiple occurrances of empty or
						// generic catch blocks
						Pattern pattern = Pattern.compile("catch\\s*\\([^\\)]+\\)\\s*\\{\\s*\\}|"
								+ "catch\\s*\\([^\\)]+\\)\\s*\\{\\s*\\/\\/ TODO Auto-generated catch block\\s*e\\.printStackTrace\\(\\)\\;\\s*\\}");

						Matcher matcher = pattern.matcher(fileString);
						while (matcher.find()) {
							int errorLineNumber = findLineNumber(fileString, matcher.start());
							Logger.cisqIssue(file, errorLineNumber, matcher.start(), matcher.end(), matcher.group());
							IMarker m = file.createMarker("AutoCISQ.javaqualityissue");
							m.setAttribute(IMarker.LINE_NUMBER, errorLineNumber);
							m.setAttribute(IMarker.MESSAGE, "Empty or generic catch clause");
							m.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_NORMAL);
							m.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);
							m.setAttribute(IMarker.CHAR_START, matcher.start());
							m.setAttribute(IMarker.CHAR_END, matcher.end());
						}
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			} catch (CoreException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

	/**
	 * Find the line number of a string index based on "\n" or "\r"
	 *
	 * @param string
	 *            - the multi-line string to search in
	 * @param index
	 *            - the index to find the line number of
	 * @return the line number of the string index
	 */
	private static int findLineNumber(String string, int index) {
		return string.substring(0, index).split("[\n|\r]").length;
	}
}
