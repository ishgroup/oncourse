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

package ish.oncourse.server.deduplication

import javax.inject.Inject
import groovy.transform.CompileStatic
import ish.oncourse.server.CayenneService
import ish.oncourse.server.api.dao.ContactDao
import ish.oncourse.server.api.dao.ContactDuplicateDao
import ish.oncourse.server.api.dao.CourseClassDao
import ish.oncourse.server.api.dao.CustomFieldDao
import ish.oncourse.server.api.dao.EnrolmentDao
import ish.oncourse.server.api.dao.InvoiceDao
import ish.oncourse.server.api.dao.NoteDao
import ish.oncourse.server.api.dao.TagDao
import ish.oncourse.server.api.function.DateFunctions
import ish.oncourse.server.api.v1.function.ContactMergeFunctions
import ish.oncourse.server.api.v1.function.EntityFunctions
import ish.oncourse.server.api.v1.model.InfoLineDTO
import ish.oncourse.server.api.v1.model.MergeLineDTO
import ish.oncourse.server.cayenne.Contact
import ish.oncourse.server.cayenne.ContactCustomField
import ish.oncourse.server.cayenne.ContactTagRelation
import ish.oncourse.server.cayenne.CourseClass
import ish.oncourse.server.cayenne.Student
import ish.oncourse.server.cayenne.Tag
import ish.oncourse.server.cayenne.Tutor
import ish.oncourse.server.services.ISystemUserService
import ish.util.DateFormatter
import ish.util.TimeFormatter
import org.apache.cayenne.ObjectContext
import org.apache.commons.lang3.StringUtils

@CompileStatic
class ContactMergeService {

    @Inject
    CayenneService cayenneService
    @Inject
    ContactDao contactDao
    @Inject
    CourseClassDao courseClassDao
    @Inject
    EnrolmentDao enrolmentDao
    @Inject
    InvoiceDao invoiceDao
    @Inject
    CustomFieldDao customFieldDao
    @Inject
    TagDao tagDao
    @Inject
    ISystemUserService systemUserService
    @Inject
    ContactDuplicateDao contactDuplicateDao
    @Inject
    NoteDao noteDao

    void checkMergeAvailability(Contact contactA, Contact contactB) {

        if (!contactA) {
            throw new Exception("Contact A not found.")
        }

        if (!contactB) {
            throw new Exception("Contact B not found.")
        }

        if (contactA.isCompany ^ contactB.isCompany) {
            throw new Exception("You can not merge company and person.")
        }

        ObjectContext context = cayenneService.newContext

        if (contactA.isStudent && contactB.isStudent) {
            if(contactA.student.usi != null && StringUtils.EMPTY != contactA.student.usi &&
                    contactB.student.usi != null && StringUtils.EMPTY != contactB.student.usi &&
                    contactA.student.usi != contactB.student.usi) {
                throw new Exception("The selected students have different unique student identifiers (USI)")
            }

            CourseClass classSameForBothContacts = courseClassDao.getSameClassesForContacts(context, context.localObject(contactA), context.localObject(contactB))[0]

            if (classSameForBothContacts != null) {
                throw new Exception("The selected students are currently enrolled in the same class (${classSameForBothContacts.uniqueCode}), " +
                        "please cancel or refund the appropriate enrolment prior to merging of those contacts.")
            }
        }
    }

    /**
     * Get object with contact differences before merge
     * @param a
     * @param b
     * @return
     */
    List<MergeLineDTO> getDifferenceAttributes(Contact a, Contact b) {
        Closure<MergeLineDTO> createStudentNumberLine = { Contact first, Contact second ->
            new MergeLineDTO().with { l ->
                l.key = "${Student.simpleName}.${Student.STUDENT_NUMBER.name}"
                l.label = ContactMergeFunctions.formatPropertyForDisplay(Student.STUDENT_NUMBER.name)
                l.a = first.isStudent ? first.student.studentNumber : null
                l.b = second.isStudent ? second.student.studentNumber : null
                l
            }
        }

        List<MergeLineDTO> result = new ArrayList<>()
        ObjectContext context = cayenneService.newContext

        result.addAll(ContactMergeFunctions
                .getAccessibleEntityAttributes(context,
                        Contact.class,
                        ContactMergeFunctions.skippedContactProperties, a, b))

        result.add(new MergeLineDTO().with { it ->
            it.key = "${Contact.simpleName}.${Contact.TAX_OVERRIDE.name}"
            it.label = "Tax Override"
            it.a = a.taxOverride?.taxCode
            it.b = b.taxOverride?.taxCode
            it
        })
        result.add(new MergeLineDTO().with { it ->
            it.key = "${Contact.simpleName}.${Contact.COUNTRY.name}"
            it.label = "Country"
            it.a = a.country?.name
            it.b = b.country?.name
            it
        })
        if (a.student != null && b.student != null) {
            result.add(createStudentNumberLine(a, b))
            result.addAll(ContactMergeFunctions.getAccessibleEntityAttributes(context, Student.class, ContactMergeFunctions.skippedStudentProperties, a.student, b.student))
            result.add(new MergeLineDTO().with { it ->
                it.key = "${Student.simpleName}.${Student.COUNTRY_OF_BIRTH.name}"
                it.label = "Country of birth"
                it.a = a.student?.countryOfBirth?.name
                it.b = b.student?.countryOfBirth?.name
                it
            })
            result.add(new MergeLineDTO().with { it ->
                it.key = "${Student.simpleName}.${Student.COUNTRY_OF_RESIDENCY.name}"
                it.label = "Country of residency"
                it.a = a.student?.countryOfResidency?.name
                it.b = b.student?.countryOfResidency?.name
                it
            })
            result.add(new MergeLineDTO().with { it ->
                it.key = "${Student.simpleName}.${Student.LANGUAGE.name}"
                it.label = "Language"
                it.a = a.student?.language?.name
                it.b = b.student?.language?.name
                it
            })
        }

        List<Tag> leafTagsA = tagDao.getRelatedFor(context, a)
        List<Tag> leafTagsB = tagDao.getRelatedFor(context, b)
        result.addAll(ContactMergeFunctions.getAccessibleTagRelations(context, leafTagsA, leafTagsB))

        result.addAll(getAccessibleContactCustomFields(context, a, b))

        if (a.tutor != null && b.tutor != null) {
            result.addAll(ContactMergeFunctions
                    .getAccessibleEntityAttributes(context,
                            Tutor.class,
                            ContactMergeFunctions.skippedTutorProperties,
                            a.tutor,
                            b.tutor))
        }

        result = ContactMergeFunctions.removeEmptyLines(result)

        result
    }

    private List<MergeLineDTO> getAccessibleContactCustomFields(ObjectContext context, Contact contactA, Contact contactB) {
        Map<String, MergeLineDTO> result = new HashMap<>()

        List<ContactCustomField> customFields = customFieldDao.getCustomFieldsForContacts(context, contactA.id, contactB.id)

        customFields.each { f ->
            String customFieldKey = 'customField.' + f.customFieldType.key

            MergeLineDTO line = result.get(customFieldKey)

            if (line == null) {
                line = new MergeLineDTO()
            }

            line.with { l ->
                l.key = customFieldKey
                if (f.relatedObject.id == contactA.id) {
                    l.a = f.value
                }
                if (f.relatedObject.id == contactB.id) {
                    l.b = f.value
                }
                l.label = f.customFieldType.name
                l
            }
            result.put(customFieldKey, line)
        }
        result.collect { entry -> entry.value }
    }


    /**
     * Merge fields and relations of contact B to A
     * @param a contact A
     * @param b contact B
     * @param mergeAttributes map of attributes and choices, get it by <code>getDifferenceAttributes</code>
     */
    void merge(Contact a, Contact b, Map<String, String> mergeAttributes) {

        Closure moveCustomFieldToA = { ObjectContext context, CustomFieldDao dao, Contact cA, Contact cB, String customFieldKey ->
            Contact localContactA = context.localObject(cA)
            Contact localContactB = context.localObject(cB)

            CustomFieldDao customFieldDao = new CustomFieldDao()
            ContactCustomField customFieldB = customFieldDao.getContactCustomFieldByKey(context, localContactB, customFieldKey)
            ContactCustomField customFieldA = customFieldDao.getContactCustomFieldByKey(context, localContactA, customFieldKey)

            if (customFieldB != null) {
                if (customFieldA != null) {
                    customFieldA.value = customFieldB.value
                } else {
                    customFieldA = context.newObject(ContactCustomField)
                    customFieldA.relatedObject = localContactA
                    customFieldA.customFieldType = customFieldB.customFieldType
                    customFieldA.value = customFieldB.value
                }
            }
        }

        Closure moveContactTagsToA = { ObjectContext context, Contact cA, Contact cB ->
            List<ContactTagRelation> tagRelationsA = new ArrayList<>(cA.taggingRelations)
            context.deleteObjects(tagRelationsA)
            List<ContactTagRelation> tagRelationsB = new ArrayList<>(cB.taggingRelations)
            tagRelationsB.each { tr -> tr.taggedContact = cA }
        }

        Closure chooseAndSaveUSI = {Contact cA, Contact cB ->
            if (cB.student?.usi != null && StringUtils.EMPTY != cB.student?.usi && (cA.student?.usi == null || StringUtils.EMPTY == cA.student?.usi)) {
                cA.student.usi = cB.student.usi
            }
        }

        ObjectContext context = cayenneService.newContext
        a = context.localObject(a)
        b = context.localObject(b)

        contactDuplicateDao.newObjectForContacts(context, a, b, systemUserService.currentUser)
        ContactMergeFunctions.mergeToManyRelations(context, a, b)

        chooseAndSaveUSI(a, b)

        mergeAttributes.each { Map.Entry<String, String> entry ->
            String[] params = entry.key.split('[.]')

            String attrGroup = null
            String attrKey = null
            String attrChoice = entry.value

            if (params.length == 2) {
                attrGroup = params[0]
                attrKey = params[1]
            } else if (params.length == 1) {
                attrGroup = params[0]
            }

            if (attrChoice != null && attrChoice.toUpperCase() == 'B') {
                switch (attrGroup) {
                    case Contact.simpleName: EntityFunctions.moveEntityFieldToA(a, b, attrKey, ContactMergeFunctions.skippedContactProperties)
                        break
                    case Student.simpleName: EntityFunctions.moveEntityFieldToA(a.student, b.student, attrKey, ContactMergeFunctions.skippedStudentProperties)
                        break
                    case Tutor.simpleName: EntityFunctions.moveEntityFieldToA(a.tutor, b.tutor, attrKey, ContactMergeFunctions.skippedTutorProperties)
                        break
                    case 'customField': moveCustomFieldToA(context, customFieldDao, a, b, attrKey)
                        break
                    case 'tags' : moveContactTagsToA(context, a, b)
                        break
                }
            }
        }

        String dStudentMergeInfo = a.student && b.student ?
                String.format("student %s ", 'B' == getStudentNumberAttributeChoice(mergeAttributes) ? a.student.studentNumber : b.student.studentNumber) : ""

        String mergeInfo = String.format("Merged %sby %s %s on %s %s",
                dStudentMergeInfo,
                systemUserService?.currentUser?.firstName,
                systemUserService?.currentUser?.lastName,
                DateFormatter.formatDate(new Date(), true, null),
                TimeFormatter.formatTime(new Date(), TimeZone.getDefault()))

        noteDao.createNoteRelatedWithContact(context, a, mergeInfo, systemUserService.currentUser)

        Long studentNumber = b.student && 'B' == getStudentNumberAttributeChoice(mergeAttributes) ? b.student.studentNumber : null

        context.deleteObjects(b.customFields)

        if(b.abandonedCarts && !b.abandonedCarts.empty) {
            if (a.abandonedCarts.isEmpty() || a.abandonedCarts.first().createdOn.before(b.abandonedCarts.first().createdOn)) {
                if(!a.abandonedCarts.empty)
                    context.deleteObjects(a.abandonedCarts)

                b.abandonedCarts.first().setPayer(a)
            }
        }

        if (b.student != null) {
            context.deleteObject(b.student)
        }

        if (b.tutor != null) {
            context.deleteObject(b.tutor)
        }
        context.deleteObject(b)
        context.commitChanges()

//        Call context.commitChanges() one more time after first context.commitChanges(), because if mergeDeliveryStatusAndAllowMarketing(a, b) will be called in all of changes before first context.commitChanges() deliveryStatusEmail and deliveryStatusSMS could be 0,
//        because contact B can have messages, then messages will be updated (id), entity message has method preUpdate() where getContact().setDeliveryStatusEmail(0) and getContact().setDeliveryStatusSms(0).
        mergeDeliveryStatusAndAllowMarketing(a, b)
        context.commitChanges()

        if (studentNumber != null) {
            a.student.studentNumber = studentNumber
            context.commitChanges()
        }
    }

    List<InfoLineDTO> getInfoLineAttributes(Contact contactA, Contact contactB) {
        ObjectContext context = cayenneService.newContext
        Closure<InfoLineDTO> buildInfoLine = { String label, String a, String b ->
            new InfoLineDTO().with { i ->
                i.label = label
                i.a = a ?: StringUtils.EMPTY
                i.b = b ?: StringUtils.EMPTY
                i
            }
        }

        Closure<String> getEnrolmentCountString = { Contact c ->
            if (c.student != null) {
                int enrolmentsCount = enrolmentDao.getEnrolmentsCount(context, c.student)
                return enrolmentsCount > 0 ? Integer.toString(enrolmentsCount) : 'None'
            }
            null
        }

        List<InfoLineDTO> result = new ArrayList<>()

        result.add(buildInfoLine(null, ContactMergeFunctions.getRolesStringFor(contactA), ContactMergeFunctions.getRolesStringFor(contactB)))
        result.add(buildInfoLine('Created', DateFormatter.formatDate(contactA.createdOn),
                DateFormatter.formatDate(contactB.createdOn)))
        result.add(buildInfoLine('Modified', DateFunctions.getTimeAgo(new Date().time - contactA.modifiedOn.getTime()),
                DateFunctions.getTimeAgo(new Date().time - contactB.modifiedOn.getTime())))
        result.add(buildInfoLine('Last enrolled', contactA.isStudent ? ContactMergeFunctions.formatLastEnrolledDate(enrolmentDao.getLastEnrolmentFor(context, contactA.student)?.createdOn) : null,
                contactB.isStudent ? ContactMergeFunctions.formatLastEnrolledDate(enrolmentDao.getLastEnrolmentFor(context, contactB.student)?.createdOn) : null))
        result.add(buildInfoLine('Invoices', Integer.toString(invoiceDao.getInvoicesCount(contactA)),
                Integer.toString(invoiceDao.getInvoicesCount(contactB))))

        result.add(buildInfoLine('Enrolments', getEnrolmentCountString(contactA), getEnrolmentCountString(contactB)))

        result.add(buildInfoLine('USI', contactA?.student?.usi, contactB?.student?.usi))

        result
    }

    private static String getStudentNumberAttributeChoice(Map<String, String> mergeAttributes) {
        mergeAttributes.get(Student.simpleName + '.' + Student.STUDENT_NUMBER.name).toUpperCase()
    }

    private static void mergeDeliveryStatusAndAllowMarketing(Contact cA, Contact cB) {
        cA.allowEmail = cA.allowEmail && cB.allowEmail
        cA.allowPost = cA.allowPost && cB.allowPost
        cA.allowSms = cA.allowSms && cB.allowSms
        cA.deliveryStatusEmail = Math.max(cA.deliveryStatusEmail, cB.deliveryStatusEmail)
        cA.deliveryStatusPost = Math.max(cA.deliveryStatusPost, cB.deliveryStatusPost)
        cA.deliveryStatusSms = Math.max(cA.deliveryStatusSms, cB.deliveryStatusSms)
    }
}
