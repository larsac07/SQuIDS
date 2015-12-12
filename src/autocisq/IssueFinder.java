package autocisq;

import java.io.File;
import java.io.IOException;
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
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.body.VariableDeclaratorId;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.CatchClause;
import com.github.javaparser.ast.stmt.TryStmt;

import autocisq.debug.Logger;
import autocisq.io.IOUtils;
import autocisq.models.Issue;
import autocisq.models.JavaResource;

public class IssueFinder {

	private static IssueFinder instance;

	private List<JavaResource> javaResources;
	private LinkedHashMap<String, Integer> layerMap;

	public static IssueFinder getInstance() {
		if (instance == null) {
			instance = new IssueFinder();
		}
		return instance;
	}

	private IssueFinder() {
		this.javaResources = new LinkedList<>();

		this.layerMap = new LinkedHashMap<>();
		this.layerMap.put("no.uib.lca092.rtms.gui.GUI", 1);
		this.layerMap.put("no.uib.lca092.rtms.gui.GUIUtils", 1);
		this.layerMap.put("no.uib.lca092.rtms.gui.SettingsGUI", 1);
		this.layerMap.put("no.uib.lca092.rtms.gui.ThemeManager", 1);
		this.layerMap.put("no.uib.lca092.rtms.TsvToHtml", 2);
		this.layerMap.put("no.uib.lca092.rtms.io.Parser", 3);

		this.layerMap.put("no.uib.mof077.shortbytes.decisiontree.EntropyManualCalculator", 1);
		this.layerMap.put("no.uib.mof077.shortbytes.decisiontree.Node", 2);
		this.layerMap.put("no.uib.mof077.shortbytes.decisiontree.Person", 2);
		this.layerMap.put("no.uib.mof077.shortbytes.decisiontree.Tree", 1);
		this.layerMap.put("no.uib.mof077.shortbytes.genetics.Candidate", 2);
		this.layerMap.put("no.uib.mof077.shortbytes.genetics.GeneticAlgorithm", 2);
		this.layerMap.put("no.uib.mof077.shortbytes.genetics.Main", 1);
		this.layerMap.put("no.uib.mof077.shortbytes.kmeans.Cluster", 2);
		this.layerMap.put("no.uib.mof077.shortbytes.kmeans.KMeans", 1);
		this.layerMap.put("no.uib.mof077.shortbytes.kmeans.Vector3", 2);
		this.layerMap.put("no.uib.mof077.shortbytes.neural.Connection", 2);
		this.layerMap.put("no.uib.mof077.shortbytes.neural.Network", 2);
		this.layerMap.put("no.uib.mof077.shortbytes.neural.Node", 2);
		this.layerMap.put("no.uib.mof077.shortbytes.neural.NodeLayer", 2);
		this.layerMap.put("no.uib.mof077.shortbytes.neural.TestNeuralNetwork", 1);
		this.layerMap.put("no.uib.mof077.shortbytes.neural.XORHomework", 1);
		this.layerMap.put("no.uib.mof077.shortbytes.neural.XORNetwork", 1);
		this.layerMap.put("no.uib.mof077.shortbytes.som.HomeworkSOM", 3);
		this.layerMap.put("no.uib.mof077.shortbytes.som.KohonenSom", 3);
		this.layerMap.put("no.uib.mof077.shortbytes.som.Layer", 3);
		this.layerMap.put("no.uib.mof077.shortbytes.som.Neuron", 3);
		this.layerMap.put("no.uib.mof077.shortbytes.som.SomMain", 3);
	}

	public Map<File, List<Issue>> findIssues(List<File> files) {
		this.javaResources = new LinkedList<>();
		Map<File, List<Issue>> fileIssuesMap = new LinkedHashMap<>();
		for (File file : files) {
			List<String> fileStringLines = IOUtils.fileToStringLines(file);
			String fileString = String.join(System.lineSeparator(), fileStringLines);

			try {
				CompilationUnit compilationUnit = JavaParser.parse(file);
				Logger.log(compilationUnit.getPackage().getPackageName() + "."
						+ compilationUnit.getTypes().get(0).getName());
				this.javaResources.add(new JavaResource(compilationUnit, file, fileString, fileStringLines));
			} catch (ParseException e) {
				System.err.println(e.getClass().getName() + ": Could not parse file " + file.getAbsolutePath());
				e.printStackTrace();
			} catch (IOException e) {
				System.err.println(e.getClass().getName() + ": Could not find file " + file.getAbsolutePath());
				e.printStackTrace();
			}
		}

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

	public static List<Issue> analyzeRegex(String fileString) {
		// Pattern for finding multiple occurrances of empty or
		// generic catch blocks
		List<Issue> issues = new LinkedList<>();
		Pattern pattern = Pattern.compile("catch\\s*\\([^\\)]+\\)\\s*\\{\\s*\\}");
		// + "|catch\\s*\\([^\\)]+\\)\\s*\\{\\s*\\/\\/ TODO Auto-generated catch
		// block\\s*e\\.printStackTrace\\(\\)\\;\\s*\\}");

		Matcher matcher = pattern.matcher(fileString);
		while (matcher.find()) {
			int errorLineNumber = findLineNumber(fileString, matcher.start());
			issues.add(new Issue(errorLineNumber, matcher.start(), matcher.end(), "Empty catch or finally block",
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

		if (rootNode instanceof CatchClause) {
			CatchClause catchClause = (CatchClause) rootNode;
			checkEmptyBlockStmt(catchClause.getCatchBlock(), fileAsString, issues);
		} else if (rootNode instanceof MethodDeclaration) {
			List<Comment> comments = rootNode.getAllContainedComments();
			for (Comment comment : comments) {
				String content = comment.toString();
				if (content.endsWith(";")
						&& (content.contains("()") || content.contains("=") || content.contains("new"))) {

					int[] indexes = columnsToIndexes(fileAsString, rootNode.getBeginLine(), rootNode.getEndLine(),
							rootNode.getBeginColumn(), rootNode.getEndColumn());
					issues.add(new Issue(rootNode.getBeginLine(), indexes[0], indexes[1], "Commented Out Instruction",
							rootNode.toString(), rootNode));
				}
			}
		}

		else if (rootNode instanceof BlockStmt && rootNode.getParentNode() instanceof TryStmt) {
			BlockStmt blockStmt = (BlockStmt) rootNode;
			checkEmptyBlockStmt(blockStmt, fileAsString, issues);
		} else if (rootNode instanceof MethodCallExpr) {
			MethodCallExpr methodCall = (MethodCallExpr) rootNode;
			if (!methodCallFromSameType(methodCall)) {
				CompilationUnit methodCompilationUnit = findMethodCompilationUnit(methodCall);
				if (methodCompilationUnit != null) {
					CompilationUnit methodCallCompilationUnit = null;
					try {
						methodCallCompilationUnit = findNodeCompilationUnit(methodCall);
					} catch (NoAncestorFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					String methodClass = methodCompilationUnit.getPackage().getPackageName() + "."
							+ methodCompilationUnit.getTypes().get(0).getName();
					String methodCallClass = methodCallCompilationUnit.getPackage().getPackageName() + "."
							+ methodCallCompilationUnit.getTypes().get(0).getName();

					Integer methodLayer = this.layerMap.get(methodClass);
					Integer methodCallLayer = this.layerMap.get(methodCallClass);

					if (methodLayer != null) {
						if (Math.abs(methodLayer - methodCallLayer) > 1) {
							int[] indexes = columnsToIndexes(fileAsString, rootNode.getBeginLine(),
									rootNode.getEndLine(), rootNode.getBeginColumn(), rootNode.getEndColumn());
							issues.add(new Issue(methodCall.getBeginLine(), indexes[0], indexes[1],
									"Layer-Skipping Call", methodCall.toString(), methodCall));
						}
					}
				}
			}
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
			issues.add(new Issue(parent.getBeginLine(), indexes[0], indexes[1], "Empty Catch Block", parent.toString(),
					parent));
		} else if (blockStmt.getStmts().size() == 1 && parent instanceof CatchClause
				&& blockStmt.getStmts().get(0).toString().equals("e.printStackTrace();")) {
			int[] indexes = columnsToIndexes(fileAsString, parent.getBeginLine(), parent.getEndLine(),
					parent.getBeginColumn() - 14, parent.getEndColumn() - 14);
			issues.add(new Issue(parent.getBeginLine(), indexes[0], indexes[1], "Auto Generated Catch Block",
					parent.toString(), parent));
		} else if (blockStmt.getStmts().isEmpty() && blockStmt.getParentNode() instanceof TryStmt) {
			int[] indexes = columnsToIndexes(fileAsString, blockStmt.getBeginLine(), blockStmt.getEndLine(),
					parent.getBeginColumn() - 14, parent.getEndColumn() - 14);
			issues.add(new Issue(blockStmt.getBeginLine(), indexes[0], indexes[1], "Empty Finally Block",
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
}
