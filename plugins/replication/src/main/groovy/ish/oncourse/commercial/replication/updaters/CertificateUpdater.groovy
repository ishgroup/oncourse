/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.commercial.replication.updaters

import ish.oncourse.server.cayenne.Certificate
import ish.oncourse.server.cayenne.Student
import ish.oncourse.server.reference.ReferenceUtil
import ish.oncourse.webservices.v22.stubs.replication.CertificateStub
import ish.util.LocalDateUtils

class CertificateUpdater extends AbstractAngelUpdater<CertificateStub, Certificate> {
	@Override
	protected void updateEntity(CertificateStub stub, Certificate entity, RelationShipCallback callback) {
		entity.setCertificateNumber(stub.getCertificateNumber())
		entity.setUniqueCode(stub.getUniqueCode())
		entity.setCreatedOn(stub.getCreated())
		entity.setModifiedOn(stub.getModified())
		entity.setPrintedOn(LocalDateUtils.dateToValue(stub.getPrintedWhen()))
		entity.setPrivateNotes(stub.getPrivateNotes())
		entity.setPublicNotes(stub.getPrivateNotes())
		entity.setIsQualification(stub.isQualification())
		if (stub.getQualificationId() != null) {
			entity.setQualification(ReferenceUtil.findQualificationByWillowId(entity.getObjectContext(), stub.getQualificationId()))
		}
		entity.setRevokedOn(LocalDateUtils.dateToValue(stub.getRevokedWhen()))
		entity.setStudentFirstName(stub.getStudentFirstName())
		entity.setStudentLastName(stub.getStudentLastName())
		if (stub.getStudentId() != null) {
			entity.setStudent(callback.updateRelationShip(stub.getStudentId(), Student.class))
		}
		entity.setIssuedOn(LocalDateUtils.dateToValue(stub.getIssued()))
		entity.setAwardedOn(LocalDateUtils.dateToValue(stub.getAwarded()))
	}
}
