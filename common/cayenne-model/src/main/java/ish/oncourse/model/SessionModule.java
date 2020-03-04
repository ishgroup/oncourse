package ish.oncourse.model;

import ish.oncourse.cayenne.AttendanceInterface;
import ish.oncourse.cayenne.OutcomeInterface;
import ish.oncourse.cayenne.SessionModuleInterface;
import ish.oncourse.model.auto._SessionModule;
import ish.oncourse.utils.QueueableObjectUtils;

public class SessionModule extends _SessionModule implements Queueable, SessionModuleInterface {
	private static final long serialVersionUID = -6936312225691806816L;

	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}

	@Override
	public boolean isAsyncReplicationAllowed() {
		return true;
	}

	@Override
	public AttendanceInterface getAttendanceForOutcome(OutcomeInterface outcomeInterface) {
		Student student = ((Outcome) outcomeInterface).getEnrolment().getStudent();
		return getSession().getAttendances().stream().filter(a -> a.getStudent().getId().equals(student.getId())).findFirst().orElse(null);
	}
}
