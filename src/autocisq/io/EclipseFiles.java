package autocisq.io;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

public class EclipseFiles {

	/**
	 * Recursive method to get a flat list of all sub-files to the resource
	 * provided, or the resource itself if it is a file
	 *
	 * @param resource
	 *            - the resource to search for files in
	 * @param fileEnding
	 *            - the file-ending to search for
	 * @param files
	 *            - the list of files to add to. If null, it will be generated
	 *            anew
	 * @return a flat list of all sub-files to the resource provided, or the
	 *         resource itself if it is a file
	 * @throws CoreException
	 */
	public static List<IFile> getFiles(IResource resource, String fileEnding, List<IFile> files) throws CoreException {
		if (files == null) {
			files = new LinkedList<>();
		}
		if (resource instanceof IContainer) {
			IContainer container = (IContainer) resource;
			IResource[] childResources = container.members();
			for (IResource childResource : childResources) {
				getFiles(childResource, fileEnding, files);
			}

		} else if (resource instanceof IFile) {
			IFile file = (IFile) resource;
			if (file.getName().endsWith(fileEnding)) {
				files.add(file);
			}
		}
		return files;
	}
}
