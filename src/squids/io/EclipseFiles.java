package squids.io;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

public class EclipseFiles {

	/**
	 * Get a flat list of all sub-files to the resource provided, or the
	 * resource itself if it is a file
	 *
	 * @param resource
	 *            - the resource to search for files in
	 * @param fileEnding
	 *            - the file-ending to search for
	 * @param filters
	 *            - the files and folders to ignore (regex)
	 * @return a flat list of all sub-files to the resource provided, or the
	 *         resource itself if it is a file
	 * @throws CoreException
	 */
	public static List<IFile> getFiles(IResource resource, String fileEnding, List<String> filters)
			throws CoreException {
		return getFiles(resource, fileEnding, filters, null);
	}

	/**
	 * Recursive method to get a flat list of all sub-files to the resource
	 * provided, or the resource itself if it is a file
	 *
	 * @param resource
	 *            - the resource to search for files in
	 * @param fileEnding
	 *            - the file-ending to search for
	 * @param filters
	 *            - the files and folders to ignore (regex)
	 * @param files
	 *            - the list of files to add to. If null, it will be generated
	 *            anew
	 * @return a flat list of all sub-files to the resource provided, or the
	 *         resource itself if it is a file
	 * @throws CoreException
	 */
	public static List<IFile> getFiles(IResource resource, String fileEnding, List<String> filters, List<IFile> files)
			throws CoreException {
		if (files == null) {
			files = new LinkedList<>();
		}
		String path = iResourceToFile(resource).getPath().toString();
		if (!IOUtils.matchesFilters(path, filters)) {
			if (resource instanceof IContainer) {
				IContainer container = (IContainer) resource;
				IResource[] childResources = container.members();
				for (IResource childResource : childResources) {
					getFiles(childResource, fileEnding, filters, files);
				}

			} else if (resource instanceof IFile) {
				IFile file = (IFile) resource;
				if (file.getName().endsWith(fileEnding)) {
					files.add(file);
				}
			}
		}
		return files;
	}

	/**
	 * Get the file associated with an IResource object
	 *
	 * @param iResource
	 *            - an IResource object
	 * @return the file associated with the IResource object
	 */
	public static File iResourceToFile(IResource iResource) {
		return iResource.getLocation().toFile();
	}
}
