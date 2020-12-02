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
import ish.oncourse.server.api.v1.model.ValidationErrorDTO
import ish.oncourse.server.cayenne.IntegrationConfiguration
import ish.oncourse.server.integration.OnSave
import ish.oncourse.server.integration.Plugin
import ish.oncourse.server.integration.PluginTrait
import ish.oncourse.server.services.AuthHelper
import org.apache.groovy.json.internal.JsonFastParser
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import javax.ws.rs.ClientErrorException
import javax.ws.rs.core.Response

import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC
import static groovyx.net.http.Method.POST
import static groovyx.net.http.Method.PUT

@CompileDynamic
@Plugin(type = 11, oneOnly = true)
class TCSIIntegration implements PluginTrait {
    public static final String TCSI_DEVICE_NAME = "deviceName"
    public static final String TCSI_ORGANISATION_ID = "organisationId"
    public static final String TCSI_ACTIVATION_CODE = "activationCode"
    public static final String TCSI_JWK_CERTIFICATE = "jwkCertificate"

    static final String BASE_URL = 'https://5.rsp.humanservices.gov.au'
    static final String BASE_URL_TEST = 'https://test.5.rsp.humanservices.gov.au'
    static final String AUTH_URL = 'https://PRODA.humanservices.gov.au'
    static final String AUTH_URL_TEST= 'https://vnd.PRODA.humanservices.gov.au'
    static final String AUTH_AUDIENCE = 'https://proda.humanservices.gov.au'
    static final String AUTH_AUDIENCE_TSCI  = 'https://tcsi.humanservices.gov.au'
    static final String TCSI_BASE_URL = 'https://test.api.humanservices.gov.au/centrelink/ext-vend/tcsi/b2g/v1'
    static final String DHS_PRODUCT_ID = '08b1e117-5efa-4b4d-b3d7-65ae18908671'

    String deviceName
    String organisationId
    String jwkCertificate
    private String authToken

    private static Logger logger = LogManager.logger

    TCSIIntegration(Map args) {
        loadConfig(args)

        this.organisationId = configuration.getIntegrationProperty(TCSI_ORGANISATION_ID).value
        this.deviceName = configuration.getIntegrationProperty(TCSI_DEVICE_NAME).value
        this.jwkCertificate = configuration.getIntegrationProperty(TCSI_JWK_CERTIFICATE).value
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

    void authenticatDevice() {

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
}
