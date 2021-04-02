/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

records = query {
    entity "Course"
    query "courseClasses not is null and taggingRelations.tag.name not is \"AlwaysOn\""
    context context
}

List<Long> redirectTagId = query {
    entity "Tag"
    query "name is \"Redirect\""
    context context
}.collect { it.id }

StringBuilder listOfCourses = new StringBuilder()
int numberOfCourses = 0

records.each { Course course ->
    List<CourseClass> recentOfFutureClasses = course.courseClasses.findAll { cc ->
        cc.isActive && ((cc.startDateTime != null && cc.endDateTime != null && cc.endDateTime > new Date()) || cc.isDistantLearningCourse)
    } as List<CourseClass>

    if (recentOfFutureClasses.empty) {
        course.setCurrentlyOffered(false)
        course.setIsShownOnWeb(false)

        ish.oncourse.server.api.v1.function.TagFunctions.updateTags(course, course.taggingRelations, redirectTagId, CourseTagRelation, course.context)

        listOfCourses.append(course.code).append(' ')
                .append(course.name).append('\n')
        numberOfCourses += 1
    }
}
context.commitChanges()

if (listOfCourses.toString() != "") {
    message {
        to "James.Laughlin@weasydney.nsw.edu.au"
        subject "${numberOfCourses} courses which were disabled"
        content "The following courses were found with no future classes. They have been disabled:\n\n" + listOfCourses.toString()
    }
}