package autocisq.measure;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;

import autocisq.models.Issue;

/**
 * The {@link Measure} class represents each CISQ Automated Quality
 * Characteristic Measures. It features the simple method
 * {@link #analyzeNode(Node, String)} which is called for every
 * {@link com.github.javaparser.ast.Node Node} in the project's AST.
 *
 * @author Lars A. V. Cabrera
 *
 */
public abstract class Measure {

	private Map<String, Object> settings;

	/**
	 * Creates a new {@link MeasureTest} with settings.
	 *
	 * @param settings
	 *            - an optional map of settings which might be required for
	 *            certain measures.
	 */
	public Measure(Map<String, Object> settings) {
		if (settings == null) {
			settings = new HashMap<>();
		}
		this.settings = settings;
	}

	/**
	 * Analyzes a Node and the original file string (if required) according to a
	 * specific measure, and returns a list of issues. The list can contain 0, 1
	 * or > 1 elements. This method is called for each node in the entire AST,
	 * so analysis efficiency is important.
	 *
	 * @param node
	 *            - the Node to be analyzed
	 * @param fileString
	 *            - the original source file string
	 * @param compilationUnits
	 *            - a list of all the CompilationUnit objects in the current
	 *            project.
	 * @return a List of Issue objects, containing none, one or many element(s),
	 *         but cannot be null.
	 */
	public abstract List<Issue> analyzeNode(Node node, String fileString, List<CompilationUnit> compilationUnits);

	/**
	 * Returns the settings map which was provided when initializing this
	 * measure.
	 *
	 * @return the settings map which was provided when initializing this
	 *         measure
	 */
	public Map<String, Object> getSettings() {
		return this.settings;
	}

	/**
	 * Returns a description of the type of issue this measure can find, i.e.
	 * the quality measure implemented by the measure.
	 *
	 * @return the type of issue this measure can find
	 */
	public abstract String getMeasureElement();

	/**
	 * Returns the name of the quality characteristic which the measure belongs
	 * to.
	 */

	public abstract String getQualityCharacteristic();

}