package ish.oncourse.willow.editor.webdav

class ErrorEmailTemplate {
    private String from
    private String subject
    private String bodyTemplate

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
        ErrorEmailTemplate errorEmailTemplate = new ErrorEmailTemplate();
        errorEmailTemplate.from = from;
        errorEmailTemplate.subject = subject;
        errorEmailTemplate.bodyTemplate = bodyTemplate;
        return errorEmailTemplate;
    }
}
