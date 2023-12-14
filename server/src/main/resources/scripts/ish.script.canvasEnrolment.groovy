if (tagName == "" || record.courseClass.course.hasTag(tagName, true)) {
    def today = new Date().clearTime()
    if (!record.courseClass.isHybrid ||
            (record.courseClass.isHybrid && record.courseClass.startDateTime <= today && record.courseClass.endDateTime >= today)) {
        canvas {
            enrolment record
            course_code record.courseClass.course.code
            section_code record.courseClass.uniqueCode
            create_section true
            create_student true
        }
    }
}
