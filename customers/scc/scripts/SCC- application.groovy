def run(args) {

    def a = args.entity

      smtp {
          subject "ACTION: Application created"
          from "info@scc.nsw.edu.au"
          to "enrolment.applications@scc.nsw.edu.au"
          content "An application for " + a.course.code + " " + a.course.name + " has been received. Please contact the applicant."
      }
}