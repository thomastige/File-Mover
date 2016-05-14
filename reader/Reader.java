package reader;

import java.io.File;
import java.io.IOException;

public class Reader {

	private String prefix="";
	private String separator="_";
	private String sourceFolder;
	private String destFolder;
	
	public Reader(String sourceFolder, String destFolder){
		this.sourceFolder = sourceFolder;
		this.destFolder = destFolder;
	}
	public Reader(String sourceFolder, String destFolder, String prefix){
		this.sourceFolder = sourceFolder;
		this.destFolder = destFolder;
		this.prefix = prefix;
	}
	public Reader(String sourceFolder, String destFolder, String prefix, String separator){
		this.sourceFolder = sourceFolder;
		this.destFolder = destFolder;
		this.prefix = prefix;
		this.separator = separator;
	}
	
	public String parseFolder() throws IOException{
		StringBuilder result = new StringBuilder();
		File folder = new File(sourceFolder);
		if (folder.isDirectory()){
			String[] list = folder.list();
			for (int i=0; i<list.length; ++i){
				String name = list[i];
				if (name.startsWith(prefix)){
					
					StringBuilder destPathBuilder = new StringBuilder();
					String nameWithoutPrefix = name.substring(prefix.length());
					
					String[] path = nameWithoutPrefix.split(separator);
					for (int j=0; j<path.length; ++j){
						destPathBuilder.append(File.separator);
						destPathBuilder.append(path[j]);
					}
					String newPath = destFolder + destPathBuilder.toString();
					
					File file = new File(sourceFolder + File.separator + name);
					File newFile = new File(newPath);
					newFile.getParentFile().mkdirs();
					if (newFile.exists()){
						newFile.delete();
					}
					result.append(file + "\n");
					file.renameTo(newFile);
				}
			}
			
		}
		return result.toString();
	}
	
}