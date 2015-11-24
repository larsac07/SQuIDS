package autocisq.junit;

import static org.junit.Assert.assertEquals;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaCore;
import org.junit.Before;
import org.junit.Test;

import autocisq.IssueFinder;

public class IssueFinderTest {

	private String astString = "";
	private String fileString = "";
	private ICompilationUnit compilationUnit;

	@Before
	public void setUp() throws Exception {
		String projectName = "aiShortbytes";
		IFile testIFile = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName)
				.getFile("EntropyManualCalculator.java");
		List<String> lines = Files.readAllLines(Paths.get("res/test/EntropyManualCalculator.java"));
		String nl = System.lineSeparator();
		for (String line : lines) {
			this.fileString += line + nl;
		}
		this.compilationUnit = JavaCore.createCompilationUnitFrom(testIFile);
		this.astString = this.compilationUnit.getSource();
	}

	@Test
	public void testColumnsToIndexes() {
		int errorStartLine = 1;
		int errorEndLine = 11;
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
