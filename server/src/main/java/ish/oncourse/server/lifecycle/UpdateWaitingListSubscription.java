/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */
package ish.oncourse.server.lifecycle;

import ish.common.types.EnrolmentStatus;
import ish.oncourse.server.cayenne.Enrolment;
import ish.oncourse.server.cayenne.WaitingList;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

import static ish.oncourse.server.cayenne.glue._WaitingList.COURSE;
import static ish.oncourse.server.cayenne.glue._WaitingList.STUDENT;

/**
 * User: akoiro
 * Date: 16/06/2016
 */
public class UpdateWaitingListSubscription {
	private static final Logger logger = LogManager.getLogger(UpdateWaitingListSubscription.class);

	private Enrolment enrolment;
	private ObjectContext context;

	private boolean isValid() {
		return EnrolmentStatus.SUCCESS.equals(enrolment.getStatus());
	}

	public void update() {
		try {
			if (isValid()) {
				deleteWaitingLists();
				context.commitChanges();
			}
		} catch (Exception e) {
			logger.catching(e);
		}
	}

	private void deleteWaitingLists() {
		var lists = ObjectSelect.query(WaitingList.class)
				.where(STUDENT.eq(enrolment.getStudent()))
				.and(COURSE.eq(enrolment.getCourseClass().getCourse())).select(context);

		context.deleteObjects(lists);
	}

	public static UpdateWaitingListSubscription valueOf(Enrolment enrolment, ObjectContext context) {
		var result = new UpdateWaitingListSubscription();
		result.enrolment = enrolment;
		result.context = context;
		return result;
	}
}
