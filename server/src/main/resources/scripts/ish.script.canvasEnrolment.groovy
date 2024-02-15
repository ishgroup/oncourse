if (tagName == "" || record.courseClass.course.hasTag(tagName, true)) {
    if (!record.courseClass.isHybrid ||
            (record.courseClass.isHybrid && record.courseClass.startDateTime <= new Date() && record.courseClass.endDateTime >= new Date())) {
        canvas {
            enrolment record
            course_code record.courseClass.course.code
            section_code record.courseClass.uniqueCode
            create_section true
            create_student true
        }
    }
}
