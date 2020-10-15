/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

Enrolment e = args.entity

document {
    action "create"
    content report {
        keycode "ish.oncourse.nonVetCertificate"
        records e
    }
    name "${e.courseClass.uniqueCode}_${e.student.contact.lastName}_${e.student.contact.firstName}_Certificate_Attendance.pdf"
    mimeType "application/pdf"
    permission AttachmentInfoVisibility.STUDENTS
    attach e
}

email {
    template "Conservatorium certificate available"
    bindings enrolment: e
    to e.student.contact
}
