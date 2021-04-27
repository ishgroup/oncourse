if (record.courseClass.sessionsCount == 1 && record.courseClass.startDateTime != null &&
        record.courseClass.endDateTime != null && !record.courseClass.tutorRoles.isEmpty()) {

    String startDate = record.courseClass.startDateTime.format("yyyy-MM-dd HH:mm")
    String endDate  = record.courseClass.endDateTime.format("yyyy-MM-dd HH:mm")
    def classes = query {
        entity "CourseClass"
        query "startDateTime not is null and startDateTime < ${endDate} and endDateTime not is null and endDateTime > ${startDate} and room.id is ${record.courseClass.room.id} and sessionsCount is 1 and id != ${record.courseClass.id}"
    }
    classes = classes.findAll { cc ->
        cc.tutorRoles.collect { tr -> tr.tutor.id }
                .contains(record.courseClass.tutorRoles.collect { tr -> tr.tutor.id }.get(0))
    }

    classes.each { cc -> cc.maximumPlaces = cc.maximumPlaces - 1 }

    classes[0]?.context.commitChanges()
}