package ish.oncourse.services.lifecycle;

import java.util.ArrayList;
import java.util.List;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.annotation.PostUpdate;

import ish.oncourse.model.Attendance;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.Outcome;
import ish.oncourse.model.Session;
import ish.oncourse.services.persistence.ICayenneService;

/**
 * Removes Attendances and Outcomes for Enrolment if its status becomes unsuccessful.
 * 
 * @author dzmitry
 *
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
		ObjectContext context = cayenneService.newContext();
		
		Enrolment enrol = (Enrolment) context.localObject(e.getObjectId(), null);
		
		if (enrol != null) {
			for (Session session : enrol.getCourseClass().getSessions()) {
				Attendance a = enrol.getAttendanceForSessionAndStudent(session, enrol.getStudent());
				if (a != null) {
					context.deleteObject(a);
				}
			}
			
			List<Outcome> outcomes = new ArrayList<Outcome>(enrol.getOutcomes());
			for (Outcome o : outcomes) {
				context.deleteObject(o);
			}
			
			context.commitChanges();
		}
	}

}
