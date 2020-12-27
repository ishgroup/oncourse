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

import ish.oncourse.server.cayenne.Contact;
import ish.oncourse.server.messaging.MailDeliveryParam;
import ish.oncourse.server.messaging.MailDeliveryService;
import org.apache.cayenne.ObjectContext;

import javax.mail.MessagingException;
import java.util.function.Function;

public class SendEmailViaSmtp {

    private SmtpParameters parameters;
    private ObjectContext context;
    private MailDeliveryService mailDeliveryService;
    private TemplateService templateService;
    private Function<Contact, Boolean> collision;

    private SendEmailViaSmtp(){}

    public static SendEmailViaSmtp valueOf(EmailSpec spec, ObjectContext context, TemplateService templateService, MailDeliveryService mailDeliveryService, Function<Contact, Boolean> collision){
        var creator = new SendEmailViaSmtp();
        creator.parameters = new SmtpParameters(spec);
        creator.templateService = templateService;
        creator.mailDeliveryService = mailDeliveryService;
        creator.context = context;
        creator.collision = collision;
        return creator;
    }

    public static SendEmailViaSmtp valueOf(SmtpParameters smtpParameters, ObjectContext context, TemplateService templateService, MailDeliveryService mailDeliveryService, Function<Contact, Boolean> collision){
        var creator = new SendEmailViaSmtp();
        creator.parameters = smtpParameters;
        creator.templateService = templateService;
        creator.mailDeliveryService = mailDeliveryService;
        creator.context = context;
        creator.collision = collision;
        return creator;
    }

    public void send() throws MessagingException {
        if (collision.apply(null)) {
            var param = MailDeliveryParamBuilder.valueOf(parameters, templateService).build();
            mailDeliveryService.sendEmail(param);

            MessageForSmtp.valueOf(context, parameters.getCreatorKey(), param).create();
        }
    }
}
