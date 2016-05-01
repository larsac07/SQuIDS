package autocisq.measure.maintainability;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;

import autocisq.io.IOUtils;
import autocisq.measure.MeasureTest;

public class CISQMM01MethodSpansLayerTest extends MeasureTest {

	private CISQMM01MethodSpansLayer measure;
	private HashMap<String, Object> settings;
	private List<Set<String>> layers;
	// Dummy CU
	private CompilationUnit cu;
	// Dummy fileString
	private String fileString;

	@Before
	public void setUp() throws Exception {
		this.issueFinder.getMeasures().clear();

		this.layers = new ArrayList<>();
		this.settings = new HashMap<>();

		this.settings.put("layer_map", this.layers);

		this.measure = new CISQMM01MethodSpansLayer(this.settings);
		this.issueFinder.putMeasure(this.measure);

		// Dummy file
		File file = new File("res/test/layers/Parser.java");
		// Dummy CU
		this.cu = JavaParser.parse(file);
		// Dummy fileString
		this.fileString = IOUtils.fileToString(file);
	}

	@Test
	public void isMethodNoParams() {
		assertEquals(true, this.measure.isMethod("some.package.uri.Class1.method1()"));
	}

	@Test
	public void isMethodTwoParams() {
		assertEquals(true, this.measure.isMethod("some.package.uri.Class1.method1(String,int)"));
	}

	@Test
	public void classIsNotMethod() {
		assertEquals(false, this.measure.isMethod("some.package.uri.Class1"));
	}

	@Test
	public void skipMethodBelongingTo1Layer() {
		Set<String> layer1 = new HashSet<>();
		layer1.add("no.uib.lca092.rtms.gui.GUI.getSettings()");

		this.layers.add(layer1);
		skipIssue(this.layers);
	}

	@Test
	public void findMethodBelongingTo2Layers() {
		Set<String> layer1 = new HashSet<>();
		layer1.add("no.uib.lca092.rtms.io.Parser.writeHeading(Writer,String[],int)");
		Set<String> layer2 = new HashSet<>();
		layer2.add("no.uib.lca092.rtms.io.Parser.writeHeading(Writer,String[],int)");

		this.layers.add(layer1);
		this.layers.add(layer2);
		findIssue(this.layers);
	}

	@Test
	public void findMethodBelongingTo3Layers() {
		Set<String> layer1 = new HashSet<>();
		layer1.add("no.uib.lca092.rtms.io.Parser.skipColumn(String)");
		Set<String> layer2 = new HashSet<>();
		layer2.add("no.uib.lca092.rtms.io.Parser.skipColumn(String)");
		Set<String> layer3 = new HashSet<>();
		layer3.add("no.uib.lca092.rtms.io.Parser.skipColumn(String)");

		this.layers.add(layer1);
		this.layers.add(layer2);
		this.layers.add(layer3);
		findIssue(this.layers);
	}

	@Test
	public void skipClassBelongingTo2Layers() {
		Set<String> layer1 = new HashSet<>();
		layer1.add("no.uib.lca092.rtms.gui.GUI");
		Set<String> layer2 = new HashSet<>();
		layer2.add("no.uib.lca092.rtms.gui.GUI");

		this.layers.add(layer1);
		this.layers.add(layer2);
		skipIssue(this.layers);
	}

	private void findIssue(List<Set<String>> layers) {
		prepare(layers);
		findIssue(this.cu, this.fileString);
	}

	private void skipIssue(List<Set<String>> layers) {
		prepare(layers);
		skipIssue(this.cu, this.fileString);
	}

	private void prepare(List<Set<String>> layers) {
		this.settings = new HashMap<>();
		this.settings.put("layer_map", this.layers);
		this.measure = new CISQMM01MethodSpansLayer(this.settings);
		this.issueFinder.putMeasure(this.measure);
	}

	@Override
	public String getIssueType() {
		return CISQMM01MethodSpansLayer.ISSUE_TYPE;
	}

}
