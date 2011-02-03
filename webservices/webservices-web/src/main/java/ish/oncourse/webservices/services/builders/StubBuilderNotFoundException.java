/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ish.oncourse.webservices.services.builders;

/**
 * Exception thrown when a builder for an Entity could not be located.
 *
 * @author marek
 */
public class StubBuilderNotFoundException extends Exception {

	private String entityName;


	/**
	 * @param message
	 * @param entityName
	 */
	public StubBuilderNotFoundException(String message, String entityName) {
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
