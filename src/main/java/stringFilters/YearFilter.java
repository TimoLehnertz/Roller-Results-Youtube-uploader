package stringFilters;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import getters.VideoMetaGetter;

public class YearFilter implements StringFilter {

	@Override
	public void filter(String string, Consumer<String> callback) {
		List<Integer> numbers = new ArrayList<>();
		String current = "";
		for (int i = 0; i < string.length(); i++) {
			char c = string.charAt(i);
			if(isDigit(c)) {
				current += c;
			} else if(current.length() > 0){
				numbers.add(Integer.parseInt(current));
				current = "";
			}
		}
		numbers.removeIf(number -> number > 2100 || number < 1900);
		VideoMetaGetter.getOrAsk(retry -> {			
			return numbers.get(0) + "";
		}, "No years found in string", callback, false, null, true);
	}
	
	private boolean isDigit(char c) {
		char[] digits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
		for (char d : digits) {
			if(d == c) return true;
		}
		return false;
	}
}