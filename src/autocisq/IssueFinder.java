package autocisq;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;

import autocisq.debug.Logger;
import autocisq.io.IOUtils;
import autocisq.measure.Measure;
import autocisq.models.Issue;
import autocisq.models.JavaResource;

public class IssueFinder {

	private List<JavaResource> javaResources;
	private List<CompilationUnit> compilationUnits;
	private Map<String, Measure> measures;

	public IssueFinder() {
		this.javaResources = new LinkedList<>();
		this.measures = new LinkedHashMap<>();
	}

	public Map<File, List<Issue>> findIssues(List<File> files, Map<String, Object> settings) {
		this.javaResources = new LinkedList<>();
		Map<File, List<Issue>> fileIssuesMap = new LinkedHashMap<>();

		createCompilationUnits(files);
		int fileIndex = 0;
		int fileTot = this.javaResources.size();

		importSettings(settings);
		for (JavaResource javaResource : this.javaResources) {
			fileIndex++;
			Logger.log("Analyzing file " + fileIndex + "/" + fileTot + ": " + javaResource.getFile().getPath());

			List<Issue> issues = new LinkedList<>();

			CompilationUnit compilationUnit = javaResource.getCompilationUnit();
			String fileString = javaResource.getFileString();
			File file = javaResource.getFile();

			analyzeNode(compilationUnit, issues, fileString);

			fileIssuesMap.put(file, issues);
		}

		return fileIssuesMap;
	}

	private void importSettings(Map<String, Object> settings) {
		try {
			@SuppressWarnings("unchecked")
			List<String> measureStrings = (List<String>) settings.get("measures");
			if (measureStrings != null && !measureStrings.isEmpty()) {
				for (String measureString : measureStrings) {
					try {
						Class<?> clazz = Class.forName(measureString);
						Measure measure = (Measure) clazz.getDeclaredConstructor(Map.class).newInstance(settings);
						putMeasure(measure);
					} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
							| IllegalArgumentException | InvocationTargetException | NoSuchMethodException
							| SecurityException e) {
						System.err.println("Could not instantiate measure " + measureString);
						e.printStackTrace();
					}
				}
			} else {
				System.err.println(this.getClass().getSimpleName()
						+ " was provided an empty list of measures. Please add a list of measures.");
			}
		} catch (NullPointerException | ClassCastException e) {

			System.err.println(this.getClass().getSimpleName()
					+ " was not provided a list of measures. Please add a list of measures.");
			e.printStackTrace();
		}
	}

	private void createCompilationUnits(List<File> files) {
		for (File file : files) {
			List<String> fileStringLines = IOUtils.fileToStringLines(file);
			String fileString = String.join(System.lineSeparator(), fileStringLines);
			try {
				CompilationUnit compilationUnit = JavaParser.parse(file);
				this.javaResources.add(new JavaResource(compilationUnit, file, fileString, fileStringLines));
			} catch (ParseException e) {
				System.err.println(e.getClass().getName() + ": Could not parse file " + file.getAbsolutePath());
				e.printStackTrace();
			} catch (IOException e) {
				System.err.println(e.getClass().getName() + ": Could not find file " + file.getAbsolutePath());
				e.printStackTrace();
			}
		}
		createCompilationUnitList();
	}

	private void createCompilationUnitList() {
		List<CompilationUnit> compilationUnits = new ArrayList<>();
		for (JavaResource javaResource : this.javaResources) {
			compilationUnits.add(javaResource.getCompilationUnit());
		}
		this.compilationUnits = compilationUnits;
	}

	/**
	 *
	 * @param rootNode
	 * @param file
	 * @throws JavaModelException
	 */

	public List<Issue> analyzeNode(Node rootNode, List<Issue> issues, String fileAsString) {
		if (issues == null) {
			issues = new LinkedList<>();
		}

		for (Measure measure : this.measures.values()) {
			try {
				List<Issue> measureIssues = measure.analyzeNode(rootNode, fileAsString, this.compilationUnits);
				if (measureIssues != null) {
					issues.addAll(measureIssues);
				}
			} catch (Exception e) {
				Logger.bug("An error occurred with the " + measure.getClass().getSimpleName()
						+ " measure. See details below:");
				e.printStackTrace();
			}
		}

		// Recursive call for each child node
		for (Node node : rootNode.getChildrenNodes()) {
			analyzeNode(node, issues, fileAsString);
		}
		return issues;
	}

	public List<JavaResource> getJavaResources() {
		return this.javaResources;
	}

	public Map<String, Measure> getMeasures() {
		return this.measures;
	}

	public void putMeasure(Measure measure) {
		this.measures.put(measure.getClass().getSimpleName(), measure);
	}

	public Measure getMeasure(String classSimpleName) {
		return this.measures.get(classSimpleName);
	}

	public boolean hasMeasure(Measure measure) {
		return hasMeasure(measure.getClass().getSimpleName());
	}

	public boolean hasMeasure(String classSimpleName) {
		return getMeasure(classSimpleName) != null;
	}

	public List<CompilationUnit> getCompilationUnits() {
		return this.compilationUnits;
	}

	public void setCompilationUnits(List<CompilationUnit> compilationUnits) {
		this.compilationUnits = compilationUnits;
	}
}
