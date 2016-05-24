package squids.measure.maintainability;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.MethodCallExpr;

import squids.io.IOUtils;
import squids.measure.MeasureTest;

public class CISQMM02LayerSkippingCallTest extends MeasureTest {

	private MethodCallExpr callDistance1;
	private MethodCallExpr callDistance2;
	private MethodCallExpr callDistance3;
	private String classGUIFileString;
	private String classTsvToHtmlFileString;
	private String classParserFileString;
	private Map<String, Object> settings;

	@Before
	public void setUp() throws Exception {

		List<Set<String>> layers = new LinkedList<>();
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
		layers.add(layer1);
		layers.add(layer2);
		layers.add(layer3);
		layers.add(layer4);

		this.settings = new HashMap<>();
		this.settings.put("layer_map", layers);

		File fileGUI = new File("res/test/layers/GUI.java");
		File fileTsvToHtml = new File("res/test/layers/TsvToHtml.java");
		File fileParser = new File("res/test/layers/Parser.java");

		this.classGUIFileString = IOUtils.fileToString(fileGUI);
		this.classTsvToHtmlFileString = IOUtils.fileToString(fileTsvToHtml);
		this.classParserFileString = IOUtils.fileToString(fileParser);

		CompilationUnit classGUI = JavaParser.parse(fileGUI);
		CompilationUnit classTsvToHtml = JavaParser.parse(fileTsvToHtml);
		CompilationUnit classParser = JavaParser.parse(fileParser);

		this.callDistance1 = (MethodCallExpr) classGUI.getTypes().get(0).getChildrenNodes().get(18).getChildrenNodes()
				.get(1).getChildrenNodes().get(0).getChildrenNodes().get(0).getChildrenNodes().get(0);
		this.callDistance2 = (MethodCallExpr) classTsvToHtml.getTypes().get(0).getChildrenNodes().get(11)
				.getChildrenNodes().get(0).getChildrenNodes().get(0).getChildrenNodes().get(0).getChildrenNodes()
				.get(1);
		this.callDistance3 = (MethodCallExpr) classParser.getTypes().get(0).getChildrenNodes().get(9).getChildrenNodes()
				.get(2).getChildrenNodes().get(1).getChildrenNodes().get(0);

		List<CompilationUnit> compilationUnits = new LinkedList<>();
		compilationUnits.add(classGUI);
		compilationUnits.add(classTsvToHtml);
		compilationUnits.add(classParser);

		this.issueFinder.setCompilationUnits(compilationUnits);
		this.issueFinder.putMeasure(new CISQMM02LayerSkippingCall(this.settings));
	}

	@Test
	public void skipCallToLayerDistance1() {
		skipIssue(this.callDistance1, this.classGUIFileString);
	}

	@Test
	public void findCallToLayerDistance2() {
		findIssue(this.callDistance2, this.classTsvToHtmlFileString);
	}

	@Test
	public void findCallToLayerDistance3() {
		findIssue(this.callDistance3, this.classParserFileString);
	}

	@Test
	public void testIsLayerSkippingCall() {
		List<Set<String>> layers = new LinkedList<>();

		Set<String> layer1 = new HashSet<>();
		layer1.add("no.uib.lca092.rtms.gui.GUI");
		Set<String> layer2 = new HashSet<>();
		Set<String> layer3 = new HashSet<>();
		layer3.add("no.uib.lca092.rtms.TsvToHtml");

		layers.add(layer1);
		layers.add(layer2);
		layers.add(layer3);
		this.settings = new HashMap<>();
		this.settings.put("layer_map", layers);

		CISQMM02LayerSkippingCall measure = new CISQMM02LayerSkippingCall(this.settings);

		assertTrue(measure.isLayerSkippingCall("no.uib.lca092.rtms.gui.GUI", "no.uib.lca092.rtms.TsvToHtml"));
	}

	@Test
	public void testIsNotLayerSkippingCall() {
		List<Set<String>> layers = new LinkedList<>();

		Set<String> layer1 = new HashSet<>();
		layer1.add("no.uib.lca092.rtms.gui.GUI");
		Set<String> layer2 = new HashSet<>();
		layer2.add("no.uib.lca092.rtms.TsvToHtml");

		layers.add(layer1);
		layers.add(layer2);
		this.settings = new HashMap<>();
		this.settings.put("layer_map", layers);

		CISQMM02LayerSkippingCall measure = new CISQMM02LayerSkippingCall(this.settings);

		assertFalse(measure.isLayerSkippingCall("no.uib.lca092.rtms.gui.GUI", "no.uib.lca092.rtms.TsvToHtml"));
	}

	@Test
	public void testDiff() {
		CISQMM02LayerSkippingCall measure = new CISQMM02LayerSkippingCall(this.settings);
		int expected = 5;
		int actual = measure.diff(3, 8);
		assertEquals(expected, actual);
	}

	@Override
	public String getIssueType() {
		return CISQMM02LayerSkippingCall.ISSUE_TYPE;
	}

}
