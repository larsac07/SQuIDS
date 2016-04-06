package autocisq;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
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

public class IssueFinder {

	private List<CompilationUnit> compilationUnits;
	private Map<String, Measure> measures;
	private Map<Measure, Long> measureTimes;

	public IssueFinder() {
		this.measures = new LinkedHashMap<>();
		this.measureTimes = new LinkedHashMap<>();
		this.compilationUnits = new LinkedList<>();
	}

	public IssueFinder(List<File> files, Map<String, Object> settings) {
		this();
		createCompilationUnits(files);
		importSettings(settings);
	}

	public List<Issue> findIssues(File file) {
		CompilationUnit compilationUnit = createCompilationUnit(file);
		String fileString = IOUtils.fileToString(file);

		List<Issue> issues = analyzeNode(compilationUnit, null, fileString);

		return issues;

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
			this.compilationUnits.add(createCompilationUnit(file));
		}
	}

	private CompilationUnit createCompilationUnit(File file) {
		try {
			return JavaParser.parse(file);
		} catch (ParseException e) {
			System.err.println(e.getClass().getName() + ": Could not parse file " + file.getAbsolutePath());
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			System.err.println(e.getClass().getName() + ": Could not find file " + file.getAbsolutePath());
			e.printStackTrace();
			return null;
		}
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
			long startTime = System.currentTimeMillis();
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
			long stopTime = System.currentTimeMillis();
			long elapsedTime = stopTime - startTime;
			addTimeToMeasure(measure, elapsedTime);
		}

		// Recursive call for each child node
		for (Node node : rootNode.getChildrenNodes()) {
			analyzeNode(node, issues, fileAsString);
		}
		return issues;
	}

	private void addTimeToMeasure(Measure measure, long elapsedTime) {
		Long totalTime = this.measureTimes.get(measure);
		if (totalTime == null) {
			totalTime = 0l;
		}
		totalTime += elapsedTime;
		this.measureTimes.put(measure, totalTime);
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

	public Map<Measure, Long> getMeasureTimes() {
		return this.measureTimes;
	}
}
