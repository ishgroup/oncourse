/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.server.replication.builders

/**
 */
class BuilderNotFoundException extends RuntimeException {

	private String entityName

	/**
	 * @param message
	 * @param entityName
	 */
	BuilderNotFoundException(final String message, final String entityName) {
		super(message)
		this.entityName = entityName
	}

	/**
	 * @return name of entity for which a builder retrieval was attempted.
	 */
	String getEntityName() {
		return entityName
	}
}
