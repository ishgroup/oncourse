/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.server.replication.builders

import ish.oncourse.server.cayenne.Contact
import ish.oncourse.server.cayenne.CorporatePass
import ish.oncourse.webservices.v21.stubs.replication.CorporatePassStub

/**
 */
class CorporatePassStubBuilder extends AbstractAngelStubBuilder<CorporatePass, CorporatePassStub> {

	/**
	 * @see AbstractAngelStubBuilder#createFullStub(ish.oncourse.server.cayenne.Queueable)
	 */
	@Override
	protected CorporatePassStub createFullStub(CorporatePass entity) {
		def stub = new CorporatePassStub()

		stub.setCreated(entity.getCreatedOn())
		stub.setModified(entity.getModifiedOn())
		stub.setExpiryDate(entity.getExpiryDate())
		stub.setInvoiceEmail(entity.getInvoiceEmail())
		stub.setPassword(entity.getPassword())

		def contact = entity.getContact()
		stub.setContactId(contact != null ? entity.getContact().getId() : null)

		return stub
	}

}
