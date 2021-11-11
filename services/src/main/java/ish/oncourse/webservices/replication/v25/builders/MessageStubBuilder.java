/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.webservices.replication.v25.builders;

import ish.oncourse.model.Message;
import ish.oncourse.model.MessagePerson;
import ish.oncourse.webservices.replication.builders.AbstractWillowStubBuilder;
import ish.oncourse.webservices.v25.stubs.replication.MessageStub;

public class MessageStubBuilder extends AbstractWillowStubBuilder<MessagePerson, MessageStub> {

    @Override
    protected MessageStub createFullStub(MessagePerson entity) {
        MessageStub stub = new MessageStub();
        stub.setNumberOfAttempts(entity.getNumberOfAttempts());
        stub.setContactId(entity.getContact().getId());
        stub.setCreated(entity.getCreated());
        stub.setModified(entity.getModified());
        stub.setDestinationAddress(entity.getDestinationAddress());
        stub.setModified(entity.getModified());
        stub.setResponse(entity.getResponse());
        stub.setStatus(entity.getStatus().getDatabaseValue());
        stub.setTimeOfDelivery(entity.getTimeOfDelivery());
        stub.setType(entity.getType().getDatabaseValue());

        Message message = entity.getMessage();
        stub.setSmsText(message.getSmsText());
        stub.setEmailBody(message.getEmailBody());
        stub.setEmailSubject(message.getEmailSubject());
        return stub;
    }
}
