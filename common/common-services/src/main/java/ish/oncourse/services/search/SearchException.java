package ish.oncourse.services.search;

public class SearchException extends RuntimeException {
	private static final long serialVersionUID = 7244912083510456164L;

	public SearchException(String message, Throwable cause) {
		super(message, cause);
	}

	public SearchException(String message) {
		super(message);
	}

	public SearchException(Throwable cause) {
		super(cause);
	}
}