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
import ish.oncourse.commercial.plugin.tcsi.api.AdmissionAPI
import ish.oncourse.commercial.plugin.tcsi.api.CampusAPI
import ish.oncourse.commercial.plugin.tcsi.api.CourseAPI
import ish.oncourse.commercial.plugin.tcsi.api.StudentAPI
import ish.oncourse.commercial.plugin.tcsi.api.UnitAPI
import ish.oncourse.server.api.v1.model.ValidationErrorDTO
import ish.oncourse.server.cayenne.Course
import ish.oncourse.server.cayenne.Enrolment
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
import static groovyx.net.http.Method.POST
import static groovyx.net.http.Method.PUT



@Plugin(type = 11, oneOnly = true)
@CompileDynamic
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
    
    static final String HIGH_EDUCATION_TYPE  = 'Higher education'

    String deviceName
    String organisationId
    String jwkCertificate
    ObjectContext objectContext
    
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
        if (!highEducation.qualification) {
            interraptExport("Highe education course has no qualification")
        }

        StudentAPI studentAPI = new StudentAPI(getClient(), enrolment, emailService, preferenceController)
        
        String studentUid = studentAPI.getStudentUid()
        if (studentUid) {
            studentAPI.updateStudent(studentUid)
        } else {
            studentAPI.createStudent()
        }

        String courseUid = new CourseAPI(getClient(), enrolment, emailService, preferenceController).getCourseGroup(highEducation.qualification.nationalCode)
        if (!courseUid) {
            interraptExport("Highe education course not found in TCSI")
        }

        AdmissionAPI admissionAPI = new AdmissionAPI(highEducation, highEducationType, courseAdmission, getClient(), enrolment, emailService, preferenceController)
        String admissionUid = admissionAPI.getAdmission(studentUid, courseUid)
        if (admissionUid) {
            admissionAPI.updateAdmission(admissionUid,studentUid,courseUid)
        } else {
            admissionUid = admissionAPI.createCourseAdmission(studentUid,courseUid)
        }


        String campuseUid = new CampusAPI(getClient(), enrolment, emailService, preferenceController).campusUid()
       
        
        if (!enrolment.courseClass.course.equalsIgnoreContext(highEducation)) {
            
            UnitAPI unitAPI = new UnitAPI(getClient(), enrolment, emailService, preferenceController)
            // export unit
            String unitUid = unitAPI.getUnit(admissionUid)
            if (unitUid) {
                unitAPI.updateUnit(unitUid,admissionUid, campuseUid)
            } else {
                unitAPI.createUnit(admissionUid, campuseUid)
            }
        }
    }
     
    RESTClient getClient() {
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
