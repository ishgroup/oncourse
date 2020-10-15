/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.server.replication.updaters

import ish.oncourse.server.cayenne.Certificate
import ish.oncourse.server.cayenne.Student
import ish.oncourse.server.reference.ReferenceUtil
import ish.oncourse.webservices.v21.stubs.replication.CertificateStub

class CertificateUpdater extends AbstractAngelUpdater<CertificateStub, Certificate> {
	@Override
	protected void updateEntity(CertificateStub stub, Certificate entity, RelationShipCallback callback) {
		entity.setCertificateNumber(stub.getCertificateNumber())
		entity.setUniqueCode(stub.getUniqueCode())
		entity.setCreatedOn(stub.getCreated())
		entity.setModifiedOn(stub.getModified())
		entity.setPrintedOn(stub.getPrintedWhen())
		entity.setPrivateNotes(stub.getPrivateNotes())
		entity.setPublicNotes(stub.getPrivateNotes())
		entity.setIsQualification(stub.isQualification())
		if (stub.getQualificationId() != null) {
			entity.setQualification(ReferenceUtil.findQualificationByWillowId(entity.getObjectContext(), stub.getQualificationId()))
		}
		entity.setRevokedOn(stub.getRevokedWhen())
		entity.setStudentFirstName(stub.getStudentFirstName())
		entity.setStudentLastName(stub.getStudentLastName())
		if (stub.getStudentId() != null) {
			entity.setStudent(callback.updateRelationShip(stub.getStudentId(), Student.class))
		}
		entity.setIssuedOn(stub.getIssued())
		entity.setAwardedOn(stub.getAwarded())
	}
}
