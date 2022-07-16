package stringFilters;

import java.util.function.Consumer;

public class DefaultStringFilter implements StringFilter {

	@Override
	public void filter(String string, Consumer<String> callback) {
		callback.accept(string);
	}
}