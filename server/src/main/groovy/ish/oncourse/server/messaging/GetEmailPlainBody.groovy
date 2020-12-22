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

package ish.oncourse.server.messaging

import groovy.transform.CompileDynamic
import ish.oncourse.server.cayenne.EmailTemplate
import ish.oncourse.server.cayenne.MessagePerson
import ish.oncourse.server.scripting.api.TemplateService

@CompileDynamic
class GetEmailPlainBody {

    private TemplateService templateService
    private String templateIdentifier
    private Map<String, Object> bindings
    private String emailBody

    private MessagePerson messagePerson

    private GetEmailPlainBody() {

    }

    static GetEmailPlainBody valueOf(String emailBody) {
        GetEmailPlainBody getEmailPlainBody = new GetEmailPlainBody()
        getEmailPlainBody.emailBody = emailBody
        getEmailPlainBody
    }

    static GetEmailPlainBody valueOf(TemplateService templateService, String templateIdentifier, Map<String, Object> bindings, String emailBody) {
        GetEmailPlainBody getEmailPlainBody = new GetEmailPlainBody()
        getEmailPlainBody.templateService = templateService
        getEmailPlainBody.templateIdentifier = templateIdentifier
        getEmailPlainBody.bindings = bindings
        getEmailPlainBody.emailBody = emailBody
        getEmailPlainBody
    }

    static GetEmailPlainBody valueOf(MessagePerson messagePerson) {
        GetEmailPlainBody getEmailPlainBody = new GetEmailPlainBody()
        getEmailPlainBody.messagePerson = messagePerson
        getEmailPlainBody
    }

    String get() {
        if (messagePerson) {
            String plain = messagePerson.message.emailBody
            if (messagePerson.message.mailingListMessage) {
                GetUnsubscribeLink getUnsubscribeLink = GetUnsubscribeLink.valueOf(messagePerson)
                plain += "\n\nUnsubscribe: ${getUnsubscribeLink.get()}".toString()
            }
            return plain
        }

        if (templateIdentifier && templateService) {
            EmailTemplate template = templateService.loadTemplate(templateIdentifier)
            return templateService.renderPlain(template, bindings)
        }

        return emailBody ?: ''
    }
}
