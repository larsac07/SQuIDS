package squids.measure.maintainability;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The CISQMM03HorizontalLayers class represents the CISQ Maintainability
 * Measure 3: # of layers (threshold 4 ≤ # Layers ≤ 8).
 *
 * @author Lars A. V. Cabrera
 *
 */
public abstract class CISQMMLayerDependentMeasure extends CISQMaintainabilityMeasure {

	protected List<Set<String>> layers;

	@SuppressWarnings("unchecked")
	public CISQMMLayerDependentMeasure(Map<String, Object> settings) {
		super(settings);
		try {
			List<Set<String>> layers = (List<Set<String>>) settings.get("layer_map");
			setLayers(layers);
		} catch (NullPointerException | ClassCastException e) {
			setLayers(new LinkedList<>());
			System.err.println(this.getClass().getSimpleName()
					+ " was not provided a layer_map and will not work. Please provide a layer_map");
		}
	}

	public void setLayers(List<Set<String>> layers) {
		if (layers == null) {
			System.err.println(this.getClass().getSimpleName()
					+ " was provided an empty layer_map and will not work. Please provide a layer_map");
			layers = new LinkedList<>();
		}
		this.layers = layers;
	}

}
