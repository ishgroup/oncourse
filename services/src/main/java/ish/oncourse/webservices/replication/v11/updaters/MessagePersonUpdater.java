package ish.oncourse.webservices.replication.v11.updaters;

import ish.common.types.MessageStatus;
import ish.common.types.MessageType;
import ish.common.types.TypesUtil;
import ish.oncourse.model.Contact;
import ish.oncourse.model.Message;
import ish.oncourse.model.MessagePerson;
import ish.oncourse.model.Student;
import ish.oncourse.model.Tutor;
import ish.oncourse.webservices.replication.updaters.AbstractWillowUpdater;
import ish.oncourse.webservices.replication.updaters.RelationShipCallback;
import ish.oncourse.webservices.replication.updaters.UpdaterException;
import ish.oncourse.webservices.v11.stubs.replication.MessagePersonStub;

public class MessagePersonUpdater extends AbstractWillowUpdater<MessagePersonStub, MessagePerson> { 

	@Override
	protected void updateEntity(MessagePersonStub stub, MessagePerson entity, RelationShipCallback callback) {
		entity.setContact(callback.updateRelationShip(stub.getContactId(), Contact.class));
		entity.setCreated(stub.getCreated());
		entity.setDestinationAddress(stub.getDestinationAddress());

		if (stub.getMessageId() == null) {
			final String errorMessage = String.format("No message object found for message person with angelid:%s willowid:%s  and messageid:%s",
				stub.getAngelId(), entity.getId(), stub.getMessageId());
			throw new UpdaterException(errorMessage);
		}
		entity.setMessage(callback.updateRelationShip(stub.getMessageId(), Message.class));

		entity.setModified(stub.getModified());
		entity.setNumberOfAttempts(stub.getNumberOfAttempts());
		entity.setResponse(stub.getResponse());
		entity.setStatus(TypesUtil.getEnumForDatabaseValue(stub.getStatus(), MessageStatus.class));
		if (stub.getStudentId() != null) {
			entity.setStudent(callback.updateRelationShip(stub.getStudentId(), Student.class));
		} else {
			entity.setTutor(callback.updateRelationShip(stub.getTutorId(), Tutor.class));
		}
		entity.setTimeOfDelivery(stub.getTimeOfDelivery());
		entity.setType(TypesUtil.getEnumForDatabaseValue(stub.getType(), MessageType.class));
	}
}


