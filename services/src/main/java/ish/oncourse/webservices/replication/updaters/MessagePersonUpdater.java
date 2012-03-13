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


        /**
         * TODO: The try-catch was introduced because we get tons of errors about MessagePerson cannot get Message.
         * TODO: The problem will be fixed when angel side gets new version(3.0).
         * TODO: SO AFTER ANGEL GETS NEW VERSION THE try-catch SHOULD BE REMOVED
         */
        try
        {
            if (stub.getMessageId() == null)
            {
                final String errorMessage = String.format("No message object found for message person with angelid:%s willowid:%s  and messageid:%s",
                        stub.getAngelId(), entity.getId(), stub.getMessageId());
                throw new UpdaterException(errorMessage);
            }
            Message message = callback.updateRelationShip(stub.getMessageId(), Message.class);
            entity.setMessage(message);
        }
        catch (Throwable e)
        {
            final String errorMessage = String.format("No message object found for message person with angelid:%s willowid:%s  and messageid:%s",
                    stub.getAngelId(), entity.getId(), stub.getMessageId());
            LOG.warn(errorMessage,e);
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
		entity.setType(type);
	}
}


