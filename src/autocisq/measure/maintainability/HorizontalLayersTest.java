package autocisq.measure.maintainability;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;

import autocisq.IssueFinder;
import autocisq.io.IOUtils;
import autocisq.measure.MeasureTest;

public class HorizontalLayersTest extends MeasureTest {

	private Map<String, Integer> layerMap7;
	private Map<String, Integer> layerMap8;
	private Map<String, Integer> layerMap9;
	private CompilationUnit testCU;
	private String fileString;
	private Map<String, Object> settings;
	private IssueFinder issueFinder;

	@Before
	public void setUp() throws Exception {
		
		this.issueFinder = IssueFinder.getInstance();
		
		this.layerMap7 = new LinkedHashMap<>();
		this.layerMap7.put("no.uib.lca092.rtms.gui.GUI", 1);
		this.layerMap7.put("no.uib.lca092.rtms.gui.GUIUtils", 2);
		this.layerMap7.put("no.uib.lca092.rtms.gui.SettingsGUI", 3);
		this.layerMap7.put("no.uib.lca092.rtms.gui.ThemeManager", 4);
		this.layerMap7.put("no.uib.lca092.rtms.TsvToHtml", 5);
		this.layerMap7.put("no.uib.lca092.rtms.io.Parser", 6);
		this.layerMap7.put("no.uib.lca092.rtms.gui.ThemeManager2", 7);

		this.layerMap8 = new LinkedHashMap<>();
		this.layerMap8.putAll(this.layerMap7);
		this.layerMap8.put("no.uib.lca092.rtms.TsvToHtml2", 8);
		
		this.layerMap9 = new LinkedHashMap<>();
		this.layerMap9.putAll(this.layerMap8);
		this.layerMap9.put("no.uib.lca092.rtms.io.Parser2", 9);
		
		this.settings = new HashMap<>();
		
		File file = new File("res/test/layers/GUI.java");
		this.testCU = JavaParser.parse(file);
		this.fileString = IOUtils.fileToString(file);
	}
	
	@Test
	public void skipProjectWith7Layers() {
		this.settings.put("layer_map", this.layerMap7);
		this.issueFinder.putMeasure(new HorizontalLayers(this.settings));
		skipIssue(this.testCU, this.fileString);
	}

	@Test
	public void skipProjectWith8Layers() {
		this.settings.put("layer_map", this.layerMap8);
		this.issueFinder.putMeasure(new HorizontalLayers(this.settings));
		skipIssue(this.testCU, this.fileString);
	}
	
	@Test
	public void findProjectWith9Layers() {
		this.settings.put("layer_map", this.layerMap9);
		this.issueFinder.putMeasure(new HorizontalLayers(this.settings));
		findIssue(this.testCU, this.fileString);
	}
	
	@Override
	public String getIssueType() {
		return HorizontalLayers.ISSUE_TYPE;
	}

}
