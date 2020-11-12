if (tagName == "" || record.courseClass.course.hasTag(tagName, true)) {
    canvas {
        enrolment record
        course_code record.courseClass.course.code
        section_code record.courseClass.uniqueCode
        create_section true
        create_student true
    }
}
