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

import ish.common.types.MessageStatus;
import ish.common.types.MessageType;
import ish.oncourse.cayenne.PersistentObjectI;

/**
 */
public interface IMessagePerson<C extends IContact, M extends IMessage> extends PersistentObjectI {

	C getContact();

	void setContact(C contact);

	void setMessage(M message);

	void setStatus(MessageStatus status);

	void setType(MessageType type);
}
