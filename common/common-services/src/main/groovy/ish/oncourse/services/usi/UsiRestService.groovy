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
                 path:   "/verify" +
                         "?studentFirstName=$request.studentFirstName" +
                         "&studentLastName=$request.studentLastName" +
                         "&studentBirthDate=${request.studentBirthDate.format('yyyy-MM-dd')}" +
                         "&usiCode=$request.usiCode" +
                         "&orgCode=$request.orgCode" +
                         "&collegeABN=$abn" +
                         "&softwareId=$ssid"
                ])
                { response, body ->
                    String error = body["errorMessage"] as String
                    if (error) {
                        result =  USIVerificationResult.valueOf(error)
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
            return  USIVerificationResult.valueOf("Can not verify USI. Please, connect support team")
        }
        return result

    }


    LocateUSIResult locate(LocateUSIRequest request) {

        String abn = preferenceController.getCollegeABN().replaceAll("\\s","")
        String ssid = preferenceController.getUsiSoftwareId()

        LocateUSIResult result = new LocateUSIResult()
        try {
            String path = "/locate?collegeABN=$abn&softwareId=$ssid&orgCode=$request.orgCode&firstName=$request.firstName&familyName=$request.familyName"
            path += request.middleName ? "&middleName=${request.middleName}" : ''
            path += request.dateOfBirth ? "&dateOfBirth${request.dateOfBirth.format('yyyy-MM-dd')}" : ''
            path += request.townCityOfBirth ? "&townCityOfBirth=${request.townCityOfBirth}" : ''
            path += request.emailAddress ? "&emailAddress=${request.emailAddress}" : ''
            path += request.userReference ? "&userReference=${request.userReference}" : ''
            path += request.gender ? "&gender=${request.gender.requestCode}" : ''


            new HTTPBuilder().get(
                    [uri: USI_URL,
                     path:   path
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
