package autocisq.handlers;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

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
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import autocisq.IssueFinder;
import autocisq.debug.Logger;
import autocisq.io.EclipseFiles;
import autocisq.measure.Measure;
import autocisq.models.FileIssue;
import autocisq.models.Issue;
import autocisq.models.ProjectIssue;
import autocisq.properties.Properties;
import autocisq.view.CISQReport;

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
	private final static String JOB_NAME = "Analyze project";
	private final static String NL = System.lineSeparator();
	private long parsingTime;
	private long analysisTime;
	private long markingTime;
	private long totalTime;

	/**
	 * the command has been executed, so extract extract the needed information
	 * from the application context.
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		String cmdID = event.getCommand().getId();
		if (cmdID.equals(COMMAND_SELECTED_PROJECTS) || cmdID.equals(COMMAND_ALL_PROJECTS)) {
			this.totalTime = System.currentTimeMillis();
			resetCISQReport();
			Set<IProject> projects = new LinkedHashSet<>();
			if (cmdID.equals(COMMAND_SELECTED_PROJECTS)) {
				projects = getSelectedProjects();
			} else {
				IProject[] projectArray = ResourcesPlugin.getWorkspace().getRoot().getProjects();
				for (IProject project : projectArray) {
					projects.add(project);
				}
			}
			for (IProject project : projects) {
				analyzeSourceFiles(project);
			}
		}
		return null;
	}

	/**
	 * @param projects
	 */
	public Set<IProject> getSelectedProjects() {
		Set<IProject> projects = new LinkedHashSet<>();
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (window != null) {
			IStructuredSelection selection = (IStructuredSelection) window.getSelectionService().getSelection();
			Object[] selections = selection.toArray();
			for (Object object : selections) {
				if (object instanceof IAdaptable) {
					IProject project = ((IAdaptable) object).getAdapter(IProject.class);
					projects.add(project);
				}
			}
		}
		return projects;
	}

	private void analyzeSourceFiles(IProject project) {
		Map<String, Object> settings = loadSettings(project);
		List<File> files = new LinkedList<>();
		analyzeSourceFiles(project, settings, files);
	}

	/**
	 * @param project
	 * @param settings
	 * @param files
	 * @param iFileMap
	 */
	private void analyzeSourceFiles(IProject project, Map<String, Object> settings, List<File> files) {
		Map<File, IFile> iFileMap = new HashMap<>();
		List<IFile> iFiles;
		try {
			List<String> ignoreFilters = getFilters(settings);
			iFiles = EclipseFiles.getFiles(project, JAVA, ignoreFilters);
			Logger.log("Analyzing project " + project.getName() + " with " + iFiles.size() + " java files");
			if (iFiles == null || iFiles.isEmpty()) {
				return;
			}
			prepareIFiles(iFiles, files, iFileMap);
			Job job = new Job(JOB_NAME + " " + project.getName()) {
				@Override
				protected IStatus run(IProgressMonitor monitor) {
					Map<String, Map<String, Integer>> qcj = new LinkedHashMap<>();
					Handler.this.parsingTime = System.currentTimeMillis();
					IssueFinder issueFinder = new IssueFinder(files, settings);
					Handler.this.parsingTime = System.currentTimeMillis() - Handler.this.parsingTime;
					int fileIndex = 1;
					int filesTot = files.size();
					monitor.beginTask("Analyzing files", files.size());
					try {
						Handler.this.markingTime = 0;
						for (File file : files) {
							if (monitor.isCanceled()) {
								return Status.CANCEL_STATUS;
							}
							String fileAnalysis = "Analyzing file " + fileIndex + "/" + filesTot + ": "
									+ file.getPath();
							monitor.subTask(fileAnalysis);
							Logger.log(fileAnalysis);

							analyzeSourceFile(project, iFileMap, qcj, issueFinder, file);

							monitor.worked(1);
							fileIndex++;
						}
					} finally {
						monitor.done();
					}
					logQCj(project.getName(), qcj);
					viewQCJ(project.getName(), qcj);

					Handler.this.totalTime = System.currentTimeMillis() - Handler.this.totalTime;
					logMeasureTimes(project.getName(), issueFinder);
					logAllTimes();
					return Status.OK_STATUS;
				}
			};
			job.setPriority(Job.LONG);
			job.schedule();
		} catch (CoreException e) {
			e.printStackTrace();
			return;
		}
	}

	protected void logAllTimes() {
		Logger.log("Analysis time (CISQMM total)\t" + this.analysisTime);
		Logger.log("Parsing time\t" + this.parsingTime);
		Logger.log("Marking time\t" + this.markingTime);
		Logger.log("Rest time\t" + (this.totalTime - this.parsingTime - this.analysisTime - this.markingTime));
		Logger.log("Total time\t" + this.totalTime);
	}

	/**
	 * @param iFiles
	 * @param files
	 * @param iFileMap
	 * @throws CoreException
	 */
	private void prepareIFiles(List<IFile> iFiles, List<File> files, Map<File, IFile> iFileMap) throws CoreException {
		for (IFile iFile : iFiles) {
			iFile.deleteMarkers("AutoCISQ.javaqualityissue", true, IResource.DEPTH_INFINITE);
			File file = EclipseFiles.iResourceToFile(iFile);
			files.add(file);
			iFileMap.put(file, iFile);
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
				settings.put(key.getLocalName(), linesToStringListOfSets(projectProps.get(key), ","));
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

	protected List<Set<String>> linesToStringListOfSets(String string, String delimiter) {
		List<Set<String>> layers = new LinkedList<>();
		Set<String> layer = new HashSet<>();
		Set<String> layerIDs = new HashSet<>();
		for (String line : string.split("\r?\n|\r")) {
			String[] parts = line.split(delimiter);
			if (parts.length >= 2) {
				String assignment = parts[0].trim();
				String layerID = parts[1].trim();
				if (layerIDs.add(layerID)) {
					layer = new HashSet<>();
					layers.add(layer);
				}
				layer.add(assignment);
			}
		}
		return layers;
	}

	/**
	 * @param project
	 * @param iFileMap
	 * @param qcj
	 * @param issueFinder
	 * @param file
	 */
	private void analyzeSourceFile(IProject project, Map<File, IFile> iFileMap, Map<String, Map<String, Integer>> qcj,
			IssueFinder issueFinder, File file) {
		List<Issue> issues = issueFinder.findIssues(file);
		IFile iFile = iFileMap.get(file);
		for (Issue issue : issues) {
			countQCJ(issue, qcj);
			long time = System.currentTimeMillis();
			markIssues(project, file, iFile, issue);
			this.markingTime += System.currentTimeMillis() - time;
		}
	}

	/**
	 * @param project
	 * @param file
	 * @param iFile
	 * @param issue
	 */
	private void markIssues(IProject project, File file, IFile iFile, Issue issue) {
		try {
			if (issue instanceof FileIssue) {
				FileIssue fileIssue = (FileIssue) issue;
				markIssue(iFile, fileIssue.getBeginLine(), fileIssue.getStartIndex(), fileIssue.getEndIndex(),
						fileIssue.getMeasureElement() + ": " + fileIssue.getMessage());
			} else if (issue instanceof ProjectIssue) {
				ProjectIssue projectIssue = (ProjectIssue) issue;
				markIssue(project, projectIssue.getMeasureElement());
			}
		} catch (CoreException e) {
			Logger.bug("Could not create marker on file " + file);
			e.printStackTrace();
		}
	}

	/**
	 * @param issueFinder
	 */
	private void logMeasureTimes(String projectName, IssueFinder issueFinder) {
		Logger.log(projectName + " measure times (ms): ");
		Map<Measure, Long> measureTimes = issueFinder.getMeasureTimes();
		this.analysisTime = 0;
		for (Measure measure : measureTimes.keySet()) {
			Long measureTime = measureTimes.get(measure);
			this.analysisTime += measureTime;
			Logger.log(measure.getClass().getSimpleName() + "\t" + measureTime);
		}
	}

	/**
	 * @param qcj
	 */
	private void logQCj(String projectName, Map<String, Map<String, Integer>> qcj) {
		String qcjString = createQCJ(projectName, qcj);
		Logger.log(qcjString);
	}

	private void viewQCJ(String projectName, Map<String, Map<String, Integer>> qcj) {
		String qcjString = createQCJ(projectName, qcj);
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				try {
					CISQReport report = (CISQReport) PlatformUI.getWorkbench().getActiveWorkbenchWindow()
							.getActivePage().showView(CISQReport.ID);
					report.add(qcjString);
				} catch (PartInitException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		});

	}

	private void resetCISQReport() {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				try {
					CISQReport report = (CISQReport) PlatformUI.getWorkbench().getActiveWorkbenchWindow()
							.getActivePage().showView(CISQReport.ID);
					report.setString("");
					;
				} catch (PartInitException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		});
	}

	private String createQCJ(String projectName, Map<String, Map<String, Integer>> qcj) {
		String qcjString = "";

		for (String qc : qcj.keySet()) {
			Map<String, Integer> qcMap = qcj.get(qc);
			qcjString += projectName + " " + qc + ": " + NL;
			int violationsTot = 0;
			// Sort measures by name
			Set<String> measureElements = new TreeSet<>(qcMap.keySet());
			for (String measureElement : measureElements) {
				Integer violations = qcMap.get(measureElement);
				violationsTot += violations;
				qcjString += " - " + measureElement + ": " + violations + NL;
			}
			qcjString += "QCj(" + qc + ") = " + violationsTot + NL + NL;
		}
		return qcjString;
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
