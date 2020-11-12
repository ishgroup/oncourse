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

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MessageSendingResult implements Serializable {

	private Map<Long, Long> unsentMessagesMap = new HashMap<>();

	public void addUnsentMessage(Long recordId, Long recipientId) {
		unsentMessagesMap.put(recordId, recipientId);
	}

	public Map<Long, Long> getUnsentMessagesMap() {
		return Collections.unmodifiableMap(unsentMessagesMap);
	}

	public boolean isEverythingSent() {
		return unsentMessagesMap.isEmpty();
	}
}
