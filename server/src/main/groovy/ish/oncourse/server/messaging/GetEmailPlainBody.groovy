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
class GetEmailPlainBody {

    private TemplateService templateService
    private String templateIdentifier
    private Map<String, Object> bindings
    private String emailBody

    private Message message

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

    static GetEmailPlainBody valueOf(Message message) {
        GetEmailPlainBody getEmailPlainBody = new GetEmailPlainBody()
        getEmailPlainBody.message = message
        getEmailPlainBody
    }

    String get() {
        if (message) {
            String plain = message.emailBody
            return plain
        }

        if (templateIdentifier && templateService) {
            EmailTemplate template = templateService.loadTemplate(templateIdentifier)
            return templateService.renderPlain(template, bindings)
        }

        return emailBody ?: ''
    }
}
