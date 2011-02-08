package ish.oncourse.model;

import ish.oncourse.model.auto._Student;

import java.util.Calendar;
import java.util.List;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.joda.time.Period;
import org.joda.time.PeriodType;

public class Student extends _Student implements Queueable {

	public Long getId() {
		return (getObjectId() != null && !getObjectId().isTemporary()) ? 
				(Long) getObjectId() .getIdSnapshot().get(ID_PK_COLUMN) : null;
	}

	/**
	 * @return the number of years since the contact's birth date.
	 */
	public Integer getYearsOfAge() {

		Integer age = null;

		if ((getContact() == null) || (getContact().getDateOfBirth() == null)) {
			Period interval = new Period(getContact().getDateOfBirth().getTime(),
					System.currentTimeMillis(), PeriodType.years());
			age = interval.getYears();
		}

		return age;
	}

	public WaitingList getActiveWaitingListForCourse(Course course) {

		List<WaitingList> waits = getWaitingLists();
		Expression qualifier = ExpressionFactory.matchExp(WaitingList.COURSE_PROPERTY, course);
		waits = qualifier.filterObjects(waits);
		if (waits.size() > 0) {
			return waits.get(waits.size() - 1);
		}
		return null;
	}

	public String getFullName() {
		return getContact().getFullName();
	}

	public String validateSchoolYear() {
		if (getYearSchoolCompleted() != null) {
			int givenYear = getYearSchoolCompleted().intValue();
			int thisYear = Calendar.getInstance().get(Calendar.YEAR);
			if (givenYear > thisYear) {
				return "Year school completed cannot be in the future if supplied.";
			}
			if (thisYear - givenYear > 100) {
				return "Year school completed if supplied should be within the last 100 years.";
			}
		}
		return null;
	}

}
