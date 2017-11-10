package ish.oncourse.willow.editor.webdav

import ish.oncourse.services.mail.EmailBuilder

class GetEmailBuilder {

    private ErrorEmailTemplate errorEmailTemplate
    private String to
    private String[] bodyParameters

    EmailBuilder get()
    {
        EmailBuilder emailBuilder = new EmailBuilder()
        emailBuilder.fromEmail = errorEmailTemplate.from
        emailBuilder.toEmails = to
        emailBuilder.subject = errorEmailTemplate.subject
        //we should replace all line breaks to <p> because our MailService sends mail as html
        emailBuilder.body = String.format(errorEmailTemplate.bodyTemplate, bodyParameters).replace('\n', '<p>')
        return emailBuilder
    }

    static GetEmailBuilder valueOf(ErrorEmailTemplate errorEmailTemplate, String to, String... bodyParameter) {
        GetEmailBuilder getEmailBuilder = new GetEmailBuilder()
        getEmailBuilder.errorEmailTemplate = errorEmailTemplate
        getEmailBuilder.to = to
        getEmailBuilder.bodyParameters = bodyParameter
        return getEmailBuilder
    }
    
}
