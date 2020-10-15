/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

records.each { Enrolment e ->
    Contact contact = e.student.contact
    List<CustomField> cfs = e.customFields
    List<ContactRelation> relations = contact.fromContacts.findAll {it.relationType.id==-1}
    Contact parent1 = relations.size() > 0 ? relations[0].fromContact : null
    Contact parent2 = relations.size() > 1 ? relations[1].fromContact : null

    csv << [
            "Student number": e.student.studentNumber,
            "First name":  contact.firstName,
            "Last name":  contact.lastName,
            "Class code": e.courseClass.uniqueCode,
            "Course name": e.courseClass.course.name,
            "Student email address": contact.email,
            "Student phone number": contact.mobilePhone?"'${contact.mobilePhone}'":contact.homePhone?"'${contact.homePhone}'":"no data",
            "Street name": contact.street,
            "Postcode": contact.postcode,
            "Suburb": contact.suburb,
            "State": contact.state,
            "Gender": contact.gender ? contact.gender.displayName : "no data",
            "Date of birth": contact.dateOfBirth ? contact.dateOfBirth.format('dd/MM/yyyy') : "no data",
            "RSProgram": cfs.find{ cf -> cf.customFieldType.key == 'RSProgram'}?.value,
            "Instrument for this enrolment": cfs.find{ cf -> cf.customFieldType.key == 'classInstrument'}?.value,
            "Current tutor name": cfs.find{ cf -> cf.customFieldType.key == 'CurrentTeacherName'}?.value,
            "Prior training":  cfs.find{ cf -> cf.customFieldType.key == 'PriorTraining'}?.value,
            "Approximate AMEB level": cfs.find{ cf -> cf.customFieldType.key == 'AMEBLevel'}?.value,
            "Years playing": cfs.find{ cf -> cf.customFieldType.key == 'Experience'}?.value,
            "School grade": cfs.find{ cf -> cf.customFieldType.key == 'SchoolGrade'}?.value,
            "School type": cfs.find{ cf -> cf.customFieldType.key == 'SchoolType'}?.value,
            "CHS instrument study": cfs.find{ cf -> cf.customFieldType.key == 'chsinstrument'}?.value,
            "CHS Teacher": cfs.find{ cf -> cf.customFieldType.key == 'chsteacher'}?.value,
            "How did you hear about Open Academy?": contact.customFields.find{cf -> cf.customFieldType.key == 'Channel'}?.value,
            "Library membership": contact.customFields.find{cf -> cf.customFieldType.key == 'libraryMembership'}?.value,
            "Library membership expiry": contact.customFields.find{cf -> cf.customFieldType.key == 'LibraryMembershipExp'}?.value,
            "AMEB candidate number": contact.customFields.find{cf -> cf.customFieldType.key == 'AMEBCandidateNo'}?.value,
            "Main instrument": contact.customFields.find{cf -> cf.customFieldType.key == 'instrument'}?.value,
            "Voice type": contact.customFields.find{cf -> cf.customFieldType.key == 'VoiceType'}?.value,
            "type of related record 1": 'Parent or Guardian',
            "first name parent or guardian record 1": parent1?.firstName,
            "last name parent or guardian record 1": parent1?.lastName,
            "email parent or guardian record 1": parent1?.email,
            "mobile parent or guardian record 1": parent1?.mobilePhone,
            "homePhone parent or guardian record 1": parent1?.homePhone,
            "workPhone parent or guardian record 1": parent1?.workPhone,
            "address parent or guardian record 1": parent1?.address,
            "suburb parent or guardian record 1": parent1?.suburb,
            "state parent or guardian record 1": parent1?.state,
            "postcode parent or guardian record 1": parent1?.postcode,
            "type of related record 2": 'Parent or Guardian',
            "first name parent or guardian record 2": parent2?.firstName,
            "last name parent or guardian record 2": parent2?.lastName,
            "email parent or guardian record 2": parent2?.email,
            "mobile parent or guardian record 2": parent2?.mobilePhone,
            "homePhone parent or guardian record 2": parent2?.homePhone,
            "workPhone parent or guardian record 2": parent2?.workPhone,
            "address parent or guardian record 2": parent2?.address,
            "suburb parent or guardian record 2": parent2?.suburb,
            "state parent or guardian record 2": parent2?.state,
            "postcode parent or guardian record 2": parent2?.postcode,
            "University of Sydney email address": cfs.find{cf -> cf.customFieldType.key == 'USYDemail'}?.value,
            "USYD Staff/Student ID": cfs.find{cf -> cf.customFieldType.key == 'USYDid'}?.value,
            "Tee-shirt size": cfs.find{cf -> cf.customFieldType.key == 'teeshirtsize'}?.value,
            "Country": contact.customFields.find{cf -> cf.customFieldType.key == 'country'}?.value,
            "international phone number": contact.customFields.find{cf -> cf.customFieldType.key == 'internationalphone'}?.value,
    ]
}
