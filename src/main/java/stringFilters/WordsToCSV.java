package stringFilters;

import java.util.function.Consumer;

public class WordsToCSV implements StringFilter {

	@Override
	public void filter(String string, Consumer<String> callback) {
		String[] words = string.split(" ");
		String out = "";
		for (String word : words) {
			out += "," + word;
		}
		callback.accept(out + ",");
	}
}