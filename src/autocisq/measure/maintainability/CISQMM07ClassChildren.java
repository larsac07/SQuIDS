package autocisq.measure.maintainability;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

import autocisq.models.FileIssue;
import autocisq.models.Issue;

/**
 * The {@link CISQMM07ClassChildren} class represents the CISQ Maintainability measure
 * 7: # of classes with >= 10 children.
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
public class CISQMM07ClassChildren extends CISQMMMaintainabilityMeasure {

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
			List<String> subClasses = getSubClasses(classDeclaration, compilationUnits);
			if (subClasses.size() >= THRESHOLD) {
				List<Issue> issues = new LinkedList<>();
				String message = MESSAGE + subClasses.toString();
				issues.add(new FileIssue(this, classDeclaration.getNameExpr(), fileString, message));
				return issues;
			}
		}
		return null;
	}

	private List<String> getSubClasses(ClassOrInterfaceDeclaration superClass,
			List<CompilationUnit> projectCompilationUnits) {
		List<String> subClasses = new LinkedList<>();
		for (CompilationUnit cu : projectCompilationUnits) {
			List<ClassOrInterfaceDeclaration> namedClasses = getNamedClasses(cu);
			for (ClassOrInterfaceDeclaration subClassCandidate : namedClasses) {
				List<ClassOrInterfaceType> anonymousClasses = getAnonymousClasses(subClassCandidate, null);
				List<ClassOrInterfaceType> allClasses = new LinkedList<>();
				allClasses.addAll(anonymousClasses);
				allClasses.addAll(subClassCandidate.getExtends());
				for (ClassOrInterfaceType extendedClass : allClasses) {
					if (extendedClass.getName().equals(superClass.getName())) {
						String classID = createClassID(cu, subClassCandidate);
						subClasses.add(classID);
					}
				}
			}
		}
		return subClasses;
	}

	/**
	 * @param cu
	 * @param classOrInterfaceDeclaration
	 * @return
	 */
	private String createClassID(CompilationUnit cu, ClassOrInterfaceDeclaration classOrInterfaceDeclaration) {
		PackageDeclaration packageDeclaration = cu.getPackage();
		String packageName = "";
		if (packageDeclaration != null) {
			packageName = packageDeclaration.getName() + ".";
		}
		String classID = packageName + classOrInterfaceDeclaration.getName();
		return classID;
	}

	private List<ClassOrInterfaceDeclaration> getNamedClasses(CompilationUnit cu) {
		List<ClassOrInterfaceDeclaration> namedClasses = new LinkedList<>();
		for (TypeDeclaration typeDeclaration : cu.getTypes()) {
			if (typeDeclaration instanceof ClassOrInterfaceDeclaration) {
				ClassOrInterfaceDeclaration classOrInterfaceDeclaration = (ClassOrInterfaceDeclaration) typeDeclaration;
				namedClasses.add(classOrInterfaceDeclaration);
			}
		}
		return namedClasses;
	}

	private List<ClassOrInterfaceType> getAnonymousClasses(Node rootNode, List<ClassOrInterfaceType> anonymousClasses) {
		if (anonymousClasses == null) {
			anonymousClasses = new LinkedList<>();
		}

		if (rootNode instanceof ObjectCreationExpr) {
			ObjectCreationExpr objectCreationExpr = (ObjectCreationExpr) rootNode;
			if (objectCreationExpr.getAnonymousClassBody() != null) {
				anonymousClasses.add(objectCreationExpr.getType());
			}
		}

		for (Node childNode : rootNode.getChildrenNodes()) {
			getAnonymousClasses(childNode, anonymousClasses);
		}
		return anonymousClasses;
	}

	@Override
	public String getMeasureElement() {
		return ISSUE_TYPE;
	}

}
