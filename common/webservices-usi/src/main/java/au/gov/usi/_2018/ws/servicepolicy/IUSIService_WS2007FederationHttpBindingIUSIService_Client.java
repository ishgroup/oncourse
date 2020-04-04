
package au.gov.usi._2018.ws.servicepolicy;

/**
 * Please modify this class to meet your needs
 * This class is not complete
 */

import javax.xml.namespace.QName;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * This class was generated by Apache CXF 3.3.5
 * 2020-03-27T13:59:31.930+11:00
 * Generated source version: 3.3.5
 *
 */
public final class IUSIService_WS2007FederationHttpBindingIUSIService_Client {

    private static final QName SERVICE_NAME = new QName("http://usi.gov.au/2018/ws/servicepolicy", "USIService");

    private IUSIService_WS2007FederationHttpBindingIUSIService_Client() {
    }

    public static void main(String args[]) throws Exception {
        URL wsdlURL = USIService.WSDL_LOCATION;
        if (args.length > 0 && args[0] != null && !"".equals(args[0])) {
            File wsdlFile = new File(args[0]);
            try {
                if (wsdlFile.exists()) {
                    wsdlURL = wsdlFile.toURI().toURL();
                } else {
                    wsdlURL = new URL(args[0]);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

        USIService ss = new USIService(wsdlURL, SERVICE_NAME);
        IUSIService port = ss.getWS2007FederationHttpBindingIUSIService();

        {
        System.out.println("Invoking getCountries...");
        au.gov.usi._2018.ws.GetCountriesType _getCountries_in = null;
        try {
            au.gov.usi._2018.ws.GetCountriesResponseType _getCountries__return = port.getCountries(_getCountries_in);
            System.out.println("getCountries.result=" + _getCountries__return);

        } catch (IUSIServiceGetCountriesErrorInfoFaultFaultMessageSingle e) {
            System.out.println("Expected exception: IUSIService_GetCountries_ErrorInfoFault_FaultMessage_Single has occurred.");
            System.out.println(e.toString());
        } catch (IUSIServiceGetCountriesErrorInfoFaultFaultMessage e) {
            System.out.println("Expected exception: IUSIService_GetCountries_ErrorInfoFault_FaultMessage has occurred.");
            System.out.println(e.toString());
        }

            }
        {
        System.out.println("Invoking bulkUploadRetrieve...");
        au.gov.usi._2018.ws.BulkUploadRetrieveType _bulkUploadRetrieve_in = null;
        try {
            au.gov.usi._2018.ws.BulkUploadRetrieveResponseType _bulkUploadRetrieve__return = port.bulkUploadRetrieve(_bulkUploadRetrieve_in);
            System.out.println("bulkUploadRetrieve.result=" + _bulkUploadRetrieve__return);

        } catch (IUSIServiceBulkUploadRetrieveErrorInfoFaultFaultMessageSingle e) {
            System.out.println("Expected exception: IUSIService_BulkUploadRetrieve_ErrorInfoFault_FaultMessage_Single has occurred.");
            System.out.println(e.toString());
        } catch (IUSIServiceBulkUploadRetrieveErrorInfoFaultFaultMessage e) {
            System.out.println("Expected exception: IUSIService_BulkUploadRetrieve_ErrorInfoFault_FaultMessage has occurred.");
            System.out.println(e.toString());
        }

            }
        {
        System.out.println("Invoking updateUSIPersonalDetails...");
        au.gov.usi._2018.ws.UpdateUSIPersonalDetailsType _updateUSIPersonalDetails_in = null;
        try {
            au.gov.usi._2018.ws.UpdateUSIPersonalDetailsResponseType _updateUSIPersonalDetails__return = port.updateUSIPersonalDetails(_updateUSIPersonalDetails_in);
            System.out.println("updateUSIPersonalDetails.result=" + _updateUSIPersonalDetails__return);

        } catch (IUSIServiceUpdateUSIPersonalDetailsErrorInfoFaultFaultMessageSingle e) {
            System.out.println("Expected exception: IUSIService_UpdateUSIPersonalDetails_ErrorInfoFault_FaultMessage_Single has occurred.");
            System.out.println(e.toString());
        } catch (IUSIServiceUpdateUSIPersonalDetailsErrorInfoFaultFaultMessage e) {
            System.out.println("Expected exception: IUSIService_UpdateUSIPersonalDetails_ErrorInfoFault_FaultMessage has occurred.");
            System.out.println(e.toString());
        }

            }
        {
        System.out.println("Invoking bulkVerifyUSI...");
        au.gov.usi._2018.ws.BulkVerifyUSIType _bulkVerifyUSI_in = null;
        try {
            au.gov.usi._2018.ws.BulkVerifyUSIResponseType _bulkVerifyUSI__return = port.bulkVerifyUSI(_bulkVerifyUSI_in);
            System.out.println("bulkVerifyUSI.result=" + _bulkVerifyUSI__return);

        } catch (IUSIServiceBulkVerifyUSIErrorInfoFaultFaultMessageSingle e) {
            System.out.println("Expected exception: IUSIService_BulkVerifyUSI_ErrorInfoFault_FaultMessage_Single has occurred.");
            System.out.println(e.toString());
        } catch (IUSIServiceCreateUSIErrorInfoFaultFaultMessage e) {
            System.out.println("Expected exception: IUSIService_CreateUSI_ErrorInfoFault_FaultMessage has occurred.");
            System.out.println(e.toString());
        }

            }
        {
        System.out.println("Invoking createUSI...");
        au.gov.usi._2018.ws.CreateUSIType _createUSI_in = null;
        try {
            au.gov.usi._2018.ws.CreateUSIResponseType _createUSI__return = port.createUSI(_createUSI_in);
            System.out.println("createUSI.result=" + _createUSI__return);

        } catch (IUSIServiceCreateUSIErrorInfoFaultFaultMessageSingle e) {
            System.out.println("Expected exception: IUSIService_CreateUSI_ErrorInfoFault_FaultMessage_Single has occurred.");
            System.out.println(e.toString());
        } catch (IUSIServiceCreateUSIErrorInfoFaultFaultMessage e) {
            System.out.println("Expected exception: IUSIService_CreateUSI_ErrorInfoFault_FaultMessage has occurred.");
            System.out.println(e.toString());
        }

            }
        {
        System.out.println("Invoking updateUSIContactDetails...");
        au.gov.usi._2018.ws.UpdateUSIContactDetailsType _updateUSIContactDetails_in = null;
        try {
            au.gov.usi._2018.ws.UpdateUSIContactDetailsResponseType _updateUSIContactDetails__return = port.updateUSIContactDetails(_updateUSIContactDetails_in);
            System.out.println("updateUSIContactDetails.result=" + _updateUSIContactDetails__return);

        } catch (IUSIServiceUpdateUSIContactDetailsErrorInfoFaultFaultMessageSingle e) {
            System.out.println("Expected exception: IUSIService_UpdateUSIContactDetails_ErrorInfoFault_FaultMessage_Single has occurred.");
            System.out.println(e.toString());
        } catch (IUSIServiceUpdateUSIContactDetailsErrorInfoFaultFaultMessage e) {
            System.out.println("Expected exception: IUSIService_UpdateUSIContactDetails_ErrorInfoFault_FaultMessage has occurred.");
            System.out.println(e.toString());
        }

            }
        {
        System.out.println("Invoking getNonDvsDocumentTypes...");
        au.gov.usi._2018.ws.GetNonDvsDocumentTypesType _getNonDvsDocumentTypes_in = null;
        try {
            au.gov.usi._2018.ws.GetNonDvsDocumentTypesResponseType _getNonDvsDocumentTypes__return = port.getNonDvsDocumentTypes(_getNonDvsDocumentTypes_in);
            System.out.println("getNonDvsDocumentTypes.result=" + _getNonDvsDocumentTypes__return);

        } catch (IUSIServiceGetNonDvsDocumentTypesErrorInfoFaultFaultMessageSingle e) {
            System.out.println("Expected exception: IUSIService_GetNonDvsDocumentTypes_ErrorInfoFault_FaultMessage_Single has occurred.");
            System.out.println(e.toString());
        } catch (IUSIServiceGetNonDvsDocumentTypesErrorInfoFaultFaultMessage e) {
            System.out.println("Expected exception: IUSIService_GetNonDvsDocumentTypes_ErrorInfoFault_FaultMessage has occurred.");
            System.out.println(e.toString());
        }

            }
        {
        System.out.println("Invoking locateUSI...");
        au.gov.usi._2018.ws.LocateUSIType _locateUSI_in = null;
        try {
            au.gov.usi._2018.ws.LocateUSIResponseType _locateUSI__return = port.locateUSI(_locateUSI_in);
            System.out.println("locateUSI.result=" + _locateUSI__return);

        } catch (IUSIServiceLocateUSIErrorInfoFaultFaultMessageSingle e) {
            System.out.println("Expected exception: IUSIService_LocateUSI_ErrorInfoFault_FaultMessage_Single has occurred.");
            System.out.println(e.toString());
        } catch (IUSIServiceLocateUSIErrorInfoFaultFaultMessage e) {
            System.out.println("Expected exception: IUSIService_LocateUSI_ErrorInfoFault_FaultMessage has occurred.");
            System.out.println(e.toString());
        }

            }
        {
        System.out.println("Invoking verifyUSI...");
        au.gov.usi._2018.ws.VerifyUSIType _verifyUSI_in = null;
        try {
            au.gov.usi._2018.ws.VerifyUSIResponseType _verifyUSI__return = port.verifyUSI(_verifyUSI_in);
            System.out.println("verifyUSI.result=" + _verifyUSI__return);

        } catch (IUSIServiceVerifyUSIErrorInfoFaultFaultMessageSingle e) {
            System.out.println("Expected exception: IUSIService_VerifyUSI_ErrorInfoFault_FaultMessage_Single has occurred.");
            System.out.println(e.toString());
        } catch (IUSIServiceVerifyUSIErrorInfoFaultFaultMessage e) {
            System.out.println("Expected exception: IUSIService_VerifyUSI_ErrorInfoFault_FaultMessage has occurred.");
            System.out.println(e.toString());
        }

            }
        {
        System.out.println("Invoking bulkUpload...");
        au.gov.usi._2018.ws.BulkUploadType _bulkUpload_in = null;
        try {
            au.gov.usi._2018.ws.BulkUploadResponseType _bulkUpload__return = port.bulkUpload(_bulkUpload_in);
            System.out.println("bulkUpload.result=" + _bulkUpload__return);

        } catch (IUSIServiceBulkUploadErrorInfoFaultFaultMessageSingle e) {
            System.out.println("Expected exception: IUSIService_BulkUpload_ErrorInfoFault_FaultMessage_Single has occurred.");
            System.out.println(e.toString());
        } catch (IUSIServiceBulkUploadErrorInfoFaultFaultMessage e) {
            System.out.println("Expected exception: IUSIService_BulkUpload_ErrorInfoFault_FaultMessage has occurred.");
            System.out.println(e.toString());
        }

            }

        System.exit(0);
    }

}
