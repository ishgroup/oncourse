package ish.oncourse.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;

import ish.oncourse.model.auto._Tutor;

public class Tutor extends _Tutor implements Queueable {

	private transient boolean doQueue = true;


	public Long getId() {
		return (getObjectId() != null && !getObjectId().isTemporary()) ?
				(Long) getObjectId().getIdSnapshot().get(ID_PK_COLUMN) : null;
	}

	public College getCollege() {
		return getContact().getCollege();
	}

	public String getFullName() {
		return getContact().getGivenName() + " " + getContact().getFamilyName();
	}
    
	/**
	 * Returns the roles of this tutor which is visible for website and not cancelled
	 * @return list of roles
	 */
	public List<TutorRole> getCurrentVisibleTutorRoles() {

		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);

		Expression validRolesQualifier = ExpressionFactory
				.matchExp(
						TutorRole.COURSE_CLASS_PROPERTY + "."
								+ CourseClass.CANCELLED_PROPERTY, false)
				.andExp(ExpressionFactory.matchExp(
						TutorRole.COURSE_CLASS_PROPERTY + "."
								+ CourseClass.CANCELLED_PROPERTY, false))
				.andExp(ExpressionFactory.noMatchExp(
						TutorRole.COURSE_CLASS_PROPERTY + "."
								+ CourseClass.END_DATE_PROPERTY, null));

		Expression qualifier = validRolesQualifier.andExp(ExpressionFactory
				.matchExp(TutorRole.TUTOR_PROPERTY, this)
				.andExp(ExpressionFactory.matchExp(
						TutorRole.IS_CONFIRMED_PROPERTY, true))
				.andExp(ExpressionFactory.greaterOrEqualExp(
						TutorRole.COURSE_CLASS_PROPERTY + "."
								+ CourseClass.END_DATE_PROPERTY,
						calendar.getTime()))
				.andExp(ExpressionFactory.matchExp(
						TutorRole.COURSE_CLASS_PROPERTY + "."
								+ CourseClass.IS_WEB_VISIBLE_PROPERTY, true)));

		SelectQuery query = new SelectQuery(TutorRole.class, qualifier);

		@SuppressWarnings("unchecked")
		List<TutorRole> result = getObjectContext().performQuery(query);

		return result == null ? new ArrayList<TutorRole>() : result;
	}

	public boolean getDoQueue() {
		return doQueue;
	}

	public void setDoQueue(boolean doQueue) {
		this.doQueue = doQueue;
	}

}
