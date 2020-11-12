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
import ish.oncourse.server.services.AuditService;
import org.apache.cayenne.ObjectContext;

public class NeedToSendEmail {

    private AuditService auditService;
    private KeyCollision collision;
    private String creatorKey;
    private ObjectContext context;
    private Contact contact;

    private NeedToSendEmail() {

    }

    public static NeedToSendEmail valueOf(AuditService auditService, KeyCollision collision, String creatorKey, ObjectContext context, Contact contact) {
        var needToSendEmail = new NeedToSendEmail();
        needToSendEmail.auditService = auditService;
        needToSendEmail.collision = collision;
        needToSendEmail.creatorKey = creatorKey;
        needToSendEmail.context = context;
        needToSendEmail.contact = contact;
        return needToSendEmail;
    }

    public boolean get() {
        var needAudit = KeyCollision.error.equals(collision);

        switch (collision) {
            case drop:
            case error:
                return MessageReceived.valueOf(context , creatorKey, contact).ifPresent(collisionObject -> {
                    if (needAudit) {
                        KeyCollisionAudit.valueOf(auditService, collisionObject, creatorKey, contact).audit();
                    }
                });
            case accept:
            default:
                return true;

        }
    }
}
