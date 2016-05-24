package autocisq.measure.maintainability;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

import autocisq.models.FileIssue;
import autocisq.models.Issue;

/**
 * The {@link CISQMM06ClassInheritanceLevel} class represents the CISQ
 * Maintainability measure 6: # of classes with inheritance levels ≥ 7.
 *
 * If a class has 7 or more ancestor superclasses (e.g. Class1 extends Class2,
 * Class2 extends Class3..., Class7 extends Class8), it has an inheritance level
 * of ≥ 7.
 *
 * @author Lars A. V. Cabrera
 *
 */
public class CISQMM06ClassInheritanceLevel extends CISQMaintainabilityMeasure {

	public final static int THRESHOLD = 7;
	public final static String ISSUE_TYPE = "CISQ MM06: Class with inheritance level >= " + THRESHOLD;

	public CISQMM06ClassInheritanceLevel(Map<String, Object> settings) {
		super(settings);
	}

	@Override
	public List<Issue> analyzeNode(Node node, String fileString, List<CompilationUnit> compilationUnits) {
		if (node instanceof ClassOrInterfaceDeclaration) {
			ClassOrInterfaceDeclaration classDecl = (ClassOrInterfaceDeclaration) node;
			if (!classDecl.isInterface()) {
				int inheritanceLevel = calcInheritanceLvl(classDecl, compilationUnits, 0);
				if (inheritanceLevel >= THRESHOLD) {
					List<Issue> issues = new LinkedList<>();
					issues.add(new FileIssue(this, classDecl.getNameExpr(), fileString));
					return issues;
				}
			}
		}
		return null;
	}

	/**
	 * Returns the inheritance level of a class based on the provided list of
	 * {@link CompilationUnit} (The list defines the scope of the calculation).
	 *
	 * @param classDecl
	 *            - the class to calculate inheritance level for
	 * @param compilationUnits
	 *            - the list of compilation units to base the calculation scope
	 *            on
	 * @param count
	 *            - the count of inheritance levels. Used recusively. Should
	 *            start at 0.
	 * @return
	 */
	private int calcInheritanceLvl(ClassOrInterfaceDeclaration classDecl, List<CompilationUnit> compilationUnits,
			int count) {
		if (count < THRESHOLD) {
			for (ClassOrInterfaceType superclass : classDecl.getExtends()) {
				for (CompilationUnit cu : compilationUnits) {
					for (TypeDeclaration typeDecl : cu.getTypes()) {
						if (typeDecl instanceof ClassOrInterfaceDeclaration) {
							ClassOrInterfaceDeclaration superclassDecl = (ClassOrInterfaceDeclaration) typeDecl;
							if (superclass.getName().equals(superclassDecl.getName())) {
								return calcInheritanceLvl(superclassDecl, compilationUnits, count + 1);
							}
						}
					}
				}
			}
			return count;
		} else {
			return count;
		}
	}

	@Override
	public String getMeasureElement() {
		return ISSUE_TYPE;
	}

}
