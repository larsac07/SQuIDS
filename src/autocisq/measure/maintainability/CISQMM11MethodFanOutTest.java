package autocisq.measure.maintainability;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Expression;

import autocisq.io.IOUtils;
import autocisq.measure.MeasureTest;

public class CISQMM11MethodFanOutTest extends MeasureTest {

	private CompilationUnit personCU;
	private List<CompilationUnit> cus;
	private ConstructorDeclaration constructorFanOut10;
	private MethodDeclaration functionFanOut12;
	private MethodDeclaration functionFanOut11;
	private MethodDeclaration functionFanOut10;
	private MethodDeclaration functionFanOut9;
	private String fileString;
	private CISQMM11MethodFanOut measure;

	@Before
	public void setUp() throws Exception {
		this.measure = new CISQMM11MethodFanOut(new HashMap<>());
		this.issueFinder.getMeasures().clear();
		this.issueFinder.putMeasure(this.measure);

		File testFile = new File("res/test/Person.java");

		this.fileString = IOUtils.fileToString(testFile);

		this.personCU = JavaParser.parse(testFile);

		this.constructorFanOut10 = (ConstructorDeclaration) this.personCU.getTypes().get(0).getChildrenNodes().get(10);
		this.functionFanOut12 = (MethodDeclaration) this.personCU.getTypes().get(0).getChildrenNodes().get(19);
		this.functionFanOut11 = (MethodDeclaration) this.personCU.getTypes().get(0).getChildrenNodes().get(20);
		this.functionFanOut10 = (MethodDeclaration) this.personCU.getTypes().get(0).getChildrenNodes().get(21);
		this.functionFanOut9 = (MethodDeclaration) this.personCU.getTypes().get(0).getChildrenNodes().get(22);

		this.cus = new ArrayList<>();
		this.cus.add(this.personCU);
	}

	@Test
	public void findConstructorWithFanOut10() {
		findIssue(this.constructorFanOut10, this.fileString);
	}

	@Test
	public void findMethodWithFanOut12() {
		findIssue(this.functionFanOut12, this.fileString);
	}

	@Test
	public void findMethodWithFanOut11() {
		findIssue(this.functionFanOut11, this.fileString);
	}

	@Test
	public void findMethodWithFanOut10() {
		findIssue(this.functionFanOut10, this.fileString);
	}

	@Test
	public void skipMethodWithFanOut9() {
		skipIssue(this.functionFanOut9, this.fileString);
	}

	@Test
	public void testNodeIsClass() {
		ClassOrInterfaceDeclaration personClass = (ClassOrInterfaceDeclaration) this.personCU.getTypes().get(0);
		List<String> tracker = new ArrayList<>();
		this.measure = new CISQMM11StoreFieldsMock(new HashMap<>(), tracker);
		this.measure.analyzeNode(personClass, this.fileString, this.cus);
		String expected = "called";
		String actual = tracker.get(0);
		assertEquals(expected, actual);
	}

	private class CISQMM11StoreFieldsMock extends CISQMM11MethodFanOut {

		private List<String> tracker;

		public CISQMM11StoreFieldsMock(Map<String, Object> settings, List<String> tracker) {
			super(settings);
			this.tracker = tracker;
		}

		@Override
		protected void storeFields(ClassOrInterfaceDeclaration classOrInterfaceDeclaration) {
			this.tracker.add("called");
		}
	}

	@Test
	public void testStoreFields() {
		ClassOrInterfaceDeclaration personClass = (ClassOrInterfaceDeclaration) this.personCU.getTypes().get(0);
		this.measure.storeFields(personClass);
		int expected = 12;
		int actual = this.measure.getFields().size();
		assertEquals(expected, actual);
	}

	@Test
	public void testIsField() {
		Expression target = (Expression) this.personCU.getTypes().get(0).getMembers().get(17).getChildrenNodes().get(11)
				.getChildrenNodes().get(0).getChildrenNodes().get(0).getChildrenNodes().get(0);
		this.issueFinder.analyzeNode(this.personCU, null, "");
		assertTrue(this.measure.isField(target));
	}

	@Override
	public String getIssueType() {
		return CISQMM11MethodFanOut.ISSUE_TYPE;
	}
}
