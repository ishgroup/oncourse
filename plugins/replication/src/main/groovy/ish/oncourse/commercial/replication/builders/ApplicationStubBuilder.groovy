/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.commercial.replication.builders

import groovy.transform.CompileStatic
import ish.oncourse.server.cayenne.Application
import ish.oncourse.webservices.v23.stubs.replication.ApplicationStub

@CompileStatic
class ApplicationStubBuilder extends AbstractAngelStubBuilder<Application, ApplicationStub> {

	@Override
	protected ApplicationStub createFullStub(Application entity) {
		def applicationStub = new ApplicationStub()

		applicationStub.setStudentId(entity.getStudent().getId())
		applicationStub.setCourseId(entity.getCourse().getId())
		applicationStub.setSource(entity.getSource().getDatabaseValue())
		applicationStub.setStatus(entity.getStatus().getDatabaseValue())
		applicationStub.setEnrolBy(entity.getEnrolBy())
		if (entity.getFeeOverride() != null) {
			applicationStub.setFeeOverride(entity.getFeeOverride().toBigDecimal())
		}
		applicationStub.setReason(entity.getReason())

		applicationStub.setCreated(entity.getCreatedOn())
		applicationStub.setModified(entity.getModifiedOn())

		if (entity.getConfirmationStatus() != null) {
			applicationStub.setConfirmationStatus(entity.getConfirmationStatus().getDatabaseValue())
		}

		return applicationStub
	}
}
