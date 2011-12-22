package ish.oncourse.services.lifecycle;

import ish.common.types.EnrolmentStatus;
import ish.oncourse.model.Attendance;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.Outcome;
import ish.oncourse.model.Session;
import ish.oncourse.services.persistence.ICayenneService;

import java.util.ArrayList;
import java.util.List;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.annotation.PostUpdate;

/**
 * Removes Attendances and Outcomes for Enrolment if its status becomes unsuccessful.
 * 
 * @author dzmitry
 */
public class EnrolmentListener {

	/**
	 * Cayenne service.
	 */
	private ICayenneService cayenneService;

	public EnrolmentListener(ICayenneService cayenneService) {
		super();
		this.cayenneService = cayenneService;
	}

	@PostUpdate(value = Enrolment.class)
	public void postUpdate(Enrolment e) {
		
		if (EnrolmentStatus.CANCELLED.equals(e.getStatus()) || EnrolmentStatus.FAILED.equals(e.getStatus())
				|| EnrolmentStatus.REFUNDED.equals(e.getStatus())) {

			ObjectContext context = cayenneService.newContext();
			Enrolment enrol = (Enrolment) context.localObject(e.getObjectId(), null);

			if (enrol != null) {
				// removing attendances
				for (Session session : enrol.getCourseClass().getSessions()) {
					Attendance a = enrol.getAttendanceForSessionAndStudent(session, enrol.getStudent());
					if (a != null) {
						context.deleteObject(a);
					}
				}

				// removing outcomes
				List<Outcome> outcomes = new ArrayList<Outcome>(enrol.getOutcomes());
				for (Outcome o : outcomes) {
					context.deleteObject(o);
				}

				context.commitChanges();
			}
		}
	}
}
