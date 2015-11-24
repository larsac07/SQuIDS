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
import com.github.javaparser.ast.stmt.CatchClause;

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

					System.out.println(fileString);
					analyzeRegex(fileString, file, false);

					try {
						CompilationUnit compilationUnit = JavaParser.parse(EclipseFiles.iFileToFile(file));
						System.out.println(compilationUnit);
						analyzeNode(compilationUnit, file, compilationUnit.toString(), true);
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
			if (lineIndex > endLine) {
				break;
			}

			if (lineIndex < startLine) {
				startIndex += line.length();
			} else if (lineIndex == startLine) {
				startIndex += startColumn - 1;
			}

			endIndex += line.length();

			if (lineIndex == endLine) {
				endIndex += endColumn - 1;
			}

			lineIndex++;
		}

		return new int[] { startIndex, endIndex };
	}

	public static void analyzeRegex(String fileString, IFile file, boolean mark) {
		// Pattern for finding multiple occurrances of empty or
		// generic catch blocks
		Pattern pattern = Pattern.compile("catch\\s*\\([^\\)]+\\)\\s*\\{\\s*\\}"
				+ "|catch\\s*\\([^\\)]+\\)\\s*\\{\\s*\\/\\/ TODO Auto-generated catch block\\s*e\\.printStackTrace\\(\\)\\;\\s*\\}");

		Matcher matcher = pattern.matcher(fileString);
		while (matcher.find())

		{
			int errorLineNumber = findLineNumber(fileString, matcher.start());
			Logger.cisqIssue(file, errorLineNumber, matcher.start(), matcher.end(), matcher.group());
			if (mark) {
				try {
					markIssue(file, errorLineNumber, matcher.start(), matcher.end());
				} catch (CoreException e) {
					Logger.bug("Could not create marker on file " + file);
					e.printStackTrace();
				}
			}
		}

	}

	/**
	 *
	 * @param rootNode
	 * @param file
	 * @throws JavaModelException
	 */
	public static void analyzeNode(Node rootNode, IFile file, String fileAsString, boolean mark) {
		List<Issue> issues = new LinkedList<>();
		if (rootNode instanceof CatchClause) {
			CatchClause catchClause = (CatchClause) rootNode;
			issues.addAll(inspectCatchClause(catchClause, fileAsString));
		}

		// Recursive call for each child node
		for (Node node : rootNode.getChildrenNodes()) {
			analyzeNode(node, file, fileAsString, mark);
		}

		// Report issues
		for (Issue issue : issues) {
			Logger.cisqIssue(file, issue.getBeginLine(), issue.getStartIndex(), issue.getEndIndex(),
					issue.getProblemArea());
			if (mark) {
				// Mark in editor
				try {
					markIssue(file, issue.getBeginLine(), issue.getStartIndex(), issue.getEndIndex());
				} catch (CoreException e) {
					Logger.bug("Could not create marker on file " + file);
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Detects empty or generic catch blocks, and adds a marker to it
	 *
	 * @param cc
	 *            - the catch clause to inspect
	 */
	public static List<Issue> inspectCatchClause(CatchClause cc, String fileAsString) {
		// TODO detect auto generated catch blocks
		// TODO add marker to file corresponding to the issue
		List<Issue> issues = new LinkedList<>();
		if (cc.getCatchBlock().getStmts().isEmpty()) {
			int[] indexes = columnsToIndexes(fileAsString, cc.getBeginLine(), cc.getEndLine(), cc.getBeginColumn(),
					cc.getEndColumn());
			issues.add(new Issue(cc.getBeginLine(), indexes[0], indexes[1], "Empty Catch Block", cc.toString()));
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
