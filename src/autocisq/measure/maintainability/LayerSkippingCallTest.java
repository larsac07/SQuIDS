package autocisq.measure.maintainability;

import static org.junit.Assert.assertTrue;

import java.io.File;
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
	private LinkedHashMap<String, Integer> layerMap;

	@Before
	public void setUp() throws Exception {
		this.layerTestFiles = new LinkedList<>();
		this.layerTestFiles.add(new File("res/test/layers/GUI.java"));
		this.layerTestFiles.add(new File("res/test/layers/GUIUtils.java"));
		this.layerTestFiles.add(new File("res/test/layers/SettingsGUI.java"));
		this.layerTestFiles.add(new File("res/test/layers/ThemeManager.java"));
		this.layerTestFiles.add(new File("res/test/layers/TsvToHtml.java"));
		this.layerTestFiles.add(new File("res/test/layers/Parser.java"));

		this.layerMap = new LinkedHashMap<>();
		this.layerMap.put("no.uib.lca092.rtms.gui.GUI", 1);
		this.layerMap.put("no.uib.lca092.rtms.gui.GUIUtils", 2);
		this.layerMap.put("no.uib.lca092.rtms.gui.SettingsGUI", 3);
		this.layerMap.put("no.uib.lca092.rtms.gui.ThemeManager", 4);
		this.layerMap.put("no.uib.lca092.rtms.TsvToHtml", 5);
		this.layerMap.put("no.uib.lca092.rtms.io.Parser", 6);
		this.layerMap.put("no.uib.lca092.rtms.gui.ThemeManager2", 7);
		this.layerMap.put("no.uib.lca092.rtms.TsvToHtml2", 8);
		this.layerMap.put("no.uib.lca092.rtms.io.Parser2", 9);
	}

	@Test
	public void findLayerSkippingCalls() {
		Map<File, List<Issue>> layerIssuesMap = IssueFinder.getInstance().findIssues(this.layerTestFiles,
				this.layerMap);

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
