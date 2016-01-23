package autocisq.handlers;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
import autocisq.models.FileIssue;
import autocisq.models.Issue;
import autocisq.models.ProjectIssue;

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
		Map<String, Integer> layerMap = new LinkedHashMap<>();
		layerMap.put("no.uib.mof077.shortbytes.decisiontree.EntropyManualCalculator", 1);
		layerMap.put("no.uib.mof077.shortbytes.decisiontree.Node", 2);
		layerMap.put("no.uib.mof077.shortbytes.decisiontree.Person", 2);
		layerMap.put("no.uib.mof077.shortbytes.decisiontree.Tree", 1);
		layerMap.put("no.uib.mof077.shortbytes.genetics.Candidate", 2);
		layerMap.put("no.uib.mof077.shortbytes.genetics.GeneticAlgorithm", 2);
		layerMap.put("no.uib.mof077.shortbytes.genetics.Main", 1);
		layerMap.put("no.uib.mof077.shortbytes.kmeans.Cluster", 2);
		layerMap.put("no.uib.mof077.shortbytes.kmeans.KMeans", 1);
		layerMap.put("no.uib.mof077.shortbytes.kmeans.Vector3", 2);
		layerMap.put("no.uib.mof077.shortbytes.neural.Connection", 2);
		layerMap.put("no.uib.mof077.shortbytes.neural.Network", 2);
		layerMap.put("no.uib.mof077.shortbytes.neural.Node", 2);
		layerMap.put("no.uib.mof077.shortbytes.neural.NodeLayer", 2);
		layerMap.put("no.uib.mof077.shortbytes.neural.TestNeuralNetwork", 1);
		layerMap.put("no.uib.mof077.shortbytes.neural.XORHomework", 1);
		layerMap.put("no.uib.mof077.shortbytes.neural.XORNetwork", 1);
		layerMap.put("no.uib.mof077.shortbytes.som.HomeworkSOM", 3);
		layerMap.put("no.uib.mof077.shortbytes.som.KohonenSom", 3);
		layerMap.put("no.uib.mof077.shortbytes.som.Layer", 3);
		layerMap.put("no.uib.mof077.shortbytes.som.Neuron", 3);
		layerMap.put("no.uib.mof077.shortbytes.som.SomMain", 3);

		List<String> dbOrIoClasses = new LinkedList<>();
		dbOrIoClasses.add("java.io.File");
		dbOrIoClasses.add("java.nio.file.Files");
		dbOrIoClasses.add("java.sql.Connection");
		dbOrIoClasses.add("java.sql.DriverManager");
		dbOrIoClasses.add("java.sql.PreparedStatement");
		dbOrIoClasses.add("java.sql.Statement");
		dbOrIoClasses.add("com.github.javaparser.JavaParser");

		List<String> measures = new LinkedList<>();
		measures.add("autocisq.measure.maintainability.ClassTooManyChildren");
		measures.add("autocisq.measure.maintainability.ContinueOrBreakOutsideSwitch");
		measures.add("autocisq.measure.maintainability.FileLOC");
		measures.add("autocisq.measure.maintainability.FileDuplicateTokens");
		measures.add("autocisq.measure.maintainability.FunctionCommentedOutInstructions");
		measures.add("autocisq.measure.maintainability.FunctionFanOut");
		measures.add("autocisq.measure.maintainability.FunctionParameters");
		measures.add("autocisq.measure.maintainability.HardCodedLiteral");
		measures.add("autocisq.measure.maintainability.HorizontalLayers");
		measures.add("autocisq.measure.maintainability.LayerSkippingCall");
		measures.add("autocisq.measure.maintainability.MethodTooManyDataOrFileOperations");
		measures.add("autocisq.measure.maintainability.MethodDirectlyUsingFieldFromOtherClass");
		measures.add("autocisq.measure.maintainability.VariableDeclaredPublic");
		measures.add("autocisq.measure.maintainability.FunctionCyclomaticComplexity");
		measures.add("autocisq.measure.maintainability.FunctionUnreachable");
		measures.add("autocisq.measure.maintainability.ClassInheritanceLevel");
		measures.add("autocisq.measure.reliability.EmptyExceptionHandlingBlock");

		Map<String, Object> settings = new HashMap<>();
		settings.put("layer_map", layerMap);
		settings.put("db_or_io_classes", dbOrIoClasses);
		settings.put("measures", measures);

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
