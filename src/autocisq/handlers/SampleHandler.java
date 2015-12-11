package autocisq.handlers;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;

import autocisq.IssueFinder;
import autocisq.debug.Logger;
import autocisq.io.EclipseFiles;
import autocisq.models.Issue;

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

			List<IFile> iFiles;
			List<File> files = new LinkedList<>();
			Map<File, IFile> iFileMap = new HashMap<>();
			try {
				iFiles = EclipseFiles.getFiles(project, "java", null);
				for (IFile iFile : iFiles) {
					iFile.deleteMarkers("AutoCISQ.javaqualityissue", true, IResource.DEPTH_INFINITE);
					File file = EclipseFiles.iFileToFile(iFile);
					files.add(file);
					iFileMap.put(file, iFile);
				}
				Map<File, List<Issue>> fileIssuesMap = IssueFinder.findIssues(files);

				for (File file : fileIssuesMap.keySet()) {
					List<Issue> issues = fileIssuesMap.get(file);
					IFile iFile = iFileMap.get(file);
					// Report issues
					for (Issue issue : issues) {
						try {
							Logger.cisqIssue(iFile, issue.getBeginLine(), issue.getStartIndex(), issue.getEndIndex(),
									issue.getProblemArea());
						} catch (NullPointerException e) {
							System.out.println(iFile);
							System.out.println(issue);
						}
						// Mark in editor
						try {
							markIssue(iFile, issue.getBeginLine(), issue.getStartIndex(), issue.getEndIndex());
						} catch (CoreException e) {
							Logger.bug("Could not create marker on file " + file);
							e.printStackTrace();
						}
					}
				}
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}

	private static void markIssue(IFile file, int errorLineNumber, int startIndex, int endIndex) throws CoreException {
		IMarker m = file.createMarker("AutoCISQ.javaqualityissue");
		m.setAttribute(IMarker.LINE_NUMBER, errorLineNumber);
		m.setAttribute(IMarker.MESSAGE, "Empty or generic catch clause");
		m.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_NORMAL);
		m.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);
		m.setAttribute(IMarker.CHAR_START, startIndex);
		m.setAttribute(IMarker.CHAR_END, endIndex);
	}

}
