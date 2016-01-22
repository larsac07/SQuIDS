package autocisq.measure.maintainability;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.ModifierSet;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.CharLiteralExpr;
import com.github.javaparser.ast.expr.DoubleLiteralExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.LongLiteralExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;

import autocisq.JavaParserHelper;
import autocisq.NoSuchAncestorFoundException;
import autocisq.models.FileIssue;
import autocisq.models.Issue;

/**
 * The {@link FunctionUnreachable} class represents the CISQ Maintainability
 * measure 5: # of unreachable functions.
 *
 * @author Lars A. V. Cabrera
 * 		
 */
public class FunctionUnreachable extends TypeDependentMeasure {

	public final static String ISSUE_TYPE = "Unreachable function";

	private List<String> referencedFunctions;

	public FunctionUnreachable(Map<String, Object> settings) {
		super(settings);
		this.referencedFunctions = new LinkedList<>();
	}
	
	@Override
	public List<Issue> analyzeNode(Node node, String fileString, List<CompilationUnit> compilationUnits) {
		storeReferences(node, fileString, compilationUnits);
		if (node instanceof MethodDeclaration) {
			MethodDeclaration methodDeclaration = (MethodDeclaration) node;
			String name = methodDeclaration.getName();
			List<String> types = new ArrayList<>();
			for (Parameter parameter : methodDeclaration.getParameters()) {
				types.add(parameter.getType().toString());
			}
			ClassOrInterfaceDeclaration enclosingClass;
			try {
				enclosingClass = (ClassOrInterfaceDeclaration) JavaParserHelper.findNodeAncestorOfType(node,
						ClassOrInterfaceDeclaration.class);
				boolean isReferenced = isReferenced(enclosingClass.getName(), name, types);
				boolean isPrivate = ModifierSet.isPrivate(methodDeclaration.getModifiers());
				boolean classIsPrivate = ModifierSet.isPrivate(enclosingClass.getModifiers());

				if (!isReferenced && (isPrivate || classIsPrivate)) {
					List<Issue> issues = new LinkedList<>();
					issues.add(new FileIssue(ISSUE_TYPE, node, fileString));
					return issues;
				}
			} catch (NoSuchAncestorFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;

	}
	
	private void storeReferences(Node node, String fileString, List<CompilationUnit> compilationUnits) {
		super.analyzeNode(node, fileString, compilationUnits);
		if (node instanceof MethodCallExpr) {
			String variableName = null;
			MethodCallExpr methodCallExpr = (MethodCallExpr) node;
			Expression expression = methodCallExpr.getScope();
			if (expression instanceof FieldAccessExpr) {
				FieldAccessExpr fieldAccessExpr = (FieldAccessExpr) expression;
				variableName = JavaParserHelper.getNameExprType(fieldAccessExpr.getFieldExpr());
			} else if (expression instanceof NameExpr) {
				NameExpr nameExpr = (NameExpr) expression;
				variableName = JavaParserHelper.getNameExprType(nameExpr);
			}
			List<String> types = expressionsToTypes(methodCallExpr.getArgs());

			if (variableName != null) {
				String type = variableToType(variableName);
				addReference(type, methodCallExpr.getName(), types);
			} else {
				try {
					ClassOrInterfaceDeclaration enclosingClass = (ClassOrInterfaceDeclaration) JavaParserHelper
							.findNodeAncestorOfType(node, ClassOrInterfaceDeclaration.class);
					addReference(enclosingClass.getName(), methodCallExpr.getName(), types);
				} catch (NoSuchAncestorFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		for (Node child : node.getChildrenNodes()) {
			storeReferences(child, fileString, compilationUnits);
		}
	}
	
	private List<String> expressionsToTypes(List<Expression> args) {
		List<String> types = new ArrayList<>();
		for (Expression arg : args) {
			if (arg instanceof NameExpr) {
				NameExpr nameExpr = (NameExpr) arg;
				types.add(variableToType(nameExpr.getName()));
			} else if (arg instanceof NullLiteralExpr) {
				types.add("null");
			} else if (arg instanceof IntegerLiteralExpr) {
				types.add("int");
			} else if (arg instanceof LongLiteralExpr) {
				types.add("long");
			} else if (arg instanceof DoubleLiteralExpr) {
				types.add("double");
			} else if (arg instanceof CharLiteralExpr) {
				types.add("char");
			} else if (arg instanceof BooleanLiteralExpr) {
				types.add("boolean");
			} else if (arg instanceof StringLiteralExpr) {
				types.add("String");
			}
		}
		return types;
	}
	
	/**
	 * Adds a method to the list of referenced functions. Concatenates class
	 * name, method name and parameter types, e.g.
	 * Class.function(String,int,boolean).
	 *
	 * @param className
	 *            - the name of the class which contains the method
	 * @param functionName
	 *            - the name of the method
	 * @param types
	 *            - the types of the parameters in the method
	 */
	private void addReference(String className, String functionName, List<String> types) {
		this.referencedFunctions.add(concatClassAndFunction(className, functionName, types));
	}
	
	/**
	 * Checks if a method is referenced.
	 *
	 * @param className
	 *            - the name of the class which contains the method
	 * @param functionName
	 *            - the name of the method
	 * @param types
	 *            - the types of the parameters in the method
	 * @return true if the method is referenced, false if not
	 */
	private boolean isReferenced(String className, String functionName, List<String> types) {
		String classAndFunction = concatClassAndFunction(className, functionName, types);
		for (String referencedFunction : this.referencedFunctions) {
			if (referencedFunction.equals(classAndFunction)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Concatenates class name, function name and parameter types, e.g.
	 * Class.function(String,int,boolean).
	 *
	 * @param className
	 *            - the name of the class which contains the method
	 * @param functionName
	 *            - the name of the method
	 * @param types
	 *            - the types of the parameters in the method
	 * @return
	 */
	private String concatClassAndFunction(String className, String functionName, List<String> types) {
		String classAndFunction = className;
		classAndFunction += "." + functionName;
		classAndFunction += "(";
		for (int i = 0; i < types.size(); i++) {
			String type = types.get(i);
			classAndFunction += type;
			if (i < types.size() - 1) {
				classAndFunction += ",";
			}
		}
		classAndFunction += ")";
		return classAndFunction;
	}
	
	@Override
	public String getIssueType() {
		return ISSUE_TYPE;
	}

}
