package uils;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.List;

public class FileExtensionFilter implements FilenameFilter {

	List<String> allowedExtensions;
	
	public FileExtensionFilter(String... allowedExtensions) {
		super();
		this.allowedExtensions = Arrays.asList(allowedExtensions);
	}
	
	@Override
	public boolean accept(File dir, String name) {
		String extension = Utils.getFileExtension(name);
		for (String allowedExt : allowedExtensions) {
			if(allowedExt.toLowerCase().contentEquals(extension.toLowerCase())) return true;
		}
		return false;
	}

}
