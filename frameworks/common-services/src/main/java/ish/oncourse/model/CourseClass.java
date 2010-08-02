package ish.oncourse.model;

import java.util.ArrayList;
import java.util.List;

import ish.oncourse.model.auto._CourseClass;

import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.Ordering;
import org.apache.cayenne.query.SortOrder;

public class CourseClass extends _CourseClass {

	public boolean hasSessions() {
		return getSessions().size() > 0;
	}

	public boolean hasManySessions() {
		return getSessions().size() > 1;
	}

	public Session firstSession() {
		if (getSessions().size() == 0) {
			return null;
		}

		List<Session> list = new ArrayList<Session>(getSessions());
		new Ordering(Session.START_TIMESTAMP_PROPERTY, SortOrder.ASCENDING)
				.orderList(list);
		return list.get(0);
	}

	public boolean hasAvailableEnrolmentPlaces() {
		return availableEnrolmentPlaces() > 0;
	}

	public int availableEnrolmentPlaces() {
		int result = -1;
		if (getMaximumPlaces() != null && getMaximumPlaces() > 0) {
			int validEnrolment = validEnrolmentsCount();
			result = getMaximumPlaces() - validEnrolment;
		}
		return Math.max(0, result);
	}

	public int validEnrolmentsCount() {
		int result = ExpressionFactory.inExp(Enrolment.STATUS_PROPERTY,
				ISHPayment.STATUSES_LEGIT).filterObjects(getEnrolments())
				.size();
		return Math.max(0, result);
	}

	public Long getRecordId() {
		return (Long) readProperty(ID_PK_COLUMN);
	}

	public String getUniqueIdentifier() {
		return getCourseForClass() + "-" + getCode();
	}

	public Course getCourseForClass() {
		return getCourse().get(0);
	}
}
