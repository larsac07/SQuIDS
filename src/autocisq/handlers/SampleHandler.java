package autocisq.handlers;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;

import autocisq.debug.Logger;
import autocisq.io.EclipseFiles;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 *
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class SampleHandler extends AbstractHandler {
	/**
	 * The constructor.
	 */
	public SampleHandler() {
	}

	/**
	 * the command has been executed, so extract extract the needed information
	 * from the application context.
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
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
						Pattern pattern = Pattern.compile(
								"catch\\s*\\([^\\)]+\\)\\s*\\{\\s*\\}|catch\\s*\\([^\\)]+\\)\\s*\\{\\s*\\/\\/ TODO Auto-generated catch block\\s*e\\.printStackTrace\\(\\)\\;\\s*\\}");

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
		return null;
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
	private int findLineNumber(String string, int index) {
		return string.substring(0, index).split("[\n|\r]").length;
	}
}
