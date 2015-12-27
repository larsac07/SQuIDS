package autocisq.measure;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.github.javaparser.ast.Node;

import autocisq.models.Issue;
import autocisq.models.ProjectIssue;

/**
 * The HorizontalLayers class represents the CISQ Maintainability Measure 3: #
 * of layers (threshold 4 ≤ # Layers ≤ 8).
 *
 * @author Lars A. V. Cabrera
 *
 */
public class HorizontalLayers implements Measure {

	private Map<String, Integer> layerMap;
	private List<Issue> issues;
	private boolean returned;

	/**
	 * Constructs a new HorizontalLayers measure with a map of layers and
	 * analyzes them.
	 *
	 * If the map passed through the parameter is null, a new map is created.
	 *
	 * The required layerMap is a map of which compilation units are assigned to
	 * which layer. The compilation unit is represented as package name + class
	 * name, e.g. java.util.List, and the layer is represented as a simple
	 * integer, e.g. "layer 1" = 1.
	 *
	 * @param layerMap
	 *            - a map of which compilation units are assigned to which
	 *            layer.
	 */
	public HorizontalLayers(Map<String, Integer> layerMap) {
		if (layerMap == null) {
			layerMap = new HashMap<>();
		}
		this.layerMap = layerMap;
		this.issues = new LinkedList<>();
		this.returned = false;
		analyzeLayers();
	}

	/**
	 * This override differs much from the other implementations. The analysis
	 * is done when creating an object of this class, and any possible issue
	 * will only returned once, when analyzing the first node in the project.
	 *
	 * @param node
	 *            - the Node to be analyzed
	 * @param fileString
	 *            - the original source file string
	 * @return a List of Issue objects, containing none, one or many element(s),
	 *         but cannot be null.
	 */
	@Override
	public List<Issue> analyzeNode(Node node, String fileString) {
		if (this.returned) {
			return new LinkedList<>();
		} else {
			this.returned = true;
			return this.issues;
		}
	}

	/**
	 * Analyzes the layer map provided in the constructor. If the number of
	 * layers is higher than 8, an issue is created and added to the list.
	 */
	private void analyzeLayers() {
		Set<Integer> distinctLayers = new HashSet<Integer>(this.layerMap.values());
		if (distinctLayers.size() > 8) {
			this.issues.add(new ProjectIssue("Too Many Horizontal Layers"));
		}
	}

}
