message {
    template applicationReceivedTemplate
    record record
}

message {
    to preference.email.admin
    subject "Application for enrolment received"
    content "Hi \n\n${record.student.contact.fullName} has just applied for ${record.course.name}. \n\nThis is an automated notification from onCourse to advise follow up may be required"
}
