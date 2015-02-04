package ish.oncourse.webservices.replication.v9.builders;

import ish.oncourse.model.MessagePerson;
import ish.oncourse.webservices.replication.builders.AbstractWillowStubBuilder;
import ish.oncourse.webservices.v9.stubs.replication.MessagePersonStub;

public class MessagePersonStubBuilder extends AbstractWillowStubBuilder<MessagePerson, MessagePersonStub> {

	@Override
	protected MessagePersonStub createFullStub(MessagePerson entity) {
		MessagePersonStub stub = new MessagePersonStub();
		stub.setNumberOfAttempts(entity.getNumberOfAttempts());
		stub.setContactId(entity.getContact().getId());
		stub.setCreated(entity.getCreated());
		stub.setModified(entity.getModified());
		stub.setDestinationAddress(entity.getDestinationAddress());
		stub.setMessageId(entity.getMessage().getId());
		stub.setModified(entity.getModified());
		stub.setResponse(entity.getResponse());
		stub.setStatus(entity.getStatus().getDatabaseValue());
		if (entity.getStudent() != null) {
			stub.setStudentId(entity.getStudent().getId());
		} else if (entity.getTutor() != null) {
			stub.setTutorId(entity.getTutor().getId());
		}
		stub.setTimeOfDelivery(entity.getTimeOfDelivery());
		stub.setType(entity.getType().getDatabaseValue());
		return stub;
	}
}
