package autocisq.measure.maintainability;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import autocisq.IssueFinder;
import autocisq.models.FileIssue;
import autocisq.models.Issue;

public class LayerSkippingCallTest {
	
	private List<File> layerTestFiles;
	private Map<String, Object> settings;
	
	@Before
	public void setUp() throws Exception {
		this.layerTestFiles = new LinkedList<>();
		this.layerTestFiles.add(new File("res/test/layers/GUI.java"));
		this.layerTestFiles.add(new File("res/test/layers/GUIUtils.java"));
		this.layerTestFiles.add(new File("res/test/layers/SettingsGUI.java"));
		this.layerTestFiles.add(new File("res/test/layers/ThemeManager.java"));
		this.layerTestFiles.add(new File("res/test/layers/TsvToHtml.java"));
		this.layerTestFiles.add(new File("res/test/layers/Parser.java"));
		
		Map<String, Integer> layerMap = new LinkedHashMap<>();
		layerMap.put("no.uib.lca092.rtms.gui.GUI", 1);
		layerMap.put("no.uib.lca092.rtms.gui.GUIUtils", 2);
		layerMap.put("no.uib.lca092.rtms.gui.SettingsGUI", 3);
		layerMap.put("no.uib.lca092.rtms.gui.ThemeManager", 4);
		layerMap.put("no.uib.lca092.rtms.TsvToHtml", 5);
		layerMap.put("no.uib.lca092.rtms.io.Parser", 6);
		layerMap.put("no.uib.lca092.rtms.gui.ThemeManager2", 7);
		layerMap.put("no.uib.lca092.rtms.TsvToHtml2", 8);
		layerMap.put("no.uib.lca092.rtms.io.Parser2", 9);
		
		List<String> measureStrings = new LinkedList<>();
		measureStrings.add(LayerSkippingCall.class.getCanonicalName());
		
		this.settings = new HashMap<>();
		this.settings.put("layer_map", layerMap);
		this.settings.put("measures", measureStrings);
	}
	
	@Test
	public void findLayerSkippingCalls() {
		Map<File, List<Issue>> layerIssuesMap = IssueFinder.getInstance().findIssues(this.layerTestFiles,
				this.settings);
				
		boolean found = false;
		search: for (List<Issue> fileIssues : layerIssuesMap.values()) {
			for (Issue issue : fileIssues) {
				if (issue.getType().equals("Layer-Skipping Call") && issue instanceof FileIssue) {
					found = true;
					break search;
				}
			}
		}
		assertTrue(found);
	}
	
}
