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
import ish.oncourse.server.scripting.api.TemplateService

@CompileDynamic
class GetSubject {

    private TemplateService templateService
    private String templateName
    private Map<String, Object> bindings
    private String subject

    private GetSubject() {

    }

    static GetSubject valueOf(String subject) {
        GetSubject getSubject = new GetSubject()
        getSubject.subject = subject
        getSubject
    }

    static GetSubject valueOf(TemplateService templateService, String templateName, Map<String, Object> bindings) {
        valueOf(templateService, templateName, bindings, null)
    }

    static GetSubject valueOf(TemplateService templateService, String templateName, Map<String, Object> bindings, String subject) {
        GetSubject getSubject = new GetSubject()
        getSubject.templateService = templateService
        getSubject.templateName = templateName
        getSubject.bindings = bindings
        getSubject.subject = subject
        getSubject
    }


    String get() {
        templateName ? templateService.renderSubject(templateName, bindings) : subject ?: ''
    }
}
