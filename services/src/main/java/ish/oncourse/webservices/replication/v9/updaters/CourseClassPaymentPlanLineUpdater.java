/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.replication.v9.updaters;


import ish.math.Money;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.CourseClassPaymentPlanLine;
import ish.oncourse.webservices.replication.updaters.AbstractWillowUpdater;
import ish.oncourse.webservices.replication.updaters.RelationShipCallback;
import ish.oncourse.webservices.v9.stubs.replication.CourseClassPaymentPlanLineStub;


public class CourseClassPaymentPlanLineUpdater extends AbstractWillowUpdater<CourseClassPaymentPlanLineStub, CourseClassPaymentPlanLine> {
	@Override
	protected void updateEntity(CourseClassPaymentPlanLineStub stub, CourseClassPaymentPlanLine entity, RelationShipCallback callback) {
		entity.setCreated(stub.getCreated());
		entity.setModified(stub.getModified());
		entity.setAmount(new Money(stub.getAmount()));
		entity.setDayOffset(stub.getDayOffset());
		entity.setCourseClass(callback.updateRelationShip(stub.getCourseClassId(), CourseClass.class));
	}
}
