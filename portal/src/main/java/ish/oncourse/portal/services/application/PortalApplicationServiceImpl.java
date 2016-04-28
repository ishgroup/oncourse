/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.portal.services.application;

import ish.common.types.ApplicationStatus;
import ish.oncourse.model.Application;
import ish.oncourse.model.Student;
import ish.oncourse.services.persistence.ICayenneService;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.Date;
import java.util.List;

public class PortalApplicationServiceImpl implements IPortalApplicationService {

	@Inject
	private ICayenneService cayenneService;
	
	@Override
	public List<Application> getActiveApplicationsBy(Student student) {
		return ObjectSelect.query(Application.class)
				.where((Application.STATUS.eq(ApplicationStatus.NEW)).orExp(Application.STATUS.eq(ApplicationStatus.OFFERED).andExp(Application.ENROL_BY.gt(new Date()).orExp(Application.ENROL_BY.isNull()))))
				.and(Application.STUDENT.eq(student))
				.orderBy(Application.CREATED.desc())
				.select(cayenneService.newContext());
	}

	@Override
	public List<Application> getAllApplicationsBy(Student student) {
		return ObjectSelect.query(Application.class)
				.where(Application.STUDENT.eq(student))
				.orderBy(Application.CREATED.desc())
				.select(cayenneService.newContext());
	}
}
