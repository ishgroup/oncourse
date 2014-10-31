/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.replication.v8.builders;

import ish.oncourse.model.Application;
import ish.oncourse.webservices.replication.v4.builders.AbstractWillowStubBuilder;
import ish.oncourse.webservices.v8.stubs.replication.ApplicationStub;

public class ApplicationStubBuilder extends AbstractWillowStubBuilder<Application, ApplicationStub> {
	
	@Override
	protected ApplicationStub createFullStub(Application entity) {
		ApplicationStub applicationStub = new ApplicationStub();
		
		applicationStub.setStudentId(entity.getStudent().getId());
		applicationStub.setCourseId(entity.getCourse().getId());
		applicationStub.setSource(entity.getSource().getDatabaseValue());
		applicationStub.setStatus(entity.getStatus().getDatabaseValue());
		applicationStub.setEnrolBy(entity.getEnrolBy());
		if (entity.getFeeOverride() != null) {
			applicationStub.setFeeOverride(entity.getFeeOverride().toBigDecimal());
		}
		applicationStub.setReason(entity.getReason());
		
		applicationStub.setCreated(entity.getCreated());
		applicationStub.setModified(entity.getModified());
		if (entity.getConfirmationStatus() != null) {
			applicationStub.setConfirmationStatus(entity.getConfirmationStatus().getDatabaseValue());
		}
		
		return applicationStub;
	}
}
