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

import ish.common.types.MessageStatus;
import ish.oncourse.server.cayenne.Message;
import ish.oncourse.server.messaging.MailDeliveryParam;
import org.apache.cayenne.ObjectContext;
import org.apache.commons.lang3.StringUtils;

public class MessageForSmtp {

    private ObjectContext context;
    private String creatorKey;
    private MailDeliveryParam param;
    private boolean batchIsOver;

    private MessageForSmtp() {

    }

    public static MessageForSmtp valueOf(ObjectContext context, String creatorKey,
                                         MailDeliveryParam param, boolean batchIsOver) {
        var messageForSmtp = new MessageForSmtp();
        messageForSmtp.context = context;
        messageForSmtp.creatorKey = creatorKey;
        messageForSmtp.param = param;
        messageForSmtp.batchIsOver = batchIsOver;
        return messageForSmtp;
    }

    public void create() {
        if (StringUtils.isNotBlank(creatorKey)) {
            var message = context.newObject(Message.class);
            message.setCreatorKey(creatorKey);
            message.setEmailFrom(param.getGetFrom().get().getAddress());
            message.setEmailSubject(param.getGetSubject().get());
            message.setEmailBody(param.getGetContent().getGetEmailPlainBody().get());
            message.setEmailHtmlBody(param.getGetContent().getGetEmailHtmlBody().get());

            var status = batchIsOver ? MessageStatus.FAILED : MessageStatus.SENT;
            message.setStatus(status);

            context.commitChanges();
        }
    }
}
