package squids;

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
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.body.VariableDeclaratorId;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;

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
			ClassOrInterfaceDeclaration parentType = findNodeClassOrInterfaceDeclaration(methodCall);
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
			return compilationUnit;
		}

		return compilationUnit;
	}

	public static ClassOrInterfaceDeclaration findNodeClassOrInterfaceDeclaration(Node node)
			throws NoSuchAncestorFoundException {
		return (ClassOrInterfaceDeclaration) findNodeAncestorOfType(node, ClassOrInterfaceDeclaration.class);
	}

	public static CompilationUnit findNodeCompilationUnit(Node node) {
		try {
			return (CompilationUnit) findNodeAncestorOfType(node, CompilationUnit.class);
		} catch (NoSuchAncestorFoundException e) {
			return null;
		}
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

	public static FieldDeclaration findFieldDeclarationInType(String fieldName, TypeDeclaration classOrInterface) {
		List<FieldDeclaration> fields = findTypeFields(classOrInterface);
		for (FieldDeclaration field : fields) {
			for (VariableDeclarator variable : field.getVariables()) {
				if (variable.getId().getName().equals(fieldName)) {
					return field;
				}
			}
		}
		return null;
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
	 * Get the NameExpr of a member (BodyDeclaration)
	 */
	public static NameExpr getNameExpr(BodyDeclaration member) {
		if (member instanceof MethodDeclaration) {
			return ((MethodDeclaration) member).getNameExpr();
		} else if (member instanceof ConstructorDeclaration) {
			return ((ConstructorDeclaration) member).getNameExpr();
		} else if (member instanceof FieldDeclaration) {
			return new NameExpr(member.getBeginLine(), member.getBeginColumn(), member.getEndLine(),
					member.getEndColumn(), ((FieldDeclaration) member).getVariables().get(0).getId().getName());
		} else {
			Node node = member;
			return new NameExpr(node.getBeginLine(), node.getBeginColumn(), node.getEndLine(), node.getEndColumn(),
					member.toStringWithoutComments());
		}
	}
}