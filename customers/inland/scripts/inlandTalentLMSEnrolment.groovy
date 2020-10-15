String classCode = record.courseClass.uniqueCode
record.courseClass.course.modules.each { module ->
    String talentCourse =  "$classCode-$module.nationalCode"
    talentLMS {
        action 'enrol'
        course talentCourse
        student record.student
    }
}
