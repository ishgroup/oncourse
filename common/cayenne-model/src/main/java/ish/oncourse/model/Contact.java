package ish.oncourse.model;

import ish.common.types.*;
import ish.oncourse.common.field.ContextType;
import ish.oncourse.common.field.FieldProperty;
import ish.oncourse.common.field.Property;
import ish.oncourse.common.field.PropertyGetSetFactory;
import ish.oncourse.common.field.Type;
import ish.oncourse.model.auto._Contact;
import ish.oncourse.utils.ContactDelegator;
import ish.oncourse.utils.QueueableObjectUtils;
import ish.util.SecurityUtil;
import ish.validation.ContactErrorCode;
import ish.validation.ContactValidator;
import org.apache.cayenne.BaseDataObject;
import org.apache.cayenne.DataObject;
import org.apache.cayenne.PersistenceState;
import org.apache.cayenne.map.ObjRelationship;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.validation.ValidationResult;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


@Type(value = ContextType.CONTACT)
public class Contact extends _Contact implements Queueable {
	
	private static final long serialVersionUID = -7158531319889954101L;

	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}

	@Property(value = FieldProperty.DATE_OF_BIRTH, type = PropertyGetSetFactory.GET, params = {})
	@Override
	public Date getDateOfBirth() {
		return super.getDateOfBirth();
	}
	
	@Property(value = FieldProperty.DATE_OF_BIRTH, type = PropertyGetSetFactory.SET, params = {Date.class})
	@Override
	public void setDateOfBirth(Date dateOfBirth) {
		super.setDateOfBirth(dateOfBirth);
	}

	@Property(value = FieldProperty.EMAIL_ADDRESS, type = PropertyGetSetFactory.GET, params = {})
	@Override
	public String getEmailAddress() {
		return super.getEmailAddress();
	}

	@Property(value = FieldProperty.EMAIL_ADDRESS, type = PropertyGetSetFactory.SET, params = {String.class})
	@Override
	public void setEmailAddress(String emailAddress) {
		super.setEmailAddress(emailAddress);
	}

	@Property(value = FieldProperty.CUSTOM_FIELD_CONTACT, type = PropertyGetSetFactory.GET, params = {String.class})
	public String getCustomFieldValue(String key) {
		CustomField field = getCustomField(key);
		return  field == null ? null : field.getValue();
	}
	
	@Property(value = FieldProperty.CUSTOM_FIELD_CONTACT, type = PropertyGetSetFactory.SET, params = {String.class, String.class})
	public void setCustomFieldValue(String key, String value){
		setCustomFieldValue(key, value, ContactCustomField.class);
	}
	
	@Override
	protected void validateForSave(ValidationResult result) {
		ContactValidator contactValidator = ContactValidator.valueOf(ContactDelegator.valueOf(this));
		Map<String, ContactErrorCode> errorCodeMap = contactValidator.validate();

		FillValidationResult fillValidationResult = FillValidationResult.valueOf(result, errorCodeMap, this);
		fillValidationResult.fill();

		super.validateForSave(result);
	}

	public String getFullName() {
		StringBuilder buff = new StringBuilder();
		String familyName = getFamilyName();
		if (Boolean.TRUE.equals(getIsCompany())) {
			if (familyName != null) {
				buff.append(familyName);
			}

		} else {
			String givenName = getGivenName();
			String middleName = getMiddleName();
			if (givenName != null) {
				buff.append(givenName);
				buff.append(" ");
			}
			if (middleName != null) {
				buff.append(middleName);
				buff.append(" ");
			}
			if (familyName != null) {
				buff.append(familyName);
			}
		}
		return buff.toString().trim();
	}

	@Override
	protected void onPostAdd() {
		if (getIsCompany() == null) {
			setIsCompany(false);
		}
		if (getIsMarketingViaEmailAllowed() == null) {
			setIsMarketingViaEmailAllowed(true);
		}
		if (getIsMarketingViaSMSAllowed() == null) {
			setIsMarketingViaSMSAllowed(true);
		}
		if (getIsMarketingViaPostAllowed() == null) {
			setIsMarketingViaPostAllowed(true);
		}
	}
	
	@Override
	protected void onPrePersist() {
		onPostAdd();

		if (getUniqueCode() == null) {
			setUniqueCode(SecurityUtil.generateRandomPassword(16));
		}
	}

	public void setNewPassword(String newPassword) {
		setPassword(newPassword);
		setPasswordRecoverExpire(null);
		setPasswordRecoveryKey(null);
	}

	public Student createNewStudent() {
		Student student = getObjectContext().newObject(Student.class);
		student.setCollege(getCollege());
		student.setEnglishProficiency(AvetmissStudentEnglishProficiency.DEFAULT_POPUP_OPTION);
		student.setIndigenousStatus(AvetmissStudentIndigenousStatus.DEFAULT_POPUP_OPTION);
		student.setHighestSchoolLevel(AvetmissStudentSchoolLevel.DEFAULT_POPUP_OPTION);
		student.setPriorEducationCode(AvetmissStudentPriorEducation.DEFAULT_POPUP_OPTION);
		setStudent(student);
		return student;
	}
	
	public String getAddress() {
		String result = "";

		if (getSuburb() != null && !getSuburb().equals("")) {
			result = result + getSuburb();
		}
		if (getState() != null && !getState().equals("")) {
			if (result.length() > 0) {
				result = result + " ";
			}
			result = result + getState();
		}
		if (getPostcode() != null && !getPostcode().trim().equals("")) {
			if (result.length() > 0) {
				result = result + " ";
			}
			result = result + getPostcode().trim();
		}

		if (getStreet() != null && !getStreet().equals("")) {
			result = getStreet() + "\n" + result;
		}
		return result;
	}
	
	@Override
	public void setStudent(final Student student) {
		setObjectToOneTargetWithCheck(STUDENT_PROPERTY, student, true, this);
	}

	@Override
	public void setTutor(final Tutor tutor) {
		setObjectToOneTargetWithCheck(TUTOR_PROPERTY, tutor, true, this);
	}

	/**
	 * The alternative for setToOneTarget(relationshipName, value, setReverse) but with check that reverse relationship also updated.
	 * Should be used only in case when we can't be sure that relationships will be changed for all required entities.
	 * @see org.apache.cayenne.BaseDataObject#setToOneTarget(java.lang.String, org.apache.cayenne.DataObject, boolean)
	 * @param relationshipName
	 * @param value
	 * @param setReverse
	 * @param object - object to update
	 */
	public static void setObjectToOneTargetWithCheck(final String relationshipName, final DataObject value, final boolean setReverse, 
		final BaseDataObject object) {
		final DataObject oldValue = (DataObject) object.readProperty(relationshipName);
		DataObject oldObject = null;
		if ((oldValue != null && !oldValue.equals(value)) && value != null || (oldValue == null && value != null)) {
			oldObject = getObjectReverseRelationShip(relationshipName, value, object);
			if (oldObject != null && object.equals(oldObject)) {
				oldObject = null;
			}
		}
		object.setToOneTarget(relationshipName, value, setReverse);
		if (oldObject != null && oldObject.readProperty(relationshipName) != null && value.equals((DataObject) oldObject.readProperty(relationshipName))) {
			oldObject.setToOneTarget(relationshipName, null, false);
		}
	}
	
	private static DataObject getObjectReverseRelationShip(final String relationshipName, final DataObject value, final BaseDataObject object) {
		final ObjRelationship relation = (ObjRelationship) object.getObjectContext().getEntityResolver()
			.getObjEntity(object.getObjectId().getEntityName()).getRelationship(relationshipName);
		final ObjRelationship reverseRelation = relation.getReverseRelationship();
		return (DataObject) value.readProperty(reverseRelation.getName());
	}

	@Override
	public boolean isAsyncReplicationAllowed() {
		return true;
	}

	public List<Membership> getMemberships() {
		List<Membership> memberships = new ArrayList<>();

		for (ProductItem item : getProducts()) {
			if (item instanceof Membership) {
				memberships.add((Membership) item);
			}
		}

		return memberships;
	}

	@Property(value = FieldProperty.FIRST_NAME, type = PropertyGetSetFactory.SET, params = {String.class})
	@Override
	public void setGivenName(String givenName) {
		super.setGivenName(givenName);
	}

	@Property(value = FieldProperty.FIRST_NAME, type = PropertyGetSetFactory.GET, params = {})
	@Override
	public String getGivenName() {
		return super.getGivenName();
	}

	@Property(value = FieldProperty.LAST_NAME, type = PropertyGetSetFactory.SET, params = {String.class})
	@Override
	public void setFamilyName(String familyName) {
		super.setFamilyName(familyName);
	}

	@Property(value = FieldProperty.LAST_NAME, type = PropertyGetSetFactory.GET, params = {})
	@Override
	public String getFamilyName() {
		return super.getFamilyName();
	}


	@Property(value = FieldProperty.STREET, type = PropertyGetSetFactory.SET, params = {String.class})
	@Override
	public void setStreet(String street) {
		super.setStreet(street);
	}

	@Property(value = FieldProperty.STREET, type = PropertyGetSetFactory.GET, params = {})
	@Override
	public String getStreet() {
		return super.getStreet();
	}

	@Property(value = FieldProperty.SUBURB, type = PropertyGetSetFactory.SET, params = {String.class})
	@Override
	public void setSuburb(String suburb) {
		super.setSuburb(suburb);
	}

	@Property(value = FieldProperty.SUBURB, type = PropertyGetSetFactory.GET, params = {})
	@Override
	public String getSuburb() {
		return super.getSuburb();
	}

	@Property(value = FieldProperty.POSTCODE, type = PropertyGetSetFactory.SET, params = {String.class})
	@Override
	public void setPostcode(String postcode) {
		super.setPostcode(postcode);
	}

	@Property(value = FieldProperty.POSTCODE, type = PropertyGetSetFactory.GET, params = {})
	@Override
	public String getPostcode() {
		return super.getPostcode();
	}


	@Property(value = FieldProperty.STATE, type = PropertyGetSetFactory.SET, params = {String.class})
	@Override
	public void setState(String state) {
		super.setState(state);
	}

	@Property(value = FieldProperty.STATE, type = PropertyGetSetFactory.GET, params = {})
	@Override
	public String getState() {
		return super.getState();
	}
	
	@Property(value = FieldProperty.COUNTRY, type = PropertyGetSetFactory.SET, params = {Country.class})
	@Override
	public void setCountry(Country country) {
		super.setCountry(country);
	}

	@Property(value = FieldProperty.COUNTRY, type = PropertyGetSetFactory.GET, params = {})
	@Override
	public Country getCountry() {
		return super.getCountry();
	}

	@Property(value = FieldProperty.HOME_PHONE_NUMBER, type = PropertyGetSetFactory.SET, params = {String.class})
	@Override
	public void setHomePhoneNumber(String homePhoneNumber) {
		super.setHomePhoneNumber(homePhoneNumber);
	}

	@Property(value = FieldProperty.HOME_PHONE_NUMBER, type = PropertyGetSetFactory.GET, params = {})
	@Override
	public String getHomePhoneNumber() {
		return super.getHomePhoneNumber();
	}

	@Property(value = FieldProperty.BUSINESS_PHONE_NUMBER, type = PropertyGetSetFactory.SET, params = {String.class})
	@Override
	public void setBusinessPhoneNumber(String businessPhoneNumber) {
		super.setBusinessPhoneNumber(businessPhoneNumber);
	}

	@Property(value = FieldProperty.BUSINESS_PHONE_NUMBER, type = PropertyGetSetFactory.GET, params = {})
	@Override
	public String getBusinessPhoneNumber() {
		return super.getBusinessPhoneNumber();
	}

	@Property(value = FieldProperty.FAX_NUMBER, type = PropertyGetSetFactory.SET, params = {String.class})
	@Override
	public void setFaxNumber(String faxNumber) {
		super.setFaxNumber(faxNumber);
	}

	@Property(value = FieldProperty.FAX_NUMBER, type = PropertyGetSetFactory.GET, params = {})
	@Override
	public String getFaxNumber() {
		return super.getFaxNumber();
	}

	@Property(value = FieldProperty.MOBILE_PHONE_NUMBER, type = PropertyGetSetFactory.SET, params = {String.class})
	@Override
	public void setMobilePhoneNumber(String mobilePhoneNumber) {
		super.setMobilePhoneNumber(mobilePhoneNumber);
	}

	@Property(value = FieldProperty.MOBILE_PHONE_NUMBER, type = PropertyGetSetFactory.GET, params = {})
	@Override
	public String getMobilePhoneNumber() {
		return super.getMobilePhoneNumber();
	}

	@Property(value = FieldProperty.ABN, type = PropertyGetSetFactory.SET, params = {String.class})
	@Override
	public void setAbn(String abn) {
		super.setAbn(abn);
	}

	@Property(value = FieldProperty.ABN, type = PropertyGetSetFactory.GET, params = {})
	@Override
	public String getAbn() {
		return super.getAbn();
	}

	@Property(value = FieldProperty.IS_MALE, type = PropertyGetSetFactory.SET, params = {Gender.class})
	@Override
	public void setGender(Gender isMale) {
		super.setGender(isMale);
	}

	@Property(value = FieldProperty.IS_MALE, type = PropertyGetSetFactory.GET, params = {})
	@Override
	public Gender getGender() {
		return super.getGender();
	}
	
	@Deprecated
	public void setIsMale(Boolean isMale) {
		if (isMale == null) {
			setGender(null);
		} else if (isMale) {
			setGender(Gender.MALE);
		} else {
			setGender(Gender.FEMALE);
		}

	}

	@Deprecated
	public Boolean getIsMale() {
		Gender gender = super.getGender();
		if (gender == null) {
			return null;
		}
		switch (super.getGender()) {
			case MALE:
				return true;
			case FEMALE:
				return false;
			default:
				return null;
		}	
	}
	
	
	@Property(value = FieldProperty.IS_MARKETING_VIA_EMAIL_ALLOWED_PROPERTY, type = PropertyGetSetFactory.SET, params = {Boolean.class})
	@Override
	public void setIsMarketingViaEmailAllowed(Boolean isMarketingViaEmailAllowed) {
		super.setIsMarketingViaEmailAllowed(isMarketingViaEmailAllowed);
	}
	@Property(value = FieldProperty.IS_MARKETING_VIA_EMAIL_ALLOWED_PROPERTY, type = PropertyGetSetFactory.GET, params = {})
	@Override
	public Boolean getIsMarketingViaEmailAllowed() {
		return super.getIsMarketingViaEmailAllowed();
	}
	
	@Property(value = FieldProperty.IS_MARKETING_VIA_POST_ALLOWED_PROPERTY, type = PropertyGetSetFactory.SET, params = {Boolean.class})
	@Override
	public void setIsMarketingViaPostAllowed(Boolean isMarketingViaPostAllowed) {
		super.setIsMarketingViaPostAllowed(isMarketingViaPostAllowed);
	}
	@Property(value = FieldProperty.IS_MARKETING_VIA_POST_ALLOWED_PROPERTY, type = PropertyGetSetFactory.GET, params = {})
	@Override
	public Boolean getIsMarketingViaPostAllowed() {
		return super.getIsMarketingViaPostAllowed();
	}

	@Property(value = FieldProperty.IS_MARKETING_VIA_SMS_ALLOWED_PROPERTY, type = PropertyGetSetFactory.SET, params = {Boolean.class})
	@Override
	public void setIsMarketingViaSMSAllowed(Boolean isMarketingViaSMSAllowed) {
		super.setIsMarketingViaSMSAllowed(isMarketingViaSMSAllowed);
	}

	@Property(value = FieldProperty.IS_MARKETING_VIA_SMS_ALLOWED_PROPERTY, type = PropertyGetSetFactory.GET, params = {})
	@Override
	public Boolean getIsMarketingViaSMSAllowed() {
		return super.getIsMarketingViaSMSAllowed();
	}

	@Property(value = FieldProperty.HONORIFIC, type = PropertyGetSetFactory.SET, params = {String.class})
	@Override
	public void setHonorific(String honorific) {
		super.setHonorific(honorific);
	}

	@Property(value = FieldProperty.HONORIFIC, type = PropertyGetSetFactory.GET, params = {})
	@Override
	public String getHonorific() {
		return super.getHonorific();
	}

	@Property(value = FieldProperty.TITLE, type = PropertyGetSetFactory.SET, params = {String.class})
	@Override
	public void setTitle(String title) {
		super.setTitle(title);
	}

	@Property(value = FieldProperty.TITLE, type = PropertyGetSetFactory.GET, params = {})
	@Override
	public String getTitle() {
		return super.getTitle();
	}

	@Property(value = FieldProperty.MIDDLE_NAME, type = PropertyGetSetFactory.SET, params = {String.class})
	@Override
	public void setMiddleName(String middleName) {
		super.setMiddleName(middleName);
	}

	@Property(value = FieldProperty.MIDDLE_NAME, type = PropertyGetSetFactory.GET, params = {})
	@Override
	public String getMiddleName() {
		return super.getMiddleName();
	}
	
	public boolean isEnrolled(Course course) {
		if (getStudent() != null && getStudent().getPersistenceState() != PersistenceState.NEW) {
			return ObjectSelect.query(Enrolment.class)
					.where(Enrolment.COURSE_CLASS.dot(CourseClass.COURSE).eq(course))
					.and(Enrolment.STATUS.eq(EnrolmentStatus.SUCCESS))
					.and(Enrolment.STUDENT.eq(getStudent()))
					.selectFirst(getObjectContext()) != null;
		}
		return false;
	}
}
