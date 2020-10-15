/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.server.replication.builders

import ish.oncourse.server.cayenne.Certificate
import ish.oncourse.webservices.v21.stubs.replication.CertificateStub

/**
 */
class CertificateStubBuilder extends AbstractAngelStubBuilder<Certificate, CertificateStub> {

	/**
	 * @see AbstractAngelStubBuilder#createFullStub(ish.oncourse.server.cayenne.Queueable)
	 */
	@Override
	protected CertificateStub createFullStub(Certificate entity) {
		def stub = new CertificateStub()
		stub.setCertificateNumber(entity.getCertificateNumber())
		stub.setCreated(entity.getCreatedOn())
		stub.setModified(entity.getModifiedOn())
		stub.setPrintedWhen(entity.getPrintedOn())
		stub.setPrivateNotes(entity.getPrivateNotes())
		stub.setPublicNotes(entity.getPublicNotes())
		stub.setQualification(entity.getIsQualification())
		if (entity.getQualification() != null) {
			stub.setQualificationId(entity.getQualification().getWillowId())
		}
		stub.setRevokedWhen(entity.getRevokedOn())
		stub.setStudentId(entity.getStudent().getId())
		stub.setStudentFirstName(entity.getStudentFirstName())
		stub.setStudentLastName(entity.getStudentLastName())
		stub.setIssued(entity.getIssuedOn())
		stub.setAwarded(entity.getAwardedOn())
		stub.setUniqueCode(entity.getUniqueCode())
		return stub
	}
}
