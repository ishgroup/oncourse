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

package ish.oncourse.commercial.plugin.tcsi

import com.nimbusds.jose.jwk.RSAKey
import groovy.transform.CompileDynamic
import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.RESTClient
import ish.common.types.DataType
import ish.oncourse.server.api.v1.model.ValidationErrorDTO
import ish.oncourse.server.cayenne.Contact
import ish.oncourse.server.cayenne.ContactCustomField
import ish.oncourse.server.cayenne.Course
import ish.oncourse.server.cayenne.CourseCustomField
import ish.oncourse.server.cayenne.CustomFieldType
import ish.oncourse.server.cayenne.Enrolment
import ish.oncourse.server.cayenne.EnrolmentCustomField
import ish.oncourse.server.cayenne.EntityRelationType
import ish.oncourse.server.cayenne.IntegrationConfiguration
import ish.oncourse.server.integration.OnSave
import ish.oncourse.server.integration.Plugin
import ish.oncourse.server.integration.PluginTrait
import ish.oncourse.server.services.AuthHelper
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.groovy.json.internal.JsonFastParser
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import javax.ws.rs.ClientErrorException
import javax.ws.rs.core.Response

import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC
import static groovyx.net.http.Method.GET
import static groovyx.net.http.Method.POST
import static groovyx.net.http.Method.PUT


@CompileDynamic
@Plugin(type = 11, oneOnly = true)
class TCSIIntegration implements PluginTrait {
    
    static boolean test = false

    TCSIIntegration() {
    }
    
    public static final String TCSI_DEVICE_NAME = "deviceName"
    public static final String TCSI_ORGANISATION_ID = "organisationId"
    public static final String TCSI_ACTIVATION_CODE = "activationCode"
    public static final String TCSI_JWK_CERTIFICATE = "jwkCertificate"

    static final String AUTH_AUDIENCE = 'https://proda.humanservices.gov.au'
    static final String AUTH_AUDIENCE_TSCI  = 'https://tcsi.humanservices.gov.au'

    static final String DHS_PRODUCT_ID_TEST = '858d06ed-7fbe-423c-be45-ac5742cf137c'
    static final String BASE_URL_TEST = 'https://test.5.rsp.humanservices.gov.au'
    static final String AUTH_URL_TEST= 'https://vnd.PRODA.humanservices.gov.au'
    static final String AUTH_HOST_TEST = 'vnd.proda.humanservices.gov.au'

    static final String TCSI_BASE_URL_TEST = 'https://test.api.humanservices.gov.au/centrelink/ext-vend/tcsi/b2g/v1'

    static final String DHS_PRODUCT_ID = test ? DHS_PRODUCT_ID_TEST : '08b1e117-5efa-4b4d-b3d7-65ae18908671'
    static final String BASE_URL = test ? BASE_URL_TEST : 'https://5.rsp.humanservices.gov.au'
    static final String AUTH_URL = test ? AUTH_URL_TEST : 'https://PRODA.humanservices.gov.au'
    static final String AUTH_HOST = test ? AUTH_HOST_TEST : 'proda.humanservices.gov.au'

    static final String TCSI_BASE_URL = test ? TCSI_BASE_URL_TEST :'https://api.humanservices.gov.au/centrelink/ext-vend/tcsi/b2g/v1'

    static final String BASE_API_PATH =  test ? '/centrelink/ext-vend/tcsi/b2g/v1': '/centrelink/ext/tcsi/b2g/v1'
    static final String STUDENTS_PATH = BASE_API_PATH + '/students'
    static final String COURSES_PATH = BASE_API_PATH + '/courses'
    static final String ADMISSIONS_PATH = BASE_API_PATH + '/course-admissions'
    static final String UNITS_PATH = BASE_API_PATH + '/unit-enrolments'
    static final String CAMPUSES_PATH =  BASE_API_PATH + '/campuses'
    
    static final String HIGH_EDUCATION_TYPE  = 'Higher education'

    String deviceName
    String organisationId
    String jwkCertificate
    ObjectContext objectContext
    
    static final String TCSI_COURSE_UID  = 'tsciCourseUid'
    CustomFieldType courseUidField

    static final String TCSI_COURSE_ADMISSION_UID  = 'tsciCourseAdmissionUid'
    CustomFieldType courseAdmissionUidField

    static final String TCSI_STUDENT_UID  = 'tsciStudentUid'
    CustomFieldType studentUidField

    static final String TCSI_ENROLMENT_UNIT_UID  = 'tsciEnrolmentUnitUid'
    CustomFieldType enrolmentUnitUidField
    
    EntityRelationType highEducationType
    Course highEducation
    Enrolment enrolment
    Enrolment courseAdmission

    private String authToken
    

    private static Logger logger = LogManager.logger
    
    TCSIIntegration(Map args) {
        loadConfig(args)
        this.organisationId = configuration.getIntegrationProperty(TCSI_ORGANISATION_ID).value
        this.deviceName = configuration.getIntegrationProperty(TCSI_DEVICE_NAME).value
        this.jwkCertificate = configuration.getIntegrationProperty(TCSI_JWK_CERTIFICATE).value
        if (cayenneService) {
            this.objectContext = cayenneService.newContext
            this.highEducationType = ObjectSelect.query(EntityRelationType)
                    .where(EntityRelationType.NAME.eq(HIGH_EDUCATION_TYPE))
                    .selectOne(objectContext)
            this.loadCustomField()
        }

    }


    private loadCustomField() {
        
        courseUidField = ObjectSelect.query(CustomFieldType).where(CustomFieldType.KEY.eq(TCSI_COURSE_UID)).selectOne(objectContext)
        if (!courseUidField) {
            courseUidField = objectContext.newObject(CustomFieldType)
            courseUidField.dataType = DataType.TEXT
            courseUidField.entityIdentifier = Course.simpleName
            courseUidField.key = TCSI_COURSE_UID
            courseUidField.name = 'TCSI course identifier'
            courseUidField.isMandatory = false
            courseUidField.sortOrder = 1001l
            objectContext.commitChanges()
        }
        
        courseAdmissionUidField = ObjectSelect.query(CustomFieldType).where(CustomFieldType.KEY.eq(TCSI_COURSE_ADMISSION_UID)).selectOne(objectContext)
        if (!courseAdmissionUidField) {
            courseAdmissionUidField = objectContext.newObject(CustomFieldType)
            courseAdmissionUidField.dataType = DataType.TEXT
            courseAdmissionUidField.entityIdentifier = Enrolment.simpleName
            courseAdmissionUidField.key = TCSI_COURSE_ADMISSION_UID
            courseAdmissionUidField.name = 'TCSI course admission identifier'
            courseAdmissionUidField.isMandatory = false
            courseAdmissionUidField.sortOrder = 1002l
            objectContext.commitChanges()
        }

        studentUidField = ObjectSelect.query(CustomFieldType).where(CustomFieldType.KEY.eq(TCSI_STUDENT_UID)).selectOne(objectContext)
        if (!studentUidField) {
            studentUidField = objectContext.newObject(CustomFieldType)
            studentUidField.dataType = DataType.TEXT
            studentUidField.entityIdentifier = Contact.simpleName
            studentUidField.key = TCSI_STUDENT_UID
            studentUidField.name = 'TCSI student identifier'
            studentUidField.isMandatory = false
            studentUidField.sortOrder = 1003l
            objectContext.commitChanges()
        }
        
        enrolmentUnitUidField = ObjectSelect.query(CustomFieldType).where(CustomFieldType.KEY.eq(TCSI_ENROLMENT_UNIT_UID)).selectOne(objectContext)
        if (!enrolmentUnitUidField) {
            enrolmentUnitUidField = objectContext.newObject(CustomFieldType)
            enrolmentUnitUidField.dataType = DataType.TEXT
            enrolmentUnitUidField.entityIdentifier = Enrolment.simpleName
            enrolmentUnitUidField.key = TCSI_ENROLMENT_UNIT_UID
            enrolmentUnitUidField.name = 'TCSI enrolment unit identifier'
            enrolmentUnitUidField.isMandatory = false
            enrolmentUnitUidField.sortOrder = 1004l
            objectContext.commitChanges()
        }
    }

    String activateDevice(String activationCode) {
        String errorMessage = null
        RSAKey jwk = RSAKey.parse(jwkCertificate)
        try {
            new RESTClient(BASE_URL).request(PUT, JSON) {
                uri.path = "/piaweb/api/b2b/v1/devices/$deviceName/jwk"
                headers.'dhs-messageId'= "deviceActivation_$deviceName".toString()
                headers.'dhs-correlationId'= "deviceActivation_${deviceName}_${activationCode}".toString()
                headers.'dhs-productId'= DHS_PRODUCT_ID
                headers.'dhs-auditId'= organisationId
                headers.'dhs-auditIdType'= 'http://humanservices.gov.au/PRODA/org'
                headers.'dhs-subjectId'= deviceName
                headers.'dhs-subjectIdType'= 'http://humanservices.gov.au/PRODA/device'
                body = [ 'otac' : activationCode,
                         'orgId': organisationId,
                         'key'  : jwk.toPublicJWK().toJSONObject()
                ]
                response.success = { resp, body ->
                    logger.warn("Device activation success: $deviceName, $organisationId")
                }
                response.failure = { resp, body ->
                    if (resp instanceof HttpResponseDecorator) {
                        HttpResponseDecorator decorator = resp as HttpResponseDecorator
                        String headers = decorator.headers.inject  {a,b -> a = "$a\n  $b"}
                    
                    
                    logger.error("HEIMS data collection system responde with error:\n" +
                            "device name: $deviceName\n" +
                            "organisation id: $organisationId\n" +
                            "activation code: $activationCode\n" +
                            "jwk certificate $jwkCertificate \n" +
                            "response satatus: $decorator.status\n" +
                            "response body: $body\n" +
                            "response headers: $headers")
                    }
                    
                    errorMessage = "HEIMS data collection system responde with error: \n"

                    body['errors'].each { error ->
                        errorMessage += "${error['code']}: ${error['message']} \n"
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Something went wrong when trying to connect to HEIMS data collection system, " +
                    "$deviceName, $organisationId, $activationCode, $jwkCertificate")
            logger.catching(e)
            errorMessage = 'Something went wrong when trying to connect to HEIMS data collection system. Please, try again or contact ish support team for more details.'
        }
        return errorMessage
    }

    Object authenticatDevice() {

        String jwtString = AuthHelper.generateJwt(jwkCertificate,
                deviceName,
                organisationId,
                AUTH_AUDIENCE,
                AUTH_AUDIENCE_TSCI)

        new RESTClient(AUTH_URL).request(POST, URLENC) {
            uri.path = '/mga/sps/oauth/oauth20/token'
            headers.'dhs-messageId'= "deviceAuthentication_$deviceName".toString()
            headers.'dhs-correlationId'= "deviceAuthentication_${deviceName}_${new Date().time}".toString()
            headers.'dhs-productId'= DHS_PRODUCT_ID
            headers.'dhs-auditId'= organisationId
            headers.'dhs-auditIdType'= 'http://humanservices.gov.au/PRODA/org'
            headers.'dhs-subjectId'= deviceName
            headers.'dhs-subjectIdType'= 'http://humanservices.gov.au/PRODA/device'
            headers.'Host' = AUTH_HOST
            body = "grant_type=urn:ietf:params:oauth:grant-type:jwt-bearer&assertion=$jwtString&client_id=$DHS_PRODUCT_ID"
            response.success = { resp, body ->
                logger.warn("Device authentication success: $deviceName, $organisationId,  $jwkCertificate")
                authToken = new JsonFastParser().parse(body.keySet()[0] as String)['access_token']
            }

            response.failure = { resp, body ->
                String errorMessage = body['error_description']

                logger.error("Device authentication error: $errorMessage \n" +
                        "$deviceName, $organisationId, $jwkCertificate \n" +
                        "resp: $resp \n" +
                        "body: $body")

                throw new RuntimeException('Device authentication error')
            }
        }

        return true
    }

    @OnSave
    static void onSave(IntegrationConfiguration configuration, Map<String,String> props) {
        String activationCode = props[TCSI_ACTIVATION_CODE]
        String deviceName = props[TCSI_DEVICE_NAME]
        String orgId =  props[TCSI_ORGANISATION_ID]
        String jwk =  AuthHelper.generateJwk(deviceName)
        configuration.setProperty(TCSI_DEVICE_NAME, deviceName)
        configuration.setProperty(TCSI_ORGANISATION_ID, orgId)
        configuration.setProperty(TCSI_JWK_CERTIFICATE, jwk)

        TCSIIntegration tcsiIntegration = new TCSIIntegration(configuration: configuration)
        String error = tcsiIntegration.activateDevice(activationCode)
        if (error) {
            throw new ClientErrorException(Response.status(Response.Status.BAD_REQUEST).entity(new ValidationErrorDTO(errorMessage: error)).build())
        }
    }
    
    void export(Enrolment e) {
        enrolment = objectContext.localObject(e)
        highEducation = TCSIUtils.getHighEducation(objectContext, highEducationType, enrolment)
        courseAdmission = enrolment.student.enrolments.find {it.courseClass.course.equalsIgnoreContext(highEducation)}
        
        if (!courseAdmission) {
            interraptExport("$enrolment.student.fullName has no enrolment for high education course: $highEducation.name")
        }
        
        if (!highEducation) {
            interraptExport("Enrolment is not a high education or unit of study")
        }
        
        String studentUid = enrolment.student.contact.getCustomFieldValue(TCSI_STUDENT_UID)?.toString() ?: createStudent()
        String courseUid = highEducation.getCustomFieldValue(TCSI_COURSE_UID)?.toString() ?: createCourseGroup()
        String admissionUid = courseAdmission.getCustomFieldValue(TCSI_COURSE_ADMISSION_UID) ?: createCourseAdmission(studentUid,courseUid)
        String campuseUid = null
        if (enrolment.courseClass.room) {
            campuseUid = getCampus()
            if (!campuseUid) {
                campuseUid = createCampus()
            }
        }
        
        if (!enrolment.courseClass.course.equalsIgnoreContext(highEducation)) {
            // export unit
            def unitUid  = enrolment.getCustomFieldValue(TCSI_ENROLMENT_UNIT_UID)
            if (unitUid) {
                interraptExport("Enrolment unit already exported")
            } else {
                createUnit(admissionUid, campuseUid)
            }
            
        }
    }
    
    String getCampus() {
        getClient().request(GET, JSON) {
            uri.path = CAMPUSES_PATH
            headers.'tcsi-pagination-page'='1'
            headers.'tcsi-pagination-pagesize'='1000'

            response.success = { resp, result ->
                def campuses =  handleResponce(result, "get campuses ")

                def campus = campuses.find { it['campus']['delivery_location_code'] == enrolment.courseClass.room.site.id.toString()}
                if (campus) {
                    return campus['campus']['campuses_uid']
                }
                return null
            }
            response.failure =  { resp, body ->
                interraptExport("Something unexpected happend, please contact ish support for more details\n ${resp.toString()}\n ${body.toString()}".toString())
            }
        }
    }

    String createCampus() {
        getClient().request(POST, JSON) {
            uri.path = CAMPUSES_PATH
            body = TCSIUtils.getCampusData(enrolment.courseClass.room.site)
            response.success = { resp, result ->
                def campus =  handleResponce(result, "Create campus")
                return campus['campuses_uid'].toString()
            }
            response.failure =  { resp, body ->
                interraptExport("Something unexpected happend, please contact ish support for more details\n ${resp.toString()}\n ${body.toString()}".toString())
            }
        }
    }

    String createUnit(String admissionUid, String campuseUid) {
        getClient().request(POST, JSON) {
            uri.path = UNITS_PATH
            body = TCSIUtils.getUnitData(enrolment, admissionUid, campuseUid)

            response.success = { resp, result ->
                def unit =  handleResponce(result as List, "Create enrolment unit")
                def uid = unit['unit_enrolments_uid'].toString()
                EnrolmentCustomField customField = objectContext.newObject(EnrolmentCustomField)
                customField.relatedObject = enrolment
                customField.customFieldType = enrolmentUnitUidField
                customField.value = uid
                objectContext.commitChanges()
                return uid
            }
            response.failure =  { resp, body ->
                interraptExport("Something unexpected happend, please contact ish support for more details\n ${resp.toString()}\n ${body.toString()}".toString())
            }
        }
    }
    
    String createCourseAdmission(String studentUID, String courseUid) {

        getClient().request(POST, JSON) {
            uri.path = ADMISSIONS_PATH
            body = TCSIUtils.getAdmissionData(enrolment.student, highEducation, highEducationType, courseAdmission, studentUID, courseUid)

            response.success = { resp, result -> 
                def admission =  handleResponce(result as List, "Create admission")
                def uid = admission['course_admissions_uid'].toString()
                EnrolmentCustomField customField = objectContext.newObject(EnrolmentCustomField)
                customField.relatedObject = courseAdmission
                customField.customFieldType = courseAdmissionUidField
                customField.value = uid
                objectContext.commitChanges()
                return uid
            }
            response.failure =  { resp, body ->
                interraptExport("Something unexpected happend, please contact ish support for more details\n ${resp.toString()}\n ${body.toString()}".toString())
            }
        } 
    }
    

    String createCourseGroup() {
        getClient().request(POST, JSON) {
            uri.path = COURSES_PATH
            body = TCSIUtils.getCourseData(highEducation, highEducationType)
            response.success = { resp, result ->
                def course = handleResponce(result as List, "Create course")
                def uid = course['courses_uid'].toString()
                CourseCustomField customField = objectContext.newObject(CourseCustomField)
                customField.relatedObject = highEducation
                customField.customFieldType = courseUidField
                customField.value = uid
                objectContext.commitChanges()
                return uid
            }
            response.failure =  { resp, body ->
                interraptExport("Something unexpected happend, please contact ish support for more details\n ${resp.toString()}\n ${body.toString()}".toString())
            }
        }
    }
    
    
    String createStudent() {
        getClient().request(POST, JSON) {
            uri.path = STUDENTS_PATH
            body = TCSIUtils.getStudentData(enrolment.student)
            response.success = { resp, result ->
                def student = handleResponce(result as List, "Create student")
                def uid = student['students_uid'].toString()
                ContactCustomField customField = objectContext.newObject(ContactCustomField)
                customField.relatedObject = enrolment.student.contact
                customField.customFieldType = studentUidField
                customField.value = uid
                objectContext.commitChanges()
                return uid
            }
            response.failure =  { resp, body ->
                interraptExport("Something unexpected happend, please contact ish support for more details\n ${resp.toString()}\n ${body.toString()}".toString())
            }
        }
    }

    private RESTClient getClient() {
        if  (!authToken) {
            authenticatDevice()
        }
        Date currentDate = new Date()
        String timestamp = (currentDate.getTime() / 1000).toPlainString()
        RESTClient client  = new RESTClient(TCSI_BASE_URL)
        client.headers["date-timestamp"] = timestamp
        client.headers["Content-Type"] = "application/json"
        client.headers["Accept"] = "application/json"
        client.headers['x-ibm-client-id'] = DHS_PRODUCT_ID
        client.headers['organisation-id'] = organisationId
        client.headers['organisation-name'] = "ish onCourse"
        client.headers['provider-type'] = "VET"
        client.headers['user-name'] = "ish"
        client.headers['message-id'] = timestamp
        client.headers['software-name'] = "onCourse"
        client.headers['software-version'] = '1.0.0'
        client.headers['access-token'] = authToken
        client.headers['tcsi-omit-links'] = true

        return client
    }
    
    private Object handleResponce(Object responceObject, String description) {
        
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
    }
    
   void interraptExport(String message) {
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
