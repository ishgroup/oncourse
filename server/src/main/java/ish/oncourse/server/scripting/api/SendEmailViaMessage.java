/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.scripting.api;

import ish.oncourse.server.IPreferenceController;
import ish.oncourse.server.cayenne.AutomationBinding;
import ish.oncourse.server.cayenne.Contact;
import org.apache.cayenne.ObjectContext;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SendEmailViaMessage {

    private EmailSpec spec;
    private TemplateService templateService;
    private ObjectContext context;
    private IPreferenceController preferenceController;
    private Function<Contact, Boolean> collision;


    private SendEmailViaMessage(){}


    public static SendEmailViaMessage valueOf(EmailSpec spec, ObjectContext context, TemplateService templateService, IPreferenceController preferenceController){
        return valueOf(spec, context, templateService, preferenceController, (c) -> true);
    }

    public static SendEmailViaMessage valueOf(EmailSpec spec, ObjectContext context, TemplateService templateService, IPreferenceController preferenceController, Function<Contact, Boolean> collision){
        var creator = new SendEmailViaMessage();
        creator.spec = spec;
        creator.templateService = templateService;
        creator.context = context;
        creator.preferenceController = preferenceController;
        creator.collision = collision;
        return creator;
    }

    public void send(){
        var message = createEmailMessage(spec.getTemplateName());

        if (spec.getFromAddress() != null) {
            message.from(spec.getFromAddress());
        }
        message.bind(spec.getBindings());
        message.createdBy(spec.getCreatedBy());
        message.creatorKey(spec.getCreatorKey());

        for (var recipient : spec.getRecipients().entrySet()) {
            if (collision.apply(recipient.getKey())) {
                message.to(recipient.getKey(), recipient.getValue());
            }
        }

        message.send();
    }

    private EmailMessage createEmailMessage (String templateName) {
        var template = templateService.loadTemplate(templateName);
        if (template == null) {
            throw new IllegalArgumentException(String.format("No template with name '%s' found.", templateName));
        }

        var localTemplate = context.localObject(template);
        var message = new EmailMessage(localTemplate, templateService);

        message.from(preferenceController.getEmailFromAddress());
        Map<String, Object> options = template.getOptions().stream().collect(Collectors.toMap(AutomationBinding::getName, AutomationBinding::getObjectValue));
        if (!options.isEmpty()) {
            message.bind(options);
        }
        return message;
    }


}
