package autocisq.measure.maintainability;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
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
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.stmt.ExplicitConstructorInvocationStmt;

import autocisq.JavaParserHelper;
import autocisq.NoSuchAncestorFoundException;
import autocisq.models.FileIssue;
import autocisq.models.Issue;

/**
 * The {@link MethodUnreachable} class represents the CISQ Maintainability
 * measure 5: # of unreachable functions.
 *
 * @author Lars A. V. Cabrera
 *
 */
public class MethodUnreachable extends TypeDependentMeasure {

	public final static String ISSUE_TYPE = "CISQ MM05: Unreachable function";

	private Set<String> referencedFunctions;

	public MethodUnreachable(Map<String, Object> settings) {
		super(settings);
		this.referencedFunctions = new HashSet<>();
	}

	@Override
	public List<Issue> analyzeNode(Node node, String fileString, List<CompilationUnit> compilationUnits) {
		super.analyzeNode(node, fileString, compilationUnits);
		storeReferences(node, fileString, compilationUnits);
		if (node instanceof MethodDeclaration || node instanceof ConstructorDeclaration) {
			String name;
			List<String> types = new ArrayList<>();
			int modifiers;
			if (node instanceof MethodDeclaration) {
				MethodDeclaration methodDeclaration = (MethodDeclaration) node;
				name = methodDeclaration.getName();
				for (Parameter parameter : methodDeclaration.getParameters()) {
					types.add(parameter.getType().toString());
				}
				modifiers = methodDeclaration.getModifiers();
			} else {
				ConstructorDeclaration constructorDeclaration = (ConstructorDeclaration) node;
				name = constructorDeclaration.getName();
				for (Parameter parameter : constructorDeclaration.getParameters()) {
					types.add(parameter.getType().toString());
				}
				modifiers = constructorDeclaration.getModifiers();
			}
			ClassOrInterfaceDeclaration enclosingClass;
			try {
				enclosingClass = (ClassOrInterfaceDeclaration) JavaParserHelper.findNodeAncestorOfType(node,
						ClassOrInterfaceDeclaration.class);
				boolean isReferenced = isReferenced(enclosingClass.getName(), name, types);
				boolean isPrivate = ModifierSet.isPrivate(modifiers);
				boolean classIsPrivate = ModifierSet.isPrivate(enclosingClass.getModifiers());

				if (!isReferenced && (isPrivate || classIsPrivate)) {
					List<Issue> issues = new LinkedList<>();
					issues.add(new FileIssue(this, node, fileString));
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
		if (node instanceof MethodCallExpr || node instanceof ObjectCreationExpr
				|| node instanceof ExplicitConstructorInvocationStmt) {
			if (node instanceof MethodCallExpr) {
				MethodCallExpr methodCallExpr = (MethodCallExpr) node;
				String variableName = getVariableName(methodCallExpr.getScope());
				String name = methodCallExpr.getName();
				List<String> types = argsToTypes(node, methodCallExpr.getArgs());
				if (variableName != null) {
					String type = variableToType(variableName);
					addReference(type, name, types);
				} else {
					addStaticMethodOrConstructorReference(node, name, types);
				}
			} else if (node instanceof ObjectCreationExpr) {
				ObjectCreationExpr objectCreationExpr = (ObjectCreationExpr) node;
				List<String> types = argsToTypes(node, objectCreationExpr.getArgs());
				addReference(objectCreationExpr.getType().getName(), objectCreationExpr.getType().getName(), types);
			} else {
				ExplicitConstructorInvocationStmt explicitConstructorInvocationStmt = (ExplicitConstructorInvocationStmt) node;
				List<String> types = argsToTypes(node, explicitConstructorInvocationStmt.getArgs());
				addStaticMethodOrConstructorReference(node, null, types);
			}
		}

		for (Node child : node.getChildrenNodes()) {
			storeReferences(child, fileString, compilationUnits);
		}
	}

	/**
	 * @param node
	 * @param methodName
	 * @param types
	 */
	private void addStaticMethodOrConstructorReference(Node node, String methodName, List<String> types) {
		try {
			ClassOrInterfaceDeclaration enclosingClass = (ClassOrInterfaceDeclaration) JavaParserHelper
					.findNodeAncestorOfType(node, ClassOrInterfaceDeclaration.class);
			if (methodName == null || methodName.isEmpty()) {
				methodName = enclosingClass.getName();
			}
			addReference(enclosingClass.getName(), methodName, types);
		} catch (NoSuchAncestorFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @param scope
	 * @return
	 */
	private String getVariableName(Expression scope) {
		if (scope instanceof FieldAccessExpr) {
			FieldAccessExpr fieldAccessExpr = (FieldAccessExpr) scope;
			return JavaParserHelper.getNameExprType(fieldAccessExpr.getFieldExpr());
		} else if (scope instanceof NameExpr) {
			NameExpr nameExpr = (NameExpr) scope;
			return JavaParserHelper.getNameExprType(nameExpr);
		}
		return null;
	}

	private List<String> argsToTypes(Node callingNode, List<Expression> args) {
		List<String> types = new ArrayList<>();
		if (args == null) {
			return types;
		} else {
			for (Expression arg : args) {
				if (arg instanceof ThisExpr) {
					try {
						ClassOrInterfaceDeclaration enclosingClass = (ClassOrInterfaceDeclaration) JavaParserHelper
								.findNodeAncestorOfType(callingNode, ClassOrInterfaceDeclaration.class);
						types.add(enclosingClass.getName());
					} catch (NoSuchAncestorFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else if (arg instanceof NameExpr) {
					NameExpr nameExpr = (NameExpr) arg;
					types.add(variableToType(nameExpr.getName()));
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
				} else if (arg instanceof NullLiteralExpr) {
					types.add("null");
				}
			}
			return types;
		}
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
		return this.referencedFunctions.contains(classAndFunction);
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
	public String getMeasureElement() {
		return ISSUE_TYPE;
	}

}
