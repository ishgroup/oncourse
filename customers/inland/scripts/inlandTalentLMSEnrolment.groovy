/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

String classCode = record.courseClass.uniqueCode
record.courseClass.course.modules.each { module ->
    String talentCourse =  "$classCode-$module.nationalCode"
    talentLMS {
        action 'enrol'
        course talentCourse
        student record.student
    }
}
