package autocisq.measure.maintainability;

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

import autocisq.IssueFinder;
import autocisq.io.IOUtils;
import autocisq.measure.MeasureTest;

public class Method7OrMoreDataOrFileOperationsTest extends MeasureTest {

	private List<String> dbOrIoClasses;
	private MethodDeclaration method6DbOrIoCalls;
	private MethodDeclaration method7DbOrIoCalls;
	private MethodDeclaration method8DbOrIoCalls;
	private MethodDeclaration methodJavaDbOrIoCalls;
	private MethodDeclaration methodExternalLibraryDbOrIoCalls;
	private String fileString;
	private CompilationUnit cabCU;
	private Map<String, Object> settings;
	
	@Before
	public void setUp() throws Exception {
		IssueFinder issueFinder = IssueFinder.getInstance();
		issueFinder.getMeasures().clear();
		List<String> dbOrIoClasses = new LinkedList<>();
		dbOrIoClasses.add("java.io.File");
		dbOrIoClasses.add("java.nio.file.Files");
		dbOrIoClasses.add("java.sql.Connection");
		dbOrIoClasses.add("java.sql.DriverManager");
		dbOrIoClasses.add("java.sql.PreparedStatement");
		dbOrIoClasses.add("java.sql.Statement");
		dbOrIoClasses.add("com.github.javaparser.JavaParser");
		this.settings = new HashMap<>();
		this.settings.put("db_or_io_classes", dbOrIoClasses);
		issueFinder.putMeasure(new Method7OrMoreDataOrFileOperations(this.settings));
		
		File testFile = new File("res/test/MethodsWithDataOrFileOperations.java");

		this.fileString = IOUtils.fileToString(testFile);

		this.cabCU = JavaParser.parse(testFile);

		this.method6DbOrIoCalls = (MethodDeclaration) this.cabCU.getTypes().get(0).getChildrenNodes().get(12);
		this.method7DbOrIoCalls = (MethodDeclaration) this.cabCU.getTypes().get(0).getChildrenNodes().get(13);
		this.method8DbOrIoCalls = (MethodDeclaration) this.cabCU.getTypes().get(0).getChildrenNodes().get(14);
		this.methodJavaDbOrIoCalls = (MethodDeclaration) this.cabCU.getTypes().get(0).getChildrenNodes().get(15);
		this.methodExternalLibraryDbOrIoCalls = (MethodDeclaration) this.cabCU.getTypes().get(0).getChildrenNodes()
				.get(16);

		this.dbOrIoClasses = new LinkedList<>();
		this.dbOrIoClasses.add("java.io.File");
		this.dbOrIoClasses.add("java.nio.file.Files");
		this.dbOrIoClasses.add("java.sql.Connection");
		this.dbOrIoClasses.add("java.sql.DriverManager");
		this.dbOrIoClasses.add("java.sql.PreparedStatement");
		this.dbOrIoClasses.add("java.sql.Statement");
		this.dbOrIoClasses.add("com.github.javaparser.JavaParser");
		IssueFinder.getInstance().analyzeNode(this.cabCU, null, this.fileString);
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
	
	@Override
	public String getIssueType() {
		return Method7OrMoreDataOrFileOperations.ISSUE_TYPE;
	}

}
