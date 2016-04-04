package autocisq.measure.maintainability;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.MethodCallExpr;

import autocisq.io.IOUtils;
import autocisq.measure.MeasureTest;

public class LayerSkippingCallTest extends MeasureTest {

	private MethodCallExpr callDistance1;
	private MethodCallExpr callDistance2;
	private MethodCallExpr callDistance3;
	private String classGUIFileString;
	private String classTsvToHtmlFileString;
	private String classParserFileString;

	@Before
	public void setUp() throws Exception {

		Map<String, Integer> layerMap = new LinkedHashMap<>();
		layerMap.put("no.uib.lca092.rtms.gui.GUI", 1);
		layerMap.put("no.uib.lca092.rtms.gui.GUIUtils", 1);
		layerMap.put("no.uib.lca092.rtms.gui.SettingsGUI", 1);
		layerMap.put("no.uib.lca092.rtms.TsvToHtml", 3);
		layerMap.put("no.uib.lca092.rtms.gui.ThemeManager", 1);
		layerMap.put("no.uib.lca092.rtms.io.Parser", 4);

		List<String> measureStrings = new LinkedList<>();
		measureStrings.add(LayerSkippingCall.class.getCanonicalName());

		Map<String, Object> settings = new HashMap<>();
		settings.put("layer_map", layerMap);
		settings.put("measures", measureStrings);

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
		this.issueFinder.putMeasure(new LayerSkippingCall(settings));
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

	@Override
	public String getIssueType() {
		return LayerSkippingCall.ISSUE_TYPE;
	}

}
