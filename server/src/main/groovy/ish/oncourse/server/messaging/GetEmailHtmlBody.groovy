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
import ish.oncourse.server.cayenne.Message
import ish.oncourse.server.scripting.api.TemplateService

@CompileDynamic
class GetEmailHtmlBody {

    private TemplateService templateService
    private String templateIdentifier
    private Map<String, Object> bindings

    private Message message

    private GetEmailHtmlBody() {

    }

    static GetEmailHtmlBody empty() {
        new GetEmailHtmlBody()
    }

    static GetEmailHtmlBody valueOf(TemplateService templateService, String templateIdentifier, Map<String, Object> bindings) {
        GetEmailHtmlBody getEmailHtmlBody = new GetEmailHtmlBody()
        getEmailHtmlBody.templateService = templateService
        getEmailHtmlBody.templateIdentifier = templateIdentifier
        getEmailHtmlBody.bindings = bindings
        getEmailHtmlBody
    }

    static GetEmailHtmlBody valueOf(Message message) {
        GetEmailHtmlBody getEmailHtmlBody = new GetEmailHtmlBody()
        getEmailHtmlBody.message = message
        getEmailHtmlBody
    }

    String get() {
        if (message) {
            String html = message.emailHtmlBody
            return html
        }

        if (templateIdentifier && templateService) {
            EmailTemplate template = templateService.loadTemplate(templateIdentifier)
            return templateService.renderHtml(template, bindings)
        }

        ''
    }
}
