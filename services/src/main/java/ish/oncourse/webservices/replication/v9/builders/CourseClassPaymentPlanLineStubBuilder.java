/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.replication.v9.builders;

import ish.oncourse.model.CourseClassPaymentPlanLine;
import ish.oncourse.webservices.replication.builders.AbstractWillowStubBuilder;
import ish.oncourse.webservices.v9.stubs.replication.CourseClassPaymentPlanLineStub;

public class CourseClassPaymentPlanLineStubBuilder extends AbstractWillowStubBuilder<CourseClassPaymentPlanLine, CourseClassPaymentPlanLineStub> {
	@Override
	protected CourseClassPaymentPlanLineStub createFullStub(CourseClassPaymentPlanLine entity) {
		CourseClassPaymentPlanLineStub stub = new CourseClassPaymentPlanLineStub();
		stub.setCreated(entity.getCreated());
		stub.setModified(entity.getModified());
		stub.setDayOffset(entity.getDayOffset());
		stub.setAmount(entity.getAmount().toBigDecimal());
		stub.setCourseClassId(entity.getCourseClass().getId());
		return stub;
	}
}
