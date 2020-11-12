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
package ish.oncourse.common;

import ish.common.types.SystemEventType;
import ish.oncourse.cayenne.PersistentObjectI;

import java.io.Serializable;

/**
 * Generic onCourse system event (e.g. enrolment success/cancel, certificate printed, etc.)
 */
public class SystemEvent implements Serializable {

	private SystemEventType eventType;
	private Object value;

	private SystemEvent(SystemEventType eventType, Object value) {
		this.eventType = eventType;
		setValue(value);
	}

	public SystemEventType getEventType() {
		return eventType;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		// use ObjectIds instead of Cayenne objects to avoid hessian serialization issues
		if (value instanceof PersistentObjectI) {
			this.value = ((PersistentObjectI) value).getObjectId();
		} else {
			this.value = value;
		}
	}

	public static SystemEvent valueOf(SystemEventType eventType, Object value) {
		SystemEvent systemEvent = new SystemEvent(eventType, value);
		return systemEvent;
	}
}
