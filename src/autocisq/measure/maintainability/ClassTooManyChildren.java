package autocisq.measure.maintainability;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

import autocisq.measure.Measure;
import autocisq.models.FileIssue;
import autocisq.models.Issue;

/**
 * The {@link ClassTooManyChildren} class represents the CISQ Maintainability
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
public class ClassTooManyChildren extends Measure {
	
	public final static String ISSUE_TYPE = "Class with >= 10 children";
	public final static int THRESHOLD = 10;
	
	private Map<ClassOrInterfaceType, List<ClassOrInterfaceDeclaration>> classSubClassMap;
	
	public ClassTooManyChildren(Map<String, Object> settings) {
		super(settings);
		this.classSubClassMap = new HashMap<>();
	}

	@Override
	public List<Issue> analyzeNode(Node node, String fileString, List<CompilationUnit> compilationUnits) {
		if (node instanceof ClassOrInterfaceDeclaration) {
			ClassOrInterfaceDeclaration classDeclaration = (ClassOrInterfaceDeclaration) node;
			List<ClassOrInterfaceType> superClasses = classDeclaration.getExtends();
			for (ClassOrInterfaceType superClass : superClasses) {
				List<ClassOrInterfaceDeclaration> subClasses = this.classSubClassMap.get(superClass);
				if (subClasses == null) {
					subClasses = new ArrayList<>();
				}

				if (existsInProject(superClass, compilationUnits)) {
					subClasses.add(classDeclaration);
					this.classSubClassMap.put(superClass, subClasses);
					if (subClasses.size() >= THRESHOLD) {
						List<Issue> issues = new ArrayList<>();
						issues.add(new FileIssue(ISSUE_TYPE, getTypeCompilationUnit(superClass, compilationUnits),
								fileString));
						return issues;
					}
					
				}
			}

		}
		return null;
	}
	
	public static boolean existsInProject(ClassOrInterfaceType classOrInterfaceType,
			List<CompilationUnit> projectCompilationUnits) {
		return getTypeCompilationUnit(classOrInterfaceType, projectCompilationUnits) != null;
	}

	public static CompilationUnit getTypeCompilationUnit(ClassOrInterfaceType classOrInterfaceType,
			List<CompilationUnit> projectCompilationUnits) {
		for (CompilationUnit cu : projectCompilationUnits) {
			for (TypeDeclaration typeDeclaration : cu.getTypes()) {
				if (typeDeclaration.getName().equals(classOrInterfaceType.getName())) {
					return cu;
				}
			}
		}
		return null;
	}
	
	@Override
	public String getIssueType() {
		return ISSUE_TYPE;
	}
	
}
