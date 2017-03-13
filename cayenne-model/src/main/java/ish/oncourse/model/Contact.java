package ish.oncourse.model;

import ish.common.types.AvetmissStudentEnglishProficiency;
import ish.common.types.AvetmissStudentIndigenousStatus;
import ish.common.types.AvetmissStudentPriorEducation;
import ish.common.types.AvetmissStudentSchoolLevel;
import ish.oncourse.common.field.ContextType;
import ish.oncourse.common.field.FieldProperty;
import ish.oncourse.common.field.Property;
import ish.oncourse.common.field.PropertyGetSetFactory;
import ish.oncourse.common.field.Type;
import ish.oncourse.model.auto._Contact;
import ish.oncourse.utils.ContactDelegator;
import ish.oncourse.utils.PhoneValidator;
import ish.oncourse.utils.QueueableObjectUtils;
import ish.util.SecurityUtil;
import ish.validation.ContactErrorCode;
import ish.validation.ContactValidator;
import org.apache.cayenne.CayenneDataObject;
import org.apache.cayenne.DataObject;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.map.ObjRelationship;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.validation.ValidationResult;
import org.apache.commons.lang.StringUtils;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
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

	@Property(value = FieldProperty.CUSTOM_FIELD, type = PropertyGetSetFactory.GET, params = {String.class})
	public String getCustomFieldValue(String key) {
		CustomField field = getCustomField(key);
		return  field == null ? null : field.getValue();
	}
	
	
	@Property(value = FieldProperty.CUSTOM_FIELD, type = PropertyGetSetFactory.SET, params = {String.class, String.class})
	public void setCustomFieldValue(String key, String value){
		CustomField field = getCustomField(key);
		if (field != null) {
			field.setValue(value);
		} else {
			ObjectContext context = getObjectContext();
			CustomFieldType customFieldType = ObjectSelect.query(CustomFieldType.class)
					.where(CustomFieldType.COLLEGE.eq(getCollege()))
					.and(CustomFieldType.KEY.eq(key))
					.selectFirst(context);
			
			if (customFieldType == null) {
				return;
			}
			CustomField customField = context.newObject(CustomField.class);
			customField.setValue(value);
			customField.setRelatedObject(this);
			customField.setCustomFieldType(customFieldType);
			customField.setCollege(getCollege());
		}
	}
	
	private CustomField getCustomField(String key) {
		for (CustomField customField : getCustomFields()) {
			String customFieldKey = customField.getCustomFieldType().getKey();
			if (customFieldKey != null && customFieldKey .equalsIgnoreCase(key)) {
				return customField;
			}
		}
		return null;
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
