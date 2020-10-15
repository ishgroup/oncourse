/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import ish.oncourse.server.AngelServer
import ish.oncourse.server.replication.handler.USIVerificationService

def run(args) {
    def context = args.context

    def usiService = AngelServer.injector.getInstance(USIVerificationService)

    //1) get all contacts with verified USI
    def contacts = ObjectSelect.query(Contact)
            .prefetch(Contact.STUDENT.joint())
            .where(Contact.STUDENT.dot(Student.USI_STATUS).eq(UsiStatus.VERIFIED))
            .select(context)

    def failedList = new ArrayList()

    //2) get all contacts with failed USI
    contacts.each() { contact ->

        def usiRequest = new USIVerificationRequest()
        usiRequest.setStudentFirstName(contact.firstName)
        usiRequest.setStudentLastName(contact.lastName)
        usiRequest.setStudentBirthDate(contact.birthDate)
        usiRequest.setUsiCode(contact.student.usi)
        usiRequest.setOrgCode(preference.avetmiss.identifier)

        def usiResult

        try {
            usiResult = usiService.verifyUsi(usiRequest)
        } catch (Exception e) {
            println e
        }

        if (!(usiResult?.usiStatus == USIVerificationStatus.VALID && usiResult?.firstNameStatus == USIFieldStatus.MATCH
                && usiResult?.lastNameStatus == USIFieldStatus.MATCH && usiResult?.dateOfBirthStatus == USIFieldStatus.MATCH)) {
            failedList.add(contact)
        }
    }

    //3) verify again contacts with birthDate one day forward.
    failedList.each() { contact ->
        def usiRequest = new USIVerificationRequest()
        usiRequest.setStudentFirstName(contact.firstName)
        usiRequest.setStudentLastName(contact.lastName)
        usiRequest.setStudentBirthDate(contact.birthDate + 1)
        usiRequest.setUsiCode(contact.student.usi)
        usiRequest.setOrgCode(preference.avetmiss.identifier)

        def usiResult

        try {
            usiResult = usiService.verifyUsi(usiRequest)
        } catch (Exception e) {
            println e
        }

        //4) if verification is successful save the changes
        if (usiResult?.usiStatus == USIVerificationStatus.VALID && usiResult?.firstNameStatus == USIFieldStatus.MATCH
                && usiResult?.lastNameStatus == USIFieldStatus.MATCH && usiResult?.dateOfBirthStatus == USIFieldStatus.MATCH) {

            //set the next day 12:00pm
            def date = contact.birthDate + 1
            date.set(hourOfDay: 12, minute: 0, second: 0)
            contact.setBirthDate(date)
            context.commitChanges()
        }
    }
}
