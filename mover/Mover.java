package mover;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Mover {

	private String prefix = "";
	private String separator = "_";
	private String DUPLICATE_FILE_SUFFIX = " (#)";
	private String sourceFolder;
	private String destFolder;

	public Mover(String sourceFolder, String destFolder) {
		this.sourceFolder = sourceFolder;
		this.destFolder = destFolder;
	}

	public Mover(String sourceFolder, String destFolder, String prefix) {
		this.sourceFolder = sourceFolder;
		this.destFolder = destFolder;
		this.prefix = prefix;
	}

	public Mover(String sourceFolder, String destFolder, String prefix, String separator) {
		this.sourceFolder = sourceFolder;
		this.destFolder = destFolder;
		this.prefix = prefix;
		this.separator = separator;
	}

	public String parseFolder() throws IOException {
		StringBuilder result = new StringBuilder();
		File folder = new File(sourceFolder);
		if (folder.isDirectory()) {
			String[] list = folder.list();
			for (int i = 0; i < list.length; ++i) {
				String name = list[i];
				if (name.startsWith(prefix) && !isNameDuplicate(name)) {
					StringBuilder destPathBuilder = new StringBuilder();
					String nameWithoutPrefix = name.substring(prefix.length());

					String[] path = nameWithoutPrefix.split(separator);
					for (int j = 0; j < path.length; ++j) {
						destPathBuilder.append(File.separator);
						destPathBuilder.append(path[j]);
					}
					String newPath = destFolder + destPathBuilder.toString();

					File file = new File(sourceFolder + File.separator + name);
					removeDuplicates(file, list);
					File newFile = new File(newPath);
					newFile.getParentFile().mkdirs();
					Merger merger = new Merger(file.getPath(), newFile.getPath());
					if (newFile.exists()) {
						newFile.delete();
					}
					cleanUp(newFile.getName(), destFolder);
					result.append(file + "\n");
//					file.renameTo(newFile);
					merger.mergeIntoDest();
				}
			}

		}
		return result.toString();
	}

	private boolean isNameDuplicate(String name) {
		String firstPart = " (";
		String secondPart = ").txt";
		int firstPartIndex = name.lastIndexOf(firstPart);
		int secondPartIndex = name.lastIndexOf(secondPart);
		if (name.contains(firstPart) && name.endsWith(secondPart)
				&& isNumber(name.substring(firstPartIndex + 2, secondPartIndex))) {
			return true;
		}
		return false;
	}

	private void removeDuplicates(File file, String[] fileList) {
		String fileNameWithoutExtension = file.getName().replace(".txt", "");
		List<File> duplicates = new ArrayList<File>();
		for (int i = 0; i < fileList.length; ++i) {
			if (fileList[i].startsWith(fileNameWithoutExtension)) {
				duplicates.add(new File(file.getParentFile() + File.separator + fileList[i]));
			}
		}
		if (duplicates.size() > 1) {
			Iterator<File> it = duplicates.iterator();
			String goodFileName = fileNameWithoutExtension
					+ DUPLICATE_FILE_SUFFIX.replace("#", duplicates.size() - 1 + "") + ".txt";
			File goodFile = null;
			while (it.hasNext()) {
				File currFile = it.next();
				if (!currFile.getName().equals(goodFileName)) {
					currFile.delete();
				} else {
					goodFile = currFile;
				}
			}
			if (goodFile != null) {
				goodFile.renameTo(new File(file.getParentFile() + File.separator + fileNameWithoutExtension + ".txt"));
			}
		}
	}

	public void cleanUp(String fileName, String location) {
		File rootFolder = new File(location);
		if (rootFolder.isDirectory()) {
			File[] contents = rootFolder.listFiles();
			for (int i = 0; i < contents.length; ++i) {
				if (contents[i].isDirectory()) {
					cleanUp(fileName, contents[i].getPath());
				} else {
					if (contents[i].getName().equals(fileName)) {
						contents[i].delete();
					}
				}
			}
		}
	}

	private boolean isNumber(String value) {
		try {
			Integer.parseInt(value);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}
}