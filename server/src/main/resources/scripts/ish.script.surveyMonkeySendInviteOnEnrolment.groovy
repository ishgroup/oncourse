if (tagName == null || tagName == "" || record.courseClass.course.hasTag(tagName)) {
    surveyMonkey {
        contact record.student.contact
    }
}