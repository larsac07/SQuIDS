package autocisq.measure.maintainability;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;

import autocisq.measure.Measure;
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
	
	public HorizontalLayers() {
		this.layerMap = new HashMap<>();
		this.issues = new ArrayList<>();
		this.returned = false;
	}
	
	/**
	 * This override differs much from the other implementations. The analysis
	 * is done when analyzing the first node in the project, and returns the
	 * issue once.
	 *
	 * @param node
	 *            - not required
	 * @param fileString
	 *            - not required
	 * @param compilationUnits
	 *            - not required
	 * @param layerMap
	 *            - a map of which compilation units are assigned to which
	 *            layer.
	 * @return a List of Issue objects, containing one or no elements.
	 */
	@Override
	public List<Issue> analyzeNode(Node node, String fileString, List<CompilationUnit> compilationUnits,
			Map<String, Integer> layerMap) {
		if (this.returned) {
			return new ArrayList<>();
		} else {
			this.returned = true;
			this.layerMap = layerMap;
			analyzeLayers();
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
