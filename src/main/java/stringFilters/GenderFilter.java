package stringFilters;

import java.util.function.Consumer;

import getters.VideoMetaGetter;

public class GenderFilter implements StringFilter {

	private static final String[] femaleShorts = {"female", "women", "ladies"};
	private static final String[] maleShorts = {"male", "men", "boys"};
	
	private static final String female = "Women";
	private static final String male = "Men";
	
	@Override
	public void filter(String string, Consumer<String> callback) {
		for (String femaleShort : femaleShorts) {
			if(string.toLowerCase().contains(femaleShort)) {
				callback.accept(female);
				return;
			}
		}
		for (String maleShort : maleShorts) {
			if(string.toLowerCase().contains(maleShort)) {
				callback.accept(male);
				return;
			}
		}
		VideoMetaGetter.getOrAsk(retry -> {			
			throw new NullPointerException();
		}, "No Gender found in string (" + female + "/" + male + ")", callback, false, null, false);
	}
}