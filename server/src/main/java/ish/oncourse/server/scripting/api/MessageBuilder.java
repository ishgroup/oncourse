package ish.oncourse.server.scripting.api;

import ish.oncourse.server.cayenne.Contact;
import ish.oncourse.server.cayenne.EmailTemplate;
import ish.oncourse.server.cayenne.Message;
import org.apache.cayenne.ObjectContext;

import java.util.Map;

public class MessageBuilder {

    private TemplateService templateService;
    private MessageSpec messageSpec;
    private EmailTemplate template;
    private Map<String, Object> bindings;
    private Contact recipient;
    private ObjectContext context;

    private MessageBuilder() {}

    public static MessageBuilder valueOf(TemplateService templateService,
                                         MessageSpec messageSpec,
                                         EmailTemplate template,
                                         Map<String, Object> bindings,
                                         ObjectContext context) {
        MessageBuilder messageBuilder = new MessageBuilder();

        messageBuilder.templateService = templateService;
        messageBuilder.messageSpec = messageSpec;
        messageBuilder.template = template;
        messageBuilder.bindings = bindings;
        messageBuilder.context = context;

        return messageBuilder;
    }

    public Message build() {
        Message message;
        switch (template.getType()) {
            case EMAIL:
                message = buildEmailMessage();
                break;
            case SMS:
                message = buildSMS();
                break;
            case POST:
            default:
                throw new IllegalArgumentException("Unsupported message type");
        }

        message.setCreatedBy(messageSpec.getCreatedBy() != null ? context.localObject(messageSpec.getCreatedBy()) : null);
        message.setCreatorKey(messageSpec.getCreatorKey());

        return message;
    }

    private Message buildEmailMessage() {

        Message message = context.newObject(Message.class);

        message.setEmailSubject(templateService.addSubject(template, bindings, null));
        message.setEmailBody(templateService.renderPlain(template, bindings));
        message.setEmailHtmlBody(templateService.renderHtml(template, bindings));
        message.setEmailFrom(messageSpec.getFromAddress());

        return message;
    }

    private Message buildSMS() {

        Message message = context.newObject(Message.class);
        message.setSmsText(templateService.renderPlain(template, bindings));

        return message;
    }
}
