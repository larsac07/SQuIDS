package squids.measure.maintainability;

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

import squids.io.IOUtils;
import squids.measure.MeasureTest;

public class CISQMM03HorizontalLayersTest extends MeasureTest {

	private List<Set<String>> layers7;
	private List<Set<String>> layers8;
	private List<Set<String>> layers9;
	private CompilationUnit testCU;
	private String fileString;
	private Map<String, Object> settings;

	@Before
	public void setUp() throws Exception {

		this.layers7 = new LinkedList<>();
		Set<String> layer1 = new HashSet<>();
		Set<String> layer2 = new HashSet<>();
		Set<String> layer3 = new HashSet<>();
		Set<String> layer4 = new HashSet<>();
		Set<String> layer5 = new HashSet<>();
		Set<String> layer6 = new HashSet<>();
		Set<String> layer7 = new HashSet<>();
		layer1.add("no.uib.lca092.rtms.gui.GUI");
		layer2.add("no.uib.lca092.rtms.gui.GUIUtils");
		layer3.add("no.uib.lca092.rtms.gui.SettingsGUI");
		layer4.add("no.uib.lca092.rtms.gui.ThemeManager");
		layer5.add("no.uib.lca092.rtms.TsvToHtml");
		layer6.add("no.uib.lca092.rtms.io.Parser");
		layer7.add("no.uib.lca092.rtms.gui.ThemeManager2");
		this.layers7.add(layer1);
		this.layers7.add(layer2);
		this.layers7.add(layer3);
		this.layers7.add(layer4);
		this.layers7.add(layer5);
		this.layers7.add(layer6);
		this.layers7.add(layer7);

		this.layers8 = new LinkedList<>();
		Set<String> layer8 = new HashSet<>();
		layer8.add("no.uib.lca092.rtms.TsvToHtml2");
		this.layers8.addAll(this.layers7);
		this.layers8.add(layer8);

		this.layers9 = new LinkedList<>();
		Set<String> layer9 = new HashSet<>();
		layer9.add("no.uib.lca092.rtms.io.Parser2");
		this.layers9.addAll(this.layers8);
		this.layers9.add(layer9);

		this.settings = new HashMap<>();

		File file = new File("res/test/layers/GUI.java");
		this.testCU = JavaParser.parse(file);
		this.fileString = IOUtils.fileToString(file);
	}

	@Test
	public void skipProjectWith7Layers() {
		this.settings.put("layer_map", this.layers7);
		this.issueFinder.putMeasure(new CISQMM03HorizontalLayers(this.settings));
		skipIssue(this.testCU, this.fileString);
	}

	@Test
	public void skipProjectWith8Layers() {
		this.settings.put("layer_map", this.layers8);
		this.issueFinder.putMeasure(new CISQMM03HorizontalLayers(this.settings));
		skipIssue(this.testCU, this.fileString);
	}

	@Test
	public void findProjectWith9Layers() {
		this.settings.put("layer_map", this.layers9);
		this.issueFinder.putMeasure(new CISQMM03HorizontalLayers(this.settings));
		findIssue(this.testCU, this.fileString);
	}

	@Override
	public String getIssueType() {
		return CISQMM03HorizontalLayers.ISSUE_TYPE;
	}

}
