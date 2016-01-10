package autocisq.measure.maintainability;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
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
public class FunctionCommentedOutInstructions implements Measure {

	public final static double threshold = 0.02d;

	@Override
	public List<Issue> analyzeNode(Node node, String fileString, List<CompilationUnit> compilationUnits,
			Map<String, Integer> layerMap) {
		if (node instanceof MethodDeclaration) {
			MethodDeclaration methodDeclaration = (MethodDeclaration) node;
			int instructions = FunctionCommentedOutInstructions.countInstructions(methodDeclaration);
			int commOutInstructions = FunctionCommentedOutInstructions.countCommentedOutInstructions(methodDeclaration);
			double result = (double) commOutInstructions / (instructions + commOutInstructions);
			if (result > threshold) {
				List<Issue> issues = new ArrayList<>();
				issues.add(new FileIssue("Function with > 2% commented out instructions", node, fileString));
				return issues;
			}
		}
		return null;
	}
	
	public static int countCommentedOutInstructions(MethodDeclaration methodDeclaration) {
		List<Comment> comments = methodDeclaration.getAllContainedComments();
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
	
	public static int countInstructions(MethodDeclaration methodDeclaration) {
		return countNodesOfType(methodDeclaration, Expression.class);
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

}
