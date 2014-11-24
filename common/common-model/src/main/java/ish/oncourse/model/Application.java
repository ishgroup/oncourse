package ish.oncourse.model;

import ish.common.types.ApplicationStatus;
import ish.common.types.ConfirmationStatus;
import ish.oncourse.model.auto._Application;
import ish.oncourse.utils.QueueableObjectUtils;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;

import java.util.List;

public class Application extends _Application implements Queueable {

	@Override
	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}

	@Override
	public boolean isAsyncReplicationAllowed() {
		return true;
	}

	@Override
	protected void onPostAdd() {
		if (getConfirmationStatus() == null) {
			setConfirmationStatus(ConfirmationStatus.DO_NOT_SEND);
		}
	}
	
	//method for automatic updating OFFERED application status to ACCEPTED if Enrolment to this course is successful
	public static void updateApplicationStatusToAccepted(ObjectContext context, Course course, Student student) {
		SelectQuery appQuery = new SelectQuery(Application.class);
		appQuery.andQualifier(ExpressionFactory.matchExp(Application.STATUS_PROPERTY, ApplicationStatus.OFFERED));
		appQuery.andQualifier(ExpressionFactory.matchExp(Application.COURSE_PROPERTY, course));
		appQuery.andQualifier(ExpressionFactory.matchExp(Application.STUDENT_PROPERTY, student));
		@SuppressWarnings("unchecked")
		List<Application> appList = context.performQuery(appQuery);
		Application app = appList.isEmpty() ? null : appList.get(0);
		if (app != null) {
			app.setStatus(ApplicationStatus.ACCEPTED);
		}
	}
}
