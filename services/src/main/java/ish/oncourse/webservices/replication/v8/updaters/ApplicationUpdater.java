/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.replication.v8.updaters;

import ish.common.types.ApplicationStatus;
import ish.common.types.PaymentSource;
import ish.common.types.TypesUtil;
import ish.math.Money;
import ish.oncourse.model.Application;
import ish.oncourse.model.Course;
import ish.oncourse.model.Student;
import ish.oncourse.webservices.replication.v4.updaters.AbstractWillowUpdater;
import ish.oncourse.webservices.replication.v4.updaters.RelationShipCallback;
import ish.oncourse.webservices.v8.stubs.replication.ApplicationStub;

public class ApplicationUpdater extends AbstractWillowUpdater<ApplicationStub, Application> {
	
	@Override
	protected void updateEntity(ApplicationStub stub, Application entity, RelationShipCallback callback) {
		
		entity.setStudent(callback.updateRelationShip(stub.getStudentId(), Student.class));
		entity.setCourse(callback.updateRelationShip(stub.getCourseId(), Course.class));
		entity.setSource(TypesUtil.getEnumForDatabaseValue(stub.getSource(), PaymentSource.class));
		entity.setStatus(TypesUtil.getEnumForDatabaseValue(stub.getStatus(), ApplicationStatus.class));
		entity.setEnrolBy(stub.getEnrolBy());
		if (stub.getFeeOverride() != null) {
			entity.setFeeOverride(new Money(stub.getFeeOverride()));
		}
		entity.setReason(stub.getReason());
		
		entity.setCreated(stub.getCreated());
		entity.setModified(stub.getModified());
		
	}
}
