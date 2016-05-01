package autocisq.measure.maintainability;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;

import autocisq.models.Issue;

public abstract class CISQMMTypeDependentMeasure extends CISQMaintainabilityMeasure {

	private Map<String, String> typeImports;
	private Map<String, String> variableTypes;
	private Map<String, Integer> variableModifiers;
	private boolean importedCompilationUnits;

	public CISQMMTypeDependentMeasure(Map<String, Object> settings) {
		super(settings);
		reset();
	}

	@Override
	public List<Issue> analyzeNode(Node node, String fileString, List<CompilationUnit> compilationUnits) {
		storeVariables(node);
		if (!this.importedCompilationUnits) {
			storeVariables(compilationUnits);
			this.importedCompilationUnits = true;
		}
		return null;
	}

	/**
	 * Stores imports from {@link CompilationUnit}, parameters from
	 * {@link ConstructorDeclaration} and {@link MethodDeclaration}, and
	 * variables from {@link FieldDeclaration} and
	 * {@link VariableDeclarationExpr}.
	 *
	 * @param node
	 *            - the node to store variables from.
	 */
	protected void storeVariables(Node node) {
		if (node instanceof CompilationUnit) {
			CompilationUnit compilationUnit = (CompilationUnit) node;
			reset();
			addImports(compilationUnit.getImports());
		} else if (node instanceof ClassOrInterfaceDeclaration) {
			ClassOrInterfaceDeclaration classOrInterfaceDeclaration = (ClassOrInterfaceDeclaration) node;
			String className = classOrInterfaceDeclaration.getName();
			this.variableTypes.put(className, className);
		} else if (node instanceof ConstructorDeclaration) {
			ConstructorDeclaration constructorDeclaration = (ConstructorDeclaration) node;
			storeParameters(constructorDeclaration.getParameters());
		} else if (node instanceof MethodDeclaration) {
			MethodDeclaration methodDeclaration = (MethodDeclaration) node;
			storeParameters(methodDeclaration.getParameters());
		} else if (node instanceof FieldDeclaration) {
			FieldDeclaration fieldDeclaration = (FieldDeclaration) node;
			storeVariableModifiers(fieldDeclaration.getVariables(), fieldDeclaration.getModifiers());
			storeVariables(fieldDeclaration.getVariables(), fieldDeclaration.getType().toString());
		} else if (node instanceof VariableDeclarationExpr) {
			VariableDeclarationExpr variableDeclarationExpr = (VariableDeclarationExpr) node;
			storeVariableModifiers(variableDeclarationExpr.getVars(), variableDeclarationExpr.getModifiers());
			storeVariables(variableDeclarationExpr.getVars(), variableDeclarationExpr.getType().toString());
		}
	}

	/**
	 * Stores all types in each {@link CompilationUnit} in type - type pairs,
	 * e.g. "File" - "File".
	 *
	 * @param compilationUnits
	 */
	protected void storeVariables(List<CompilationUnit> compilationUnits) {
		for (CompilationUnit cu : compilationUnits) {
			for (TypeDeclaration type : cu.getTypes()) {
				this.variableModifiers.put(type.getName(), type.getModifiers());
				storeType(type.getName());
			}
		}
	}

	/**
	 * Stores import declarations in type - package pairs
	 *
	 * @param importDeclarations
	 */
	protected void addImports(List<ImportDeclaration> importDeclarations) {
		for (ImportDeclaration importDeclaration : importDeclarations) {
			String packageString = importDeclaration.getName().toString();
			String packageType = getPackageType(packageString);
			this.typeImports.put(packageType, packageString);
			this.variableTypes.put(packageType, packageType);
		}
	}

	/**
	 * Returns the last joint from a package string, e.g. "File" from
	 * "java.io.File".
	 *
	 * @param packageString
	 *            - e.g. "java.io.File"
	 * @return the last joint from the package string, e.g. "File" from
	 *         "java.io.File"
	 */
	protected String getPackageType(String packageString) {
		String[] packageStringParts = packageString.split("\\.");
		String packageType = packageStringParts[packageStringParts.length - 1];
		return packageType;
	}

	/**
	 * Stores the parameters in both identifier - type and type - type pairs
	 * (for static calls), e.g. "file" - "File" and "File" - "File".
	 *
	 * @param parameters
	 *            - the parameters to store
	 */
	protected void storeParameters(List<Parameter> parameters) {
		for (Parameter parameter : parameters) {
			this.variableModifiers.put(parameter.getId().getName(), parameter.getModifiers());
			this.variableTypes.put(parameter.getId().getName(), parameter.getType().toString());
			this.variableTypes.put(parameter.getType().toString(), parameter.getType().toString());
		}
	}

	/**
	 * Stores the variables in both identifier - type and type - type pairs (for
	 * static calls), e.g. "file" - "File" and "File" - "File".
	 *
	 * @param variableDeclarators
	 *            - the variables to store
	 * @param type
	 *            - the type of the variables
	 */
	protected void storeVariables(List<VariableDeclarator> variableDeclarators, String type) {
		for (VariableDeclarator variableDeclarator : variableDeclarators) {
			storeVariable(variableDeclarator, type);
		}
		storeType(type);
	}

	/**
	 * Stores variable modifiers in identifer - modifiers pairs, e.g. "file" -
	 * 010110.
	 */
	private void storeVariableModifiers(List<VariableDeclarator> vars, int modifiers) {
		for (VariableDeclarator var : vars) {
			this.variableModifiers.put(var.getId().getName(), modifiers);
		}
	}

	/**
	 * Get the stored variable modifiers. If no modifiers were stored (e.g. the
	 * variable is an imported class) or the variable cannot be found, null is
	 * returned.
	 *
	 * @param variableName
	 *            - the name of the variable
	 * @return the stored variable modifiers, or null if none were found
	 */
	protected Integer getVariableModifiers(String variableName) {
		return this.variableModifiers.get(variableName);
	}

	/**
	 * Stores the variable in identifier - type, e.g. "file" - "File".
	 *
	 * @param variableDeclarator
	 *            - the variable to store
	 * @param type
	 *            - the type of the variable
	 */
	protected void storeVariable(VariableDeclarator variableDeclarator, String type) {
		this.variableTypes.put(variableDeclarator.getId().getName(), type);
	}

	/**
	 * Stores the type in type - type pairs (for static calls), e.g. "File" -
	 * "File".
	 *
	 * @param type
	 *            - the type
	 */
	protected void storeType(String type) {
		this.variableTypes.put(type, type);
	}

	/**
	 * Returns the import string of a type, e.g. "File" returns "java.io.File".
	 *
	 * @param type
	 *            - the type to find the package for
	 * @return the import string of the type
	 */
	protected String typeToImport(String type) {
		return this.typeImports.get(type);
	}

	/**
	 * Returns the package of a type, e.g. "File" returns "java.io".
	 *
	 * @param type
	 *            - the type to find the package for
	 * @return the package string of the type
	 */
	protected String typeToPackage(String type) {
		String importString = typeToImport(type);
		if (importString != null) {
			int typeIndex = importString.lastIndexOf(".");
			String packageString = importString.substring(0, typeIndex);
			return packageString;
		} else {
			return null;
		}
	}

	/**
	 * Returns the type of a variable, e.g. "file" returns "File".
	 *
	 * @param variableName
	 *            - the name of the variable
	 * @return the type of the variable
	 */
	protected String variableToType(String variableName) {
		return this.variableTypes.get(variableName);
	}

	/**
	 * Resets import and variable maps.
	 */
	protected void reset() {
		this.typeImports = new HashMap<>();
		this.variableTypes = new HashMap<>();
		this.variableModifiers = new HashMap<>();
		this.importedCompilationUnits = false;
	}

	@Override
	public abstract String getMeasureElement();

	public Map<String, String> getTypeImports() {
		return this.typeImports;
	}

	public Map<String, String> getVariableTypes() {
		return this.variableTypes;
	}

}
