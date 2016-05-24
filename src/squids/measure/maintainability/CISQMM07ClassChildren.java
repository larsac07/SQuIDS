package squids.measure.maintainability;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

import squids.JavaParserHelper;
import squids.models.FileIssue;
import squids.models.Issue;

/**
 * The {@link CISQMM07ClassChildren} class represents the CISQ Maintainability
 * measure 7: # of classes with >= 10 children.
 *
 * Classes are considered as {@link ClassOrInterfaceDeclaration}, i.e. both
 * classes and interfaces.
 *
 * It maps each {@link ClassOrInterfaceDeclaration} that extends a class to its
 * super class, and returns an issue if the threshold of 10 is reached.
 *
 * {@link ClassOrInterfaceDeclaration ClassOrInterfaceDeclarations} which extend
 * classes which are not included in the compilationUnits parameter in
 * {@link #analyzeNode(Node, String, List, Map)} are ignored.
 *
 * @author Lars A. V. Cabrera
 *
 */
public class CISQMM07ClassChildren extends CISQMaintainabilityMeasure {

	public final static String ISSUE_TYPE = "CISQ MM07: Class with >= 10 children";
	public final static int THRESHOLD = 10;

	private final static String MESSAGE = "Found the following subclasses: ";

	public CISQMM07ClassChildren(Map<String, Object> settings) {
		super(settings);
	}

	@Override
	public List<Issue> analyzeNode(Node node, String fileString, List<CompilationUnit> compilationUnits) {
		if (node instanceof ClassOrInterfaceDeclaration) {
			ClassOrInterfaceDeclaration classDeclaration = (ClassOrInterfaceDeclaration) node;
			if (!classDeclaration.isInterface()) {
				List<String> subClasses = getSubClasses(classDeclaration, compilationUnits);
				if (subClasses.size() >= THRESHOLD) {
					List<Issue> issues = new LinkedList<>();
					String message = MESSAGE + subClasses.toString();
					issues.add(new FileIssue(this, classDeclaration.getNameExpr(), fileString, message));
					return issues;
				}
			}
		}
		return null;
	}

	public List<String> getSubClasses(ClassOrInterfaceDeclaration superClass,
			List<CompilationUnit> projectCompilationUnits) {
		List<String> subClasses = new LinkedList<>();
		for (CompilationUnit cu : projectCompilationUnits) {
			List<String> subClassesInCU = getAllExtendingClasses(cu, superClass, cu, null);
			subClasses.addAll(subClassesInCU);
		}
		return subClasses;
	}

	/**
	 * @param cu
	 * @param classOrInterfaceDeclaration
	 * @return
	 */
	private String createClassID(CompilationUnit cu, ClassOrInterfaceDeclaration classOrInterfaceDeclaration) {
		String classID = JavaParserHelper.getMemberPath(classOrInterfaceDeclaration.getParentNode())
				+ classOrInterfaceDeclaration.getName();
		return classID;
	}

	private String createClassID(CompilationUnit cu, ObjectCreationExpr objectCreationExpr) {
		String classID = JavaParserHelper.getMemberPath(objectCreationExpr) + "AnonymousInnerClass";
		return classID;
	}

	private List<String> getAllExtendingClasses(CompilationUnit cu, ClassOrInterfaceDeclaration superClass,
			Node rootNode, List<String> innerClasses) {
		if (innerClasses == null) {
			innerClasses = new LinkedList<>();
		}

		if (rootNode instanceof ObjectCreationExpr) {
			ObjectCreationExpr objectCreationExpr = (ObjectCreationExpr) rootNode;
			if (objectCreationExpr.getAnonymousClassBody() != null) {
				if (objectCreationExpr.getType().getName().equals(superClass.getName())) {
					String classID = createClassID(cu, objectCreationExpr);
					innerClasses.add(classID);
				}
			}
		} else if (rootNode instanceof ClassOrInterfaceDeclaration) {
			ClassOrInterfaceDeclaration classOrInterfaceDeclaration = (ClassOrInterfaceDeclaration) rootNode;
			for (ClassOrInterfaceType type : classOrInterfaceDeclaration.getExtends()) {
				if (type.getName().equals(superClass.getName())) {
					String classID = createClassID(cu, classOrInterfaceDeclaration);
					innerClasses.add(classID);
				}
			}
		}

		for (Node childNode : rootNode.getChildrenNodes()) {
			getAllExtendingClasses(cu, superClass, childNode, innerClasses);
		}
		return innerClasses;
	}

	@Override
	public String getMeasureElement() {
		return ISSUE_TYPE;
	}

}
