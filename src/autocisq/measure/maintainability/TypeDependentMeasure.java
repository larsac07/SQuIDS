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
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;

import autocisq.measure.Measure;
import autocisq.models.Issue;

public abstract class TypeDependentMeasure extends Measure {

	private Map<String, String> typeImports;
	private Map<String, String> variableTypes;

	public TypeDependentMeasure(Map<String, Object> settings) {
		super(settings);
	}
	
	@Override
	public List<Issue> analyzeNode(Node node, String fileString, List<CompilationUnit> compilationUnits) {
		storeVariables(node);
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
			storeVariables(fieldDeclaration.getVariables(), fieldDeclaration.getType().toString());
		} else if (node instanceof VariableDeclarationExpr) {
			VariableDeclarationExpr variableDeclarationExpr = (VariableDeclarationExpr) node;
			storeVariables(variableDeclarationExpr.getVars(), variableDeclarationExpr.getType().toString());
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
			this.variableTypes.put(variableDeclarator.getId().getName(), type);
			this.variableTypes.put(type.toString(), type);
		}
	}
	
	/**
	 * Returns the package of a type, e.g. "File" returns "java.io.File".
	 *
	 * @param type
	 *            - the type to find the package for
	 * @return the package string of the type
	 */
	protected String typeToPackage(String type) {
		return this.typeImports.get(type);
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
	}

	@Override
	public abstract String getIssueType();

	public Map<String, String> getTypeImports() {
		return this.typeImports;
	}

	public Map<String, String> getVariableTypes() {
		return this.variableTypes;
	}

}
