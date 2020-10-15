/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.server.replication.updaters

import ish.math.Money
import ish.oncourse.server.cayenne.CourseClass
import ish.oncourse.server.cayenne.CourseClassPaymentPlanLine
import ish.oncourse.webservices.v21.stubs.replication.CourseClassPaymentPlanLineStub

class CourseClassPaymentPlanLineUpdater extends AbstractAngelUpdater<CourseClassPaymentPlanLineStub, CourseClassPaymentPlanLine> {
	@Override
	protected void updateEntity(CourseClassPaymentPlanLineStub stub, CourseClassPaymentPlanLine entity, RelationShipCallback callback) {
		entity.setCreatedOn(stub.getCreated())
		entity.setModifiedOn(stub.getModified())
		entity.setDayOffset(stub.getDayOffset())
		entity.setAmount(new Money(stub.getAmount()))
		entity.setCourseClass(callback.updateRelationShip(stub.getCourseClassId(), CourseClass.class))
	}
}
