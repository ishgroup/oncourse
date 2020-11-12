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
package ish.oncourse.server.integration;

import ish.common.types.SystemEventType;
import ish.oncourse.common.SystemEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * onCourse system event service. Keeps track of all the registered listeners and calls
 * the appropriate ones when new event is posted.
 */
public class EventService {

	private Map<SystemEventType, Set<OnCourseEventListener>> listenerMap = new HashMap<>();

	/**
	 * Post new system event.
	 *
	 * @param event onCourse system event object
	 */
	public void postEvent(SystemEvent event) {
		var listeners = listenerMap.get(event.getEventType());

		if (listeners != null) {
			for (var listener : listeners) {
				listener.dispatchEvent(event);
			}
		}
	}

	/**
	 * Register listener for onCourse system events.
	 *
	 * @param listener listener object
	 * @param eventTypes event types listener needs to handle
	 */
	public void registerListener(OnCourseEventListener listener, SystemEventType... eventTypes) {
		for (var eventType : eventTypes) {
			if (listenerMap.get(eventType) == null) {
				listenerMap.put(eventType, new HashSet<>());
			}

			listenerMap.get(eventType).add(listener);
		}
	}
}
