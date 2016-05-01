package autocisq;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NamedNode;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.MultiTypeParameter;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.body.VariableDeclaratorId;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.type.Type;

public abstract class JavaParserHelper {
	public static boolean methodCallFromSameType(MethodCallExpr methodCall) {
		return methodCall.getScope() == null;
	}

	public static String getMemberPath(Node node) {
		String memberPath = "";
		try {
			List<Node> typeAncestors = findNodeAncestorsOfType(node, null, ClassOrInterfaceDeclaration.class,
					MethodDeclaration.class, ConstructorDeclaration.class, CompilationUnit.class);
			// Traverse backwards
			for (int i = typeAncestors.size() - 1; i >= 0; i--) {
				Node nodeAncestor = typeAncestors.get(i);
				if (nodeAncestor instanceof NamedNode) {
					memberPath += ((NamedNode) nodeAncestor).getName();
					if (nodeAncestor instanceof MethodDeclaration || nodeAncestor instanceof ConstructorDeclaration) {
						List<Parameter> params;
						if (nodeAncestor instanceof MethodDeclaration) {
							params = ((MethodDeclaration) nodeAncestor).getParameters();
						} else {
							params = ((ConstructorDeclaration) nodeAncestor).getParameters();
						}
						memberPath += "(";
						for (int paramI = 0; paramI < params.size(); paramI++) {
							Parameter param = params.get(paramI);
							memberPath += param.getType().toString();
							if (paramI < params.size() - 1) {
								memberPath += ",";
							}
						}
						memberPath += ")";
					}
					if (i > 0) {
						memberPath += ".";
					}
				} else if (nodeAncestor instanceof CompilationUnit) {
					PackageDeclaration packageDeclaration = ((CompilationUnit) nodeAncestor).getPackage();
					if (packageDeclaration != null) {
						memberPath += packageDeclaration.getName().toString();
						if (i > 0) {
							memberPath += ".";
						}
					}
				}
			}
			return memberPath;
		} catch (NoSuchAncestorFoundException e) {
			return memberPath;
		}
	}

	public static CompilationUnit findMethodCompilationUnit(MethodCallExpr methodCall,
			List<CompilationUnit> compilationUnits) {
		CompilationUnit compilationUnit = null;
		Expression scopeExpression = methodCall.getScope();
		try {
			TypeDeclaration parentType = findNodeClassOrInterfaceDeclaration(methodCall);
			search: for (FieldDeclaration fieldDeclaration : findTypeFields(parentType)) {
				List<VariableDeclarator> variables = fieldDeclaration.getVariables();
				for (VariableDeclarator variable : variables) {
					VariableDeclaratorId variableId = variable.getId();
					if (scopeExpression.toString().equals(variableId.getName())) {
						compilationUnit = findCompilationUnit(fieldDeclaration.getType().toString(), compilationUnits);
						break search;
					}
				}
			}
			if (compilationUnit == null) {
				compilationUnit = findCompilationUnit(scopeExpression.toString(), compilationUnits);
			}

		} catch (NoSuchAncestorFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return compilationUnit;
	}

	public static ClassOrInterfaceDeclaration findNodeClassOrInterfaceDeclaration(Node node)
			throws NoSuchAncestorFoundException {
		return (ClassOrInterfaceDeclaration) findNodeAncestorOfType(node, ClassOrInterfaceDeclaration.class);
	}

	public static CompilationUnit findNodeCompilationUnit(Node node) throws NoSuchAncestorFoundException {
		return (CompilationUnit) findNodeAncestorOfType(node, CompilationUnit.class);
	}

	/**
	 * Find a Node's ancestors of a specified class
	 *
	 * @param node
	 *            - the node of reference
	 * @param ancestorClass
	 *            - the class of the ancestor you wish to find
	 * @return all ancestors found of the specified class in order
	 * @throws NoSuchAncestorFoundException
	 */

	@SafeVarargs
	public static List<Node> findNodeAncestorsOfType(Node node, List<Node> ancestors,
			Class<? extends Node>... ancestorClasses) throws NoSuchAncestorFoundException {
		if (ancestors == null) {
			ancestors = new LinkedList<>();
		}
		if (node == null && !ancestors.isEmpty()) {
			return ancestors;
		} else if (node == null && ancestors.isEmpty()) {
			throw new NoSuchAncestorFoundException();
		} else {

			for (Class<? extends Node> ancestorClass : ancestorClasses) {
				if (node.getClass().equals(ancestorClass)) {
					ancestors.add(node);
				}
			}
		}
		return findNodeAncestorsOfType(node.getParentNode(), ancestors, ancestorClasses);
	}

	/**
	 * Find a Node's ancestor of a specified class
	 *
	 * @param node
	 *            - the node of reference
	 * @param ancestorClass
	 *            - the class of the ancestor you wish to find
	 * @return the closest ancestor found of the specified class
	 * @throws NoSuchAncestorFoundException
	 */
	@SafeVarargs
	public static Node findNodeAncestorOfType(Node node, Class<? extends Node>... ancestorClasses)
			throws NoSuchAncestorFoundException {
		if (node == null) {
			throw new NoSuchAncestorFoundException();
		} else {
			for (Class<? extends Node> ancestorClass : ancestorClasses) {
				if (node.getClass().equals(ancestorClass)) {
					return node;
				}
			}
		}
		return findNodeAncestorOfType(node.getParentNode(), ancestorClasses);
	}

	public static List<FieldDeclaration> findTypeFields(TypeDeclaration typeDeclaration) {
		List<FieldDeclaration> fields = new LinkedList<>();
		for (BodyDeclaration bodyDeclaration : typeDeclaration.getMembers()) {
			if (bodyDeclaration instanceof FieldDeclaration) {
				fields.add((FieldDeclaration) bodyDeclaration);
			}
		}
		return fields;
	}

	public static CompilationUnit findCompilationUnit(String className, List<CompilationUnit> compilationUnits) {
		for (CompilationUnit compilationUnit : compilationUnits) {
			for (TypeDeclaration typeDeclaration : compilationUnit.getTypes()) {
				if (typeDeclaration.getName().equals(className)) {
					return compilationUnit;
				}
			}
		}
		return null;
	}

	public static ClassOrInterfaceDeclaration findClassOrInterfaceDeclaration(String className,
			List<CompilationUnit> compilationUnits) {
		for (CompilationUnit compilationUnit : compilationUnits) {
			for (TypeDeclaration typeDeclaration : compilationUnit.getTypes()) {
				if (typeDeclaration instanceof ClassOrInterfaceDeclaration
						&& typeDeclaration.getName().equals(className)) {
					return ((ClassOrInterfaceDeclaration) typeDeclaration);
				}
			}
		}
		return null;
	}

	public static int[] columnsToIndexes(String string, int startLine, int endLine, int startColumn, int endColumn) {
		int startIndex = 0;
		int endIndex = 0;
		int lineIndex = 1;

		String[] lines = string.split("\r\n|\r|\n");
		for (String line : lines) {

			// Account for newline characters
			int lineLength = line.length() + 1;

			if (lineIndex > endLine) {
				break;
			}

			int tabCorrection = line.replaceFirst("(\t+).*", "$1").length();
			tabCorrection -= (tabCorrection * 8) + 1;

			if (lineIndex < startLine) {
				startIndex += lineLength;
			} else if (lineIndex == startLine) {
				startIndex += startColumn + tabCorrection;
			}

			if (lineIndex == endLine) {
				endIndex += endColumn + tabCorrection + 1;
			} else {
				endIndex += lineLength;
			}

			lineIndex++;
		}

		return new int[] { startIndex, endIndex };
	}

	public static FieldDeclaration findFieldDeclarationTopDown(String fieldName, Node node) {
		if (node instanceof FieldDeclaration) {
			FieldDeclaration fieldDeclaration = (FieldDeclaration) node;
			for (VariableDeclarator variableDeclarator : fieldDeclaration.getVariables()) {
				if (variableDeclarator.getId().getName().equals(fieldName)) {
					return fieldDeclaration;
				}
			}
		}

		for (Node child : node.getChildrenNodes()) {
			FieldDeclaration childFieldDeclaration = findFieldDeclarationTopDown(fieldName, child);
			if (childFieldDeclaration != null) {
				return childFieldDeclaration;
			}
		}

		return null;
	}

	public static List<Type> findVariableTypeBottomUp(String variableName, Node node) throws NoSuchVariableException {
		Node parent = node.getParentNode();
		List<Type> types = new ArrayList<>();

		if (parent != null) {
			for (Node sibling : parent.getChildrenNodes()) {
				if (sibling instanceof VariableDeclarationExpr) {
					VariableDeclarationExpr variableDeclarationExpr = (VariableDeclarationExpr) sibling;
					for (VariableDeclarator variableDeclarator : variableDeclarationExpr.getVars()) {
						if (variableDeclarator.getId().getName().equals(variableName)) {
							types.add(variableDeclarationExpr.getType());
							return types;
						}
					}
				} else if (sibling instanceof Parameter) {
					Parameter parameter = (Parameter) sibling;
					if (parameter.getId().getName().equals(variableName)) {
						types.add(parameter.getType());
						return types;
					}
				} else if (sibling instanceof MultiTypeParameter) {
					MultiTypeParameter multiTypeParameter = (MultiTypeParameter) sibling;
					if (multiTypeParameter.getId().getName().equals(variableName)) {
						types.addAll(multiTypeParameter.getTypes());
						return types;
					}
				} else if (sibling instanceof FieldDeclaration) {
					FieldDeclaration fieldDeclaration = (FieldDeclaration) sibling;
					for (VariableDeclarator variableDeclarator : fieldDeclaration.getVariables()) {
						if (variableDeclarator.getId().getName().equals(variableName)) {
							types.add(fieldDeclaration.getType());
							return types;
						}
					}
				} else if (sibling instanceof ExpressionStmt) {
					try {
						VariableDeclarationExpr variableDeclarationExpr = (VariableDeclarationExpr) findNodeDescendantOfType(
								sibling, VariableDeclarationExpr.class);
						for (VariableDeclarator variableDeclarator : variableDeclarationExpr.getVars()) {
							if (variableDeclarator.getId().getName().equals(variableName)) {
								types.add(variableDeclarationExpr.getType());
								return types;
							}
						}
					} catch (NoSuchDescendantFoundException e) {
					}
				}
			}

			return findVariableTypeBottomUp(variableName, parent);

		} else {
			throw new NoSuchVariableException("Could not find variable", variableName);
		}
	}

	public static Node findNodeDescendantOfType(Node node, Class<? extends Node> klass)
			throws NoSuchDescendantFoundException {
		if (node.getClass().isAssignableFrom(klass)) {
			return node;
		} else {
			for (Node child : node.getChildrenNodes()) {
				return findNodeDescendantOfType(child, klass);
			}
		}
		throw new NoSuchDescendantFoundException();
	}

	/**
	 * Generates a list of {@link Node} elements which are descendants of the
	 * provided node and are assignable from (instanceof) the specified class.
	 *
	 * @param node
	 *            - the root node to search from
	 * @param klass
	 *            - the class filter for the search
	 * @param nodes
	 *            - the list of nodes. Initally, this can be null. Used for
	 *            recursion.
	 * @return a list of nodes of the specified class which are descendants of
	 *         the provided node
	 * @throws NoSuchDescendantFoundException
	 */
	@SafeVarargs
	public static List<Node> findNodeDescendantsOfType(Node node, Class<? extends Node>... classes)
			throws NoSuchDescendantFoundException {
		return findNodeDescendantsOfType(node, null, classes);
	}

	@SafeVarargs
	private static List<Node> findNodeDescendantsOfType(Node node, List<Node> nodes, Class<? extends Node>... classes)
			throws NoSuchDescendantFoundException {
		if (nodes == null) {
			nodes = new LinkedList<>();
		}
		for (Class<? extends Node> klass : classes) {
			if (node.getClass().isAssignableFrom(klass)) {
				nodes.add(node);
			}
		}
		for (Node child : node.getChildrenNodes()) {
			findNodeDescendantsOfType(child, nodes, classes);
		}
		if (nodes.isEmpty()) {
			throw new NoSuchDescendantFoundException();
		} else {
			return nodes;
		}
	}

	/**
	 * Get the type of a {@link NameExpr}. Works for cases such as System.out
	 * and Files.
	 *
	 * @param nameExpr
	 *            - the {@link NameExpr} to get the type from
	 * @return the type of the {@link NameExpr}
	 */
	public static String getNameExprType(NameExpr nameExpr) {
		String nameExprString = nameExpr.getName();
		if (nameExprString.contains(".")) {
			String[] parts = nameExpr.getName().split("\\.");
			return parts[0];
		} else {
			return nameExprString;
		}
	}

	/**
	 * Returns the complete file string (toString() value) of the
	 * {@link CompilationUnit} ancestor of a node.
	 *
	 * @param node
	 *            - the node to get the complete file string for
	 * @return the toString() value of the {@link CompilationUnit} ancestor of a
	 *         node
	 */
	public static String getNodeFileString(Node node) {
		try {
			CompilationUnit cu = findNodeCompilationUnit(node);
			return cu.toString();
		} catch (NoSuchAncestorFoundException e) {
			return null;
		}
	}
}