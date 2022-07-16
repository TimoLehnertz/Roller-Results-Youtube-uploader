package stringFilters;

import java.util.function.Consumer;

public interface StringFilter {

	void filter(String string, Consumer<String> callback);
}