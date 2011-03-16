package ish.oncourse.util;

public class ValidationFailure {

	private String message;

	private ValidationFailureType type;

	public ValidationFailure(String message, ValidationFailureType type) {
		this.message = message;
		this.type = type;
	}

	/**
	 * Returns string representation of error: {@see #message}.
	 */
	@Override
	public String toString() {
		return message;
	}

	/**
	 * @return the type
	 */
	public ValidationFailureType getType() {
		return type;
	}

}
