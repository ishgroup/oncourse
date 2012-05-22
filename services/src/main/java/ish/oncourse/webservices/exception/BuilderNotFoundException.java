/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ish.oncourse.webservices.exception;

/**
 * Exception thrown when a builder for an Entity could not be located.
 *
 * @author marek
 */
public class BuilderNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 3971215992430028578L;
	private String entityName;


	/**
	 * @param message
	 * @param entityName
	 */
	public BuilderNotFoundException(String message, String entityName) {
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
