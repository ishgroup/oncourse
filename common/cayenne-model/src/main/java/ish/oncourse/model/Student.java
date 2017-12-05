package ish.oncourse.model;

import ish.common.types.*;
import ish.oncourse.common.field.*;
import ish.oncourse.model.auto._Student;
import ish.oncourse.utils.QueueableObjectUtils;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import java.util.Calendar;
import java.util.List;

import static ish.oncourse.common.field.PropertyGetSetFactory.GET;
import static ish.oncourse.common.field.PropertyGetSetFactory.SET;

@Type(value = ContextType.STUDENT)
public class Student extends _Student implements Queueable {
	private static final long serialVersionUID = 8299657371084046806L;
    public static final int MIN_SCHOOL_COMPLETION_YEAR = 1940;

    public static final String LABOUR_FORCE_STATUS_PROPERTY = "labourForceStatus";


    public Long getId() {
		return QueueableObjectUtils.getId(this);
	}

	@Property(value = FieldProperty.LABOUR_FORCE_STATUS, type = GET, params = {})
	public AvetmissStudentLabourStatus getLabourForceStatus() {
		return TypesUtil.getEnumForDatabaseValue(getLabourForceType(), AvetmissStudentLabourStatus.class);
	}

	@Property(value = FieldProperty.LABOUR_FORCE_STATUS, type = SET, params = {AvetmissStudentLabourStatus.class})
	public void setLabourForceStatus(AvetmissStudentLabourStatus labourForceStatus) {
		setLabourForceType(labourForceStatus.getDatabaseValue());
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
		if (getSpecialNeedsAssistance() == null) {
			setSpecialNeedsAssistance(false);
		}
	}

	@Override
	public boolean isAsyncReplicationAllowed() {
		return true;
	}

	@Property(value = FieldProperty.CITIZENSHIP, type = SET, params = {StudentCitizenship.class})
	@Override
	public void setCitizenship(StudentCitizenship citizenship) {
		super.setCitizenship(citizenship);
	}

	@Property(value = FieldProperty.CITIZENSHIP, type = GET, params = {})
	@Override
	public StudentCitizenship getCitizenship() {
		return super.getCitizenship();
	}


	@Property(value = FieldProperty.COUNTRY_OF_BIRTH, type = SET, params = {Country.class})
	@Override
	public void setCountryOfBirth(Country country) {
		super.setCountryOfBirth(country);
	}

	@Property(value = FieldProperty.COUNTRY_OF_BIRTH, type = GET, params = {})
	@Override
	public Country getCountryOfBirth() {
		return super.getCountryOfBirth();
	}

	@Property(value = FieldProperty.LANGUAGE_HOME, type = SET, params = {Language.class})
	@Override
	public void setLanguageHome(Language languageHome) {
		super.setLanguageHome(languageHome);
	}

	@Property(value = FieldProperty.LANGUAGE_HOME, type = GET, params = {})
	@Override
	public Language getLanguageHome() {
		return super.getLanguageHome();
	}

	@Property(value = FieldProperty.YEAR_SCHOOL_COMPLETED, type = SET, params = {Integer.class})
	@Override
	public void setYearSchoolCompleted(Integer yearSchoolCompleted) {
		super.setYearSchoolCompleted(yearSchoolCompleted);
	}

	@Property(value = FieldProperty.YEAR_SCHOOL_COMPLETED, type = GET, params = {})
	@Override
	public Integer getYearSchoolCompleted() {
		return super.getYearSchoolCompleted();
	}

	@Property(value = FieldProperty.ENGLISH_PROFICIENCY, type = SET, params = {AvetmissStudentEnglishProficiency.class})
	@Override
	public void setEnglishProficiency(AvetmissStudentEnglishProficiency englishProficiency) {
		super.setEnglishProficiency(englishProficiency);
	}

	@Property(value = FieldProperty.ENGLISH_PROFICIENCY, type = GET, params = {})
	@Override
	public AvetmissStudentEnglishProficiency getEnglishProficiency() {
		return super.getEnglishProficiency();
	}

	@Property(value = FieldProperty.INDIGENOUS_STATUS, type = SET, params = {AvetmissStudentIndigenousStatus.class})
	@Override
	public void setIndigenousStatus(AvetmissStudentIndigenousStatus indigenousStatus) {
		super.setIndigenousStatus(indigenousStatus);
	}

	@Property(value = FieldProperty.INDIGENOUS_STATUS, type = GET, params = {})
	@Override
	public AvetmissStudentIndigenousStatus getIndigenousStatus() {
		return super.getIndigenousStatus();
	}

	@Property(value = FieldProperty.HIGHEST_SCHOOL_LEVEL, type = SET, params = {AvetmissStudentSchoolLevel.class})
	@Override
	public void setHighestSchoolLevel(AvetmissStudentSchoolLevel studentSchoolLevel) {
		super.setHighestSchoolLevel(studentSchoolLevel);
	}

	@Property(value = FieldProperty.HIGHEST_SCHOOL_LEVEL, type = GET, params = {})
	@Override
	public AvetmissStudentSchoolLevel getHighestSchoolLevel() {
		return super.getHighestSchoolLevel();
	}

	@Property(value = FieldProperty.IS_STILL_AT_SCHOOL, type = SET, params = {Boolean.class})
	@Override
	public void setIsStillAtSchool(Boolean isStillAtSchool) {
		super.setIsStillAtSchool(isStillAtSchool);
	}

	@Property(value = FieldProperty.IS_STILL_AT_SCHOOL, type = GET, params = {})
	@Override
	public Boolean getIsStillAtSchool() {
		return super.getIsStillAtSchool();
	}

	@Property(value = FieldProperty.PRIOR_EDUCATION_CODE, type = SET, params = {AvetmissStudentPriorEducation.class})
	@Override
	public void setPriorEducationCode(AvetmissStudentPriorEducation priorEducationCode) {
		super.setPriorEducationCode(priorEducationCode);
	}

	@Property(value = FieldProperty.PRIOR_EDUCATION_CODE, type = GET, params = {})
	@Override
	public AvetmissStudentPriorEducation getPriorEducationCode() {
		return super.getPriorEducationCode();
	}

	@Property(value = FieldProperty.DISABILITY_TYPE, type = SET, params = {AvetmissStudentDisabilityType.class})
	@Override
	public void setDisabilityType(AvetmissStudentDisabilityType disabilityType) {
		super.setDisabilityType(disabilityType);
	}

	@Property(value = FieldProperty.DISABILITY_TYPE, type = GET, params = {})
	@Override
	public AvetmissStudentDisabilityType getDisabilityType() {
		return super.getDisabilityType();
	}

	@Property(value = FieldProperty.SPECIAL_NEEDS, type = SET, params = {String.class})
	@Override
	public void setSpecialNeeds(String specialNeeds) {
		super.setSpecialNeeds(specialNeeds);
	}

	@Property(value = FieldProperty.SPECIAL_NEEDS, type = GET, params = {})
	@Override
	public String getSpecialNeeds() {
		return super.getSpecialNeeds();
	}
}
