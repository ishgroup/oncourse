package ish.oncourse.model;

import ish.common.types.*;
import ish.oncourse.model.auto._Student;
import ish.oncourse.utils.QueueableObjectUtils;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.joda.time.Period;
import org.joda.time.PeriodType;

import java.util.Calendar;
import java.util.List;

public class Student extends _Student implements Queueable {
	private static final long serialVersionUID = 8299657371084046806L;
    public static final int MIN_SCHOOL_COMPLETION_YEAR = 1940;


    public Long getId() {
		return QueueableObjectUtils.getId(this);
	}
	
	public AvetmissStudentLabourStatus getLabourForceStatus() {
		return TypesUtil.getEnumForDatabaseValue(getLabourForceType(), AvetmissStudentLabourStatus.class);
	}
	
	public void setLabourForceStatus(AvetmissStudentLabourStatus labourForceStatus) {
		setLabourForceType(labourForceStatus.getDatabaseValue());
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
			int givenYear = getYearSchoolCompleted();
			int thisYear = Calendar.getInstance().get(Calendar.YEAR);
			if (givenYear > thisYear) {
				return "Year school completed cannot be in the future if supplied.";
			}
			if (givenYear < MIN_SCHOOL_COMPLETION_YEAR) {
				return "Year school completed if supplied should be within not earlier than 1940.";
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
		if (getFeeHelpEligible() == null) {
			setFeeHelpEligible(Boolean.FALSE);
		}
		if (getCitizenship() == null) {
			setCitizenship(StudentCitizenship.NO_INFORMATION);
		}
		if (getUsiStatus() == null) {
			setUsiStatus(UsiStatus.DEFAULT_NOT_SUPPLIED);
		}
	}

	@Override
	public boolean isAsyncReplicationAllowed() {
		return true;
	}
}
