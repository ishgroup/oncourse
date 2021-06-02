/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.commercial.replication.builders

import groovy.transform.CompileStatic
import ish.oncourse.server.cayenne.Certificate
import ish.oncourse.webservices.v23.stubs.replication.CertificateStub
import ish.util.LocalDateUtils

@CompileStatic
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
		stub.setPrintedWhen(LocalDateUtils.valueToDate(entity.getPrintedOn()))
		stub.setPrivateNotes(entity.getPrivateNotes())
		stub.setPublicNotes(entity.getPublicNotes())
		stub.setQualification(entity.getIsQualification())
		if (entity.getQualification() != null) {
			stub.setQualificationId(entity.getQualification().getWillowId())
		}
		stub.setRevokedWhen(LocalDateUtils.valueToDate(entity.getRevokedOn()))
		stub.setStudentId(entity.getStudent().getId())
		stub.setStudentFirstName(entity.getStudentFirstName())
		stub.setStudentLastName(entity.getStudentLastName())
		stub.setIssued(LocalDateUtils.valueToDate(entity.getIssuedOn()))
		stub.setAwarded(LocalDateUtils.valueToDate(entity.getAwardedOn()))
		stub.setUniqueCode(entity.getUniqueCode())
		return stub
	}
}
