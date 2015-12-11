package autocisq.models;

import java.io.File;
import java.util.List;

import com.github.javaparser.ast.CompilationUnit;

/**
 * JavaResource is a POJO holder class for the CompilationUnit, File, file
 * string and file string as individual lines. These are closely associated
 * objects, but are hard to keep track of during analysis without this class.
 *
 * @author Lars A. V. Cabrera
 *
 */
public class JavaResource {

	private CompilationUnit compilationUnit;
	private File file;
	private String fileString;
	private List<String> fileStringLines;

	public JavaResource(CompilationUnit compilationUnit, File file, String fileString, List<String> fileStringLines) {
		this.compilationUnit = compilationUnit;
		this.file = file;
		this.fileString = fileString;
		this.fileStringLines = fileStringLines;
	}

	public CompilationUnit getCompilationUnit() {
		return this.compilationUnit;
	}

	public void setCompilationUnit(CompilationUnit compilationUnit) {
		this.compilationUnit = compilationUnit;
	}

	public File getFile() {
		return this.file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public String getFileString() {
		return this.fileString;
	}

	public void setFileString(String fileString) {
		this.fileString = fileString;
	}

	public List<String> getFileStringLines() {
		return this.fileStringLines;
	}

	public void setFileStringLines(List<String> fileStringLines) {
		this.fileStringLines = fileStringLines;
	}

}
