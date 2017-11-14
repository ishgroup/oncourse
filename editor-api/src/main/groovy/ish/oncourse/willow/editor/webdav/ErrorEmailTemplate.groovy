package ish.oncourse.willow.editor.webdav

class ErrorEmailTemplate {
    private String from
    private String subject
    private String bodyTemplate

    void setFrom(String from) {
        this.from = from
    }

    void setSubject(String subject) {
        this.subject = subject
    }

    void setBodyTemplate(String bodyTemplate) {
        this.bodyTemplate = bodyTemplate
    }

    String getFrom() {
        return from
    }

    String getSubject() {
        return subject
    }

    String getBodyTemplate() {
        return bodyTemplate
    }

    static ErrorEmailTemplate valueOf(String from, String subject, String bodyTemplate) {
        ErrorEmailTemplate errorEmailTemplate = new ErrorEmailTemplate()
        errorEmailTemplate.from = from
        errorEmailTemplate.subject = subject
        errorEmailTemplate.bodyTemplate = bodyTemplate
        return errorEmailTemplate
    }
}
