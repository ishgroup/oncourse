/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.cayenne

import ish.common.types.ContactType
import ish.common.types.Gender
import ish.math.Money
import ish.oncourse.API
import ish.oncourse.cayenne.*
import ish.oncourse.function.GetContactFullName
import ish.oncourse.server.cayenne.glue._Contact
import ish.util.LocalDateUtils
import ish.util.SecurityUtil
import ish.validation.AngelContactValidator
import org.apache.cayenne.exp.Expression
import org.apache.cayenne.exp.ExpressionFactory
import org.apache.cayenne.validation.BeanValidationFailure
import org.apache.cayenne.validation.ValidationResult
import org.apache.commons.lang3.StringUtils
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import javax.annotation.Nonnull
import javax.annotation.Nullable
import java.time.LocalDate

/**
 * Contacts are at the heart of onCourse. A Contact might be an individual or a company. Contacts can be extended by
 * Student or Tutor classes, however a Contact can also exist on its own.
 */
@API
@QueueableEntity
class Contact extends _Contact implements ContactTrait, ExpandableTrait, ContactInterface, Queueable, NotableTrait, AttachableTrait {


	public static final String FULL_NAME_KEY = "fullName"
	public static final String IS_MALE_KEY = "isMale"
	public static final String PHONES_PROP = "phones"
	public static final String MESSAGE_KEY = "message"

	private static final Logger logger = LogManager.getLogger()

	@Override
	void validateForSave(@Nonnull final ValidationResult result) {
		super.validateForSave(result)
		AngelContactValidator.valueOf(result, this).validate()
	}

	@Override
	void postAdd() {
		super.postAdd()
		if (getIsCompany() == null) {
			setIsCompany(false)
		}
		if (getIsStudent() == null) {
			setIsStudent(Boolean.FALSE)
		}
		if (getIsTutor() == null) {
			setIsTutor(Boolean.FALSE)
		}
		if (getDeliveryStatusEmail() == null) {
			setDeliveryStatusEmail(0)
		}
		if (getDeliveryStatusSms() == null) {
			setDeliveryStatusSms(0)
		}
		if (getDeliveryStatusPost() == null) {
			setDeliveryStatusPost(0)
		}
		if (getAllowEmail() == null) {
			setAllowEmail(true)
		}
		if (getAllowSms() == null) {
			setAllowSms(true)
		}
		if (getAllowPost() == null) {
			setAllowPost(true)
		}

		updateUniqueCode()
	}

	@Override
	void preUpdate() {
		super.preUpdate()

		updateUniqueCode()
		updateStudentTutorFlags()

	}

	@Override
	void setUniqueCode(final String uniqueCode) {
		if (StringUtils.trimToNull(uniqueCode) != null) {
			super.setUniqueCode(uniqueCode)
		}
	}

	@Override
	void prePersist() {
		updateUniqueCode()
		super.prePersist()
	}

	@Override
	void validateForDelete(@Nonnull ValidationResult result) {
		super.validateForDelete(result)
		if (getPaymentsIn() != null && getPaymentsIn().size() > 0) {
			result.addFailure(new BeanValidationFailure(this, PAYMENTS_IN_PROPERTY, "There are payments for this contact."))
		}
		if (getInvoices() != null && getInvoices().size() > 0) {
			result.addFailure(new BeanValidationFailure(this, INVOICES_PROPERTY, "There are invoices for this contact."))
		}
		if (getMessages() != null && getMessages().size() > 0) {
			result.addFailure(new BeanValidationFailure(this, MESSAGES_PROPERTY, "There are messages waiting to be sent to this contact."))
		}
	}

	/***
	 * @return the full name for this contact including the middle name, or just the name of the company
	 */
	@Nonnull
	@API
	String getFullName() {
		if (isCompany || StringUtils.equals(firstName, lastName)) {
			return StringUtils.trimToEmpty(lastName);
		}
		StringBuilder builder = new StringBuilder();

		if (StringUtils.isNotBlank(firstName)) { builder.append(firstName); }
		if (StringUtils.isNotBlank(middleName)) { builder.append(StringUtils.SPACE).append(middleName); }
		if (StringUtils.isNotBlank(lastName)) { builder.append(StringUtils.SPACE).append(lastName); }

		return builder.toString();
	}

	/**
	 * @return address details concatenated together: suburb, state, postcode with line breaks as appropriate
	 */
	@Nonnull
	@API
	String getAddress() {
		String result = ''
		if (StringUtils.trimToNull(street)) {
			result += street + "\n"
		}
		if (StringUtils.trimToNull(suburb)) {
			result += suburb + " "
		}
		if (StringUtils.trimToNull(state)) {
			result += state + " "
		}
		if (StringUtils.trimToNull(postcode)) {
			result += postcode + " "
		}
		return result

	}

	/**
	 * @return total of all owing amounts for this contact, regardless of due date or payment plan
	 */
	@API
	@Override
	Money getTotalOwing() {
		return Invoice.amountOwingForPayer(this)
	}

	/**
	 * Trims input.
	 *
	 */
	@Override
	void setEmail(@Nullable String email) {
		String newValue = email == null ? null : email.trim()
		super.setEmail(newValue)
	}

	/**
	 * Trims input.
	 *
	 */
	@Override
	void setFirstName(@Nullable String firstName) {
		String newValue = firstName == null ? null : firstName.trim()
		super.setFirstName(newValue)
	}

	/**
	 * Trims input.
	 *
	 */
	@Override
	void setLastName(@Nullable String lastName) {
		String newValue = lastName == null ? null : lastName.trim()
		super.setLastName(newValue)
	}

	/**
	 * @return all payment in and payment out records linked to this contact
	 */
	@Nonnull
	@API
	@Override
	List<PaymentInterface> getPayments() {
		List<PaymentInterface> aList = new ArrayList<>()
		aList.addAll(getPaymentsIn() as List<PaymentInterface>)
		aList.addAll(getPaymentsOut() as List<PaymentInterface>)
		return aList
	}

	@Override
	void addToAttachmentRelations(AttachmentRelation relation) {
		addToAttachmentRelations((ContactAttachmentRelation) relation)
	}

	@Override
	void removeFromAttachmentRelations(AttachmentRelation relation) {
		removeFromAttachmentRelations((ContactAttachmentRelation) relation)
	}

	@Override
	Class<? extends AttachmentRelation> getRelationClass() {
		return ContactAttachmentRelation.class
	}

	@Override
	void setStudent(Student student) {
		super.setStudent(student)
		super.setIsStudent(getStudent() != null)
	}

	@Override
	void setTutor(Tutor tutor) {
		super.setTutor(tutor)
		super.setIsTutor(getTutor() != null)
	}

	/**
	 * Convenience method, same as getName(false)
	 *
	 * @return contact name in a format it is most commonly used in reports: 'Smith, John'
	 */
	@Nullable
	@API
	@Override
	String getName() {
		return getName(true)
	}

	/**
	 * Use getFullName() instead
	 */
	@Nullable
	@Deprecated
	@Override
	String getName(boolean firstNameFirst) {
		return GetContactFullName.valueOf(this, firstNameFirst).get()
	}

	private void updateUniqueCode() {
		if (getUniqueCode() == null) {
			setUniqueCode(SecurityUtil.generateRandomPassword(16))
		}
	}

	private void updateStudentTutorFlags() {
		Boolean studentFlag = null
		Boolean tutorFlag = null
		if (getIsStudent() == null || getIsStudent() != (getStudent() != null)) {
			studentFlag = getStudent() != null
		}
		if (getIsTutor() == null || getIsTutor() != (getTutor() != null)) {
			tutorFlag = getTutor() != null
		}

		if (studentFlag != null || tutorFlag != null) {
			setIsStudent(getStudent() != null)
			setIsTutor(getTutor() != null)
		}
	}

	/**
	 * @return invoices that belong to contact which have positive amount owing value
	 */
	@API
	@Nonnull
	@Override
	List<Invoice> getOwingInvoices() {
		Expression qualifier = ExpressionFactory.greaterExp(InvoiceInterface.AMOUNT_OWING_PROPERTY, Money.ZERO)
		List<Invoice> result = qualifier.filterObjects(getInvoices())
		Invoice.INVOICES_DUE_ORDERING.orderList(result)
		return result
	}

	/**
	 * @return all the memberships (including expired) subscribed to by this contact
	 */
	@Nonnull
	@API
	List<Membership> getMemberships() {
		List<Membership> memberships = new ArrayList<>()

		for (ProductItem item : getProductItems()) {
			if (item instanceof Membership) {
				memberships.add((Membership) item)
			}
		}

		return memberships
	}

	/**
	 * @return ABN (Australian Business Number) mostly for companies, but tutors might also have one for payment
	 */
	@API
	@Override
	@Nullable String getAbn() {
		return super.getAbn()
	}

	/**
	 * @return whether the contact opted into email marketing
	 */
	@API
	@Nonnull
	@Override
	Boolean getAllowEmail() {
		return super.getAllowEmail()
	}

	/**
	 * @return whether the contact opted into postal (mail) marketing
	 */
	@Nonnull
	@API
	@Override
	Boolean getAllowPost() {
		return super.getAllowPost()
	}

	/**
	 * @return whether the contact opted into SMS marketing
	 */
	@Nonnull
	@API
	@Override
	Boolean getAllowSms() {
		return super.getAllowSms()
	}

	/**
	 * @return yep, the contact's date of birth as LocalDate
	 */
	@API
	@Nullable
	@Override
	LocalDate getBirthDate() {
		return super.getBirthDate()
	}

	/**
	 * @return the date and time this record was created
	 */
	@API
	@Nullable
	@Override
	Date getCreatedOn() {
		return super.getCreatedOn()
	}

	/**
	 * @return  number of failed consecutive attempts to deliver email to this contact, when this value
	 * reaches 6 onCourse considers emails to this contact undeliverable
	 */
	@Nonnull
	@API
	@Override
	Integer getDeliveryStatusEmail() {
		return super.getDeliveryStatusEmail()
	}

	/**
	 * @return number which shows if postal messages can be delivered to this contact, 0 means messages can be delivered,
	 * 6 means that postage is undeliverable
	 */
	@Nonnull
	@API
	@Override
	Integer getDeliveryStatusPost() {
		return super.getDeliveryStatusPost()
	}

	/**
	 * @return number of failed consecutive attempts to deliver SMS message to this contact, when this value
	 * reaches 6 onCourse considers messages to this contact undeliverable
	 */
	@Nonnull
	@API
	@Override
	Integer getDeliveryStatusSms() {
		return super.getDeliveryStatusSms()
	}

	@API
	@Nullable
	@Override
	String getEmail() {
		return super.getEmail()
	}

	/**
	 * @return facsimile number (do we still use those?)
	 */
	@API
	@Nullable
	@Override
	String getFax() {
		return super.getFax()
	}

	/**
	 * @return first name, null for companies
	 */
	@API
	@Nullable
	@Override
	String getFirstName() {
		return super.getFirstName()
	}

	/**
	 * @return home phone number, formatted just how it was entered in the UI
	 */
	@API
	@Nullable
	@Override
	String getHomePhone() {
		return super.getHomePhone()
	}

	/**
	 * @return the honorific is the bit before the name (eg. Dr, Mrs, The Honorable)
	 */
	@API
	@Nullable
	@Override
	String getHonorific() {
		return super.getHonorific()
	}


	/**
	 * @return true if this contact is a company
	 */
	@API
	@Override
	@Nonnull Boolean getIsCompany() {
		return super.getIsCompany()
	}

	/**
	 * @return true if this contact is male
	 * @Deprecated Use getGender() instead
	 */
	@Nullable
	@Deprecated
	Boolean getIsMale() {
		switch (getGender()) {
			case Gender.MALE:
				return true
			case Gender.FEMALE:
				return false
			default:
				return null
		}
	}

	/**
	 * @return gender for the contact
	 */
	@API
	@Nullable
	@Override
	Gender getGender() {
		return super.getGender()
	}

		/**
	 * @return last name of this contact
	 */
	@API
	@Override
	String getLastName() {
		return super.getLastName()
	}

	/**
	 * @return message (alert for operator) associated with this contact
	 */
	@API
	@Nullable
	@Override
	String getMessage() {
		return super.getMessage()
	}

	/**
	 * @return contact's mobile phone
	 */
	@API
	@Nullable
	@Override
	String getMobilePhone() {
		return super.getMobilePhone()
	}

	/**
	 * @return the date and time this record was modified
	 */
	@API
	@Override
	Date getModifiedOn() {
		return super.getModifiedOn()
	}

	/**
	 * @return contact's postcode
	 */
	@API
	@Nullable
	@Override
	String getPostcode() {
		return super.getPostcode()
	}

	/**
	 * @return contact's state
	 */
	@API
	@Nullable
	@Override
	String getState() {
		return super.getState()
	}

	/**
	 * @return contact's address
	 */
	@API
	@Nullable
	@Override
	String getStreet() {
		return super.getStreet()
	}

	/**
	 * @return contact's suburb
	 */
	@API
	@Nullable
	@Override
	String getSuburb() {
		return super.getSuburb()
	}

	/**
	 * @return contact's TFN (Tax File Number)
	 */
	@API
	@Nullable
	@Override
	String getTfn() {
		return super.getTfn()
	}

	/**
	 * @return contact's title
	 */
	@API
	@Nullable
	@Override
	String getTitle() {
		return super.getTitle()
	}

	/**
	 * @return alphanumeric 16 character unique code associated with this contact
	 */
	@Nonnull
	@API
	@Override
	String getUniqueCode() {
		return super.getUniqueCode()
	}

	/**
	 * @return contact's work phone
	 */
	@API
	@Nullable
	@Override
	String getWorkPhone() {
		return super.getWorkPhone()
	}

	/**
	 * @return ClassCost records associated with this contact
	 */
	@Nonnull
	@API
	@Override
	List<ClassCost> getClassCosts() {
		return super.getClassCosts()
	}

	/**
	 * @return list of concessions authorised by this contact
	 */
	@Nonnull
	@API
	@Override
	List<StudentConcession> getConcessionsAuthorised() {
		return super.getConcessionsAuthorised()
	}

	/**
	 * @return
	 */
	@Nonnull
	@Override
	List<ContactDuplicate> getContactDuplicate() {
		return super.getContactDuplicate()
	}

	/**
	 * @return CorporatePass records associated with this contact
	 */
	@Nonnull
	@API
	@Override
	List<CorporatePass> getCorporatePasses() {
		return super.getCorporatePasses()
	}

	/**
	 * Some validation rules (such as postcode) only apply if this country is "Australia"
	 *
	 * @return the country for the address of this contact
	 */
	@Nullable
	@API
	@Override
	Country getCountry() {
		return super.getCountry()
	}

	/**
	 * @return all the custom field data for this contact
	 */
	@Nonnull
	@Override
	List<ContactCustomField> getCustomFields() {
		return super.getCustomFields()
	}

	/**
	 * Remember that a contact's invoices don't necessarily represent what they enrolled in. A contact might be billed
	 * for someone else's enrolments or other purchases.
	 *
	 * @return all the invoices linked to this contact, including those already paid
	 */
	@Nonnull
	@API
	@Override
	List<Invoice> getInvoices() {
		return super.getInvoices()
	}


	/**
	 * @return the contact's date of birth as Date
	 */
	@API
	@Override
	Date getDateOfBirth() {
		return LocalDateUtils.valueToDate(getBirthDate())
	}

	/**
	 * @return list of all MessagePerson records which were sent to this contact
	 */
	@Nonnull
	@API
	@Override
	List<MessagePerson> getMessages() {
		return super.getMessages()
	}

	/**
	 * @return all PaymentIn records associated with this contact
	 */
	@Nonnull
	@API
	@Override
	List<PaymentIn> getPaymentsIn() {
		return super.getPaymentsIn()
	}

	/**
	 * @return all PaymentOut records associated with this contact
	 */
	@Nonnull
	@API
	@Override
	List<PaymentOut> getPaymentsOut() {
		return super.getPaymentsOut()
	}

	/**
	 * @return all Payslip records associated with this contact
	 */
	@Nonnull
	@API
	@Override
	List<Payslip> getPayslips() {
		return super.getPayslips()
	}

	/**
	 * @return all ProductItem records associated with this contact
	 */
	@Nonnull
	@API
	@Override
	List<ProductItem> getProductItems() {
		return super.getProductItems()
	}

	/**
	 * @return Student record associated with this contact
	 */
	@API
	@Nullable
	@Override
	Student getStudent() {
		return super.getStudent()
	}

	/**
	 * @return all contacts related to this one
	 */
	@Nonnull
	@API
	List<Contact> getRelatedContacts() {
		return super.getToContacts()*.getToContact() +
				super.getFromContacts()*.getFromContact()
	}

	/**
	 * Get all related contacts with a specific relationship type name
	 * @param relationName (eg. 'parent')
	 * @return
	 */
	@Nonnull
	@API
	List<Contact> getRelatedContacts(String relationName) {
		def toContacts = super.getToContacts().findAll { it.relationType.fromContactName == relationName }
		def fromContacts = super.getFromContacts().findAll { it.relationType.toContactName == relationName }

		return toContacts*.getToContact() + fromContacts*.getFromContact()
	}

	/**
	 * @return Tutor record associated with this contact
	 */
	@API
	@Nullable
	@Override
	Tutor getTutor() {
		return super.getTutor()
	}

	/**
	 * @return The list of tags assigned to this contact
	 */
	@Nonnull
	@API
	List<Tag> getTags() {
		List<Tag> tagList = new ArrayList<>(getTaggingRelations().size())
		for (ContactTagRelation relation : getTaggingRelations()) {
			tagList.add(relation.getTag())
		}
		return tagList
	}

	/**
	 * @return middle name
	 */
	@API
	@Override
	String getMiddleName() {
		return super.getMiddleName()
	}

	/**
	 * When an invoice is created in Quick Enrol, or as a manual invoice,
	 * use value from the contact to set the due date.
	 * This date can still be changed during creation to another date by the user.
	 * Picks up the default preference value if it isn't overridden by the contact.
	 * @return invoice terms
	 */
	@API
	@Nullable
	@Override
	Integer getInvoiceTerms() {
		return super.getInvoiceTerms()
	}

	@API
    ContactType getContactType() {
		if (getIsStudent() && getIsTutor()) {
			return ContactType.TUTOR_STUDENT
		} else if (getIsStudent()) {
			return ContactType.STUDENT
		} else if (getIsTutor()) {
			return ContactType.TUTOR
		} else if (getIsCompany()) {
			return ContactType.COMPANY
		}
		return ContactType.PLAIN_CONTACT
	}

	@Override
	Set<Class<? extends Taggable>> getClassesForTags() {
		Set<Class<? extends Taggable>> result = new LinkedHashSet<>(super.getClassesForTags())
		if (Boolean.TRUE == getIsTutor()) {
			result.add(Tutor.class)
		}
		if (Boolean.TRUE == getIsStudent()) {
			result.add(Student.class)
		}
		return result
	}

	@Override
	String getSummaryDescription() {
		if (getIsCompany() != null && getIsCompany()) {
			return getLastName()
		}
		String description = getName(false)
		if (getIsStudent() != null && getIsStudent() && getStudent() != null && getStudent().getStudentNumber() != null) {
			return getStudent().getStudentNumber() + " " + description
		}
		return description
	}

	@Override
	Class<? extends CustomField> getCustomFieldClass() {
		return ContactCustomField
	}
}
