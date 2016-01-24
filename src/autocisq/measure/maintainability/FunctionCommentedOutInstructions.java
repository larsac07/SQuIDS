package autocisq.measure.maintainability;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.Statement;

import autocisq.lexical.TextOrJavaCode;
import autocisq.measure.Measure;
import autocisq.models.FileIssue;
import autocisq.models.Issue;

/**
 * The {@link FunctionCommentedOutInstructions} class represents the CISQ
 * Maintainability measure 14: # of functions with > 2% commented out
 * instructions.
 *
 * @author Lars A. V. Cabrera
 * 		
 */
public class FunctionCommentedOutInstructions extends Measure {
	
	public final static double THRESHOLD = 0.02d;
	public final static String ISSUE_TYPE = "Function with > " + (int) (THRESHOLD * 100)
			+ "% commented out instructions";
			
	public FunctionCommentedOutInstructions(Map<String, Object> settings) {
		super(settings);
	}

	@Override
	public List<Issue> analyzeNode(Node node, String fileString, List<CompilationUnit> compilationUnits) {
		if (node instanceof MethodDeclaration || node instanceof ConstructorDeclaration) {
			int instructions = countInstructions(node);
			int commOutInstructions = countCommentedOutInstructions(node);
			double result = (double) commOutInstructions / (instructions + commOutInstructions);
			if (result > THRESHOLD) {
				List<Issue> issues = new ArrayList<>();
				issues.add(new FileIssue(ISSUE_TYPE, node, fileString));
				return issues;
			}
		}
		return null;
	}
	
	public static int countCommentedOutInstructions(Node node) {
		List<Comment> comments = node.getAllContainedComments();
		String instructions = "";
		for (Comment comment : comments) {
			String uncommented = comment.getContent().trim();
			if (TextOrJavaCode.isJava(uncommented)) {
				instructions += uncommented + System.lineSeparator();
			}
		}
		try {
			Statement statement = JavaParser.parseStatement("{" + instructions + "}");
			return countNodesOfType(statement, Expression.class);
		} catch (ParseException e) {
			return 0;
		}
	}
	
	public static int countInstructions(Node node) {
		return countNodesOfType(node, Expression.class);
	}

	public static int countNodesOfType(Node rootNode, Class<? extends Node> klass) {
		int count = 0;
		if (klass.isAssignableFrom(rootNode.getClass())) {
			count++;
		}

		for (Node child : rootNode.getChildrenNodes()) {
			count += countNodesOfType(child, klass);
		}

		return count;
	}
	
	@Override
	public String getIssueType() {
		return ISSUE_TYPE;
	}

}
