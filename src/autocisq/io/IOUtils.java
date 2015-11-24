package autocisq.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.eclipse.core.resources.IFile;

public abstract class IOUtils {

	public static String fileToString(IFile file) {
		return fileToString(EclipseFiles.iFileToFile(file));
	}

	public static String fileToString(File file) {
		return fileToString(file.toPath());
	}

	public static String fileToString(Path file) {
		String fileString = "";
		try {
			List<String> lines = Files.readAllLines(file);
			String nl = System.lineSeparator();
			for (String line : lines) {
				fileString += line + nl;
			}
		} catch (FileNotFoundException e) {
			System.err.println(e.getClass().getName() + ": Could not find file " + file.toAbsolutePath().toString());
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println(e.getClass().getName() + ": Could not find file " + file.toAbsolutePath().toString());
			e.printStackTrace();
		}
		return fileString;
	}
}