/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.commercial.plugin.tcsi.api

import groovy.json.JsonOutput
import groovy.transform.CompileDynamic
import groovyx.net.http.RESTClient
import ish.common.types.DeliveryMode
import ish.common.types.EnrolmentStatus
import ish.common.types.OutcomeStatus
import ish.common.types.RecognitionOfPriorLearningIndicator
import ish.common.types.StudentStatusForUnitOfStudy
import ish.math.Money
import ish.oncourse.commercial.plugin.tcsi.TCSIIntegration
import ish.oncourse.server.PreferenceController
import ish.oncourse.server.cayenne.CourseClass
import ish.oncourse.server.cayenne.Enrolment
import ish.oncourse.server.scripting.api.EmailService

import java.time.Duration
import java.time.LocalDate

import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.Method.*

class UnitAPI extends TCSI_API {

    static final String UNITS_PATH = TCSIIntegration.BASE_API_PATH + '/unit-enrolments'

    UnitAPI(RESTClient client, Enrolment enrolment, EmailService emailService, PreferenceController preferenceController) {
        super(client, enrolment, emailService, preferenceController)
    }
    
    String getUnit(String admissionUid) {
        String message = 'getting unit enrolments'
        String unitCode = enrolment.outcomes[0].module?.fieldOfEducation
        if (!unitCode) {
            interraptExport("The unit course has no 'discipline_code'")
        }
        
        client.request(GET, JSON) {
            uri.path = AdmissionAPI.ADMISSIONS_PATH + "/$admissionUid/unit-enrolments"
            response.success = { resp, result ->

                def units = handleResponce(result, message)
                if (units.empty) {
                    def unit = units.find {it['unit_enrolment']['discipline_code'] == unitCode}
                    if (unit) {
                        return unit['unit_enrolment']['unit_enrolments_uid']
                    }
                }
                return null
            }
            response.failure =  { resp, body ->
                interraptExport("Something unexpected happend while $message, please contact ish support for more details\n ${resp.toString()}\n ${body.toString()}".toString())
            }
        }
    }

    String createUnit(String admissionUid, String campuseUid) {
        String message = "creating enrolment unit"
        client.request(POST, JSON) {
            uri.path = UNITS_PATH
            //POST as array with single JSON object inside
            body = JsonOutput.toJson([getUnitData(admissionUid, campuseUid)])
            response.success = { resp, result -> 
                handleResponce(result as List, message)
            }
            response.failure =  { resp, body ->
                interraptExport("Something unexpected happend white $message, please contact ish support for more details\n ${resp.toString()}\n ${body.toString()}".toString())
            }
        }
    }
    
    String updateUnit(String unitUid, String admissionUid, String campuseUid) {
        String message = "creating enrolment unit"
        client.request(PUT, JSON) {
            uri.path = UNITS_PATH +"/$unitUid"
            //PUT as single JSON object
            body = JsonOutput.toJson(getUnitData(admissionUid, campuseUid)) 

            response.success = { resp, result ->
                handleResponce(result as List, message)
            }
            response.failure =  { resp, body ->
                interraptExport("Something unexpected happend white $message, please contact ish support for more details\n ${resp.toString()}\n ${body.toString()}".toString())
            }
        }
    }

    @CompileDynamic
    Map<String, Object> getUnitData( String admissionUid, String campuseUid) {
        Map<String, Object> unit = [:]
        CourseClass clazz = enrolment.courseClass
        unit["course_admissions_uid"] = Long.valueOf(admissionUid)
        unit["unit_of_study_code"] =  clazz.course.code
        if (campuseUid) {
            unit["campuses_uid"] = Long.valueOf(campuseUid)
        }
        if (clazz.censusDate) {
            unit["unit_of_study_census_date"] = clazz.censusDate.format(DATE_FORMAT)
        }
        if (!enrolment.outcomes.empty  && enrolment.outcomes[0].module?.fieldOfEducation ) {
            unit["discipline_code"] =enrolment.outcomes[0].module?.fieldOfEducation
        }

        if (clazz.startDateTime && clazz.endDateTime) {
            unit["unit_of_study_year_long_indicator"] = (Duration.between(clazz.startDateTime.toInstant(), clazz.endDateTime.toInstant()).toDays() > 300)
        }
        LocalDate strtDate = enrolment.outcomes.findAll {it.startDate}*.startDate.sort().first()
        if (strtDate) {
            unit["unit_of_study_commencement_date"] = strtDate.format(DATE_FORMAT)
        }
        if (clazz.course.fullTimeLoad) {
            try {
                unit['eftsl'] = new BigDecimal(clazz.course.fullTimeLoad)
            } catch  (Exception ignor) {
                unit['eftsl'] = 0
            }
        }

        if (enrolment.status == EnrolmentStatus.CANCELLED) {
            unit["unit_of_study_status_code"] = "1"
        } else {
            if (enrolment.outcomes.any {(it.endDate?.isAfter(LocalDate.now())) || !(it.status in OutcomeStatus.STATUSES_VALID_FOR_CERTIFICATE)}) {
                unit["unit_of_study_status_code"] = "3"
            } else {
                unit["unit_of_study_status_code"] = "4"
            }
        }


        LocalDate endDate = enrolment.outcomes.findAll {it.endDate}*.endDate.sort().last()
        if (endDate) {
            unit["unit_of_study_outcome_date"] = endDate.format(DATE_FORMAT) //E601 
        }

        unit["course_assurance_indicator"] = false

        if (clazz.deliveryMode) { //E329
            switch (clazz.deliveryMode) {
                case DeliveryMode.CLASSROOM:
                case DeliveryMode.CLASSROOM_AND_WORKSPACE:
                    unit["mode_of_attendance_code"] = '1'
                    break
                case DeliveryMode.WORKPLACE:
                    unit["mode_of_attendance_code"] = '6'
                    break
                default:
                    unit["mode_of_attendance_code"] = '3'
                    break
            }
        } else {
            unit["mode_of_attendance_code"] = '3'
        }


        if (enrolment.feeStatus) {
            switch (enrolment.feeStatus)  {
                case StudentStatusForUnitOfStudy.DEFERRED_ALL_OR_PART_OF_TUITION_FEE_THROUGH_VET_FEE_HELP_NON_STATE_GOVERNMENT_SUBSIDISED:
                    unit["student_status_code"] = "401"
                    break
                case StudentStatusForUnitOfStudy.DEFERRED_ALL_OR_PART_OF_TUITION_FEE_THROUGH_VET_FEE_HELP_RESTRICTED_ACCESS_ARRANGEMENT:
                    unit["student_status_code"] = "402"
                    break
                case StudentStatusForUnitOfStudy.DEFERRED_ALL_OR_PART_OF_TUITION_FEE_THROUGH_VET_FEE_HELP_VICTORIAN_STATE_GOVERNMENT_SUBSIDISED:
                    unit["student_status_code"] = "403"
                    break
                case StudentStatusForUnitOfStudy.DEFERRED_ALL_OR_PART_OF_TUITION_FEE_THROUGH_VET_FEE_HELP_NEW_SOUTH_WALES_STATE_GOVERNMENT_SUBSIDISED:
                    unit["student_status_code"] = "404"
                    break
                case StudentStatusForUnitOfStudy.DEFERRED_ALL_OR_PART_OF_TUITION_FEE_THROUGH_VET_FEE_HELP_QUEENSLAND_STATE_GOVERNMENT_SUBSIDISED:
                    unit["student_status_code"] = "405"
                    break
                case StudentStatusForUnitOfStudy.DEFERRED_ALL_OR_PART_OF_TUITION_FEE_THROUGH_VET_FEE_HELP_SOUTH_AUSTRALIAN_STATE_GOVERNMENT_SUBSIDISED:
                    unit["student_status_code"] = "406"
                    break
                case StudentStatusForUnitOfStudy.DEFERRED_ALL_OR_PART_OF_TUITION_FEE_THROUGH_VET_FEE_HELP_WESTERN_AUSTRALIAN_STATE_GOVERNMENT_SUBSIDISED:
                    unit["student_status_code"] = "407"
                    break
                case StudentStatusForUnitOfStudy.DEFERRED_ALL_OR_PART_OF_TUITION_FEE_THROUGH_VET_FEE_HELP_TASMANIA_STATE_GOVERNMENT_SUBSIDISED:
                    unit["student_status_code"] = "408"
                    break
                case StudentStatusForUnitOfStudy.DEFERRED_ALL_OR_PART_OF_TUITION_FEE_THROUGH_VET_FEE_HELP_NORTHERN_TERRITORY_GOVERNMENT_SUBSIDISED:
                    unit["student_status_code"] = "409"
                    break
                case StudentStatusForUnitOfStudy.DEFERRED_ALL_OR_PART_OF_TUITION_FEE_THROUGH_VET_FEE_HELP_AUSTRALIAN_CAPITAL_TERRITORY_GOVERNMENT_SUBSIDISED:
                    unit["student_status_code"] = "410"
                    break
            }
        }

        BigDecimal feeCharged =  enrolment.invoiceLines.empty ?
                Money.ZERO.toBigDecimal() :
                enrolment.originalInvoiceLine.priceTotalIncTax.toBigDecimal()
        unit["amount_charged"] = feeCharged
        unit["amount_paid_upfront"] = feeCharged
        if (enrolment.feeHelpAmount) {
            unit["loan_fee"] =  enrolment.feeHelpAmount.multiply(0.2).toBigDecimal()
            unit["help_loan_amount"] =  enrolment.feeHelpAmount.toBigDecimal()
        }
        if (enrolment.creditTotal) {
            switch (enrolment.creditTotal) {
                case RecognitionOfPriorLearningIndicator.NOT_RPL_UNIT_OF_STUDY:
                    break
                case RecognitionOfPriorLearningIndicator.UNIT_OF_STUDY_CONSISTS_WHOLLY_OF_RPL:
                    unit["recognition_of_prior_learning_code"]='1'
                    break
                case RecognitionOfPriorLearningIndicator.UNIT_OF_STUDY_HAS_A_COMPONENT_OF_RPL:
                    unit["recognition_of_prior_learning_code"]='2'
                    break
            }
        }

        def unitData  = [
                'correlation_id' : "unit_${System.currentTimeMillis()}",
                'unit_enrolment' : unit
        ]

        return unitData
    }

}
