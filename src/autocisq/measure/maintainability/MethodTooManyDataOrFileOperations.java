package autocisq.measure.maintainability;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;

import autocisq.measure.Measure;
import autocisq.models.FileIssue;
import autocisq.models.Issue;

/**
 * The {@link MethodTooManyDataOrFileOperations} class represents the CISQ
 * Maintainability measure 19: # of methods with ≥ 7 data or file operations.
 *
 * It depends on a {@link List} of canonical name strings (e.g. "java.io.File")
 * for classes which contain data or file operations.
 *
 * The list must be provided in the settings map parameter in the constructor,
 * with the key "data_or_io_classes".
 *
 * The measure does not take into account package names using the asterisk
 * operator (*). Therefore, both the list of db_or_io_classes and the import
 * declarations in the file to be analyzed must use complete canonical names,
 * e.g. java.io.File, not java.io.*.
 *
 * @author Lars A. V. Cabrera
 * 		
 */
public class MethodTooManyDataOrFileOperations extends Measure {

	private final static int THRESHOLD = 7;

	public final static String ISSUE_TYPE = "Method with >= " + THRESHOLD + " data or file operations";

	private List<String> dbOrIoClasses;
	private Map<String, String> typeImports;
	private Map<String, String> variableTypes;
	private int count;

	/**
	 * Creates a new {@link MethodTooManyDataOrFileOperations} object and tries
	 * to retrieve a list of classes which contain data or file operations.
	 *
	 * @param settings
	 *            - a map of settings, including at least a key
	 *            "db_or_io_classes" with a {@link List} of canonical name
	 *            strings (e.g. "java.io.File") for classes which contain data
	 *            or file operations.
	 */
	@SuppressWarnings("unchecked")
	public MethodTooManyDataOrFileOperations(Map<String, Object> settings) {
		super(settings);
		try {
			this.dbOrIoClasses = (List<String>) settings.get("db_or_io_classes");
			if (this.dbOrIoClasses == null) {
				System.err.println(this.getClass().getSimpleName()
						+ " was provided an empty db_or_io_classes list and will not work. Please provide a db_or_io_classes list");
				this.dbOrIoClasses = new ArrayList<>();
			}
		} catch (NullPointerException | ClassCastException e) {
			this.dbOrIoClasses = new ArrayList<>();
			System.err.println(this.getClass().getSimpleName()
					+ " was not provided a db_or_io_classes list and will not work. Please provide a db_or_io_classes list");
			e.printStackTrace();
		}
		reset();
	}

	/**
	 * Filters out {@link MethodCallExpr} objects to check if they are called
	 * upon classes which contain data or file operations. Calls
	 * {@link MethodTooManyDataOrFileOperations#storeVariables(Node)} if the
	 * node is not a {@link MethodCallExpr}.
	 *
	 * @return - a list of issues with 0 or 1 {@link FileIssue}, depending on
	 *         whether an issue was found.
	 */
	@Override
	public List<Issue> analyzeNode(Node node, String fileString, List<CompilationUnit> compilationUnits) {
		if (node instanceof MethodCallExpr) {
			MethodCallExpr methodCallExpr = (MethodCallExpr) node;

			Expression expression = methodCallExpr.getScope();
			if (expression instanceof FieldAccessExpr) {
				FieldAccessExpr fieldAccessExpr = (FieldAccessExpr) expression;
				NameExpr nameExpr = fieldAccessExpr.getFieldExpr();
				return dbOrIoMethodCall(methodCallExpr, fileString, nameExpr);
			} else if (expression instanceof NameExpr) {
				NameExpr nameExpr = (NameExpr) expression;
				return dbOrIoMethodCall(methodCallExpr, fileString, nameExpr);
			}
		} else {
			storeVariables(node);
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
	private void storeVariables(Node node) {
		if (node instanceof CompilationUnit) {
			CompilationUnit compilationUnit = (CompilationUnit) node;
			reset();
			addImports(compilationUnit.getImports());
		} else if (node instanceof ConstructorDeclaration) {
			ConstructorDeclaration constructorDeclaration = (ConstructorDeclaration) node;
			storeParameters(constructorDeclaration.getParameters());
			resetCount();
		} else if (node instanceof MethodDeclaration) {
			MethodDeclaration methodDeclaration = (MethodDeclaration) node;
			storeParameters(methodDeclaration.getParameters());
			resetCount();
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
	private void addImports(List<ImportDeclaration> importDeclarations) {
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
	private String getPackageType(String packageString) {
		String[] packageStringParts = packageString.split("\\.");
		String packageType = packageStringParts[packageStringParts.length - 1];
		return packageType;
	}
	
	/**
	 * Returns a list of issues with 0 or 1 {@link FileIssue}, depending on
	 * whether or not the {@link NameExpr} is found in the db_or_io_classes
	 * list.
	 *
	 * @param methodCallExpr
	 *            - the {@link MethodCallExpr} to blame if an issue was found
	 * @param fileString
	 *            - the complete string of the source file
	 * @param nameExpr
	 *            - the {@link NameExpr} to analyze
	 * @return a list of issues with 0 or 1 {@link FileIssue}, depending on
	 *         whether or not an issue was found
	 */
	private List<Issue> dbOrIoMethodCall(MethodCallExpr methodCallExpr, String fileString, NameExpr nameExpr) {
		String nameExprType = getNameExprType(nameExpr);
		String type = this.variableTypes.get(nameExprType);
		if (type != null) {
			String packageName = typeToPackage(type);
			if (isDbOrIoClass(packageName)) {
				this.count++;
				if (this.count >= THRESHOLD) {
					List<Issue> issues = new ArrayList<>();
					issues.add(new FileIssue(ISSUE_TYPE, methodCallExpr, fileString));
					return issues;
				}
			}
		}
		return null;
	}
	
	/**
	 * Stores the parameters in both identifier - type and type - type pairs
	 * (for static calls), e.g. "file" - "File" and "File" - "File".
	 *
	 * @param parameters
	 *            - the parameters to store
	 */
	private void storeParameters(List<Parameter> parameters) {
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
	private void storeVariables(List<VariableDeclarator> variableDeclarators, String type) {
		for (VariableDeclarator variableDeclarator : variableDeclarators) {
			this.variableTypes.put(variableDeclarator.getId().getName(), type);
			this.variableTypes.put(type.toString(), type);
		}
	}
	
	/**
	 * Returns the package of a type, e.g. "File" returns "java.io.File"
	 *
	 * @param type
	 * @return
	 */
	private String typeToPackage(String type) {
		return this.typeImports.get(type);
	}

	/**
	 * Decides whether or not a class contains data or file operations.
	 *
	 * @param classCanonicalName
	 *            - the canonical name of the class, e.g. "java.io.File"
	 * @return true if the class contains data or file operations, false if not
	 */
	private boolean isDbOrIoClass(String classCanonicalName) {
		for (String dbOrIoClass : this.dbOrIoClasses) {
			if (dbOrIoClass.equals(classCanonicalName)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Get the type of a {@link NameExpr}. Works for cases such as System.out
	 * and Files.
	 *
	 * @param nameExpr
	 *            - the {@link NameExpr} to get the type from
	 * @return the type of the {@link NameExpr}
	 */
	private String getNameExprType(NameExpr nameExpr) {
		String nameExprString = nameExpr.getName();
		if (nameExprString.contains(".")) {
			String[] parts = nameExpr.getName().split("\\.");
			return parts[0];
		} else {
			return nameExprString;
		}
	}

	/**
	 * Resets import and variable maps and calls
	 * {@link MethodTooManyDataOrFileOperations#resetCount()}.
	 */
	private void reset() {
		this.typeImports = new HashMap<>();
		this.variableTypes = new HashMap<>();
		resetCount();
	}
	
	/**
	 * Resets the count of {@link MethodCallExpr} which contain data or file
	 * operations in a {@link CompilationUnit}.
	 */
	private void resetCount() {
		this.count = 0;
	}
	
	@Override
	public String getIssueType() {
		return ISSUE_TYPE;
	}
}
