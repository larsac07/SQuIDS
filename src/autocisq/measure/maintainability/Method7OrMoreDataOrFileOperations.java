package autocisq.measure.maintainability;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
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
 * TODO Javadoc
 *
 * @author Lars A. V. Cabrera
 *		
 */
public class Method7OrMoreDataOrFileOperations implements Measure {

	public final static String ISSUE_TYPE = "Method with >= 7 data or file operations";

	private final static int THRESHOLD = 7;

	private List<String> dbOrIoClasses;
	private Map<String, String> typeImports;
	private Map<String, String> variableTypes;
	private int count;
	
	public Method7OrMoreDataOrFileOperations() {
		// TODO push to user input
		this.dbOrIoClasses = new LinkedList<>();
		this.dbOrIoClasses.add("java.io.File");
		this.dbOrIoClasses.add("java.nio.file.Files");
		this.dbOrIoClasses.add("java.sql.Connection");
		this.dbOrIoClasses.add("java.sql.DriverManager");
		this.dbOrIoClasses.add("java.sql.PreparedStatement");
		this.dbOrIoClasses.add("java.sql.Statement");
		this.dbOrIoClasses.add("com.github.javaparser.JavaParser");
		reset();
	}

	@Override
	public List<Issue> analyzeNode(Node node, String fileString, List<CompilationUnit> compilationUnits,
			Map<String, Integer> layerMap) {
		if (node instanceof MethodCallExpr) {
			MethodCallExpr methodCallExpr = (MethodCallExpr) node;

			Expression expression = methodCallExpr.getScope();
			if (expression instanceof FieldAccessExpr) {
				FieldAccessExpr fieldAccessExpr = (FieldAccessExpr) expression;
				NameExpr nameExpr = fieldAccessExpr.getFieldExpr();
				return dbOrIoMethodCall(node, fileString, nameExpr);
			} else if (expression instanceof NameExpr) {
				NameExpr nameExpr = (NameExpr) expression;
				return dbOrIoMethodCall(node, fileString, nameExpr);
			}
		} else {
			storeVariables(node);
		}
		return null;
	}

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
	
	private void addImports(List<ImportDeclaration> importDeclarations) {
		for (ImportDeclaration importDeclaration : importDeclarations) {
			String packageString = importDeclaration.getName().toString();
			String packageType = getPackageType(packageString);
			this.typeImports.put(packageType, packageString);
			this.variableTypes.put(packageType, packageType);
		}
	}

	private String getPackageType(String packageString) {
		String[] packageStringParts = packageString.split("\\.");
		String packageType = packageStringParts[packageStringParts.length - 1];
		return packageType;
	}
	
	private List<Issue> dbOrIoMethodCall(Node node, String fileString, NameExpr nameExpr) {
		String nameExprType = getNameExprType(nameExpr);
		String type = this.variableTypes.get(nameExprType);
		if (type != null) {
			String packageName = typeToPackage(type);
			if (isDbOrIoClass(packageName)) {
				this.count++;
				if (this.count >= THRESHOLD) {
					List<Issue> issues = new ArrayList<>();
					issues.add(new FileIssue(ISSUE_TYPE, node, fileString));
					return issues;
				}
			}
		}
		return null;
	}
	
	private void storeParameters(List<Parameter> parameters) {
		for (Parameter parameter : parameters) {
			this.variableTypes.put(parameter.getId().getName(), parameter.getType().toString());
			this.variableTypes.put(parameter.getType().toString(), parameter.getType().toString());
		}
	}

	private void storeVariables(List<VariableDeclarator> variableDeclarators, String type) {
		for (VariableDeclarator variableDeclarator : variableDeclarators) {
			this.variableTypes.put(variableDeclarator.getId().getName(), type);
			this.variableTypes.put(type.toString(), type);
		}
	}
	
	private String typeToPackage(String type) {
		return this.typeImports.get(type);
	}

	private boolean isDbOrIoClass(String packageName) {
		for (String dbOrIoClass : this.dbOrIoClasses) {
			if (dbOrIoClass.equals(packageName)) {
				return true;
			}
		}
		return false;
	}

	private String getNameExprType(NameExpr nameExpr) {
		String nameExprString = nameExpr.getName();
		if (nameExprString.contains(".")) {
			String[] parts = nameExpr.getName().split("\\.");
			return parts[0];
		} else {
			return nameExprString;
		}
	}

	private void reset() {
		this.typeImports = new HashMap<>();
		this.variableTypes = new HashMap<>();
		resetCount();
	}
	
	private void resetCount() {
		this.count = 0;
	}
}
