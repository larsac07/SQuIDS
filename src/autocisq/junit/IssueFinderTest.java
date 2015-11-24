package autocisq.junit;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;

import autocisq.IssueFinder;

public class IssueFinderTest {

	private String astString = "";
	private String fileString = "";
	private CompilationUnit compilationUnit;

	@Before
	public void setUp() throws Exception {
		File testFile = new File("res/test/EntropyManualCalculator.java");
		List<String> lines = Files.readAllLines(testFile.toPath());
		String nl = System.lineSeparator();
		for (String line : lines) {
			this.fileString += line + nl;
		}
		this.compilationUnit = JavaParser.parse(testFile);
		this.astString = this.compilationUnit.toString();
	}

	@Test
	public void testColumnsToIndexes() {
		int errorStartLine = 1;
		int errorEndLine = 8;
		int[] fileIndexes = IssueFinder.columnsToIndexes(this.fileString, errorStartLine, errorEndLine, 1, 2);
		int[] astIndexes = IssueFinder.columnsToIndexes(this.astString, errorStartLine, errorEndLine, 1, 2);
		System.out.println(this.fileString);
		System.out.println(this.astString);
		System.out.println("startIndex:" + fileIndexes[0] + " endIndex:" + fileIndexes[1]);
		System.out.println("startIndex:" + astIndexes[0] + " endIndex:" + astIndexes[1]);
		assertEquals(fileIndexes[0], astIndexes[0]);
		assertEquals(fileIndexes[1], astIndexes[1]);
	}

}
