package ish.oncourse.webservices.exception;

public class UpdaterNotFoundException extends RuntimeException {
	
	private String entityName;


	/**
	 * @param message
	 * @param entityName
	 */
	public UpdaterNotFoundException(String message, String entityName) {
		super(message);
		this.entityName = entityName;
	}

	/**
	 * @return name of entity for which a builder retrieval was attempted.
	 */
	public String getEntityName() {
		return entityName;
	}
}
