package autocisq.measure.maintainability;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;

import autocisq.JavaParserHelper;
import autocisq.NoSuchAncestorFoundException;
import autocisq.models.FileIssue;
import autocisq.models.Issue;

/**
 * The {@link ClassCoupling} class represents the CISQ Maintainability measure
 * 12: # of objects with coupling > 7.
 *
 * This measure is implemented according to Chidamber & Kemerer's Coupling
 * Between Object Classes from "A Metrics Suite for Object-Oriented Design"
 * (1994).
 *
 * It counts the number of classes which are coupled to each class, and returns
 * an issue if that count is higher than the threshold of 7.
 *
 * A class A is considered to be coupled to another class B if it uses methods
 * or instance variables of class B.
 *
 * In {@link JavaParser} terms, a {@link ClassOrInterfaceDeclaration} A is
 * coupled to a {@link ClassOrInterfaceDeclaration} B if A contains
 * {@link MethodCallExpr} or non-static and non-final {@link FieldAccessExpr}
 * from B.
 *
 * @author Lars A. V. Cabrera
 *
 */
public class ClassCoupling extends TypeDependentMeasure {

	public final static int THRESHOLD = 7;
	public final static String ISSUE_TYPE = "Class with coupling > " + THRESHOLD;

	// E.g. <package.uri.Class, 8>
	private Map<String, Integer> couplingMap;
	private Set<String> countedClasses;

	public ClassCoupling(Map<String, Object> settings) {
		super(settings);
		this.couplingMap = new HashMap<>();
		this.countedClasses = new HashSet<>();
	}

	@Override
	public List<Issue> analyzeNode(Node node, String fileString, List<CompilationUnit> compilationUnits) {
		super.analyzeNode(node, fileString, compilationUnits);
		if (node instanceof MethodCallExpr || node instanceof FieldAccessExpr) {
			String scope;
			if (node instanceof MethodCallExpr) {
				MethodCallExpr methodCall = (MethodCallExpr) node;
				scope = methodCall.getScope().toString();
			} else {
				FieldAccessExpr fieldAccess = (FieldAccessExpr) node;
				scope = fieldAccess.getScope().toString();
			}
			String type = variableToType(scope);

			if (type != null) {
				try {
					ClassOrInterfaceDeclaration containingClass = (ClassOrInterfaceDeclaration) JavaParserHelper
							.findNodeAncestorOfType(node, ClassOrInterfaceDeclaration.class);
					if (classNotCounted(containingClass.getName())) {
						ClassOrInterfaceDeclaration classToBlame = JavaParserHelper
								.findClassOrInterfaceDeclaration(type, compilationUnits);
						if (classToBlame != null) {
							int count = addCoupling(type);
							return checkCoupling(count, classToBlame, fileString);
						}
					}
				} catch (NoSuchAncestorFoundException e) {
					return null;
				}
			}
		} else if (node instanceof ClassOrInterfaceDeclaration) {
			ClassOrInterfaceDeclaration classToBlame = (ClassOrInterfaceDeclaration) node;
			int count = getCoupling(classToBlame.getName());
			return checkCoupling(count, classToBlame, fileString);
		}
		return null;
	}

	private boolean classNotCounted(String name) {
		return this.countedClasses.add(name);
	}

	private List<Issue> checkCoupling(int count, ClassOrInterfaceDeclaration classToBlame, String fileString) {
		if (count > THRESHOLD) {
			List<Issue> issues = new LinkedList<>();
			issues.add(new FileIssue(ISSUE_TYPE, classToBlame, fileString));
			return issues;
		}
		return null;
	}

	private int addCoupling(String type) {
		Integer count = this.couplingMap.get(type);
		if (count == null) {
			count = 0;
		}
		count++;
		this.couplingMap.put(type, count);

		return count;

	}

	private int getCoupling(String type) {
		Integer count = this.couplingMap.get(type);
		if (count == null) {
			return 0;
		} else {
			return count;
		}
	}

	@Override
	public String getIssueType() {
		return ISSUE_TYPE;
	}

}
