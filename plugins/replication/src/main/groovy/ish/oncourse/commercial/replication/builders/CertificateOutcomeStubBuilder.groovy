/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.commercial.replication.builders

import groovy.transform.CompileStatic
import ish.oncourse.server.cayenne.CertificateOutcome
import ish.oncourse.webservices.v23.stubs.replication.CertificateOutcomeStub

@CompileStatic
class CertificateOutcomeStubBuilder extends AbstractAngelStubBuilder<CertificateOutcome, CertificateOutcomeStub> {

	/**
	 * @see AbstractAngelStubBuilder#createFullStub(ish.oncourse.server.cayenne.Queueable)
	 */
	@Override
	protected CertificateOutcomeStub createFullStub(CertificateOutcome entity) {
		def stub = new CertificateOutcomeStub()
		stub.setCertificateId(entity.getCertificate().getId())
		stub.setCreated(entity.getCreatedOn())
		stub.setModified(entity.getModifiedOn())
		stub.setOutcomeId(entity.getOutcome().getId())
		return stub
	}
}
