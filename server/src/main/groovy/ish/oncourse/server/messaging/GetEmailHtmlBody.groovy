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
class GetEmailHtmlBody {

    private TemplateService templateService
    private String templateName
    private Map<String, Object> bindings

    private MessagePerson messagePerson

    private GetEmailHtmlBody() {

    }

    static GetEmailHtmlBody empty() {
        new GetEmailHtmlBody()
    }

    static GetEmailHtmlBody valueOf(TemplateService templateService, String templateName, Map<String, Object> bindings) {
        GetEmailHtmlBody getEmailHtmlBody = new GetEmailHtmlBody()
        getEmailHtmlBody.templateService = templateService
        getEmailHtmlBody.templateName = templateName
        getEmailHtmlBody.bindings = bindings
        getEmailHtmlBody
    }

    static GetEmailHtmlBody valueOf(MessagePerson messagePerson) {
        GetEmailHtmlBody getEmailHtmlBody = new GetEmailHtmlBody()
        getEmailHtmlBody.messagePerson = messagePerson
        getEmailHtmlBody
    }

    String get() {
        if (messagePerson) {
            String html = messagePerson.message.emailHtmlBody
            if (messagePerson.message.mailingListMessage) {
                GetUnsubscribeLink getUnsubscribeLink = GetUnsubscribeLink.valueOf(messagePerson)
                html += "\n<br/>\n<p><small><a href=\"${getUnsubscribeLink.get()}\">Unsubsribe</a></small></p>".toString()
            }
            return html
        }

        if (templateName && templateService) {
            EmailTemplate template = templateService.loadTemplate(templateName)
            return templateService.renderHtml(template, bindings)
        }

        ''
    }
}
