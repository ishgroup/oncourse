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

package ish.oncourse.server.scripting.api;

import ish.oncourse.server.cayenne.Contact;
import ish.oncourse.server.cayenne.Message;
import ish.oncourse.server.cayenne.MessagePerson;
import ish.oncourse.server.cayenne.glue.CayenneDataObject;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;

import java.util.List;
import java.util.function.Consumer;

public class MessageReceived {

    private ObjectContext context;
    private String creatorKey;
    private Contact contact;

    private MessageReceived() {

    }

    public static MessageReceived valueOf(ObjectContext context, String creatorKey) {
        return valueOf(context, creatorKey, null);
    }

    public static MessageReceived valueOf(ObjectContext context, String creatorKey, Contact contact) {
        var getLastMessage = new MessageReceived();
        getLastMessage.context = context;
        getLastMessage.creatorKey = creatorKey;
        getLastMessage.contact = contact;
        return getLastMessage;
    }

    public boolean isPresent() {
        return getMessage() != null;
    }

    public boolean ifPresent(Consumer<CayenneDataObject> auditCollision) {
        var message = getMessage();
        if (message == null) {
            return true;
        } else {
            CayenneDataObject collisionObject = message;
            if (contact != null) {
                collisionObject = message.getMessagePersons().stream()
                        .filter(messagePerson -> messagePerson.getContact().getId().equals(contact.getId()))
                        .findFirst()
                        .get();
            }

            auditCollision.accept(collisionObject);
            return false;
        }
    }

    private Message getMessage() {
        var select = ObjectSelect.query(Message.class)
                .where(Message.CREATOR_KEY.eq(creatorKey))
                .orderBy(Message.ID.desc());

        if (contact != null) {
            select.and(Message.MESSAGE_PERSONS.dot(MessagePerson.CONTACT).dot(Contact.ID).eq(contact.getId()))
                    .prefetch(Message.MESSAGE_PERSONS.joint());
        }

        var messages = select.select(context);
        return messages.isEmpty() ? null : messages.get(0);
    }

}
