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

package ish.oncourse.server.integration.usi


import groovyx.net.http.HTTPBuilder
import ish.common.types.IntegrationType
import ish.common.types.USIFieldStatus
import ish.common.types.USIVerificationResult
import ish.common.types.USIVerificationStatus
import ish.oncourse.server.PreferenceController
import ish.oncourse.server.cayenne.IntegrationConfiguration
import ish.oncourse.server.integration.Plugin

import ish.oncourse.server.integration.PluginTrait
import ish.oncourse.server.license.LicenseService
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import java.time.LocalDate

@Plugin(type = IntegrationType.USI_AGENCY, oneOnly = true)
class USIIntegration implements PluginTrait {

    private static final Logger logger = LogManager.getLogger(USIIntegration)

    PreferenceController preferenceController
    LicenseService licenseService

    USIIntegration(Map<String, Object> args) {
        this.licenseService = args.get("licenseService") as LicenseService
        this.preferenceController = args.get("preferenceController") as PreferenceController
    }

    USIVerificationResult verifyUsi(String firstName, String lastName, LocalDate dateOfBirth, String usiCode) {
        String usiUrl = licenseService.getUsi_host()
        String abn = preferenceController.getCollegeABN()?.replaceAll("\\s","")
        String ssid = preferenceController.getUsiSoftwareId()
        String orgCode = preferenceController.getAvetmissID()

        USIVerificationResult result = new USIVerificationResult()
        try {
            new HTTPBuilder().get(
                    [uri: usiUrl,
                     path:   '/usi/verify',
                     query: ['studentFirstName' : firstName,
                             'studentLastName' : lastName,
                             'studentBirthDate' : dateOfBirth.format('yyyy-MM-dd'),
                             'usiCode' : usiCode,
                             'orgCode' : orgCode,
                             'collegeABN' : abn,
                             'softwareId' : ssid,
                             'collegeKey': licenseService.getCollege_key()]
                    ])
                    { response, body ->
                        String error = body["errorMessage"] as String
                        if (error) {
                            result.errorMessage = error
                        } else {
                            result.usiStatus = USIVerificationStatus.values().find {it.name() == body["usiStatus"] }
                            result.firstNameStatus = USIFieldStatus.values().find {it.name() == body["firstNameStatus"] }
                            result.lastNameStatus = USIFieldStatus.values().find {it.name() == body["lastNameStatus"] }
                            result.dateOfBirthStatus = USIFieldStatus.values().find {it.name() == body["dateOfBirthStatus"] }
                        }

                    }
        } catch (Exception e) {
            logger.error("Can not connect to internal USI service")
            logger.error("can not verify usi: ${usiUrl}, ${orgCode}, ${abn}, ${ssid}")
            logger.catching(e)
            result.errorMessage = "Can not connect to internal USI service. Please, contact ish support"
        }

        return result
    }
}
