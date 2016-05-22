package autocisq.measure.maintainability;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;

import autocisq.models.Issue;

public class CISQMMLayerDependentMeasureTest {

	private CISQMMLayerDependentMeasure mockMeasure;
	private List<Set<String>> layers;

	/**
	 * Private mock subclass of the abstract class
	 * {@link CISQMMLayerDependentMeasure} to test constructor.
	 *
	 * @author Lars A. V. Cabrera
	 *
	 */
	private class CISQMMLayerDependentMeasureMock extends CISQMMLayerDependentMeasure {

		public CISQMMLayerDependentMeasureMock(Map<String, Object> settings) {
			super(settings);
		}

		@Override
		public List<Issue> analyzeNode(Node node, String fileString, List<CompilationUnit> compilationUnits) {
			return null;
		}

		@Override
		public String getMeasureElement() {
			return null;
		}

	}

	@Before
	public void setUp() throws Exception {
		this.layers = new LinkedList<>();
		Set<String> layer1 = new HashSet<>();
		layer1.add("no.uib.lca092.rtms.gui.GUI");
		layer1.add("no.uib.lca092.rtms.gui.GUIUtils");
		layer1.add("no.uib.lca092.rtms.gui.SettingsGUI");
		layer1.add("no.uib.lca092.rtms.gui.ThemeManager");
		Set<String> layer2 = new HashSet<>();
		Set<String> layer3 = new HashSet<>();
		layer3.add("no.uib.lca092.rtms.TsvToHtml");
		Set<String> layer4 = new HashSet<>();
		layer4.add("no.uib.lca092.rtms.io.Parser");
		this.layers.add(layer1);
		this.layers.add(layer2);
		this.layers.add(layer3);
		this.layers.add(layer4);
	}

	@Test
	public void testLayersNotNull() {
		this.mockMeasure = new CISQMMLayerDependentMeasureMock(null);
		assertNotNull(this.mockMeasure.layers);
	}

	@Test
	public void testLayersFormat() {
		Map<String, Object> settings = new HashMap<>();
		settings.put("layer_map", this.layers);
		this.mockMeasure = new CISQMMLayerDependentMeasureMock(settings);
		List<Set<String>> expected = this.layers;
		List<Set<String>> actual = this.mockMeasure.layers;
		assertEquals(expected, actual);
	}

	@Test
	public void testSetLayersNotNullAndNotEmptyWhenNotNull() {
		this.mockMeasure = new CISQMMLayerDependentMeasureMock(null);
		this.mockMeasure.setLayers(this.layers);
		assertNotNull(this.mockMeasure.layers);
		assertFalse(this.mockMeasure.layers.isEmpty());
	}

	@Test
	public void testSetLayersNotNullButEmptyWhenNull() {
		this.mockMeasure = new CISQMMLayerDependentMeasureMock(null);
		this.mockMeasure.setLayers(null);
		assertNotNull(this.mockMeasure.layers);
		assertTrue(this.mockMeasure.layers.isEmpty());
	}

	@Test
	public void testSetLayersEquals() {
		this.mockMeasure = new CISQMMLayerDependentMeasureMock(null);
		this.mockMeasure.setLayers(this.layers);
		List<Set<String>> expected = this.layers;
		List<Set<String>> actual = this.mockMeasure.layers;
		assertEquals(expected, actual);
	}

}
