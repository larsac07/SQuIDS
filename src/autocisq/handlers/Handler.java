package autocisq.handlers;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

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

	public final static String COMMAND_SELECTED_PROJECTS = "AutoCISQ.commands.analyzeSelectedProjects";
	public final static String COMMAND_ALL_PROJECTS = "AutoCISQ.commands.analyzeAllProjects";
	private final static String JAVA = "java";

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
		String cmdID = event.getCommand().getId();
		if (cmdID.equals(COMMAND_SELECTED_PROJECTS)) {
			IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
			if (window != null) {
				IStructuredSelection selection = (IStructuredSelection) window.getSelectionService().getSelection();
				Object[] selections = selection.toArray();
				Set<IProject> projects = new HashSet<>();
				for (Object object : selections) {
					if (object instanceof IAdaptable) {
						IProject project = ((IAdaptable) object).getAdapter(IProject.class);
						projects.add(project);
					}
				}
				for (IProject project : projects) {
					analyzeSourceFiles(project);
				}
			}
		} else if (cmdID.equals(COMMAND_ALL_PROJECTS)) {
			IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
			for (IProject project : projects) {
				analyzeSourceFiles(project);
			}
		}
		return null;
	}

	private void analyzeSourceFiles(IProject project) {
		Map<String, Object> settings = loadSettings(project);
		List<String> ignoreFilters = getFilters(settings);
		List<IFile> iFiles;
		List<File> files = new LinkedList<>();
		Map<File, IFile> iFileMap = new HashMap<>();
		try {
			iFiles = EclipseFiles.getFiles(project, JAVA, ignoreFilters);
			Logger.log("Analyzing " + iFiles.size() + " java files");
			if (iFiles != null && !iFiles.isEmpty()) {
				for (IFile iFile : iFiles) {
					iFile.deleteMarkers("AutoCISQ.javaqualityissue", true, IResource.DEPTH_INFINITE);
					File file = EclipseFiles.iResourceToFile(iFile);
					files.add(file);
					iFileMap.put(file, iFile);
				}
				Map<File, List<Issue>> fileIssuesMap = IssueFinder.getInstance().findIssues(files, settings);
				Map<String, Map<String, Integer>> qcj = new LinkedHashMap<>();
				for (File file : fileIssuesMap.keySet()) {
					List<Issue> issues = fileIssuesMap.get(file);
					IFile iFile = iFileMap.get(file);
					// Report issues
					for (Issue issue : issues) {
						countQCJ(issue, qcj);
						try {
							if (issue instanceof FileIssue) {
								FileIssue fileIssue = (FileIssue) issue;
								markIssue(iFile, fileIssue.getBeginLine(), fileIssue.getStartIndex(),
										fileIssue.getEndIndex(), fileIssue.getMeasureElement());
							} else if (issue instanceof ProjectIssue) {
								ProjectIssue projectIssue = (ProjectIssue) issue;
								markIssue(project, projectIssue.getMeasureElement());
							}
						} catch (CoreException e) {
							Logger.bug("Could not create marker on file " + file);
							e.printStackTrace();
						}
					}
				}
				for (String qc : qcj.keySet()) {
					Map<String, Integer> qcMap = qcj.get(qc);
					Logger.log("#############\n" + qc + ": ");
					int violationsTot = 0;
					for (String measureElement : qcMap.keySet()) {
						Integer violations = qcMap.get(measureElement);
						violationsTot += violations;
						Logger.log(" - " + measureElement + ": " + violations);
					}
					Logger.log("QCj(" + qc + ") = " + violationsTot);
				}
			}
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void countQCJ(Issue issue, Map<String, Map<String, Integer>> qcj) {
		Map<String, Integer> qcMap = qcj.get(issue.getQualityCharacteristic());
		if (qcMap == null) {
			qcMap = new LinkedHashMap<>();
		}
		Integer violations = qcMap.get(issue.getMeasureElement());
		if (violations == null) {
			violations = 1;
		} else {
			violations++;
		}
		qcMap.put(issue.getMeasureElement(), violations);
		qcj.put(issue.getQualityCharacteristic(), qcMap);

	}

	@SuppressWarnings("unchecked")
	private List<String> getFilters(Map<String, Object> settings) {
		List<String> filters;
		try {
			filters = (List<String>) settings.get(Properties.KEY_IGNORE_FILTER);
			if (filters == null) {
				filters = new ArrayList<>();
			}
		} catch (NullPointerException | ClassCastException e) {
			filters = new ArrayList<>();
		}
		return filters;
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
			String qualifier = key.getQualifier();
			if (qualifier.equals(Properties.KEY_MEASURES) || qualifier.equals(Properties.KEY_DB_OR_IO_CLASSES)
					|| qualifier.equals(Properties.KEY_IGNORE_FILTER)) {
				settings.put(key.getLocalName(), linesToList(projectProps.get(key)));
			} else if (qualifier.equals(Properties.KEY_LAYER_MAP)) {
				settings.put(key.getLocalName(), linesToStringIntMap(projectProps.get(key), ","));
			}

		}
		return settings;
	}

	private List<String> linesToList(String string) {
		List<String> list = new LinkedList<>();
		for (String line : string.split("\r?\n|\r")) {
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
