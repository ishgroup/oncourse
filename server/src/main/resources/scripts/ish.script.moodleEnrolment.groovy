if (tagName == null || tagName == "" || record.courseClass.course.hasTag(tagName)) {
    moodle {
        enrolment record
    }
}
