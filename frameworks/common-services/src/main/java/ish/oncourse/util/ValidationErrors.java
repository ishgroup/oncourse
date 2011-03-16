package ish.oncourse.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ValidationErrors implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3675286810916569738L;
	private List<ValidationFailure> failures;

	public ValidationErrors() {
		failures = new ArrayList<ValidationFailure>();
	}

	/**
	 * Add a failure to the validation result.
	 * 
	 * @param failure
	 *            failure to be added. It may not be null.
	 */
	public void addFailure(String failure, ValidationFailureType type) {
		if (failure == null || type == null) {
			throw new IllegalArgumentException("failure message and type cannot be null.");
		}
		ValidationFailure validationFailure = new ValidationFailure(failure, type);
		if (!failures.contains(validationFailure)) {
			failures.add(validationFailure);
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

		for (ValidationFailure failure : failures) {
			if (ret.length() > 0) {
				ret.append(separator);
			}
			ret.append(failure);
		}

		return ret.toString();
	}

	public void clear() {
		failures = new ArrayList<ValidationFailure>();
	}

	public boolean contains(String error) {
		for (ValidationFailure failure : failures) {
			if (failure.toString().equals(error)) {
				return true;
			}
		}
		return false;
	}

	public int getSize() {
		return failures.size();
	}

	public boolean hasSyntaxFailures() {
		return hasSpecificFailures(ValidationFailureType.SYNTAX);
	}

	public boolean hasContentNotFoundFailures() {
		return hasSpecificFailures(ValidationFailureType.CONTENT_NOT_FOUND);
	}

	private boolean hasSpecificFailures(ValidationFailureType type) {
		for (ValidationFailure failure : failures) {
			if (failure.getType() == type) {
				return true;
			}
		}
		return false;
	}

	public void appendErrors(ValidationErrors errors) {
		failures.addAll(errors.failures);
	}

}
