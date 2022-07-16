package uils;

import java.io.File;

public class Utils {

	public static String elipse(String text, int maxLength) {
		if(text.length() <= maxLength) return text;
		return text.substring(0, maxLength - 3) + "...";
	}
	
	public static String fileElipse(String text, int maxLength) {
		String extension = getFileExtension(text);
		if(text.length() <= maxLength) return text;
		return text.substring(0, maxLength - 3 - extension.length()) + "..." + extension;
	}
	
	public static String getFileNameNoExtension(String fileName) {
		String extension = getFileExtension(fileName);
		return fileName.substring(0, fileName.length() - extension.length() - 1);
	}
	
	public static boolean isFileOneOf(File f, String ... extensions) {
		String fExtension = getFileExtension(f);
		for (String extension : extensions) {
			if(fExtension.toLowerCase().contentEquals(extension)) return true;
		}
		return false;
	}
	
	public static String getFileExtension(File file) {
		return getFileExtension(file.getName());
	}
	
	public static String getFileExtension(String filename) {
		if(filename.length() < 2 || filename.indexOf('.') == -1) return "";
		return filename.substring(filename.lastIndexOf('.') + 1);
	}
	
	public static String getFileName(File file) {
		String filename = file.getName();
		if(filename.indexOf('.') == -1) return filename;
		return filename.substring(0, filename.lastIndexOf('.'));
	}
}
