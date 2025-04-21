if (record.student.contact.email && record.student.contact.allowEmail) {
    mailchimp {
        name nameOfIntegration
        action integrationAction
        email record.student.contact.email
        firstName record.student.contact.firstName
        lastName record.student.contact.lastName
        postcode record.student.contact.postcode
    }
}
