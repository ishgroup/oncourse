/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.commercial.plugin.tcsi.api

import groovy.transform.CompileDynamic
import groovyx.net.http.RESTClient
import ish.oncourse.commercial.plugin.tcsi.TCSIException
import ish.oncourse.server.PreferenceController
import ish.oncourse.server.cayenne.Enrolment
import ish.oncourse.server.scripting.api.EmailService
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

@CompileDynamic
class TCSI_API {
    static final String DATE_FORMAT='yyyy-MM-dd'

    private static Logger logger = LogManager.logger

    protected Enrolment enrolment
    protected EmailService emailService
    protected PreferenceController preferenceController
    protected RESTClient client

    TCSI_API(RESTClient client, Enrolment enrolment, EmailService emailService, PreferenceController preferenceController) {
        this.client = client
        this.emailService = emailService
        this.enrolment = enrolment
        this.preferenceController = preferenceController
    }
    
    protected Object handleResponce(Object responceObject, String description) {

        def response = responceObject instanceof List ? (responceObject as List)[0] : responceObject
        if (response['success']) {
            return response['result']
        }

        String errorInfo = "Interrapt tcsi export for: $enrolment.student.contact.fullName, error happend while $description\nError info:\n"

        List<Map> errors =  response['result']['errors']

        errors.each {error ->
            errorInfo += error["error_description"]
            errorInfo +="\n"
        }

        this.interraptExport(errorInfo)
        return null
    }

    protected interraptExport(String message) {
        logger.error(message)

        emailService.email {
            subject("TCSI export failed for: $enrolment.student.contact.fullName  $enrolment.courseClass.uniqueCode")
            content(message)
            from (preferenceController.emailFromAddress)
            to (preferenceController.emailAdminAddress)
            cc ('artyom@ish.com.au')
        }
        throw new TCSIException(message)
    }
    
    
}
