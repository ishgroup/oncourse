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

package ish.oncourse.server.api.service

import com.google.inject.Inject
import groovy.transform.CompileStatic
import ish.common.types.AttachmentSpecialType
import ish.common.types.USIFieldStatus
import ish.common.types.USIVerificationResult
import ish.oncourse.server.api.v1.model.PayslipPayTypeDTO
import org.apache.commons.lang3.StringUtils
import ish.oncourse.server.document.DocumentService
import org.apache.commons.lang3.StringUtils

import static ish.common.types.USIVerificationStatus.VALID
import ish.common.types.UsiStatus
import ish.oncourse.function.GetContactFullName
import ish.oncourse.server.CayenneService
import ish.oncourse.server.PreferenceController
import ish.oncourse.server.api.dao.ContactDao
import ish.oncourse.server.api.dao.ContactRelationDao
import ish.oncourse.server.api.dao.ContactRelationTypeDao
import ish.oncourse.server.api.dao.CountryDao
import ish.oncourse.server.api.dao.CustomFieldTypeDao
import ish.oncourse.server.api.dao.LanguageDao
import ish.oncourse.server.api.dao.PaymentInDao
import ish.oncourse.server.api.dao.TagDao
import ish.oncourse.server.api.dao.TaxDao
import static ish.oncourse.server.api.v1.function.ContactFunctions.getFinancialDataForContact
import static ish.oncourse.server.api.v1.function.ContactFunctions.getProfilePictureDocument
import static ish.oncourse.server.api.v1.function.ContactFunctions.isValidEmailAddress
import static ish.oncourse.server.api.v1.function.ContactFunctions.isValidUsiString
import static ish.oncourse.server.api.v1.function.ContactFunctions.toRestMessagePerson
import static ish.oncourse.server.api.v1.function.ContactFunctions.updateContactRelations
import static ish.oncourse.server.api.v1.function.ContactFunctions.updateProfilePicture
import static ish.oncourse.server.api.v1.function.ContactFunctions.validateContactRelations
import static ish.oncourse.server.api.v1.function.ContactFunctions.validateStudentConcessions
import static ish.oncourse.server.api.v1.function.ContactFunctions.validateStudentYearSchoolCompleted
import ish.oncourse.server.api.v1.function.CountryFunctions
import static ish.oncourse.server.api.v1.function.CustomFieldFunctions.updateCustomFields
import static ish.oncourse.server.api.v1.function.DocumentFunctions.toRestDocument
import static ish.oncourse.server.api.v1.function.DocumentFunctions.toRestDocumentMinimized
import static ish.oncourse.server.api.v1.function.DocumentFunctions.updateDocuments
import static ish.oncourse.server.api.v1.function.HolidayFunctions.toRestHoliday
import static ish.oncourse.server.api.v1.function.HolidayFunctions.updateAvailabilityRules
import static ish.oncourse.server.api.v1.function.HolidayFunctions.validateFoUpdate
import static ish.oncourse.server.api.v1.function.HolidayFunctions.validateForDelete
import ish.oncourse.server.api.v1.function.LanguageFunctions
import static ish.oncourse.server.api.v1.function.StudentConcessionFunctions.toRestConcession
import static ish.oncourse.server.api.v1.function.StudentConcessionFunctions.updateStudentConcessions
import static ish.oncourse.server.api.v1.function.TagFunctions.toRestTagMinimized
import static ish.oncourse.server.api.v1.function.TagFunctions.updateTags
import ish.oncourse.server.api.v1.model.AvetmissStudentDisabilityTypeDTO
import ish.oncourse.server.api.v1.model.AvetmissStudentEnglishProficiencyDTO
import ish.oncourse.server.api.v1.model.AvetmissStudentIndigenousStatusDTO
import ish.oncourse.server.api.v1.model.AvetmissStudentLabourStatusDTO
import ish.oncourse.server.api.v1.model.AvetmissStudentPriorEducationDTO
import ish.oncourse.server.api.v1.model.AvetmissStudentSchoolLevelDTO
import ish.oncourse.server.api.v1.model.ClientIndustryEmploymentTypeDTO
import ish.oncourse.server.api.v1.model.ClientOccupationIdentifierTypeDTO
import ish.oncourse.server.api.v1.model.ContactDTO
import ish.oncourse.server.api.v1.model.ContactGenderDTO
import ish.oncourse.server.api.v1.model.ContactRelationDTO
import ish.oncourse.server.api.v1.model.DocumentDTO
import ish.oncourse.server.api.v1.model.HolidayDTO
import ish.oncourse.server.api.v1.model.StudentCitizenshipDTO
import ish.oncourse.server.api.v1.model.StudentDTO
import ish.oncourse.server.api.v1.model.TutorDTO
import ish.oncourse.server.api.v1.model.UsiStatusDTO
import ish.oncourse.server.api.v1.model.UsiVerificationResultDTO
import ish.oncourse.server.api.v1.model.UsiVerificationStatusDTO
import ish.oncourse.server.api.v1.model.ValidationErrorDTO
import ish.oncourse.server.api.v1.model.WorkingWithChildrenStatusDTO
import ish.oncourse.server.api.validation.EntityValidator
import ish.oncourse.server.cayenne.Contact
import ish.oncourse.server.cayenne.ContactAttachmentRelation
import ish.oncourse.server.cayenne.ContactCustomField
import ish.oncourse.server.cayenne.ContactRelation
import ish.oncourse.server.cayenne.ContactTagRelation
import ish.oncourse.server.cayenne.ContactUnavailableRuleRelation
import ish.oncourse.server.cayenne.CourseClass
import ish.oncourse.server.cayenne.CourseTrait
import ish.oncourse.server.cayenne.Document
import ish.oncourse.server.cayenne.IntegrationConfiguration
import ish.oncourse.server.cayenne.Student
import ish.oncourse.server.cayenne.Tag
import ish.oncourse.server.cayenne.Tax
import ish.oncourse.server.cayenne.Tutor
import ish.oncourse.server.integration.Plugin
import ish.oncourse.server.integration.usi.USIIntegration
import ish.oncourse.server.license.LicenseService
import ish.oncourse.server.services.ISystemUserService
import ish.util.LocalDateUtils
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import static org.apache.commons.lang.StringUtils.EMPTY
import static org.apache.commons.lang.StringUtils.isBlank
import static org.apache.commons.lang.StringUtils.isNotBlank
import static org.apache.commons.lang.StringUtils.isNumericSpace
import static org.apache.commons.lang.StringUtils.trimToNull
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import javax.ws.rs.ClientErrorException
import javax.ws.rs.core.Response
import java.time.LocalDate

@CompileStatic
class ContactApiService extends TaggableApiService<ContactDTO, Contact, ContactDao> {

    @Inject
    private PreferenceController preferenceController

    @Inject
    private LicenseService licenseService

    @Inject
    private DocumentService documentService

    @Inject
    private CountryDao countryDao

    @Inject
    private ContactDao contactDao

    @Inject
    private ContactRelationTypeDao contactRelationTypeDao

    @Inject
    private ContactRelationDao contactRelationDao

    @Inject
    private LanguageDao languageDao

    @Inject
    private TagDao tagDao

    @Inject
    private TaxDao taxDao

    @Inject
    private CustomFieldTypeDao customFieldTypeDao

    @Inject
    private ISystemUserService systemUserService

    @Inject
    private CayenneService cayenneService

    @Inject
    private PaymentInDao paymentInDao

    @Inject
    private ProductItemApiService productItemApiService

    private static final Logger logger = LogManager.logger


    @Override
    Class<Contact> getPersistentClass() {
        return Contact
    }

    @Override
    ContactDTO toRestModel(Contact cayenneModel) {
        new ContactDTO().with { dto ->
            dto.id = cayenneModel.id
            dto.student = toRestStudent(cayenneModel.student)
            dto.tutor = toRestTutor(cayenneModel.tutor)
            dto.abn = cayenneModel.abn
            dto.birthDate = cayenneModel.birthDate
            dto.country = CountryFunctions.toRestCountry(cayenneModel.country)
            dto.fax = cayenneModel.fax
            dto.isCompany = cayenneModel.isCompany
            dto.gender = ContactGenderDTO.values()[0].fromDbType(cayenneModel.gender)
            dto.message = cayenneModel.message
            dto.homePhone = cayenneModel.homePhone
            dto.mobilePhone = cayenneModel.mobilePhone
            dto.workPhone = cayenneModel.workPhone
            dto.postcode = cayenneModel.postcode
            dto.state = cayenneModel.state
            dto.street = cayenneModel.street
            dto.suburb = cayenneModel.suburb
            dto.tfn = cayenneModel.tfn
            dto.deliveryStatusEmail = cayenneModel.deliveryStatusEmail
            dto.deliveryStatusSms = cayenneModel.deliveryStatusSms
            dto.deliveryStatusPost = cayenneModel.deliveryStatusPost
            dto.allowPost = cayenneModel.allowPost
            dto.allowSms = cayenneModel.allowSms
            dto.allowEmail = cayenneModel.allowEmail
            dto.uniqueCode = cayenneModel.uniqueCode
            dto.honorific = cayenneModel.honorific
            dto.title = cayenneModel.title
            dto.email = cayenneModel.email
            dto.firstName = cayenneModel.firstName
            dto.lastName = cayenneModel.lastName
            dto.middleName = cayenneModel.middleName
            dto.invoiceTerms = cayenneModel.invoiceTerms
            dto.taxId = cayenneModel.taxOverride?.id
            dto.customFields = cayenneModel.customFields.collectEntries { [(it.customFieldType.key) : it.value] }
            dto.documents = cayenneModel.documents
                    .findAll{doc ->
                        doc.attachmentRelations.findAll{ r -> AttachmentSpecialType.PROFILE_PICTURE != r.specialType }
                    }.findAll{ doc ->
                        !doc.isRemoved
                    }.collect{ d ->
                        toRestDocumentMinimized(d, d.currentVersion.id, documentService)
                    }
            dto.tags = cayenneModel.tags.collect{ toRestTagMinimized(it) }
            dto.memberships = cayenneModel.memberships.collect {  productItemApiService.toRestModel(it) }
            dto.profilePicture = getProfilePicture(cayenneModel)

            dto.relations += cayenneModel.toContacts.collect{ toRestToContactRelation(it as ContactRelation) }
            dto.relations += cayenneModel.fromContacts.collect{ toRestFromContactRelation(it as ContactRelation)}
            dto.financialData = getFinancialDataForContact(cayenneModel)
            dto.messages = cayenneModel.messages.collect{ toRestMessagePerson(it) }
            dto.rules = cayenneModel.unavailableRuleRelations*.rule.collect{ toRestHoliday(it)}
            dto
        }
    }

    @Override
    Contact toCayenneModel(ContactDTO dto, Contact cayenneModel) {
        ObjectContext context = cayenneModel.context

        toStudentCayenneModel(context, cayenneModel, dto.student, cayenneModel.student)
        if (cayenneModel.student != null) {
            cayenneModel.isStudent = true
        }
        toTutorCayenneModel(context, cayenneModel, dto.tutor, cayenneModel.tutor)
        if (cayenneModel.tutor != null) {
            cayenneModel.isTutor = true
        }
        cayenneModel.abn = dto.abn
        cayenneModel.birthDate = dto.birthDate
        cayenneModel.country = countryDao.getById(context, dto?.country?.id)
        cayenneModel.fax = dto.fax
        cayenneModel.isCompany = dto.isCompany
        cayenneModel.gender = dto.gender?.dbType
        cayenneModel.message = dto.message
        cayenneModel.homePhone = dto.homePhone
        cayenneModel.mobilePhone = dto.mobilePhone
        cayenneModel.workPhone = dto.workPhone
        cayenneModel.postcode = dto.postcode
        cayenneModel.state = dto.state
        cayenneModel.street = dto.street
        cayenneModel.suburb = dto.suburb
        cayenneModel.tfn = StringUtils.trimToNull(dto.tfn)
        cayenneModel.deliveryStatusEmail = dto.deliveryStatusEmail
        cayenneModel.deliveryStatusSms = dto.deliveryStatusSms
        cayenneModel.deliveryStatusPost = dto.deliveryStatusPost
        cayenneModel.allowEmail = dto.allowEmail
        cayenneModel.allowSms = dto.allowSms
        cayenneModel.allowPost = dto.allowPost
        cayenneModel.honorific = dto.honorific
        cayenneModel.firstName = dto.firstName
        cayenneModel.middleName = dto.middleName
        cayenneModel.lastName = dto.lastName
        cayenneModel.email = dto.email
        cayenneModel.title = dto.title
        cayenneModel.invoiceTerms = dto.invoiceTerms
        cayenneModel.taxOverride = dto.taxId != null ? taxDao.getById(context, dto.taxId) : null as Tax
        updateCustomFields(context, cayenneModel, dto.customFields, ContactCustomField)
        updateDocuments(cayenneModel, cayenneModel.attachmentRelations, dto.documents, ContactAttachmentRelation, context)
        updateTags(cayenneModel, cayenneModel.taggingRelations, dto.tags*.id, ContactTagRelation, context)
        updateProfilePicture(cayenneModel, dto.profilePicture)
        updateContactRelations(context, cayenneModel, dto.relations)
        updateAvailabilityRules(cayenneModel, cayenneModel.unavailableRuleRelations*.rule, dto.rules, ContactUnavailableRuleRelation)

        if (dto.removeCChistory) {
            paymentInDao.removeCChistory(cayenneModel)
        }
        cayenneModel
    }

    Student toStudentCayenneModel(ObjectContext context, Contact contact, StudentDTO dto, Student cayenneModel) {

        if (dto == null) {
            return null
        }

        if (cayenneModel == null) {
            cayenneModel = context.newObject(Student)
            contact.student = cayenneModel
        }

        cayenneModel.countryOfBirth = countryDao.getById(context, dto?.countryOfBirth?.id)
        cayenneModel.countryOfResidency = countryDao.getById(context, dto?.countryOfResidency?.id)
        cayenneModel.labourForceStatus = dto?.labourForceStatus?.getDbType()
        cayenneModel.disabilityType = dto?.disabilityType?.getDbType()
        cayenneModel.chessn = dto.chessn
        cayenneModel.language = languageDao.getById(context, dto?.language?.id)
        cayenneModel.citizenship = dto?.citizenship?.getDbType()
        cayenneModel.clientIndustryEmployment = dto?.clientIndustryEmployment?.getDbType()
        cayenneModel.clientOccupationIdentifier = dto?.clientOccupationIdentifier?.getDbType()
        cayenneModel.englishProficiency = dto?.englishProficiency?.getDbType()
        cayenneModel.feeHelpEligible = dto.feeHelpEligible
        cayenneModel.highestSchoolLevel = dto?.highestSchoolLevel?.getDbType()
        cayenneModel.indigenousStatus = dto?.indigenousStatus?.getDbType()
        cayenneModel.isOverseasClient = dto.isOverseasClient
        cayenneModel.isStillAtSchool = dto.isStillAtSchool
        cayenneModel.medicalInsurance = dto.medicalInsurance
        cayenneModel.passportNumber = dto.passportNumber
        cayenneModel.priorEducationCode = dto?.priorEducationCode?.getDbType()
        cayenneModel.specialNeeds = dto.specialNeeds
        cayenneModel.specialNeedsAssistance = dto.specialNeedsAssistance
        cayenneModel.townOfBirth = dto.townOfBirth
        cayenneModel.uniqueLearnerIndentifier = dto.uniqueLearnerIdentifier
        if (dto.usiStatus == UsiStatusDTO.EXEMPTION || dto.usiStatus == UsiStatusDTO.INTERNATIONAL) {
            cayenneModel.usiStatus = dto.usiStatus.getDbType()
            cayenneModel.usi = null
        } else if (dto.usi) {
            cayenneModel.usi = dto.usi
            cayenneModel.usiStatus = dto.usiStatus.dbType
        } else {
            cayenneModel.usi = null
            cayenneModel.usiStatus = UsiStatus.DEFAULT_NOT_SUPPLIED
        }
        cayenneModel.visaExpiryDate = LocalDateUtils.valueToDate(dto.visaExpiryDate)
        cayenneModel.visaNumber = dto.visaNumber
        cayenneModel.visaType = dto.visaType
        cayenneModel.yearSchoolCompleted = dto.yearSchoolCompleted
        updateStudentConcessions(cayenneModel.contact, dto.concessions)
        cayenneModel
    }

    Tutor toTutorCayenneModel(ObjectContext context, Contact contact, TutorDTO dto, Tutor cayenneModel) {

        if (dto == null) {
            return null
        }

        if (cayenneModel == null) {
            cayenneModel = context.newObject(Tutor)
            contact.tutor = cayenneModel
        }

        cayenneModel.dateStarted = LocalDateUtils.valueToDate(dto.dateStarted)
        cayenneModel.dateFinished = LocalDateUtils.valueToDate(dto.dateFinished)
        cayenneModel.familyNameLegal = dto.familyNameLegal
        cayenneModel.givenNameLegal = dto.givenNameLegal
        cayenneModel.payrollRef = dto.payrollRef
        cayenneModel.resume = dto.resume
        cayenneModel.wwChildrenCheckedOn = LocalDateUtils.valueToDate(dto.wwChildrenCheckedOn)
        cayenneModel.wwChildrenExpiry = LocalDateUtils.valueToDate(dto.wwChildrenExpiry)
        cayenneModel.wwChildrenRef = dto.wwChildrenRef
        cayenneModel.wwChildrenStatus = dto.wwChildrenStatus?.getDbType()
        cayenneModel.payType = dto.defaultPayType?.dbType

        cayenneModel
    }

    @Override
    void remove(Contact contact, ObjectContext context) {
        if (contact.student) {
            context.deleteObject(contact.student)
        }
        if (contact.tutor) {
            context.deleteObject(contact.tutor)
        }
        context.deleteObject(contact)
    }


    void validateModelBeforeSave(ContactDTO dto, ObjectContext context, Long id) {

        checkContactNames(dto.firstName, dto.lastName, dto.isCompany)
        checkContactFieldsLength(validator, dto)
        checkEmail(dto.email)

        if (dto.birthDate != null) {
            final int givenYear = dto.birthDate.year

            if (givenYear > Calendar.getInstance().get(Calendar.YEAR)) {
                validator.throwClientErrorException(Student.YEAR_SCHOOL_COMPLETED.name, 'The birth date can no be in future.')
            }
        }

        if (isNotBlank(dto.abn) && !isNumericSpace(dto.abn)) {
                validator.throwClientErrorException(Contact.ABN.name, 'Business Number (ABN) should be numeric.')
        }

        if (isNotBlank(dto.tfn) && !isNumericSpace(dto.tfn)) {
            validator.throwClientErrorException(Contact.TFN.name, 'Tax File Number (TFN) should be numeric.')
        }

        validateContactRelations(context, contactDao, contactRelationTypeDao, contactRelationDao, validator, dto)

        dto.rules.each {
            ValidationErrorDTO error = validateFoUpdate(context, it as HolidayDTO, false)
            if (error != null) {
                throw new ClientErrorException(Response.status(Response.Status.BAD_REQUEST).entity(error).build())
            }
        }

        if (dto.student != null) {

            if (dto.isCompany) {
                validator.throwClientErrorException(Contact.IS_COMPANY.name, 'A student cannot be a company.')
            }

            checkStudentFieldsLength(validator, dto.student)

            validateStudentYearSchoolCompleted(validator, dto.student)
            validateStudentConcessions(context, validator, dto.student)

            if (dto.student.usi && !(dto.student.usiStatus in [UsiStatusDTO.EXEMPTION, UsiStatusDTO.INTERNATIONAL]) && !isValidUsiString(dto.student.usi) ) {
                validator.throwClientErrorException(Student.USI.name, "The USI code is not valid.")
            }

//            if (!isBlank(dto.student.usi)) {
//                if (isValidUsiString(dto.student.usi)) {
//                    if (isBlank(dto.firstName)) {
//                        validator.throwClientErrorException(Contact.FIRST_NAME.name, 'Please provide first name to continue verify USI.')
//                    } else if (isBlank(dto.lastName)) {
//                        validator.throwClientErrorException(Contact.LAST_NAME.name, 'Please provide last name to continue verify USI.')
//                    } else if (dto.birthDate == null) {
//                        validator.throwClientErrorException(Contact.STUDENT.dot(Student.USI).name, 'Please provide date of birth to continue verify USI.')
//                    } else if (isBlank(preferenceController.avetmissID)) {
//                        validator.throwClientErrorException(Contact.STUDENT.dot(Student.USI).name, 'Please provide AVETMISS identifier to continue verify USI.');
//                    }
//
//                    Contact cayenneModel = id != null ? getRecordById(context, Contact, id) : null
//
//                    if (cayenneModel && cayenneModel.student) {
//                        if (cayenneModel.student.usiStatus != VERIFIED || isAnyUsiFieldEdited(cayenneModel, dto)) {
//                            UsiVerificationResultDTO verificationResult = verifyUsi(dto.firstName, dto.lastName, dto.birthDate, dto.student.usi)
//                            if (verificationResult.verifyStatus == UsiVerificationStatusDTO.VALID) {
//                                dto.student.usiStatus = UsiStatusDTO.VERIFIED
//                            } else if (verificationResult.verifyStatus == UsiVerificationStatusDTO.INVALID) {
//                                dto.student.usiStatus = UsiStatusDTO.NOT_VERIFIED
//                            } else {
//                                dto.student.usiStatus = UsiStatusDTO.values()[0].fromDbType(cayenneModel.student.usiStatus)
//                            }
//                        }
//                    } else {
//                        UsiVerificationResultDTO verificationResult = verifyUsi(dto.firstName, dto.lastName, dto.birthDate, dto.student.usi)
//                        if (verificationResult.verifyStatus == UsiVerificationStatusDTO.VALID) {
//                            dto.student.usiStatus = UsiStatusDTO.VERIFIED
//                        } else if (verificationResult.verifyStatus == UsiVerificationStatusDTO.INVALID) {
//                            dto.student.usiStatus = UsiStatusDTO.NOT_VERIFIED
//                        } else {
//                            dto.student.usiStatus = UsiStatusDTO.NOT_SUPPLIED
//                        }
//                    }
//                } else {
//                    if (dto.student.usiStatus != UsiStatusDTO.EXEMPTION && dto.student.usiStatus != UsiStatusDTO.INTERNATIONAL) {
//                        validator.throwClientErrorException(Student.USI.name, "The USI code is not valid.")
//                    }
//                }
//            } else {
//                dto.student.usiStatus = UsiStatusDTO.NOT_SUPPLIED
//            }
        }

        if (dto.tutor != null) {
            checkTutorFieldsLength(validator, dto.tutor)
            if (trimToNull(dto.tutor?.wwChildrenRef) != null && dto.tutor?.wwChildrenExpiry == null) {
                validator.throwClientErrorException(Tutor.WW_CHILDREN_EXPIRY.name, 'WWCC expiry date must be specified.')
            }
            if (dto.tutor?.wwChildrenExpiry != null && trimToNull(dto.tutor?.wwChildrenRef) == null) {
                validator.throwClientErrorException(Tutor.WW_CHILDREN_REF.name, 'WWCC number must be specified.')
            }
        }

        List<Tag> relatedTags = tagDao.getRelatedForQueueableId(context, Contact.simpleName, dto?.id)
        if (dto.student == null && relatedTags.find{it.entityName == Student.simpleName}) {
            validator.throwClientErrorException(Contact.TAGGING_RELATIONS.name, 'Contact is not a student and can not be tagged by student tag.')
        } else if (dto.tutor == null && relatedTags.find{it.entityName == Tutor.simpleName}) {
            validator.throwClientErrorException(Contact.TAGGING_RELATIONS.name, 'Contact is not a tutor and can not be tagged by tutor tag.')
        }

        List<String> mandatoryCustomFieldTypeKeys = customFieldTypeDao.getMandatoryTypesForClass(context, Contact.class).collect{ t -> t.key }
        List<String> modelCustomFieldTypeKeys = dto.customFields.findAll {e -> e.value != null && EMPTY != e.value}.collect { e -> e.key}
        String missedCustomFieldTypeKey = mandatoryCustomFieldTypeKeys.find { k -> !modelCustomFieldTypeKeys.contains(k)}
        if (missedCustomFieldTypeKey != null) {
            validator.throwClientErrorException(Contact.CUSTOM_FIELDS.name, "Custom field type '${missedCustomFieldTypeKey}' is mandatory for contact.")
        }
    }

    private boolean isAnyUsiFieldEdited(Contact cayenneModel, ContactDTO dto) {
        cayenneModel.student.usi != dto.student.usi ||
                cayenneModel.firstName != dto.firstName ||
                cayenneModel.lastName != dto.lastName ||
                cayenneModel.birthDate != dto.birthDate
    }

    private void checkContactFieldsLength(EntityValidator validator, ContactDTO dto) {
        checkStringLength(dto.firstName, 128, validator, Contact.FIRST_NAME.name, 'First name length should be less than 128 characters.')
        checkStringLength(dto.lastName, 128, validator, Contact.LAST_NAME.name, 'Last name length should be less than 128 characters.')
        checkStringLength(dto.middleName, 128, validator, Contact.MIDDLE_NAME.name, 'Middle name length should be less than 128 characters.')
        checkStringLength(dto.email, 128, validator, Contact.EMAIL.name, 'Email length should be less than 128 characters.')
        checkStringLength(dto.abn, 50, validator, Contact.ABN.name, 'Business Number (ABN) should be less than 50 characters.')
        checkStringLength(dto.homePhone, 20, validator, Contact.HOME_PHONE.name, 'Home phone should be less than 20 characters.')
        checkStringLength(dto.mobilePhone, 20, validator, Contact.MOBILE_PHONE.name, 'Mobile phone should be less than 20 characters.')
        checkStringLength(dto.workPhone, 20, validator, Contact.WORK_PHONE.name, 'Work phone should be less than 20 characters.')
        checkStringLength(dto.fax, 20, validator, Contact.FAX.name, 'Fax should be less than 20 characters.')
        checkStringLength(dto.honorific, 256, validator, Contact.HONORIFIC.name, 'Honorific should be less than 256 characters.')
        checkStringLength(dto.message, 500, validator, Contact.MESSAGE.name, 'Message should be less than 500 characters.')
        checkStringLength(dto.postcode, 20, validator, Contact.POSTCODE.name, 'Postcode should be less than 20 characters.')
        checkStringLength(dto.state, 20, validator, Contact.STATE.name, 'State should be less than 20 characters.')
        checkStringLength(dto.street, 200, validator, Contact.STREET.name, 'Street should be less than 200 characters.')
        checkStringLength(dto.suburb, 128, validator, Contact.SUBURB.name, 'Suburb should be less than 128 characters.')
        checkStringLength(dto.tfn, 64, validator, Contact.TFN.name, 'Tax File Number should be less than 64 characters.')
        checkStringLength(dto.title, 32, validator, Contact.TITLE.name, 'Title should be less than 32 characters.')
    }

    private void checkContactNames(String firstName, String lastName, Boolean isCompany) {
        if (isBlank(firstName) && !isCompany) {
            validator.throwClientErrorException(Contact.FIRST_NAME.name, 'You need to enter a contact first name.')
        }

        if (isBlank(lastName)) {
            String lastNameMessageError
            if (isCompany) {
                lastNameMessageError =  'You need to enter a company name.'
            } else {
                lastNameMessageError = 'You need to enter a contact last name.'
            }
            validator.throwClientErrorException(Contact.LAST_NAME.name, lastNameMessageError)
        }
    }

    private void checkEmail(String email) {
        if (isValidEmailAddress(email)) {
            validator.throwClientErrorException(Contact.EMAIL.name, 'Please enter an email address in the correct format.')
        }
    }

    private void checkStudentFieldsLength(EntityValidator validator, StudentDTO dto) {
        checkStringLength(dto.chessn, 10, validator, Student.CHESSN.name, 'Commonwealth Higher Education Student Support Number should be less than 10 characters.')
        checkStringLength(dto.medicalInsurance, 500, validator, Student.MEDICAL_INSURANCE.name, 'Medical Insurance length should be less than 500 characters.')
        checkStringLength(dto.passportNumber, 100, validator, Student.PASSPORT_NUMBER.name, 'Passport Number length should be less than 100 characters.')
        checkStringLength(dto.townOfBirth, 256, validator, Student.TOWN_OF_BIRTH.name, 'Town of Birth length should be less than 256 characters.')
        checkStringLength(dto.uniqueLearnerIdentifier, 10, validator, Student.UNIQUE_LEARNER_INDENTIFIER.name, 'Unique Learner Identifier length should be less than 10 characters.')
        checkStringLength(dto.usi, 64, validator, Student.USI.name, 'USI length should be less than 64 characters.')
        checkStringLength(dto.visaNumber, 100, validator, Student.VISA_NUMBER.name, 'Visa Number length should be less than 100 characters.')
        checkStringLength(dto.visaType, 500, validator, Student.VISA_TYPE.name, 'Visa Type length should be less than 500 characters.')
    }

    private void checkTutorFieldsLength(EntityValidator validator, TutorDTO dto) {
        checkStringLength(dto.payrollRef, 32, validator, Tutor.PAYROLL_REF.name, 'Payroll Reference length should be less than 32 characters.')
        checkStringLength(dto.resume, 32000, validator, Tutor.RESUME.name, 'Tutor Resume length should be less than 32000 characters.')
        checkStringLength(dto.wwChildrenRef, 32, validator, Tutor.WW_CHILDREN_REF.name, 'WW Children Reference Number length should be less than 32 characters.')
    }

    private void checkStringLength(String value, int maxLength, EntityValidator validator, String fieldKey, String errorMessage) {
        if (value != null && value.length() > maxLength) {
            validator.throwClientErrorException(fieldKey, errorMessage)
        }
    }

    @Override
    void validateModelBeforeRemove(Contact cayenneModel) {
        if (cayenneModel.paymentsIn != null && cayenneModel.paymentsIn.size() > 0) {
            validator.throwClientErrorException(Contact.PAYMENTS_IN.name, 'There are payments for this contact.')
        }
        if (cayenneModel.invoices != null && cayenneModel.invoices.size() > 0) {
            validator.throwClientErrorException(Contact.INVOICES.name, 'There are invoices for this contact.')
        }
        if (cayenneModel.messages != null && cayenneModel.messages.size() > 0) {
            validator.throwClientErrorException(Contact.MESSAGES.name, 'There are messages waiting to be sent to this contact.')
        }

        if (cayenneModel?.student?.enrolments != null && cayenneModel.student.enrolments.size() > 0) {
            validator.throwClientErrorException(Student.ENROLMENTS.name, 'There are enrolments for this student.')
        }
        if (cayenneModel?.student?.priorLearnings != null && cayenneModel.student.priorLearnings.size() > 0) {
            validator.throwClientErrorException(Student.PRIOR_LEARNINGS.name, 'There are prior learnings for this student.')
        }
        if (cayenneModel?.student?.waitingLists != null && cayenneModel.student.waitingLists.size() > 0) {
            validator.throwClientErrorException(Student.WAITING_LISTS.name, 'This student is on a waiting list.')
        }

        if (cayenneModel?.tutor?.courseClassRoles?.size() > 0) {
            validator.throwClientErrorException(Tutor.COURSE_CLASS_ROLES.name, 'The tutor cannot be deleted, there are classses for this tutor.');
        }

        cayenneModel.unavailableRuleRelations*.rule.each { validateForDelete(cayenneModel.context, it.id as String)}
    }

    private static StudentDTO toRestStudent(Student dbStudent) {
        if(dbStudent == null) {
            return null
        }
        new StudentDTO().with { student ->
            student.id = dbStudent.id
            student.countryOfBirth = CountryFunctions.toRestCountry(dbStudent.countryOfBirth)
            student.countryOfResidency = CountryFunctions.toRestCountry(dbStudent.countryOfResidency)
            student.labourForceStatus = AvetmissStudentLabourStatusDTO.values()[0].fromDbType(dbStudent.labourForceStatus)
            student.language = LanguageFunctions.toRestLanguage(dbStudent.language)
            student.disabilityType = AvetmissStudentDisabilityTypeDTO.values()[0]
                    .fromDbType(dbStudent.disabilityType)
            student.chessn = dbStudent.chessn
            student.citizenship = StudentCitizenshipDTO.values()[0].fromDbType(dbStudent.citizenship)
            student.clientIndustryEmployment = ClientIndustryEmploymentTypeDTO.values()[0]
                    .fromDbType(dbStudent.clientIndustryEmployment)
            student.clientOccupationIdentifier = ClientOccupationIdentifierTypeDTO.values()[0]
                    .fromDbType(dbStudent.clientOccupationIdentifier)
            student.englishProficiency = AvetmissStudentEnglishProficiencyDTO.values()[0]
                    .fromDbType(dbStudent.englishProficiency)
            student.feeHelpEligible = dbStudent.feeHelpEligible
            student.highestSchoolLevel = AvetmissStudentSchoolLevelDTO.values()[0]
                    .fromDbType(dbStudent.highestSchoolLevel)
            student.indigenousStatus = AvetmissStudentIndigenousStatusDTO.values()[0]
                    .fromDbType(dbStudent.indigenousStatus)
            student.isOverseasClient = dbStudent.isOverseasClient
            student.isStillAtSchool = dbStudent.isStillAtSchool
            student.medicalInsurance = dbStudent.medicalInsurance
            student.passportNumber = dbStudent.passportNumber
            student.priorEducationCode = AvetmissStudentPriorEducationDTO.values()[0]
                    .fromDbType(dbStudent.priorEducationCode)
            student.specialNeeds = dbStudent.specialNeeds
            student.specialNeedsAssistance = dbStudent.specialNeedsAssistance
            student.studentNumber = dbStudent.studentNumber
            student.townOfBirth = dbStudent.townOfBirth
            student.uniqueLearnerIdentifier = dbStudent.uniqueLearnerIndentifier
            student.usi = dbStudent.usi
            student.usiStatus = UsiStatusDTO.values()[0].fromDbType(dbStudent.usiStatus)
            student.visaExpiryDate = LocalDateUtils.dateToValue(dbStudent.visaExpiryDate)
            student.visaNumber = dbStudent.visaNumber
            student.visaType = dbStudent.visaType
            student.yearSchoolCompleted = dbStudent.yearSchoolCompleted
            student.waitingLists = dbStudent.waitingLists.collect{ l -> l.course.name }
            student.concessions = dbStudent?.concessions?.collect{ toRestConcession(it) }
            student
        }
    }

    private static TutorDTO toRestTutor(Tutor dbTutor) {
        if(dbTutor == null) {
            return null
        }
        new TutorDTO().with { tutor ->
            tutor.id = dbTutor.id
            tutor.defaultPayType = PayslipPayTypeDTO.values()[0].fromDbType(dbTutor.payType)
            tutor.dateFinished = LocalDateUtils.dateToValue(dbTutor.dateFinished)
            tutor.dateStarted = LocalDateUtils.dateToValue(dbTutor.dateStarted)
            tutor.familyNameLegal = dbTutor.familyNameLegal
            tutor.givenNameLegal = dbTutor.givenNameLegal
            tutor.payrollRef = dbTutor.payrollRef
            tutor.resume = dbTutor.resume
            tutor.wwChildrenCheckedOn = LocalDateUtils.dateToValue(dbTutor.wwChildrenCheckedOn)
            tutor.wwChildrenExpiry = LocalDateUtils.dateToValue(dbTutor.wwChildrenExpiry)
            tutor.wwChildrenRef = dbTutor.wwChildrenRef
            tutor.wwChildrenStatus = WorkingWithChildrenStatusDTO.values()[0]
                    .fromDbType(dbTutor.wwChildrenStatus)
            List<CourseClass> classes =  dbTutor.courseClasses

            CourseTrait count = [getCourseClasses:  { -> classes } ] as CourseTrait

            tutor.currentClassesCount = count.currentClassesCount
            tutor.futureClasseCount = count.futureClasseCount
            tutor.selfPacedclassesCount = count.selfPacedClassesCount
            tutor.unscheduledClasseCount = count.unscheduledClassesCount
            tutor.passedClasseCount = count.passedClassesCount
            tutor.cancelledClassesCount = count.cancelledClassesCount

            tutor
        }
    }

    private static ContactRelationDTO toRestFromContactRelation(ContactRelation rel) {
        new ContactRelationDTO().with { dto ->
            dto.id = rel.id
            dto.contactFromId = rel.fromContact.id
            dto.contactFromName = GetContactFullName.valueOf(rel.fromContact, true).get()
            dto.relationId = rel.relationType.id
            dto
        }
    }

    private static ContactRelationDTO toRestToContactRelation(ContactRelation rel) {
        new ContactRelationDTO().with { dto ->
            dto.id = rel.id
            dto.contactToId = rel.toContact.id
            dto.contactToName = GetContactFullName.valueOf(rel.toContact, true).get()
            dto.relationId = rel.relationType.id
            dto
        }
    }

    private DocumentDTO getProfilePicture(Contact contact) {
        Document profilePictureDocument = getProfilePictureDocument(contact)
        if (profilePictureDocument) {
            return toRestDocument(profilePictureDocument, profilePictureDocument.currentVersion.id, documentService)
        }
        null
    }

    UsiVerificationResultDTO verifyUsi(String firstName, String lastName, LocalDate dateOfBirth, String usiCode) {

        UsiVerificationResultDTO result = new UsiVerificationResultDTO()

        if (isValidUsiString(usiCode)) {
            int usiType = USIIntegration.getAnnotation(Plugin).type()
            boolean hasUsiIntegration = ObjectSelect.query(IntegrationConfiguration).where(IntegrationConfiguration.TYPE.eq(usiType)).selectFirst(cayenneService.newContext) != null
            boolean isAvetmissIdProvided = isNotBlank(preferenceController.getAvetmissID())
            boolean hasABN = isNotBlank(preferenceController.getCollegeABN())
            if (hasUsiIntegration && isAvetmissIdProvided && hasABN) {

                USIIntegration usi = new USIIntegration(preferenceController: preferenceController, licenseService: licenseService)

                USIVerificationResult verificationResult = usi.verifyUsi(firstName, lastName, dateOfBirth, usiCode)

                if (verificationResult.hasError()) {
                    result.verifyStatus = UsiVerificationStatusDTO.SERVICE_UNAVAILABLE
                    result.errorMessage = verificationResult.errorMessage
                } else if (verificationResult.firstNameStatus != USIFieldStatus.MATCH) {
                    result.errorMessage = 'First name does not match.'
                    result.verifyStatus = UsiVerificationStatusDTO.INVALID
                } else if (verificationResult.lastNameStatus != USIFieldStatus.MATCH) {
                    result.errorMessage = 'Last name does not match.'
                    result.verifyStatus = UsiVerificationStatusDTO.INVALID
                } else if (verificationResult.usiStatus != VALID) {
                    result.errorMessage = 'USI code is invalid for this student.'
                    result.verifyStatus = UsiVerificationStatusDTO.INVALID
                } else if (verificationResult.dateOfBirthStatus != USIFieldStatus.MATCH) {
                    result.errorMessage = 'Date of birth does not match.'
                    result.verifyStatus = UsiVerificationStatusDTO.INVALID
                } else {
                    result.verifyStatus = UsiVerificationStatusDTO.VALID
                }

            } else {
                result.verifyStatus = UsiVerificationStatusDTO.DISABLED
                if (!hasUsiIntegration) {
                    result.errorMessage = 'Upgrade for automatic verification. Please contact ish support.'
                } else if (!isAvetmissIdProvided) {
                    result.errorMessage = 'Please provide AVETMISS identifier to continue verify USI.'
                } else if (!hasABN) {
                    result.errorMessage = 'Please provide ABN to continue verify USI.'
                }
            }
        } else {
            result.verifyStatus = UsiVerificationStatusDTO.INVALID_FORMAT
            result.errorMessage = 'Invalid USI code format.'
        }

        result
    }

    Closure getAction (String key, String value) {
        Closure action = super.getAction(key, value)
        if (!action) {
            validator.throwClientErrorException(key, "Unsupported attribute")
        }
        action
    }
}
