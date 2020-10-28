package ish.oncourse.services.usi

import groovyx.net.http.ContentType
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.HttpResponseException
import ish.common.types.LocateUSIRequest
import ish.common.types.LocateUSIResult
import ish.common.types.LocateUSIType
import ish.common.types.USIFieldStatus
import ish.common.types.USIGender
import ish.common.types.USIVerificationRequest
import ish.common.types.USIVerificationResult
import ish.common.types.USIVerificationStatus
import ish.oncourse.configuration.Configuration
import ish.oncourse.services.preference.PreferenceController
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.apache.tapestry5.ioc.annotations.Inject

import static ish.oncourse.configuration.Configuration.AppProperty.USI_LOCATION

class UsiRestService implements IUsiRestService {

    private static final Logger logger = LogManager.logger

    private final PreferenceController preferenceController
    private final String USI_URL

    @Inject
    UsiRestService(PreferenceController preferenceController) {
        this.preferenceController = preferenceController
        USI_URL = Configuration.getValue(USI_LOCATION)
    }


    USIVerificationResult verify(USIVerificationRequest request) {
        String abn = preferenceController.getCollegeABN().replaceAll("\\s","")
        String ssid = preferenceController.getUsiSoftwareId()
        USIVerificationResult result = new USIVerificationResult()
        try {
            new HTTPBuilder().get(
                [uri: USI_URL,
                 path:   '/usi/verify',
                 query: ['studentFirstName' : request.studentFirstName,
                         'studentLastName' : request.studentLastName,
                         'studentBirthDate' : request.studentBirthDate.format('yyyy-MM-dd'),
                         'usiCode' : request.usiCode,
                         'orgCode' : request.orgCode,
                         'collegeABN' : abn,
                         'softwareId' : ssid]
                ])
                { response, body ->
                    String error = body["errorMessage"] as String
                    if (error) {
                        USIVerificationResult usiResult = new USIVerificationResult()
                        usiResult.setErrorMessage(error)
                        result =  usiResult
                    } else {
                        result.usiStatus = USIVerificationStatus.values().find {it.name() == body["usiStatus"] }
                        result.firstNameStatus = USIFieldStatus.values().find {it.name() == body["firstNameStatus"] }
                        result.lastNameStatus = USIFieldStatus.values().find {it.name() == body["lastNameStatus"] }
                        result.dateOfBirthStatus = USIFieldStatus.values().find {it.name() == body["dateOfBirthStatus"] }
                    }

                }
        } catch (Exception e) {
            logger.error("can not verify usi: $request, $abn, $ssid")
            logger.catching(e)
            USIVerificationResult usiResult = new USIVerificationResult()
            usiResult.setErrorMessage("Can not verify USI. Please, connect support team")
            return usiResult
        }
        return result

    }


    LocateUSIResult locate(LocateUSIRequest request) {

        String abn = preferenceController.getCollegeABN().replaceAll("\\s","")
        String ssid = preferenceController.getUsiSoftwareId()

        LocateUSIResult result = new LocateUSIResult()

        Map queryParams = ['collegeABN' : abn,
                           'softwareId' : ssid,
                           'orgCode' : request.orgCode,
                           'firstName' : request.firstName,
                           'familyName' : request.familyName ]
        if (request.middleName) {
            queryParams['middleName'] = request.middleName
        }
        if (request.dateOfBirth) {
            queryParams['dateOfBirth'] = request.dateOfBirth.format('yyyy-MM-dd')
        }
        if (request.townCityOfBirth) {
            queryParams['townCityOfBirth'] = request.townCityOfBirth
        }
        if (request.emailAddress) {
            queryParams['emailAddress'] = request.emailAddress
        }
        if (request.userReference) {
            queryParams['userReference'] = request.userReference
        }
        if (request.gender) {
            queryParams['gender'] = request.gender.requestCode
        }


        try {

            new HTTPBuilder().get(
                    [uri: USI_URL,
                     path:   "/usi/locate",
                     query: queryParams
                    ]) { response, body ->
                            result.resultType = LocateUSIType.values().find {it.name() == body["resultType"] }
                            result.usi = body["usi"]
                            result.message =  body["message"]
                            result.error = body["error"]
                    }
        } catch (Exception e) {
            logger.error("can not locate usi: $request, $abn, $ssid")
            logger.catching(e)
            result.error = "Can not locate USI. Please, connect support team"
            return result
        }
        return result;

    }



}
