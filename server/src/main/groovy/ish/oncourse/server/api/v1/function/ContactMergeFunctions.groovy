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

package ish.oncourse.server.api.v1.function


import ish.common.util.DisplayableExtendedEnumeration
import ish.oncourse.server.api.v1.model.MergeLineDTO
import ish.oncourse.server.cayenne.Contact
import ish.oncourse.server.cayenne.Student
import ish.oncourse.server.cayenne.Tag
import ish.oncourse.server.cayenne.Tutor
import ish.oncourse.server.cayenne.glue.CayenneDataObject
import ish.util.DateFormatter
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.PersistentObject
import org.apache.commons.lang3.StringUtils

class ContactMergeFunctions {

    static final List<String> skippedContactProperties = new ArrayList<>()
    static final List<String> skippedStudentProperties = new ArrayList<>()
    static final List<String> skippedTutorProperties = new ArrayList<>()

    static final List<String> toManyContactProperties = new ArrayList<>()
    static final List<String> toManyStudentProperties = new ArrayList<>()
    static final List<String> toManyTutorProperties = new ArrayList<>()

    static {
        skippedContactProperties.add(Contact.ID.name)
        skippedContactProperties.add(Contact.IS_STUDENT.name)
        skippedContactProperties.add(Contact.IS_TUTOR.name)
        skippedContactProperties.add(Contact.IS_COMPANY.name)
        skippedContactProperties.add(Contact.FULLNAME_FIRSTNAME_LASTNAME_PROP)
        skippedContactProperties.add(Contact.CONTACT_DUPLICATE.name)
        skippedContactProperties.add(Contact.CREATED_ON.name)
        skippedContactProperties.add(Contact.MODIFIED_ON.name)
        skippedContactProperties.add(Contact.WILLOW_ID.name)
        skippedContactProperties.add(Contact.ALLOW_EMAIL.name)
        skippedContactProperties.add(Contact.ALLOW_SMS.name)
        skippedContactProperties.add(Contact.ALLOW_POST.name)
        skippedContactProperties.add(Contact.DELIVERY_STATUS_EMAIL.name)
        skippedContactProperties.add(Contact.DELIVERY_STATUS_SMS.name)
        skippedContactProperties.add(Contact.DELIVERY_STATUS_POST.name)
        skippedContactProperties.add(Contact.UNIQUE_CODE.name)
        skippedContactProperties.add(Contact.PHONES_PROP)
        skippedContactProperties.add(Contact.STUDENT.name)
        skippedContactProperties.add(Contact.TUTOR.name)

        skippedStudentProperties.add(Student.OUTCOMES) // those are derrived from enrolments
        skippedStudentProperties.add(Student.ID.name)
        skippedStudentProperties.add(Student.WILLOW_ID.name)
        skippedStudentProperties.add(Student.CREATED_ON.name)
        skippedStudentProperties.add(Student.MODIFIED_ON.name)
        skippedStudentProperties.add(Student.CONCESSION_TYPE_OBSOLETE.name)
        skippedStudentProperties.add(Student.CONCESSION_NUMBER_OBSOLETE.name)
		skippedStudentProperties.add(Student.STUDENT_NUMBER.name)
        skippedStudentProperties.add(Student.USI.name)
        skippedStudentProperties.add(Student.USI_STATUS.name)
        skippedStudentProperties.add(Student.CONTACT.name)

        skippedTutorProperties.add(Tutor.ID.name)
        skippedTutorProperties.add(Tutor.WILLOW_ID.name)
        skippedTutorProperties.add(Tutor.CREATED_ON.name)
        skippedTutorProperties.add(Tutor.MODIFIED_ON.name)
        skippedTutorProperties.add(Tutor.CONTACT.name)
        skippedTutorProperties.add(Tutor.SESSIONS.name)

        //------------------
        //automatic merging can be only with to many relationships
        toManyContactProperties.add(Contact.ATTACHMENT_RELATIONS.name)
        toManyContactProperties.add(Contact.CLASS_COSTS.name)
        toManyContactProperties.add(Contact.CONCESSIONS_AUTHORISED.name)
        toManyContactProperties.add(Contact.CORPORATE_PASSES.name)
        toManyContactProperties.add(Contact.FROM_CONTACTS.name)
        toManyContactProperties.add(Contact.INVOICES.name)
        toManyContactProperties.add(Contact.MESSAGES.name)
        toManyContactProperties.add(Contact.NOTE_RELATIONS.name)
        toManyContactProperties.add(Contact.PAYMENTS_IN.name)
        toManyContactProperties.add(Contact.PAYMENTS_OUT.name)
        toManyContactProperties.add(Contact.PAYSLIPS.name)
        toManyContactProperties.add(Contact.PRODUCT_ITEMS.name)
        toManyContactProperties.add(Contact.UNAVAILABLE_RULE_RELATIONS.name)
        toManyContactProperties.add(Contact.TO_CONTACTS.name)
        toManyContactProperties.add(Contact.CONTACT_DUPLICATE.name)
        toManyContactProperties.add(Contact.COUNTRY.name)
        toManyContactProperties.add(Contact.TAX_OVERRIDE.name)
        toManyContactProperties.add(Contact.QUOTES.name)
        toManyContactProperties.add(Contact.LEADS.name)
        toManyContactProperties.add(Contact.CHECKOUT_RELATIONS.name)


        toManyStudentProperties.add(Student.APPLICATIONS.name)
        toManyStudentProperties.add(Student.ATTACHMENT_RELATIONS.name)
        toManyStudentProperties.add(Student.ATTENDANCES.name)
        toManyStudentProperties.add(Student.CERTIFICATES.name)
        toManyStudentProperties.add(Student.CONCESSIONS.name)
        toManyStudentProperties.add(Student.ENROLMENTS.name)
        toManyStudentProperties.add(Student.PRIOR_LEARNINGS.name)
        toManyStudentProperties.add(Student.WAITING_LISTS.name)
        toManyStudentProperties.add(Student.COUNTRY_OF_BIRTH.name)
        toManyStudentProperties.add(Student.COUNTRY_OF_RESIDENCY.name)

        toManyTutorProperties.add(Tutor.COURSE_CLASS_ROLES.name)
        toManyTutorProperties.add(Tutor.MARKED_ATTENDANCES.name)
        toManyTutorProperties.add(Tutor.MARKED_OUTCOMES.name)
    }

    static String formatLastEnrolledDate(Date lastEnrolmentDate) {
        lastEnrolmentDate != null ? DateFormatter.formatDate(lastEnrolmentDate) : 'Never'
    }

    static String getRolesStringFor(Contact c) {
        List<String> contactRoles = new ArrayList<>()

        if (c.isTutor) {
            contactRoles.add('tutor')
        }

        if (c.isStudent) {
            contactRoles.add('student')
        }

        if (c.isCompany) {
            contactRoles.add('company')
        }

        StringUtils.capitalize(StringUtils.join(contactRoles, ', '))
    }

    static List<MergeLineDTO> getAccessibleEntityAttributes(ObjectContext context, Class<? extends PersistentObject> entityClass, List<String> skippedProperties, CayenneDataObject destination, CayenneDataObject source) {
        List<MergeLineDTO> differenceAttributeSet = new ArrayList<>()

        List<String> contactAttributes = context.getEntityResolver().getObjEntity(entityClass.simpleName).getAttributes().collect {it.name}
        contactAttributes.each {attr ->
            if (!skippedProperties.contains(attr)) {
                MergeLineDTO line = new MergeLineDTO().with { l ->
                    l.key = entityClass.simpleName + '.' + attr
                    l.label = formatPropertyForDisplay(attr)
                    l.a = formatValue(attr, destination?.getValueForKey(attr))
                    l.b = formatValue(attr, source?.getValueForKey(attr))
                    l
                }
                differenceAttributeSet += line
            }
        }
        differenceAttributeSet
    }

    static List<MergeLineDTO> getAccessibleTagRelations(ObjectContext context, List<Tag> relatedToA, List<Tag> relatedToB) {
        List<MergeLineDTO> result = new ArrayList<>()

        List<Tag> leafsA = relatedToA.findAll { it.childTags == null || it.childTags.size() == 0}
        List<Tag> leafsB = relatedToB.findAll { it.childTags == null || it.childTags.size() == 0}

        result.add(new MergeLineDTO().with { l ->
            l.key = 'tags'
            l.label ='Tags'
            l.a = StringUtils.join(leafsA.collect{t -> "#${t.name}"}, ' ')
            l.b = StringUtils.join(leafsB.collect{t -> "#${t.name}"}, ' ')
            l
        })

        result
    }

    static void mergeToManyRelations(ObjectContext context, Contact a, Contact b) {
        // merging the contact relationships requires special code, need to detect and avoid relationship to self and creating duplicates
        ContactFunctions.mergeToContactRelationshipsToA(a, b)
        ContactFunctions.mergeFromContactRelationshipsToA(a, b)

        ContactFunctions.mergeContactAttachmentRelationsToA(context, a, b)

        ContactFunctions.mergeContactToManyRelationshipsToA(context.localObject(a), context.localObject(b), toManyContactProperties)

        if (a.isStudent && b.isStudent) {
            StudentFunctions.mergeEnrolments(a.student, b.student)
            StudentFunctions.mergeStudentToManyRelationshipsToA(context.localObject(a.student), context.localObject(b.student), toManyStudentProperties)
        } else if (!a.isStudent && b.isStudent) {
            b.student.contact = a
        }

        if (a.isTutor && b.isTutor) {
            TutorFunctions.mergeTutorToManyRelationshipsToA(context.localObject(a.tutor), context.localObject(b.tutor), toManyTutorProperties)
        } else if (!a.getIsTutor() && b.getIsTutor()) {
            b.tutor.contact = a
        }
    }

    static String formatPropertyForDisplay(String property) {
        if (property == Contact.GENDER.name) {
            return "Gender"
        }
        if (property == Student.IS_STILL_AT_SCHOOL.name) {
            return "Still at school"
        }
        if (property == Student.IS_OVERSEAS_CLIENT.name) {
            return "Overseas"
        }
        if (property == Student.LABOUR_FORCE_STATUS.name) {
            return "Employment category"
        }
        if (property == Student.UNIQUE_LEARNER_INDENTIFIER.name) {
            return "Government student number (VIC/QLD)"
        }
        if (property == Student.MEDICAL_INSURANCE.name) {
            return "Overseas health care cover"
        }
        if (property == Student.PRIOR_EDUCATION_CODE.name) {
            return "Prior educational achievement"
        }
        if (property == Student.YEAR_SCHOOL_COMPLETED.name) {
            return "Achieved in year"
        }
        if (property == Student.SPECIAL_NEEDS_ASSISTANCE.name) {
            return "Disability support requested"
        }
        if (property == Student.ENGLISH_PROFICIENCY.name) {
            return "Proficiency in spoken English"
        }

        StringBuilder buff = new StringBuilder()
        int i = 0
        for (int count = property.length(); i < count; i++) {
            char ch = property.charAt(i)
            if (i == 0) {
                buff.append(Character.toUpperCase(ch))
            } else {
                if (Character.isUpperCase(ch)) {
                    buff.append(" ")
                }
                buff.append(Character.toLowerCase(ch))
            }
        }
        return buff.toString()
    }

    static String formatValue(String key, Object value) {
        if (value instanceof Boolean) {
            return value ? 'Yes' : 'No'
        }

        if (value instanceof DisplayableExtendedEnumeration) {
            return value.displayName
        }

        if (value == null)
            return null

        if (value instanceof Date) {
            return DateFormatter.formatDate(value)
        }
        if (value instanceof BigDecimal) {
            return ((BigDecimal)value).toPlainString()
        }
        return value.toString()
    }

    static List<MergeLineDTO> removeEmptyLines(List<MergeLineDTO> lines) {
        lines.findAll{ line -> line.a != null || line.b != null }
    }
}
