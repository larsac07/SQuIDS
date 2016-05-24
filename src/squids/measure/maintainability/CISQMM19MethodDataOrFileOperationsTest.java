package squids.measure.maintainability;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;

import squids.io.IOUtils;
import squids.measure.MeasureTest;

public class CISQMM19MethodDataOrFileOperationsTest extends MeasureTest {

	private MethodDeclaration method6DbOrIoCalls;
	private MethodDeclaration method7DbOrIoCalls;
	private MethodDeclaration method8DbOrIoCalls;
	private MethodDeclaration methodJavaDbOrIoCalls;
	private MethodDeclaration methodExternalLibraryDbOrIoCalls;
	private String fileString;
	private CompilationUnit cabCU;
	private Map<String, Object> settings;
	private CISQMM19MethodDataOrFileOperations measure;

	@Before
	public void setUp() throws Exception {
		this.issueFinder.getMeasures().clear();
		List<String> dbOrIoClasses = new LinkedList<>();
		dbOrIoClasses.add("java.io.*");
		dbOrIoClasses.add("java.nio.*");
		dbOrIoClasses.add("java.sql.*");
		dbOrIoClasses.add("com.github.javaparser.JavaParser");
		this.settings = new HashMap<>();
		this.settings.put("db_or_io_classes", dbOrIoClasses);
		this.measure = new CISQMM19MethodDataOrFileOperations(this.settings);
		this.issueFinder.putMeasure(this.measure);

		File testFile = new File("res/test/MethodsWithDataOrFileOperations.java");

		this.fileString = IOUtils.fileToString(testFile);

		this.cabCU = JavaParser.parse(testFile);

		this.method6DbOrIoCalls = (MethodDeclaration) this.cabCU.getTypes().get(0).getChildrenNodes().get(12);
		this.method7DbOrIoCalls = (MethodDeclaration) this.cabCU.getTypes().get(0).getChildrenNodes().get(13);
		this.method8DbOrIoCalls = (MethodDeclaration) this.cabCU.getTypes().get(0).getChildrenNodes().get(14);
		this.methodJavaDbOrIoCalls = (MethodDeclaration) this.cabCU.getTypes().get(0).getChildrenNodes().get(15);
		this.methodExternalLibraryDbOrIoCalls = (MethodDeclaration) this.cabCU.getTypes().get(0).getChildrenNodes()
				.get(16);

		this.issueFinder.analyzeNode(this.cabCU, null, this.fileString);
	}

	@Test
	public void skipMethod6DbOrIoCalls() {
		skipIssue(this.method6DbOrIoCalls, this.fileString);
	}

	@Test
	public void findMethod7DbOrIoCalls() {
		findIssue(this.method7DbOrIoCalls, this.fileString);
	}

	@Test
	public void findMethod8DbOrIoCalls() {
		findIssue(this.method8DbOrIoCalls, this.fileString);
	}

	@Test
	public void findMethodJavaDbOrIoCalls() {
		findIssue(this.methodJavaDbOrIoCalls, this.fileString);
	}

	@Test
	public void findMethodExternalLibraryDbOrIoCalls() {
		findIssue(this.methodExternalLibraryDbOrIoCalls, this.fileString);
	}

	@Test
	public void isDbOrIoClassNullReturnsFalse() {
		assertFalse(this.measure.isDbOrIoClass(null));
	}

	@Test
	public void createListsCalledNoProvidedDbOrIoClasses() {
		CISQMM19CreateListsMock mockMeasure = new CISQMM19CreateListsMock(new HashMap<>());
		String expected = "called";
		String actual = mockMeasure.tracker;
		assertEquals(expected, actual);
	}

	@Test
	public void createListsCalledNullSettings() {
		CISQMM19CreateListsMock mockMeasure = new CISQMM19CreateListsMock(null);
		String expected = "called";
		String actual = mockMeasure.tracker;
		assertEquals(expected, actual);
	}

	private class CISQMM19CreateListsMock extends CISQMM19MethodDataOrFileOperations {
		private String tracker;

		public CISQMM19CreateListsMock(Map<String, Object> settings) {
			super(settings);
		}

		@Override
		protected void createLists(List<String> dbOrIoClassesAndPackages) {
			this.tracker = "called";
		}

	}

	@Override
	public String getIssueType() {
		return CISQMM19MethodDataOrFileOperations.ISSUE_TYPE;
	}

}
