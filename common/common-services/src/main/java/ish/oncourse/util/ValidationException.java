package ish.oncourse.util;

public class ValidationException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4887170981365226859L;
	private ValidationErrors errors;

	public ValidationException(String message) {
		super(message);
	}

	public ValidationException(ValidationErrors errors) {
		super("Validation failures: " + errors.toString());
		this.errors = errors;
	}

	@Override
	public String toString() {
		return super.toString() + System.getProperty("line.separator")
				+ this.errors;
	}
}
