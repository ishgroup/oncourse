def application = record
message {
    template applicationReceivedTemplate
    record application
}

email {
    to preference.email.admin
    subject "Application for enrolment received"
    content "Hi \n\n${application.student.contact.fullName} has just applied for ${application.course.name}. \n\nThis is an automated notification from onCourse to advise follow up may be required"
}
