package autocisq;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jdt.core.JavaModelException;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.CatchClause;
import com.github.javaparser.ast.stmt.TryStmt;
import com.github.javaparser.ast.type.Type;

import autocisq.io.IOUtils;
import autocisq.models.Issue;
import autocisq.models.JavaResource;

public class IssueFinder {

	private static IssueFinder instance;

	private List<JavaResource> javaResources = new LinkedList<>();

	public static IssueFinder getInstance() {
		if (instance == null) {
			instance = new IssueFinder();
		}
		return instance;
	}

	private IssueFinder() {
		this.javaResources = new LinkedList<>();
	}

	public Map<File, List<Issue>> findIssues(List<File> files) {
		this.javaResources = new LinkedList<>();
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

	public static List<Issue> analyzeNode(Node rootNode, List<Issue> issues, String fileAsString) {
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
			Expression expr = methodCall.getScope();
			List<Expression> exprs = methodCall.getArgs();
			List<Type> types = methodCall.getTypeArgs();
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

	public static boolean methodCallFromAnotherType(MethodCallExpr methodCall) {
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

	public static TypeDeclaration findNodeTypeDeclaration(Node node) throws NoParentFoundException {
		return (TypeDeclaration) findNodeParentOfType(node, TypeDeclaration.class);
	}

	public static CompilationUnit findNodeCompilationUnit(Node node) throws NoParentFoundException {
		return (CompilationUnit) findNodeParentOfType(node, CompilationUnit.class);
	}

	public static Node findNodeParentOfType(Node node, Class<? extends Node> parentClass)
			throws NoParentFoundException {
		if (node == null) {
			throw new NoParentFoundException();
		} else if (node.getClass().equals(parentClass)) {
			return node;
		} else {
			return findNodeParentOfType(node.getParentNode(), parentClass);
		}
	}
}
