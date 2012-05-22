package ish.oncourse.webservices.exception;

public class UpdaterNotFoundException extends RuntimeException {	
	private static final long serialVersionUID = -2266471072981378627L;
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
