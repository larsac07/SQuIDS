package autocisq.measure.maintainability;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;

import autocisq.models.Issue;
import autocisq.models.ProjectIssue;

/**
 * The CISQMM03HorizontalLayers class represents the CISQ Maintainability
 * Measure 3: # of layers (threshold 4 ≤ # Layers ≤ 8).
 *
 * @author Lars A. V. Cabrera
 *
 */
public class CISQMM03HorizontalLayers extends CISQMMLayerDependentMeasure {

	public final static int THRESHOLD = 8;
	public final static String ISSUE_TYPE = "CISQ MM03: Horizontal Layers > " + THRESHOLD;

	private List<Issue> issues;
	private boolean returned;

	public CISQMM03HorizontalLayers(Map<String, Object> settings) {
		super(settings);
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
	public List<Issue> analyzeNode(Node node, String fileString, List<CompilationUnit> compilationUnits) {
		if (this.returned) {
			return new ArrayList<>();
		} else {
			this.returned = true;
			analyzeLayers();
			return this.issues;
		}
	}

	/**
	 * Analyzes the layer map provided in the constructor. If the number of
	 * layers is higher than 8, an issue is created and added to the list.
	 */
	private void analyzeLayers() {
		Collection<Integer> layers = this.layerMap.values();
		if (layers != null) {
			Set<Integer> distinctLayers = new HashSet<Integer>(layers);
			if (distinctLayers.size() > THRESHOLD) {
				this.issues.add(new ProjectIssue(this));
			}
		}
	}

	@Override
	public String getMeasureElement() {
		return ISSUE_TYPE;
	}

}
