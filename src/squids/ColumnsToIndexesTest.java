package squids;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.SwitchEntryStmt;
import com.github.javaparser.ast.stmt.SwitchStmt;

import squids.io.IOUtils;

public class ColumnsToIndexesTest {

	private MethodDeclaration method1;
	private SwitchStmt switch1;
	private SwitchEntryStmt case1;
	private String fileString;
	private int[] expected;
	private int[] actual;

	@Before
	public void setUp() throws Exception {
		File file = new File("res/test/ContinuesAndBreaks.java");
		this.fileString = IOUtils.fileToString(file);
		CompilationUnit testCU = JavaParser.parse(file);

		this.method1 = (MethodDeclaration) testCU.getTypes().get(0).getMembers().get(0);
		this.switch1 = (SwitchStmt) testCU.getTypes().get(0).getMembers().get(3).getChildrenNodes().get(1)
				.getChildrenNodes().get(2);
		this.case1 = this.switch1.getEntries().get(0);
	}

	@Test
	public void oneTab() {
		this.expected = new int[] { 36, 413 };
		this.actual = JavaParserHelper.columnsToIndexes(this.fileString, this.method1.getBeginLine(),
				this.method1.getEndLine(), this.method1.getBeginColumn(), this.method1.getEndColumn());
		validate();
	}

	@Test
	public void twoTabs() {
		this.expected = new int[] { 1201, 1828 };
		this.actual = JavaParserHelper.columnsToIndexes(this.fileString, this.switch1.getBeginLine(),
				this.switch1.getEndLine(), this.switch1.getBeginColumn(), this.switch1.getEndColumn());
		validate();
	}

	@Test
	public void threeTabs() {
		this.expected = new int[] { 1221, 1264 };
		this.actual = JavaParserHelper.columnsToIndexes(this.fileString, this.case1.getBeginLine(),
				this.case1.getEndLine(), this.case1.getBeginColumn(), this.case1.getEndColumn());
		validate();
	}

	private void validate() {
		assertEquals(this.expected[0], this.actual[0]);
		assertEquals(this.expected[1], this.actual[1]);
	}

}
