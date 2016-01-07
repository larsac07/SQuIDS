package autocisq.measure;

import java.util.List;
import java.util.Map;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;

import autocisq.models.Issue;

/**
 * The Measure interface represents each CISQ Automated Quality Characteristic
 * Measures. It features the simple method {@link #analyzeNode(Node, String)}
 * which is called for every {@link com.github.javaparser.ast.Node Node} in the
 * project's AST.
 *
 * @author Lars A. V. Cabrera
 * 		
 */
public interface Measure {
	
	/**
	 * Analyzes a Node and the original file string (if required) according to a
	 * specific measure, and returns a list of issues. The list can contain 0, 1
	 * or > 1 elements. This method is called for each node in the entire AST,
	 * so analysis efficiency is important.
	 *
	 * The required layerMap is a map of which compilation units are assigned to
	 * which layer. The compilation unit is represented as package name + class
	 * name, e.g. java.util.List, and the layer is represented as a simple
	 * integer, e.g. "layer 1" = 1.
	 *
	 * @param node
	 *            - the Node to be analyzed
	 * @param fileString
	 *            - the original source file string
	 * @param compilationUnits
	 *            - a list of all the CompilationUnit objects in the current
	 *            project.
	 * @param layerMap
	 *            - a map of which compilation units are assigned to which
	 *            layer.
	 * @return a List of Issue objects, containing none, one or many element(s),
	 *         but cannot be null.
	 */
	public List<Issue> analyzeNode(Node node, String fileString, List<CompilationUnit> compilationUnits,
			Map<String, Integer> layerMap);
			
}