package squids.measure.maintainability;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;

import squids.JavaParserHelper;
import squids.models.FileIssue;
import squids.models.Issue;

/**
 * The {@link CISQMM19MethodDataOrFileOperations} class represents the CISQ
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
public class CISQMM19MethodDataOrFileOperations extends CISQMMTypeDependentMeasure {

	private final static int THRESHOLD = 7;
	private final static String ASTERISK = "*";
	private final static String POINT = ".";

	public final static String ISSUE_TYPE = "CISQ MM19: Method with >= " + THRESHOLD + " data or file operations";

	private List<String> dbOrIoClasses;
	private List<String> dbOrIoPackages;
	private int count;

	/**
	 * Creates a new {@link CISQMM19MethodDataOrFileOperations} object and tries
	 * to retrieve a list of classes which contain data or file operations.
	 *
	 * @param settings
	 *            - a map of settings, including at least a key
	 *            "db_or_io_classes" with a {@link List} of canonical name
	 *            strings (e.g. "java.io.File") for classes which contain data
	 *            or file operations.
	 */
	@SuppressWarnings("unchecked")
	public CISQMM19MethodDataOrFileOperations(Map<String, Object> settings) {
		super(settings);
		try {
			List<String> dbOrIoClassesAndPackages = (List<String>) settings.get("db_or_io_classes");
			if (dbOrIoClassesAndPackages == null) {
				System.err.println(this.getClass().getSimpleName()
						+ " was provided an empty db_or_io_classes list and will not work. Please provide a db_or_io_classes list");
				createLists(null);
			} else {
				createLists(dbOrIoClassesAndPackages);
			}
		} catch (NullPointerException | ClassCastException e) {
			createLists(null);
			System.err.println(this.getClass().getSimpleName()
					+ " was not provided a db_or_io_classes list and will not work. Please provide a db_or_io_classes list");
		}
		reset();
	}

	/**
	 * Filters out {@link MethodCallExpr} objects to check if they are called
	 * upon classes which contain data or file operations. Calls
	 * {@link CISQMM19MethodDataOrFileOperations#storeVariables(Node)} if the
	 * node is not a {@link MethodCallExpr}.
	 *
	 * @return - a list of issues with 0 or 1 {@link FileIssue}, depending on
	 *         whether an issue was found.
	 */
	@Override
	public List<Issue> analyzeNode(Node node, String fileString, List<CompilationUnit> compilationUnits) {
		super.analyzeNode(node, fileString, compilationUnits);
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
		}
		return null;
	}

	/**
	 * Calls {@link CISQMMTypeDependentMeasure#storeVariables(Node)} and resets
	 * count if node is {@link ConstructorDeclaration} or
	 * {@link MethodDeclaration}
	 *
	 * @param node
	 *            - the node to store variables from.
	 */
	@Override
	protected void storeVariables(Node node) {
		super.storeVariables(node);
		if (node instanceof ConstructorDeclaration || node instanceof MethodDeclaration) {
			resetCount();
		}
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
		String nameExprType = JavaParserHelper.getNameExprType(nameExpr);
		String type = getVariableTypes().get(nameExprType);
		if (type != null) {
			String packageName = typeToImport(type);
			if (isDbOrIoClass(packageName)) {
				this.count++;
				if (this.count >= THRESHOLD) {
					List<Issue> issues = new ArrayList<>();
					issues.add(new FileIssue(this, methodCallExpr, fileString));
					return issues;
				}
			}
		}
		return null;
	}

	/**
	 * Decides whether or not a class contains data or file operations.
	 *
	 * @param classCanonicalName
	 *            - the canonical name of the class, e.g. "java.io.File"
	 * @return true if the class contains data or file operations, false if not
	 */
	protected boolean isDbOrIoClass(String classCanonicalName) {
		if (classCanonicalName == null) {
			return false;
		}
		String classPackage = classCanonicalName.substring(0, classCanonicalName.lastIndexOf(POINT));
		for (String dbOrIoClass : this.dbOrIoClasses) {
			if (dbOrIoClass.equals(classCanonicalName)) {
				return true;
			}
		}
		for (String dbOrIoPackage : this.dbOrIoPackages) {
			if (classPackage.contains(dbOrIoPackage)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Resets import and variable maps and calls
	 * {@link CISQMM19MethodDataOrFileOperations#resetCount()}.
	 */
	@Override
	protected void reset() {
		super.reset();
		resetCount();
	}

	/**
	 * Resets the count of {@link MethodCallExpr} which contain data or file
	 * operations in a {@link CompilationUnit}.
	 */
	private void resetCount() {
		this.count = 0;
	}

	protected void createLists(List<String> dbOrIoClassesAndPackages) {
		this.dbOrIoClasses = new LinkedList<>();
		this.dbOrIoPackages = new LinkedList<>();
		if (dbOrIoClassesAndPackages != null) {
			for (String line : dbOrIoClassesAndPackages) {
				if (line.endsWith(ASTERISK)) {
					this.dbOrIoPackages.add(line.substring(0, line.lastIndexOf(POINT)));
				} else {
					this.dbOrIoClasses.add(line);
				}
			}
		}
	}

	@Override
	public String getMeasureElement() {
		return ISSUE_TYPE;
	}
}
