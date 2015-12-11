package autocisq.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public abstract class IOUtils {

	public static String fileToString(File file) {
		return fileToString(file.toPath());
	}

	public static String fileToString(Path file) {
		String fileString = "";
		List<String> lines = fileToStringLines(file);
		String nl = System.lineSeparator();
		for (String line : lines) {
			fileString += line + nl;
		}

		return fileString;
	}

	public static List<String> fileToStringLines(File file) {
		return fileToStringLines(file.toPath());
	}

	public static List<String> fileToStringLines(Path file) {
		List<String> lines = null;
		try {
			lines = Files.readAllLines(file);
		} catch (FileNotFoundException e) {
			System.err.println(e.getClass().getName() + ": Could not find file " + file.toAbsolutePath().toString());
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println(e.getClass().getName() + ": Could not find file " + file.toAbsolutePath().toString());
			e.printStackTrace();
		}
		return lines;
	}
}