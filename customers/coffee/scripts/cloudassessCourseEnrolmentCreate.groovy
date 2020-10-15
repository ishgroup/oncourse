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