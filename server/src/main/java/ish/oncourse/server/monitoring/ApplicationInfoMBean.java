/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.monitoring;

public interface ApplicationInfoMBean {

	/**
	 * Returns number of users currently logged in.
	 */
	int getLoggedInUsersCount();

	/**
	 * Returns server's SSL port.
	 */
	int getSslPort();

	/**
	 * Returns IP address jetty is listening to.
	 */
	String getIpAddress();

	/**
	 * Returns total number of Enrolment records existing in the system regardless of their status.
	 */
	int getEnrolmentsCount();

	String getVersion();
}
