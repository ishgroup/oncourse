/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.server.replication.builders

import ish.oncourse.server.cayenne.CourseClassPaymentPlanLine
import ish.oncourse.webservices.v21.stubs.replication.CourseClassPaymentPlanLineStub

class CourseClassPaymentPlanLineStubBuilder extends AbstractAngelStubBuilder<CourseClassPaymentPlanLine, CourseClassPaymentPlanLineStub>  {
	@Override
	protected CourseClassPaymentPlanLineStub createFullStub(CourseClassPaymentPlanLine entity) {
		def stub = new CourseClassPaymentPlanLineStub()
		stub.setCreated(entity.getCreatedOn())
		stub.setModified(entity.getModifiedOn())
		stub.setDayOffset(entity.getDayOffset())
		stub.setAmount(entity.getAmount().toBigDecimal())
		stub.setCourseClassId(entity.getCourseClass().getId())
		return stub
	}
}
