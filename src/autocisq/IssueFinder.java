package autocisq;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.body.VariableDeclaratorId;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.CatchClause;
import com.github.javaparser.ast.stmt.TryStmt;

import autocisq.io.IOUtils;
import autocisq.measure.Measure;
import autocisq.measure.maintainability.FunctionPassing7OrMoreParameters;
import autocisq.measure.maintainability.FunctionWithFanOut10OrMore;
import autocisq.measure.maintainability.HorizontalLayers;
import autocisq.measure.maintainability.LayerSkippingCall;
import autocisq.measure.maintainability.MoreThan1000LOC;
import autocisq.measure.maintainability.VariableDeclaredPublic;
import autocisq.measure.reliability.EmptyExceptionHandlingBlock;
import autocisq.models.FileIssue;
import autocisq.models.Issue;
import autocisq.models.JavaResource;

public class IssueFinder {
	
	private static IssueFinder instance;

	private List<JavaResource> javaResources;
	private Map<String, Integer> layerMap;
	private Map<String, Measure> measures;

	public static IssueFinder getInstance() {
		if (instance == null) {
			instance = new IssueFinder();
		}
		return instance;
	}

	private IssueFinder() {
		this.javaResources = new LinkedList<>();
		this.layerMap = new LinkedHashMap<>();
		this.measures = new LinkedHashMap<>();

	}

	public Map<File, List<Issue>> findIssues(List<File> files, Map<String, Integer> layerMap) {
		this.javaResources = new LinkedList<>();
		if (files != null) {
			this.layerMap = layerMap;
		}
		Map<File, List<Issue>> fileIssuesMap = new LinkedHashMap<>();
		for (File file : files) {
			List<String> fileStringLines = IOUtils.fileToStringLines(file);
			String fileString = String.join(System.lineSeparator(), fileStringLines);

			try {
				CompilationUnit compilationUnit = JavaParser.parse(file);
				this.javaResources.add(new JavaResource(compilationUnit, file, fileString, fileStringLines));
			} catch (ParseException e) {
				System.err.println(e.getClass().getName() + ": Could not parse file " + file.getAbsolutePath());
				e.printStackTrace();
			} catch (IOException e) {
				System.err.println(e.getClass().getName() + ": Could not find file " + file.getAbsolutePath());
				e.printStackTrace();
			}
		}

		List<CompilationUnit> compilationUnits = new ArrayList<>();
		for (JavaResource javaResource : this.javaResources) {
			compilationUnits.add(javaResource.getCompilationUnit());
		}
		putMeasure(new HorizontalLayers(this.layerMap));
		putMeasure(new LayerSkippingCall(compilationUnits, layerMap));
		putMeasure(new EmptyExceptionHandlingBlock());
		putMeasure(new MoreThan1000LOC());
		putMeasure(new VariableDeclaredPublic());
		putMeasure(new FunctionPassing7OrMoreParameters());
		putMeasure(new FunctionWithFanOut10OrMore());

		for (JavaResource javaResource : this.javaResources) {
			List<Issue> issues = new LinkedList<>();

			CompilationUnit compilationUnit = javaResource.getCompilationUnit();
			String fileString = javaResource.getFileString();
			File file = javaResource.getFile();

			analyzeNode(compilationUnit, issues, fileString);

			fileIssuesMap.put(file, issues);
		}

		return fileIssuesMap;
	}

	/**
	 * Find the line number of a string index based on "\n" or "\r"
	 *
	 * @param string
	 *            - the multi-line string to search in
	 * @param index
	 *            - the index to find the line number of
	 * @return the line number of the string index
	 */
	public static int findLineNumber(String string, int index) {
		return string.substring(0, index).split("[\n|\r]").length;
	}

	public static int[] columnsToIndexes(String string, int startLine, int endLine, int startColumn, int endColumn) {
		int startIndex = 0;
		int endIndex = 0;
		int lineIndex = 1;

		String[] lines = string.split("[\n|\r]");
		for (String line : lines) {

			// Account for newline characters
			int lineLength = line.length() + 1;

			if (lineIndex > endLine) {
				break;
			}

			if (lineIndex < startLine) {
				startIndex += lineLength;
			} else if (lineIndex == startLine) {
				startIndex += startColumn - 1;
			}

			if (lineIndex == endLine) {
				endIndex += endColumn;
			} else {
				endIndex += lineLength;
			}

			lineIndex++;
		}

		return new int[] { startIndex, endIndex };
	}

	public static List<FileIssue> analyzeRegex(String fileString) {
		// Pattern for finding multiple occurrances of empty or
		// generic catch blocks
		List<FileIssue> issues = new LinkedList<>();
		Pattern pattern = Pattern.compile("catch\\s*\\([^\\)]+\\)\\s*\\{\\s*\\}");
		// + "|catch\\s*\\([^\\)]+\\)\\s*\\{\\s*\\/\\/ TODO Auto-generated catch
		// block\\s*e\\.printStackTrace\\(\\)\\;\\s*\\}");

		Matcher matcher = pattern.matcher(fileString);
		while (matcher.find()) {
			int errorLineNumber = findLineNumber(fileString, matcher.start());
			issues.add(new FileIssue(errorLineNumber, matcher.start(), matcher.end(), "Empty catch or finally block",
					matcher.group(), null));
		}
		return issues;
	}

	/**
	 *
	 * @param rootNode
	 * @param file
	 * @throws JavaModelException
	 */

	public List<Issue> analyzeNode(Node rootNode, List<Issue> issues, String fileAsString) {
		if (issues == null) {
			issues = new LinkedList<>();
		}

		for (Measure measure : this.measures.values()) {
			issues.addAll(measure.analyzeNode(rootNode, fileAsString));
		}

		// Recursive call for each child node
		for (Node node : rootNode.getChildrenNodes()) {
			analyzeNode(node, issues, fileAsString);
		}
		return issues;
	}

	public static List<Issue> checkEmptyBlockStmt(BlockStmt catchClause, String fileAsString) {
		return checkEmptyBlockStmt(catchClause, fileAsString, null);
	}

	/**
	 * Detects empty or generic catch blocks, and adds a marker to it
	 *
	 * @param blockStmt
	 *            - the catch clause to inspect
	 */
	public static List<Issue> checkEmptyBlockStmt(BlockStmt blockStmt, String fileAsString, List<Issue> issues) {
		// TODO detect auto generated catch blocks
		// TODO add marker to file corresponding to the issue
		if (issues == null) {
			issues = new LinkedList<>();
		}
		Node parent = blockStmt.getParentNode();
		if (blockStmt.getStmts().isEmpty() && parent instanceof CatchClause) {
			int[] indexes = columnsToIndexes(fileAsString, parent.getBeginLine(), parent.getEndLine(),
					parent.getBeginColumn() - 14, parent.getEndColumn() - 14);
			issues.add(new FileIssue(parent.getBeginLine(), indexes[0], indexes[1], "Empty Catch Block",
					parent.toString(), parent));
		} else if (blockStmt.getStmts().size() == 1 && parent instanceof CatchClause
				&& blockStmt.getStmts().get(0).toString().equals("e.printStackTrace();")) {
			int[] indexes = columnsToIndexes(fileAsString, parent.getBeginLine(), parent.getEndLine(),
					parent.getBeginColumn() - 14, parent.getEndColumn() - 14);
			issues.add(new FileIssue(parent.getBeginLine(), indexes[0], indexes[1], "Auto Generated Catch Block",
					parent.toString(), parent));
		} else if (blockStmt.getStmts().isEmpty() && blockStmt.getParentNode() instanceof TryStmt) {
			int[] indexes = columnsToIndexes(fileAsString, blockStmt.getBeginLine(), blockStmt.getEndLine(),
					parent.getBeginColumn() - 14, parent.getEndColumn() - 14);
			issues.add(new FileIssue(blockStmt.getBeginLine(), indexes[0], indexes[1], "Empty Finally Block",
					blockStmt.toString(), blockStmt));
		}
		return issues;
	}

	public CompilationUnit findCompilationUnit(String className) {
		for (JavaResource javaResource : this.javaResources) {
			for (TypeDeclaration typeDeclaration : javaResource.getCompilationUnit().getTypes()) {
				if (typeDeclaration.getName().equals(className)) {
					return javaResource.getCompilationUnit();
				}
			}
		}
		return null;
	}

	public CompilationUnit findMethodCompilationUnit(MethodCallExpr methodCall) {
		CompilationUnit compilationUnit = null;
		Expression scopeExpression = methodCall.getScope();
		try {
			TypeDeclaration parentType = findNodeClassOrInterfaceDeclaration(methodCall);
			search: for (FieldDeclaration fieldDeclaration : findTypeFields(parentType)) {
				List<VariableDeclarator> variables = fieldDeclaration.getVariables();
				for (VariableDeclarator variable : variables) {
					VariableDeclaratorId variableId = variable.getId();
					if (scopeExpression.toString().equals(variableId.getName())) {
						compilationUnit = findCompilationUnit(fieldDeclaration.getType().toString());
						break search;
					}
				}
			}
			if (compilationUnit == null) {
				compilationUnit = findCompilationUnit(scopeExpression.toString());
			}

		} catch (NoAncestorFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return compilationUnit;
	}

	public static boolean methodCallFromSameType(MethodCallExpr methodCall) {
		return methodCall.getScope() == null;
	}

	public static List<FieldDeclaration> findTypeFields(TypeDeclaration typeDeclaration) {
		List<FieldDeclaration> fields = new LinkedList<>();
		for (BodyDeclaration bodyDeclaration : typeDeclaration.getMembers()) {
			if (bodyDeclaration instanceof FieldDeclaration) {
				fields.add((FieldDeclaration) bodyDeclaration);
			}
		}
		return fields;
	}

	public static ClassOrInterfaceDeclaration findNodeClassOrInterfaceDeclaration(Node node)
			throws NoAncestorFoundException {
		return (ClassOrInterfaceDeclaration) findNodeAncestorOfType(node, ClassOrInterfaceDeclaration.class);
	}

	public static CompilationUnit findNodeCompilationUnit(Node node) throws NoAncestorFoundException {
		return (CompilationUnit) findNodeAncestorOfType(node, CompilationUnit.class);
	}

	/**
	 * Find a Node's ancestor of a specified class
	 *
	 * @param node
	 *            - the node of reference
	 * @param ancestorClass
	 *            - the class of the ancestor you wish to find
	 * @return the closest ancestor found of the specified class
	 * @throws NoAncestorFoundException
	 */
	public static Node findNodeAncestorOfType(Node node, Class<? extends Node> ancestorClass)
			throws NoAncestorFoundException {
		if (node == null) {
			throw new NoAncestorFoundException();
		} else if (node.getClass().equals(ancestorClass)) {
			return node;
		} else {
			return findNodeAncestorOfType(node.getParentNode(), ancestorClass);
		}
	}

	public List<JavaResource> getJavaResources() {
		return this.javaResources;
	}

	public Map<String, Integer> getLayerMap() {
		return this.layerMap;
	}

	public Map<String, Measure> getMeasures() {
		return this.measures;
	}
	
	public void putMeasure(Measure measure) {
		this.measures.put(measure.getClass().getSimpleName(), measure);
	}
	
	public Measure getMeasure(String classSimpleName) {
		return this.measures.get(classSimpleName);
	}
	
	public boolean hasMeasure(Measure measure) {
		return hasMeasure(measure.getClass().getSimpleName());
	}
	
	public boolean hasMeasure(String classSimpleName) {
		return getMeasure(classSimpleName) != null;
	}
}
