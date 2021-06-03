/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.commercial.plugin.tcsi.api

import groovy.json.JsonOutput
import groovy.transform.CompileDynamic
import groovyx.net.http.RESTClient
import ish.oncourse.commercial.plugin.tcsi.TCSIIntegration
import ish.oncourse.server.PreferenceController
import ish.oncourse.server.cayenne.Enrolment
import ish.oncourse.server.cayenne.Site
import ish.oncourse.server.scripting.api.EmailService

import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.Method.*

@CompileDynamic
class CampusAPI extends TCSI_API {
    static final String CAMPUSES_PATH =  TCSIIntegration.BASE_API_PATH + '/campuses'

    CampusAPI(RESTClient client, Enrolment enrolment, EmailService emailService, PreferenceController preferenceController) {
        super(client, enrolment, emailService, preferenceController)
    }

    String campusUid() {
        String campuseUid = null
        if (enrolment.courseClass.room) {
            campuseUid = getCampus()
            if (!campuseUid) {
                campuseUid = createCampus()
            } else {
                updateCampus(campuseUid)   
            }
        }
        return campuseUid
    }


    String getCampus() {
        String message = 'getting campus'
        client.request(GET, JSON) {
            uri.path = CAMPUSES_PATH
            headers.'tcsi-pagination-page'='1'
            headers.'tcsi-pagination-pagesize'='1000'

            response.success = { resp, result ->
                def campuses =  handleResponce(result, message)

                def campus = campuses.find { it['campus']['delivery_location_code'] == enrolment.courseClass.room.site.id.toString()}
                if (campus) {
                    return campus['campus']['campuses_uid']
                }
                return null
            }
            response.failure =  { resp, body ->
                interraptExport("Something unexpected happend while $message, please contact ish support for more details\n ${resp.toString()}\n ${body.toString()}".toString())
            }
        }
    }

    String createCampus() {
        String message = 'creating campus'

        client.request(POST, JSON) {
            uri.path = CAMPUSES_PATH
            body = getCampusData()
            response.success = { resp, result ->
                def campus =  handleResponce(result, message)
                return campus['campuses_uid'].toString()
            }
            response.failure =  { resp, body ->
                interraptExport("Something unexpected happend while $message, please contact ish support for more details\n ${resp.toString()}\n ${body.toString()}".toString())
            }
        }
    }

    String updateCampus(String campusId) {
        String message = 'update campus'

        client.request(PUT, JSON) {
            uri.path = CAMPUSES_PATH + "/$campusId"
            body = getCampusData()
            response.success = { resp, result ->
                handleResponce(result, message)
            }
            response.failure =  { resp, body ->
                interraptExport("Something unexpected happend while $message, please contact ish support for more details\n ${resp.toString()}\n ${body.toString()}".toString())
            }
        }
    }

    @CompileDynamic
    String getCampusData () {
        Map<String, Object> campus = [:]
        Site site = enrolment.courseClass.room.site
        campus["delivery_location_code"] = site.id.toString()
        campus["campus_effective_from_date"] = '2000-01-01'
        campus["delivery_location_street_address"] = site.street?:preferenceController.avetmissAddress1
        campus["delivery_location_suburb"] = site.suburb ?: preferenceController.avetmissSuburb
        campus["delivery_location_country_code"] = (site.country?.saccCode?.toString()) ?: '1101'
        campus["delivery_location_postcode"] = site.postcode ?: preferenceController.avetmissPostcode
        campus["delivery_location_state"] = site.state ?: preferenceController.avetmissStateName


        def campuseData  = [
                'correlation_id' : "campuse_${System.currentTimeMillis()}",
                'campus' : campus
        ]

        return JsonOutput.toJson(campuseData)
    }
}
