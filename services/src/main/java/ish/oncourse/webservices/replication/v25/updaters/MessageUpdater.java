package ish.oncourse.webservices.replication.v25.updaters;

import ish.common.types.MessageStatus;
import ish.common.types.MessageType;
import ish.common.types.TypesUtil;
import ish.oncourse.model.Contact;
import ish.oncourse.model.Message;
import ish.oncourse.webservices.replication.updaters.AbstractWillowUpdater;
import ish.oncourse.webservices.replication.updaters.RelationShipCallback;
import ish.oncourse.webservices.v25.stubs.replication.MessageStub;

public class MessageUpdater extends AbstractWillowUpdater<MessageStub, Message> {

	@Override
	protected void updateEntity(MessageStub stub, Message entity, RelationShipCallback callback) {
		entity.setCreated(stub.getCreated());
		entity.setEmailBody(stub.getEmailBody());
		entity.setEmailSubject(stub.getEmailSubject());
		entity.setModified(stub.getModified());
		entity.setSmsText(stub.getSmsText());
		entity.setContact(callback.updateRelationShip(stub.getContactId(), Contact.class));
		entity.setDestinationAddress(stub.getDestinationAddress());
		entity.setNumberOfAttempts(stub.getNumberOfAttempts());
		entity.setResponse(stub.getResponse());
		entity.setStatus(TypesUtil.getEnumForDatabaseValue(stub.getStatus(), MessageStatus.class));
		entity.setTimeOfDelivery(stub.getTimeOfDelivery());
		entity.setType(TypesUtil.getEnumForDatabaseValue(stub.getType(), MessageType.class));
	}
}
