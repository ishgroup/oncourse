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
import ish.common.types.PaymentSource;
import ish.common.types.SystemEventType;
import ish.oncourse.common.SystemEvent;
import ish.oncourse.server.ICayenneService;
import ish.oncourse.server.cayenne.Enrolment;
import ish.oncourse.server.integration.EventService;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.annotation.PostPersist;
import org.apache.cayenne.annotation.PostUpdate;
import org.apache.cayenne.annotation.PrePersist;
import org.apache.cayenne.annotation.PreRemove;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EnrolmentLifecycleListener {

	private static final Logger logger = LogManager.getLogger();

	private ICayenneService cayenneService;
	private EventService eventService;

	public EnrolmentLifecycleListener(ICayenneService cayenneService, EventService eventService) {
		this.cayenneService = cayenneService;
		this.eventService = eventService;
	}

	@PrePersist(value = Enrolment.class)
	public void prePersist(Enrolment enrol) {
		SetFundingContact.valueOf(enrol).set();
	}

	@PostPersist(value = Enrolment.class)
	public void postPersist(Enrolment enrol) {
		updateAttendancesAndOutcomes(enrol, true);
		UpdateWaitingListSubscription.valueOf(enrol, cayenneService.getNewContext()).update();
		postEnrolmentSuccessfulEvents(enrol);
	}

	@PostUpdate(value = Enrolment.class)
	public void postUpdate(Enrolment enrol) {
		updateAttendancesAndOutcomes(enrol, false);
		UpdateWaitingListSubscription.valueOf(enrol, cayenneService.getNewContext()).update();
	}

	@PreRemove(value = Enrolment.class)
	public void preRemove(Enrolment enrol) {
		if (enrol != null && enrol.getCourseClass() != null) {
			var sessions = enrol.getCourseClass().getSessions();
			for (var session : sessions) {

				var a = enrol.getAttendanceForSessionAndStudent(session, enrol.getStudent());
				if (a != null && a.canDeleteRecord() && !a.getObjectId().isTemporary()) {
					enrol.getObjectContext().deleteObjects(a);
					session.removeFromAttendance(a);
				}

			}
		}
	}

	private void updateAttendancesAndOutcomes(Enrolment enrol, boolean create) {
		ObjectContext context = cayenneService.getNewContext();
		new UpdateAttendancesAndOutcomes(context, enrol, create).update();
		context.commitChanges();

	}
	public void postEnrolmentSuccessfulEvents(Enrolment enrol) {
		if (PaymentSource.SOURCE_WEB.equals(enrol.getSource()) && EnrolmentStatus.SUCCESS.equals(enrol.getStatus())) {
				eventService.postEvent(SystemEvent.valueOf(SystemEventType.ENROLMENT_SUCCESSFUL, enrol));
		}
	}
}
