package ish.oncourse.model;

import ish.common.types.AvetmissStudentEnglishProficiency;
import ish.common.types.AvetmissStudentIndigenousStatus;
import ish.common.types.AvetmissStudentPriorEducation;
import ish.common.types.AvetmissStudentSchoolLevel;
import ish.oncourse.model.auto._Contact;
import ish.oncourse.utils.ContactDelegator;
import ish.oncourse.utils.PhoneValidator;
import ish.oncourse.utils.QueueableObjectUtils;
import ish.util.SecurityUtil;
import ish.validation.ContactErrorCode;
import ish.validation.ContactValidator;
import org.apache.cayenne.CayenneDataObject;
import org.apache.cayenne.DataObject;
import org.apache.cayenne.map.ObjRelationship;
import org.apache.cayenne.validation.ValidationResult;
import org.apache.commons.lang.StringUtils;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class Contact extends _Contact implements Queueable {
	static final String INVALID_EMAIL_MESSAGE = "The email address does not appear to be valid.";
	private static final long serialVersionUID = -7158531319889954101L;

	public Long getId() {
		return QueueableObjectUtils.getId(this);
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

	private String getEntityName() {
		if (getStudent() != null) {
			return "student";
		}
		if (Boolean.TRUE.equals(getIsCompany())) {
			return "company";
		}
		if (getTutor() != null) {
			return "tutor";
		}
		return "contact";
	}

	/**
	 * Validate methods; will return the string representation of error or null,
	 * if the field is valid
	 */
	public String validateGivenName() {
		if (!Boolean.TRUE.equals(getIsCompany())) {
			String givenName = getGivenName();
			return validateGivenName(getEntityName(), givenName);
		}
		return null;
	}

	/**
	 * Validate first name utility method
	 * 
	 * @param entityName
	 *            company, student or tutor
	 * @param givenName
	 * @return
	 */
	@Deprecated
	public static String validateGivenName(String entityName, String givenName) {
		givenName = StringUtils.trimToNull(givenName);
		if (givenName == null) {
			return "The " + entityName + "'s first name is required.";
		}

		if (givenName.split("\\d").length != 1) {
			return "The first name cannot contain number characters.";
		}

		return null;
	}

	public String validateFamilyName() {
		String familyName = getFamilyName();
		return validateFamilyName(getEntityName(), familyName);
	}

	/**
	 * Validate last name untility method
	 * 
	 * @param entityName
	 * @param familyName
	 * @return
	 */
	@Deprecated
	public static String validateFamilyName(String entityName, String familyName) {
		familyName = StringUtils.trimToNull(familyName);
		if (familyName == null) {
			return "The " + entityName + "'s last name is required.";
		}
		if (familyName.split("\\d").length != 1) {
			return "The last name cannot contain number characters.";
		}
		return null;
	}

	public String validateEmail() {
		String emailAddress = getEmailAddress();
		return validateEmail(getEntityName(), emailAddress);
	}

	private static String getEmptyEmailMessage(String entityName) {
		return "The " + entityName + "'s email is required.";
	}
	
	public String getEmptyEmailMessage() {
		return getEmptyEmailMessage(getEntityName());
	}
	
	/**
	 * Utility method to validate email address.
	 * @param entityName
	 * @param emailAddress
	 * @return
	 */
	@Deprecated
	public static String validateEmail(String entityName, String emailAddress) {
		emailAddress = StringUtils.trimToNull(emailAddress);
		if (emailAddress == null) {
			return "The " + entityName + "'s email is required.";
		}

		try {
			InternetAddress emailAddr = new InternetAddress(emailAddress);
			emailAddr.validate();
		} catch (AddressException ex) {
			return INVALID_EMAIL_MESSAGE;
		}
		return null;
	}

	public String validateSuburb() {
		String suburb = getSuburb();
		if (suburb == null || "".equals(suburb)) {
			// not required
			return null;
		}
		if (suburb.split("\\d").length != 1) {
			return "A suburb name cannot contain numeric digits.";
		}
		return null;
	}

	public String validatePostcode() {
		String suburb = getPostcode();
		if (suburb == null || "".equals(suburb)) {
			// not required
			return null;
		}
		if (!suburb.matches("(\\d){4}")) {
			return "Enter 4 digit postcode for Australian postcodes.";
		}
		return null;
	}

	public String validateState() {
		String state = getState();
		if (state == null || "".equals(state)) {
			// not required
			return null;
		}
		if (state.length() > 20) {
			return "The name of the state cannot exceed 20 characters.";
		}
		return null;
	}

	public String validateHomePhone() {
		String homePhone = getHomePhoneNumber();
		if (homePhone == null || "".equals(homePhone)) {
			// not required
			return null;
		}
		try {
			setHomePhoneNumber(PhoneValidator.validatePhoneNumber("home", homePhone));
		} catch (Exception ex) {
			return ex.getMessage();
		}
		return null;
	}

	public String validateMobilePhone() {
		String mobilePhone = getMobilePhoneNumber();
		if (mobilePhone == null || "".equals(mobilePhone)) {
			// not required
			return null;
		}
		try {
			setMobilePhoneNumber(PhoneValidator.validateMobileNumber(mobilePhone));
		} catch (Exception ex) {
			return ex.getMessage();
		}
		return null;
	}

	public String validateBusinessPhone() {
		String businessPhone = getBusinessPhoneNumber();
		if (businessPhone == null || "".equals(businessPhone)) {
			// not required
			return null;
		}
		try {
			setBusinessPhoneNumber(PhoneValidator.validatePhoneNumber("business", businessPhone));
		} catch (Exception ex) {
			return ex.getMessage();
		}
		return null;
	}

	public String validateFax() {
		String fax = getFaxNumber();
		if (fax == null || "".equals(fax)) {
			// not required
			return null;
		}
		try {
			setFaxNumber(PhoneValidator.validatePhoneNumber("fax", fax));
		} catch (Exception ex) {
			return ex.getMessage();
		}
		return null;
	}

	public String validatePassword() {
		String password = getPassword();
		return validatedPassword(password, false);
	}

	public String validatePasswordConfirm(String confirmPassword) {
		return validatedPassword(confirmPassword, true);
	}

	public String validateBirthDate() {
		Date birthDate = getDateOfBirth();

        //birthDate is not required field,
		if (birthDate == null) {
            return null;
		}

		if (birthDate.after(new Date())) {
			return "The birth date cannot be in the future.";
		}

		return null;
	}

	private String validatedPassword(String aValue, boolean isConfirm) {

		String prefix = "The password" + (isConfirm ? " confirm" : "") + " ";
		int minimumPasswordChars = 4;
		if (aValue == null || aValue.length() < minimumPasswordChars) {
			return prefix + "must be at least " + minimumPasswordChars + " characters long.";
		}
		if (aValue.split("\\s").length != 1) {
			return prefix + "cannot contain blank spaces.";
		}
		if (isConfirm && !aValue.equals(getPassword())) {
			return prefix + "does not match the given password.";
		}
		return null;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see ish.oncourse.model.auto._Contact#onPrePersist()
	 */
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
	 * @see org.apache.cayenne.CayenneDataObject#setToOneTarget(java.lang.String, org.apache.cayenne.DataObject, boolean)
	 * @param relationshipName
	 * @param value
	 * @param setReverse
	 * @param object - object to update
	 */
	public static void setObjectToOneTargetWithCheck(final String relationshipName, final DataObject value, final boolean setReverse, 
		final CayenneDataObject object) {
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
	
	private static DataObject getObjectReverseRelationShip(final String relationshipName, final DataObject value, final CayenneDataObject object) {
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
}
