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
import ish.oncourse.webservices.v25.stubs.replication.MessageStub

/**
 */
class MessageUpdater extends AbstractAngelUpdater<MessageStub, Message> {

	/**
	 * @see AbstractAngelUpdater#updateEntity(ish.oncourse.webservices.util.GenericReplicationStub,
	 *      ish.oncourse.server.cayenne.Queueable, RelationShipCallback)
	 */
	@Override
	protected void updateEntity(MessageStub stub, Message entity, RelationShipCallback callback) {
		entity.setNumberOfAttempts(stub.getNumberOfAttempts())
		entity.setContact(callback.updateRelationShip(stub.getContactId(), Contact.class))
		entity.setCreatedOn(stub.getCreated())
		entity.setDestinationAddress(stub.getDestinationAddress())
		entity.setEmailBody(stub.getEmailBody())
		entity.setEmailSubject(stub.getEmailSubject())
		entity.setModifiedOn(stub.getModified())
		entity.setResponse(stub.getResponse())
		entity.setTimeOfDelivery(stub.getTimeOfDelivery())
		entity.setSmsText(stub.getSmsText())
		entity.setStatus(TypesUtil.getEnumForDatabaseValue(stub.getStatus(), MessageStatus.class))
		entity.setType(TypesUtil.getEnumForDatabaseValue(stub.getType(), MessageType.class))
	}
}
