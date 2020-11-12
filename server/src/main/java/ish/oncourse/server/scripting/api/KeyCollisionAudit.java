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

import ish.oncourse.types.AuditAction;
import ish.oncourse.server.cayenne.Contact;
import ish.oncourse.server.cayenne.glue.CayenneDataObject;
import ish.oncourse.server.services.AuditService;

public class KeyCollisionAudit {

    private static final String MESSAGE = "Message with key '%s' is already sent";
    private static final String MESSAGE_PERSON = "Message with key '%s' is already sent for contact %s (%s); contact.id = %d";

    private AuditService auditService;

    private CayenneDataObject collisionObject;
    private String creatorKey;
    private Contact contact;

    private KeyCollisionAudit(){

    }

    public void audit() {
        String message;
        if (contact == null) {
            message = String.format(MESSAGE, creatorKey);
        } else {
            message = String.format(MESSAGE_PERSON, creatorKey, contact.getFullName(), contact.getEmail(), contact.getId());
        }

        auditService.submit(collisionObject, AuditAction.COLLISION, message);
    }

    public static KeyCollisionAudit valueOf(AuditService auditService, CayenneDataObject collisionObject,String creatorKey){
        return KeyCollisionAudit.valueOf(auditService, collisionObject, creatorKey,null);
    }

    public static KeyCollisionAudit valueOf(AuditService auditService, CayenneDataObject collisionObject, String creatorKey, Contact contact){
        var keyCollisionAudit = new KeyCollisionAudit();
        keyCollisionAudit.auditService = auditService;
        keyCollisionAudit.collisionObject = collisionObject;
        keyCollisionAudit.creatorKey = creatorKey;
        keyCollisionAudit.contact = contact;
        return keyCollisionAudit;
    }
}
