package ish.oncourse.webservices.replication.updaters;

import ish.oncourse.model.Contact;
import ish.oncourse.model.Message;
import ish.common.types.TypesUtil;
import ish.oncourse.model.MessagePerson;
import ish.oncourse.model.Student;
import ish.oncourse.model.Tutor;
import ish.oncourse.webservices.v4.stubs.replication.MessagePersonStub;
import ish.common.types.MessageStatus;
import ish.common.types.MessageType;

public class MessagePersonUpdater extends AbstractWillowUpdater<MessagePersonStub, MessagePerson> { 
	/* (non-Javadoc)
	 * @see ish.oncourse.webservices.replication.updaters.AbstractWillowUpdater#updateEntity(ish.oncourse.webservices.v4.stubs.replication.ReplicationStub, ish.oncourse.model.Queueable, ish.oncourse.webservices.replication.updaters.RelationShipCallback)
	 */
	@Override
	protected void updateEntity(MessagePersonStub stub, MessagePerson entity,
			RelationShipCallback callback) {
		entity.setContact(callback.updateRelationShip(stub.getContactId(), Contact.class));
		entity.setCreated(stub.getCreated());
		entity.setDestinationAddress(stub.getDestinationAddress());
		Message message = callback.updateRelationShip(stub.getMessageId(), Message.class);
		if (message != null) {
			entity.setMessage(message);
		} else {
			final String errorMessage = String.format("No message object found for message person with angelid:%s willowid:%s  and messageid:%s", 
				stub.getAngelId(), entity.getId(), stub.getMessageId());
			throw new UpdaterException(errorMessage);
		}
		entity.setModified(stub.getModified());
		entity.setNumberOfAttempts(stub.getNumberOfAttempts());
		entity.setResponse(stub.getResponse());
		entity.setStatus(TypesUtil.getEnumForDatabaseValue(stub.getStatus(), MessageStatus.class));
		if (stub.getStudentId() != null) {
			entity.setStudent(callback.updateRelationShip(stub.getStudentId(), Student.class));
		}
		else {
			entity.setTutor(callback.updateRelationShip(stub.getTutorId(), Tutor.class));
		}
		entity.setTimeOfDelivery(stub.getTimeOfDelivery());
		MessageType type = TypesUtil.getEnumForDatabaseValue(stub.getType(), MessageType.class);
		if (MessageType.SMS.equals(type)) {
			entity.setType(type);
		} else {
			throw new UpdaterException(String.format("Only sms type message person should be replicated but received %s with angelid:%s willowid:%s", 
				type, stub.getAngelId(), entity.getId()));
		}
	}
}


