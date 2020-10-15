def run(args) {
    
    /**
    * Get classes ended yesterday to send feedback form
    */
    def endDate = Calendar.getInstance().getTime()
    endDate.set(hourOfDay: 0, minute: 0, second: 0)

    def classes = ObjectSelect.query(CourseClass)
            .where(CourseClass.IS_CANCELLED.eq(false))
            .and(CourseClass.START_DATE_TIME.ne(null))
            .and(CourseClass.END_DATE_TIME.between(endDate - 2, endDate-1))
            .select(args.context)

    classes.removeAll { it.course.hasTag("Subjects/Discussion Groups", true) }

    classes.each { cc ->
        def course = cc.course
        
        /**
        * Creates the class list
        */

        if (cc.tutorRoles.size() > 0 ) {
            List<CourseClass> cclist = cc?.tutorRoles?.first()?.tutor.courseClasses
            
            // removes classes of the same course the student just completed
            cclist.removeAll { it.course.name.equals(cc.course.name) || (it.startDateTime < endDate) }
            cclist.removeAll { !(it.isShownOnWeb || it.isActive) }
            cclist.sort{ it.startDateTime }

            
            if(cclist.size() >= 3){
                cclist = cclist.subList(0, 3)
            }

            /**
            * Get primary subject tag
            */

            def tagPaths = []

            def tags =  getSubjects(cc.course.tags)
            
            tags.removeAll{ tag -> !(tag.isWebVisible) }
            tags.each { tag ->
                def tagpath = tag.pathName.split("-")
                def tagString = tagpath.drop(0).join(",").replace(",", "/").replace(" ", "+").toLowerCase()
                tagPaths << tagString
            }

            cc.successAndQueuedEnrolments.each() { e -> 
                email {
                    template "WEA Syd Course Completion 2.0"
                    from "info@weasydney.nsw.edu.au"
                    to e.student.contact
                    bindings enrolment:e, tagPaths: tagPaths, cclist: cclist
                }
            } 
        }
    }
} 

def getSubjects(List<Tag> tags) {
    tags.findAll { t ->
        t.root.name.equals("Subjects")
    }
    return tags
}  