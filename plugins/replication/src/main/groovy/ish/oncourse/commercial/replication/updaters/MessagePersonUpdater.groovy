/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.commercial.replication.updaters

import ish.common.types.MessageStatus
import ish.common.types.MessageType
import ish.common.types.TypesUtil
import ish.oncourse.server.cayenne.Contact
import ish.oncourse.server.cayenne.Message
import ish.oncourse.server.cayenne.MessagePerson
import ish.oncourse.webservices.v23.stubs.replication.MessagePersonStub

/**
 */
class MessagePersonUpdater extends AbstractAngelUpdater<MessagePersonStub, MessagePerson> {

	/**
	 * @see AbstractAngelUpdater#updateEntity(ish.oncourse.webservices.util.GenericReplicationStub,
	 *      ish.oncourse.server.cayenne.Queueable, RelationShipCallback)
	 */
	@Override
	protected void updateEntity(MessagePersonStub stub, MessagePerson entity, RelationShipCallback callback) {
		entity.setAttemptCount(stub.getNumberOfAttempts())
		entity.setContact(callback.updateRelationShip(stub.getContactId(), Contact.class))
		entity.setCreatedOn(entity.getCreatedOn())
		entity.setDestinationAddress(entity.getDestinationAddress())
		entity.setMessage(callback.updateRelationShip(stub.getMessageId(), Message.class))
		entity.setModifiedOn(stub.getModified())
		entity.setResponse(stub.getResponse())
		entity.setSentOn(stub.getTimeOfDelivery())
		entity.setStatus(TypesUtil.getEnumForDatabaseValue(stub.getStatus(), MessageStatus.class))
		entity.setType(TypesUtil.getEnumForDatabaseValue(stub.getType(), MessageType.class))
	}
}
