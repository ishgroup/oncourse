package ish.oncourse.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ValidationErrors implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3675286810916569738L;
	private List<String> failures;

	public ValidationErrors() {
		failures = new ArrayList<String>();
	}

	/**
	 * Add a failure to the validation result.
	 * 
	 * @param failure
	 *            failure to be added. It may not be null.
	 */
	public void addFailure(String failure) {
		if (failure == null) {
			throw new IllegalArgumentException("failure cannot be null.");
		}
		if (!failures.contains(failure)) {
			failures.add(failure);
		}
	}

	/**
	 * Returns true if at least one failure has been added to this result. False
	 * otherwise.
	 */
	public boolean hasFailures() {
		return !failures.isEmpty();
	}

	@Override
	public String toString() {
		StringBuilder ret = new StringBuilder();
		String separator = System.getProperty("line.separator");

		for (String failure : failures) {
			if (ret.length() > 0) {
				ret.append(separator);
			}
			ret.append(failure);
		}

		return ret.toString();
	}
}
