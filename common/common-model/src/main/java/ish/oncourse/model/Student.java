package ish.oncourse.model;

import ish.common.types.AvetmissStudentDisabilityType;
import ish.common.types.AvetmissStudentEnglishProficiency;
import ish.common.types.AvetmissStudentIndigenousStatus;
import ish.common.types.AvetmissStudentPriorEducation;
import ish.common.types.AvetmissStudentSchoolLevel;
import ish.oncourse.model.auto._Student;
import ish.oncourse.utils.QueueableObjectUtils;

import java.util.Calendar;
import java.util.List;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.joda.time.Period;
import org.joda.time.PeriodType;

public class Student extends _Student implements Queueable {
	private static final long serialVersionUID = 8299657371084046806L;

	public Long getId() {
		return QueueableObjectUtils.getId(this);
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

	@Override
	protected void onPostAdd() {
		
		if (getEnglishProficiency() == null)
			setEnglishProficiency(AvetmissStudentEnglishProficiency.DEFAULT_POPUP_OPTION);
		if (getDisabilityType() == null)
			setDisabilityType(AvetmissStudentDisabilityType.DEFAULT_POPUP_OPTION);
		if (getHighestSchoolLevel() == null)
			setHighestSchoolLevel(AvetmissStudentSchoolLevel.DEFAULT_POPUP_OPTION);
		if (getIndigenousStatus() == null)
			setIndigenousStatus(AvetmissStudentIndigenousStatus.DEFAULT_POPUP_OPTION);
		if (getIsOverseasClient() == null)
			setIsOverseasClient(Boolean.FALSE);
		if (getPriorEducationCode() == null)
			setPriorEducationCode(AvetmissStudentPriorEducation.DEFAULT_POPUP_OPTION);
	}

}
