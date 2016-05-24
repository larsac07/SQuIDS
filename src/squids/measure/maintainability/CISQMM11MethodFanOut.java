package squids.measure.maintainability;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.stmt.ExplicitConstructorInvocationStmt;

import squids.JavaParserHelper;
import squids.NoSuchAncestorFoundException;
import squids.models.FileIssue;
import squids.models.Issue;

/**
 * The CISQMM11MethodFanOut class represents the CISQ Maintainability Measure
 * 11: # of functions that have a fan-out ≥ 10.
 *
 * The fan-out is calculated by summing up the number of called functions
 * (methods) and the number of member variables set.
 *
 * @author Lars A. V. Cabrera
 *
 */
public class CISQMM11MethodFanOut extends CISQMMTypeDependentMeasure {

	public final static int THRESHOLD = 10;
	public final static String ISSUE_TYPE = "CISQ MM11: Function with fan-out >= " + THRESHOLD;
	private final static String MESSAGE = " has a fan-out of ";
	private Set<Node> markedMemberVariablesAndMethodCalls;
	private Set<String> fields;

	public CISQMM11MethodFanOut(Map<String, Object> settings) {
		super(settings);
		this.fields = new HashSet<>();
	}

	@Override
	public List<Issue> analyzeNode(Node node, String fileString, List<CompilationUnit> compilationUnits) {
		super.analyzeNode(node, fileString, compilationUnits);
		if (node instanceof ClassOrInterfaceDeclaration) {
			ClassOrInterfaceDeclaration classOrInterfaceDeclaration = (ClassOrInterfaceDeclaration) node;
			storeFields(classOrInterfaceDeclaration);
		} else if (node instanceof MethodDeclaration || node instanceof ConstructorDeclaration) {
			BodyDeclaration member = (BodyDeclaration) node;
			this.markedMemberVariablesAndMethodCalls = new HashSet<>();
			int fanOut = calculateFanOut(node);
			if (fanOut >= THRESHOLD) {
				List<Issue> issues = new ArrayList<>();
				String message = createMessage(member, fanOut);
				NameExpr methodHeader = JavaParserHelper.getNameExpr((BodyDeclaration) node);
				issues.add(new FileIssue(this, methodHeader, fileString, message));
				return issues;
			}
		}
		return null;
	}

	protected String createMessage(BodyDeclaration member, int fanOut) {
		String methodID = createMemberID(member);
		return methodID + MESSAGE + fanOut;
	}

	protected String createMemberID(BodyDeclaration member) {
		String memberID = "";
		try {
			ClassOrInterfaceDeclaration type = JavaParserHelper.findNodeClassOrInterfaceDeclaration(member);
			CompilationUnit cu = JavaParserHelper.findNodeCompilationUnit(type);
			PackageDeclaration packageDeclaration = cu.getPackage();
			if (packageDeclaration != null) {
				memberID += packageDeclaration.getName() + ".";
			}
			memberID += type.getName() + ".";
		} catch (NoSuchAncestorFoundException e) {
		}
		if (member instanceof MethodDeclaration) {
			memberID += ((MethodDeclaration) member).getName();
		} else if (member instanceof ConstructorDeclaration) {
			memberID += ((ConstructorDeclaration) member).getName();
		}
		return memberID;
	}

	protected void storeFields(ClassOrInterfaceDeclaration classOrInterfaceDeclaration) {
		this.fields = new HashSet<>();
		for (BodyDeclaration member : classOrInterfaceDeclaration.getMembers()) {
			if (member instanceof FieldDeclaration) {
				FieldDeclaration field = (FieldDeclaration) member;
				for (VariableDeclarator variable : field.getVariables()) {
					this.fields.add(variable.getId().getName());
				}
			}
		}
	}

	protected int calculateFanOut(Node node) {
		List<Issue> issues = new ArrayList<>();
		return calculateFanOut(node, issues);
	}

	protected int calculateFanOut(Node node, List<Issue> issues) {
		int fanOut = 0;
		if (node instanceof AssignExpr) {
			AssignExpr assignExpr = (AssignExpr) node;
			Expression target = assignExpr.getTarget();
			if (target instanceof FieldAccessExpr || isField(target)) {
				if (!isMarked(assignExpr.getTarget())) {
					mark(assignExpr.getTarget());
					fanOut++;
				}
			}
		} else if (node instanceof MethodCallExpr || node instanceof ExplicitConstructorInvocationStmt
				|| node instanceof ObjectCreationExpr) {
			if (!isMarked(node)) {
				mark(node);
				fanOut++;
			}
		}

		for (Node child : node.getChildrenNodes()) {
			fanOut += calculateFanOut(child, issues);
		}
		return fanOut;
	}

	protected boolean isField(Expression target) {
		if (target instanceof NameExpr) {
			String name = ((NameExpr) target).getName();
			if (this.fields.contains(name)) {
				return true;
			}
		}
		return false;
	}

	private boolean isMarked(Node node) {
		return this.markedMemberVariablesAndMethodCalls.contains(node);
	}

	private void mark(Node node) {
		this.markedMemberVariablesAndMethodCalls.add(node);
	}

	@Override
	public String getMeasureElement() {
		return ISSUE_TYPE;
	}

	public Set<String> getFields() {
		return this.fields;
	}

}
