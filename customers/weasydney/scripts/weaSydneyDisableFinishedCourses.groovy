/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

def run(args) {

    def result = query {
           entity "Course"
           query "isShownOnWeb and currentlyOffered"
           context args.context
       }

    def today = new Date()

    result.removeAll { c -> c.tags.find { t -> t.name == "AlwaysOn" }  }

    result.each { Course c ->
        def recentOfFutureClasses =  c.courseClasses.findAll { cc ->
            !cc.isCancelled && cc.startDateTime != null && (cc.startDateTime > today ||  (today - cc.startDateTime) <= 22)
        }
        if(!recentOfFutureClasses) {
            c.setCurrentlyOffered(false)
            c.setIsShownOnWeb(false)
        }
    }
    args.context.commitChanges()
}
