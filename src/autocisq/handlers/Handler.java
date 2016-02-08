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
import org.eclipse.core.runtime.QualifiedName;

import autocisq.IssueFinder;
import autocisq.debug.Logger;
import autocisq.io.EclipseFiles;
import autocisq.models.FileIssue;
import autocisq.models.Issue;
import autocisq.models.ProjectIssue;
import autocisq.properties.Properties;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 *
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class Handler extends AbstractHandler {

	/**
	 * The constructor.
	 */
	public Handler() {
	}

	/**
	 * the command has been executed, so extract extract the needed information
	 * from the application context.
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		for (IProject project : projects) {
			Map<String, Object> settings = loadSettings(project);

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

				Map<File, List<Issue>> fileIssuesMap = IssueFinder.getInstance().findIssues(files, settings);

				for (File file : fileIssuesMap.keySet()) {
					List<Issue> issues = fileIssuesMap.get(file);
					IFile iFile = iFileMap.get(file);
					// Report issues
					for (Issue issue : issues) {
						try {
							if (issue instanceof FileIssue) {
								FileIssue fileIssue = (FileIssue) issue;
								Logger.logIssue(iFile, (FileIssue) issue);
								markIssue(iFile, fileIssue.getBeginLine(), fileIssue.getStartIndex(),
										fileIssue.getEndIndex(), fileIssue.getType());
							} else if (issue instanceof ProjectIssue) {
								ProjectIssue projectIssue = (ProjectIssue) issue;
								Logger.logIssue(iFile, (ProjectIssue) issue);
								markIssue(project, projectIssue.getType());
							}
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

	private Map<String, Object> loadSettings(IProject project) {
		Map<String, Object> settings = new HashMap<>();
		Map<QualifiedName, String> projectProps = new HashMap<>();
		try {
			projectProps = project.getPersistentProperties();
		} catch (CoreException e) {
			return settings;
		}
		for (QualifiedName key : projectProps.keySet()) {

			if (key.equals(Properties.KEY_MEASURES) || key.equals(Properties.KEY_DB_OR_IO_CLASSES)) {
				settings.put(key.getLocalName(), linesToList(projectProps.get(key)));
			} else if (key.equals(Properties.KEY_LAYER_MAP)) {
				settings.put(key.getLocalName(), linesToStringIntMap(projectProps.get(key), ","));
			}

		}
		return settings;
	}

	private List<String> linesToList(String string) {
		System.out.println(string);
		List<String> list = new LinkedList<>();
		for (String line : string.split("\r?\n|\r")) {
			System.out.println(line.trim());
			list.add(line.trim());
		}
		return list;
	}

	private Map<String, Integer> linesToStringIntMap(String string, String delimiter) {
		Map<String, Integer> map = new HashMap<>();
		for (String line : string.split("\r?\n|\r")) {
			String[] parts = line.split(delimiter);
			if (parts.length >= 2) {
				map.put(parts[0].trim(), Integer.parseInt(parts[1].trim()));
			}
		}
		return null;
	}

	private static void markIssue(IFile file, int errorLineNumber, int startIndex, int endIndex, String message)
			throws CoreException {
		IMarker m = file.createMarker("AutoCISQ.javaqualityissue");
		m.setAttribute(IMarker.LINE_NUMBER, errorLineNumber);
		m.setAttribute(IMarker.MESSAGE, message);
		m.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_NORMAL);
		m.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);
		m.setAttribute(IMarker.CHAR_START, startIndex);
		m.setAttribute(IMarker.CHAR_END, endIndex);
	}

	private static void markIssue(IProject project, String message) throws CoreException {
		IMarker m = project.createMarker("AutoCISQ.javaqualityissue");
		m.setAttribute(IMarker.MESSAGE, message);
		m.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_NORMAL);
		m.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);
	}
}
