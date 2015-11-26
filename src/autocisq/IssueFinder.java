package autocisq;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.JavaModelException;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.CatchClause;
import com.github.javaparser.ast.stmt.TryStmt;

import autocisq.debug.Logger;
import autocisq.io.EclipseFiles;
import autocisq.io.IOUtils;
import autocisq.models.Issue;

public abstract class IssueFinder {

	public static void findIssues(IWorkspace workspace) {

		IProject[] projects = workspace.getRoot().getProjects();
		for (IProject project : projects) {

			List<IFile> files;
			try {
				files = EclipseFiles.getFiles(project, "java", null);
				for (IFile file : files) {
					file.deleteMarkers("AutoCISQ.javaqualityissue", true, IResource.DEPTH_INFINITE);

					String fileString = IOUtils.fileToString(file);

					try {
						CompilationUnit compilationUnit = JavaParser.parse(EclipseFiles.iFileToFile(file));
						List<Issue> issues = analyzeNode(compilationUnit, null, fileString);
						// Report issues
						for (Issue issue : issues) {
							Logger.cisqIssue(file, issue.getBeginLine(), issue.getStartIndex(), issue.getEndIndex(),
									issue.getProblemArea());
							// Mark in editor
							try {
								markIssue(file, issue.getBeginLine(), issue.getStartIndex(), issue.getEndIndex());
							} catch (CoreException e) {
								Logger.bug("Could not create marker on file " + file);
								e.printStackTrace();
							}
						}
					} catch (ParseException e) {
						System.err.println(e.getClass().getName() + ": Could not parse file "
								+ file.getFullPath().toFile().getAbsolutePath());
						e.printStackTrace();
					} catch (IOException e) {
						System.err.println(e.getClass().getName() + ": Could not find file "
								+ file.getFullPath().toFile().getAbsolutePath());
						e.printStackTrace();
					}

				}
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
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
		} else if (rootNode instanceof BlockStmt && rootNode.getParentNode() instanceof TryStmt) {
			BlockStmt blockStmt = (BlockStmt) rootNode;
			checkEmptyBlockStmt(blockStmt, fileAsString, issues);
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

	private static void markIssue(IFile file, int errorLineNumber, int startIndex, int endIndex) throws CoreException {
		IMarker m = file.createMarker("AutoCISQ.javaqualityissue");
		m.setAttribute(IMarker.LINE_NUMBER, errorLineNumber);
		m.setAttribute(IMarker.MESSAGE, "Empty or generic catch clause");
		m.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_NORMAL);
		m.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);
		m.setAttribute(IMarker.CHAR_START, startIndex);
		m.setAttribute(IMarker.CHAR_END, endIndex);
	}
}
