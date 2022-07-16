package main;

@FunctionalInterface
public interface ProgressListener {

	/**
	 * @param progress smalle 0 shows error, 0.5 equals 50%, greater or equal 1 means succsess
	 */
	void progressChanged(double progress);
}
