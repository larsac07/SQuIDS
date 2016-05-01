package autocisq.measure.maintainability;

import java.util.HashMap;
import java.util.Map;

/**
 * The CISQMM03HorizontalLayers class represents the CISQ Maintainability
 * Measure 3: # of layers (threshold 4 ≤ # Layers ≤ 8).
 *
 * @author Lars A. V. Cabrera
 *
 */
public abstract class CISQMMLayerDependentMeasure extends CISQMaintainabilityMeasure {

	public final static int THRESHOLD = 8;
	public final static String ISSUE_TYPE = "CISQ MM03: Horizontal Layers > " + THRESHOLD;

	protected Map<String, Integer> layerMap;

	@SuppressWarnings("unchecked")
	public CISQMMLayerDependentMeasure(Map<String, Object> settings) {
		super(settings);
		try {
			this.layerMap = (Map<String, Integer>) settings.get("layer_map");
			if (this.layerMap == null) {
				System.err.println(this.getClass().getSimpleName()
						+ " was provided an empty layer_map and will not work. Please provide a layer_map");
				this.layerMap = new HashMap<>();
			}
		} catch (NullPointerException | ClassCastException e) {
			this.layerMap = new HashMap<>();
			System.err.println(this.getClass().getSimpleName()
					+ " was not provided a layer_map and will not work. Please provide a layer_map");
			e.printStackTrace();
		}
	}
}
