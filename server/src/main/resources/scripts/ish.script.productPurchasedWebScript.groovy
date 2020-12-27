if (PaymentSource.SOURCE_WEB.equals(record.invoiceLine.invoice.source)) {
    message {
        to preference.email.admin
        from preference.email.admin
        subject "${record.product.name} purchased"
        content "Hi \n\n${record.contact.fullName} has just purchased ${record.product.name} on the web \n\nThis is an automated notification from onCourse"
    }
}
