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
package ish.messaging;

import ish.oncourse.cayenne.PersistentObjectI;
import ish.oncourse.cayenne.SessionInterface;
import ish.oncourse.server.cayenne.Room;

import java.util.List;
import java.util.TimeZone;

public interface ISession extends PersistentObjectI, SessionInterface {

	String COURSE_CLASS_KEY = "courseClass";

	Room getRoom();

	List<? extends ISessionModule> getSessionModules();

	List<? extends ITutorAttendance> getSessionTutors();

	TimeZone getTimeZone();

	Integer getPayAdjustment();

	boolean hasModule(IModule module);

	List<? extends IAttendance> getAttendance();

}
