/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

def run(args) {
    def e = args.entity

  if ( e.courseClass.deliveryMode == ish.common.types.DeliveryMode.ONLINE || e.courseClass.course.code == "baristacourse" ) {




    cloudassess {
        name "cloud assess"
        action "enrol"
        enrolment e
    }

    e.addTag("LMS/CloudAssess")
   args.context.commitChanges()

   }
}
