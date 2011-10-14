package ish.oncourse.webservices.replication.updaters;

import ish.oncourse.model.Contact;
import ish.oncourse.model.Message;
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
		// TODO Auto-generated method stub
		entity.setContact(callback.updateRelationShip(stub.getContactId(), Contact.class));
		entity.setCreated(stub.getCreated());
		entity.setDestinationAddress(stub.getDestinationAddress());
		entity.setMessage(callback.updateRelationShip(stub.getMessageId(), Message.class));
		entity.setModified(stub.getModified());
		entity.setNumberOfAttempts(stub.getNumberOfAttempts());
		entity.setResponse(stub.getResponse());
		
		entity.setStatus(MessageStatus.getEnumForDatabaseValue(stub.getStatus()));
		
		if (stub.getStudentId() != null) {
			entity.setStudent(callback.updateRelationShip(stub.getStudentId(), Student.class));
		}
		else {
			entity.setTutor(callback.updateRelationShip(stub.getTutorId(), Tutor.class));
		}
		
		
		entity.setTimeOfDelivery(stub.getTimeOfDelivery());
		entity.setType(MessageType.getEnumForDatabaseValue(stub.getType()));
	}
}


