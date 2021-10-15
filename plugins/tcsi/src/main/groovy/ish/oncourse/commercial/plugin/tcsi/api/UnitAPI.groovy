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
import ish.math.Money
import ish.oncourse.commercial.plugin.tcsi.TCSIIntegration
import ish.oncourse.common.ExportJurisdiction
import ish.oncourse.server.PreferenceController
import ish.oncourse.server.cayenne.Course
import ish.oncourse.server.cayenne.CourseClass
import ish.oncourse.server.cayenne.Enrolment
import ish.oncourse.server.scripting.api.EmailService

import java.math.RoundingMode
import java.time.Duration
import java.time.LocalDate

import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.Method.*
import static ish.common.types.StudentStatusForUnitOfStudy.*

@CompileDynamic
class UnitAPI extends TCSI_API {
    
    private Course highEducation
    
    static final String UNITS_PATH = TCSIIntegration.BASE_API_PATH + '/unit-enrolments'

    UnitAPI(Course highEducation, RESTClient client, Enrolment enrolment, EmailService emailService, PreferenceController preferenceController) {
        super(client, enrolment, emailService, preferenceController)
        this.highEducation = highEducation
    }
    
    String getUnit(String admissionUid) {
        String message = 'getting unit enrolments'
        
        
        client.request(GET, JSON) {
            uri.path = AdmissionAPI.ADMISSIONS_PATH + "/$admissionUid/unit-enrolments"
            response.success = { resp, result ->

                def units = handleResponce(result, message)
                if (!units.empty) {
                    def unit = units.find {it['unit_enrolment']['unit_of_study_code'] == enrolment.courseClass.uniqueCode}
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

        String jsonBody = JsonOutput.toJson([getUnitData(admissionUid, campuseUid)])
        client.request(POST, JSON) {
            uri.path = UNITS_PATH
            //POST as array with single JSON object inside
            body = jsonBody
            response.success = { resp, result -> 
                handleResponce(result, message)
            }
            response.failure =  { resp, body ->
                interraptExport("Something unexpected happend white $message, please contact ish support for more details\n ${resp.toString()}\n ${body.toString()}\n requestBody: $jsonBody".toString())
            }
        }
    }
    
    void updateUnit(String unitUid, String admissionUid, String campuseUid) {
        String message = "update enrolment unit"
        client.request(PUT, JSON) {
            uri.path = UNITS_PATH +"/$unitUid"
            //PUT as single JSON object
            body = JsonOutput.toJson(getUnitData(admissionUid, campuseUid)) 

            response.success = { resp, result ->
                handleResponce(result, message)
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
        unit["unit_of_study_code"] =  clazz.uniqueCode
        if (campuseUid) {
            unit["campuses_uid"] = Long.valueOf(campuseUid)
        }
        if (clazz.censusDate) {
            unit["unit_of_study_census_date"] = clazz.censusDate.format(DATE_FORMAT)
        }
        
        List<String> foes = highEducation.modules*.fieldOfEducation.grep()
        if (!foes.empty) {
            unit["discipline_code"] = foes.countBy { it }.max { it.value }.key
        }
        

        if (clazz.startDateTime && clazz.endDateTime) {
            unit["unit_of_study_year_long_indicator"] = (Duration.between(clazz.startDateTime.toInstant(), clazz.endDateTime.toInstant()).toDays() > 300)
        }

        if (!enrolment.outcomes.empty) {
            LocalDate strtDate = enrolment.outcomes.findAll {it.startDate}*.startDate.sort().first()
            if (strtDate) {
                unit["unit_of_study_commencement_date"] = strtDate.format(DATE_FORMAT)
            }
            LocalDate endDate = enrolment.outcomes.findAll {it.endDate}*.endDate.sort().last()
            if (endDate) {
                unit["unit_of_study_outcome_date"] = endDate.format(DATE_FORMAT) //E601 
            }
        }
        
        if (clazz.course.fullTimeLoad) {
            try {
                unit['eftsl'] = new BigDecimal(clazz.course.fullTimeLoad)
            } catch  (Exception ignor) {
                unit['eftsl'] = 0
            }
        }

        if (enrolment.status in [EnrolmentStatus.CANCELLED, EnrolmentStatus.REFUNDED]) {
            unit["unit_of_study_status_code"] = "1"
        } else {
            if (enrolment.outcomes.any {(it.endDate?.isAfter(LocalDate.now())) || !(it.status in OutcomeStatus.STATUSES_VALID_FOR_CERTIFICATE)}) {
                unit["unit_of_study_status_code"] = "4"
            } else {
                unit["unit_of_study_status_code"] = "3"
            }
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

        ExportJurisdiction jurisdiction = enrolment.relatedFundingSource?.flavour
        Integer statusCode = null
        if (jurisdiction) {
            switch (jurisdiction) {
                case ExportJurisdiction.PLAIN:
                    // NCVER (Standard AVETMISS) -> 401 non State Government subsidised
                    statusCode=401
                    break
                  
                case ExportJurisdiction.VIC:
                    // Skills Victoria -> 403 Victorian State Government subsidised
                    statusCode=403
                    break
                case ExportJurisdiction.OLIV:
                case ExportJurisdiction.SMART:
                    // CSO (Community Colleges) -> 404 New South Wales State Government subsidised
                    // STSOnline (NSW) -> 404 New South Wales State Government subsidised
                    statusCode=404
                    break
                case ExportJurisdiction.QLD:
                    // DETConnect (Queensland) -> 405 Queensland State Government subsidised
                    statusCode=405
                    break
                case ExportJurisdiction.SA:
                    //STELA (South Australia) ->  South Australian State Government subsidised
                    statusCode=406
                    break
                case ExportJurisdiction.RAPT:
                case ExportJurisdiction.WA:    
                    // WA RAPT -> 407 Western Australian State Government subsidised
                    // STARS (WA) -> 407 Western Australian State Government subsidised
                    statusCode=407
                    break
                case ExportJurisdiction.TAS:
                    // Skills Tasmania -> 408 Tasmania State Government subsidised
                    statusCode=408
                    break
                case ExportJurisdiction.NTVETPP:
                    // Northern Territories VET Provider Portal -> 409 Northern Territory Government subsidised
                    statusCode=409
                    break
                case ExportJurisdiction.AVETARS:
                    // AVETARS (ACT) ->  402  Restricted Access Arrangement
                    statusCode=410
                    break
                case ExportJurisdiction.AQTF:
                case ExportJurisdiction.NSW:
                    break
            }
        }

        BigDecimal feeCharged =  getFeeChargedAmount().toBigDecimal()
        BigDecimal helpLoanAmount 
        if (enrolment.feeHelpAmount) {
            helpLoanAmount = enrolment.feeHelpAmount.toBigDecimal()
        } else {
            helpLoanAmount = BigDecimal.ZERO
            if (statusCode != null) {
                statusCode = statusCode + 100
            }
        }
        unit["student_status_code"] = statusCode.toString() //E490
        unit["amount_charged"] = feeCharged //E384
        unit["help_loan_amount"] = helpLoanAmount // E558
        unit["amount_paid_upfront"] = feeCharged.subtract(helpLoanAmount) //E381
        
        if (statusCode != null &&  (statusCode in [401, 402])) {
            LocalDate threshold_1 = LocalDate.parse('01-04-2020','dd-MM-yyyy')
            LocalDate threshold_2 = LocalDate.parse('01-07-2021','dd-MM-yyyy')

            if (clazz.censusDate.isAfter(threshold_1) && clazz.censusDate.isBefore(threshold_2)) {
                unit["loan_fee"] = BigDecimal.ZERO
            } else {
                unit["loan_fee"] =  helpLoanAmount.multiply(new BigDecimal(0.2)).setScale(2, RoundingMode.UP)// E529
            } 
        } else  {
            unit["loan_fee"] = BigDecimal.ZERO
        }
        

        if (enrolment.creditTotal) { //E577
            switch (enrolment.creditTotal) {
                case RecognitionOfPriorLearningIndicator.NOT_RPL_UNIT_OF_STUDY:
                    unit["recognition_of_prior_learning_code"]=null
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
    
    Money getFeeChargedAmount() {
        if (enrolment.invoiceLines?.empty)  {
            return Money.ZERO
        } else {
            return enrolment.invoiceLines.findAll {it.invoice.contact == enrolment.student.contact }*.finalPriceToPayIncTax.inject { a, b -> a.add(b) }
        }
    }

}
