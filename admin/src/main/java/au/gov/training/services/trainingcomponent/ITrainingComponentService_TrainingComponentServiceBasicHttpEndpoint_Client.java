
package au.gov.training.services.trainingcomponent;

/**
 * Please modify this class to meet your needs
 * This class is not complete
 */

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.Action;
import javax.xml.ws.FaultAction;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

/**
 * This class was generated by Apache CXF 2.4.0
 * 2011-10-12T16:55:20.884+03:00
 * Generated source version: 2.4.0
 * 
 */
public final class ITrainingComponentService_TrainingComponentServiceBasicHttpEndpoint_Client {

    private static final QName SERVICE_NAME = new QName("http://training.gov.au/services/", "TrainingComponentService");

    private ITrainingComponentService_TrainingComponentServiceBasicHttpEndpoint_Client() {
    }

    public static void main(String args[]) throws Exception {
        URL wsdlURL = TrainingComponentService.WSDL_LOCATION;
        if (args.length > 0) { 
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
      
        TrainingComponentService ss = new TrainingComponentService(wsdlURL, SERVICE_NAME);
        ITrainingComponentService port = ss.getTrainingComponentServiceBasicHttpEndpoint();  
        
        {
        System.out.println("Invoking getClassificationSchemes...");
        try {
            au.gov.training.services.trainingcomponent.ArrayOfNrtClassificationSchemeResult _getClassificationSchemes__return = port.getClassificationSchemes();
            System.out.println("getClassificationSchemes.result=" + _getClassificationSchemes__return);

        } catch (ITrainingComponentServiceGetClassificationSchemesValidationFaultFaultFaultMessage e) { 
            System.out.println("Expected exception: ITrainingComponentService_GetClassificationSchemes_ValidationFaultFault_FaultMessage has occurred.");
            System.out.println(e.toString());
        }
            }
        {
        System.out.println("Invoking add...");
        au.gov.training.services.trainingcomponent.TrainingComponent _add_request = null;
        try {
            port.add(_add_request);

        } catch (ITrainingComponentServiceAddValidationFaultFaultFaultMessage e) { 
            System.out.println("Expected exception: ITrainingComponentService_Add_ValidationFaultFault_FaultMessage has occurred.");
            System.out.println(e.toString());
        }
            }
        {
        System.out.println("Invoking getValidationCodes...");
        try {
            au.gov.training.services.trainingcomponent.ArrayOfValidationCode _getValidationCodes__return = port.getValidationCodes();
            System.out.println("getValidationCodes.result=" + _getValidationCodes__return);

        } catch (ITrainingComponentServiceGetValidationCodesValidationFaultFaultFaultMessage e) { 
            System.out.println("Expected exception: ITrainingComponentService_GetValidationCodes_ValidationFaultFault_FaultMessage has occurred.");
            System.out.println(e.toString());
        }
            }
        {
        System.out.println("Invoking getServerTime...");
        try {
            javax.xml.datatype.XMLGregorianCalendar _getServerTime__return = port.getServerTime();
            System.out.println("getServerTime.result=" + _getServerTime__return);

        } catch (ITrainingComponentServiceGetServerTimeValidationFaultFaultFaultMessage e) { 
            System.out.println("Expected exception: ITrainingComponentService_GetServerTime_ValidationFaultFault_FaultMessage has occurred.");
            System.out.println(e.toString());
        }
            }
        {
        System.out.println("Invoking delete...");
        au.gov.training.services.trainingcomponent.TrainingComponentDeleteRequest _delete_request = null;
        try {
            port.delete(_delete_request);

        } catch (ITrainingComponentServiceDeleteValidationFaultFaultFaultMessage e) { 
            System.out.println("Expected exception: ITrainingComponentService_Delete_ValidationFaultFault_FaultMessage has occurred.");
            System.out.println(e.toString());
        }
            }
        {
        System.out.println("Invoking search...");
        au.gov.training.services.trainingcomponent.TrainingComponentSearchRequest _search_request = null;
        try {
            au.gov.training.services.trainingcomponent.TrainingComponentSearchResult _search__return = port.search(_search_request);
            System.out.println("search.result=" + _search__return);

        } catch (ITrainingComponentServiceSearchValidationFaultFaultFaultMessage e) { 
            System.out.println("Expected exception: ITrainingComponentService_Search_ValidationFaultFault_FaultMessage has occurred.");
            System.out.println(e.toString());
        }
            }
        {
        System.out.println("Invoking getDetails...");
        au.gov.training.services.trainingcomponent.TrainingComponentDetailsRequest _getDetails_request = null;
        try {
            au.gov.training.services.trainingcomponent.TrainingComponent _getDetails__return = port.getDetails(_getDetails_request);
            System.out.println("getDetails.result=" + _getDetails__return);

        } catch (ITrainingComponentServiceGetDetailsValidationFaultFaultFaultMessage e) { 
            System.out.println("Expected exception: ITrainingComponentService_GetDetails_ValidationFaultFault_FaultMessage has occurred.");
            System.out.println(e.toString());
        }
            }
        {
        System.out.println("Invoking getLookup...");
        au.gov.training.services.trainingcomponent.LookupRequest _getLookup_request = null;
        try {
            au.gov.training.services.trainingcomponent.ArrayOfLookup _getLookup__return = port.getLookup(_getLookup_request);
            System.out.println("getLookup.result=" + _getLookup__return);

        } catch (ITrainingComponentServiceGetLookupValidationFaultFaultFaultMessage e) { 
            System.out.println("Expected exception: ITrainingComponentService_GetLookup_ValidationFaultFault_FaultMessage has occurred.");
            System.out.println(e.toString());
        }
            }
        {
        System.out.println("Invoking searchDeletedByDeletedDate...");
        au.gov.training.services.trainingcomponent.DeletedSearchRequest _searchDeletedByDeletedDate_request = null;
        try {
            au.gov.training.services.trainingcomponent.ArrayOfDeletedTrainingComponent _searchDeletedByDeletedDate__return = port.searchDeletedByDeletedDate(_searchDeletedByDeletedDate_request);
            System.out.println("searchDeletedByDeletedDate.result=" + _searchDeletedByDeletedDate__return);

        } catch (ITrainingComponentServiceSearchDeletedByDeletedDateValidationFaultFaultFaultMessage e) { 
            System.out.println("Expected exception: ITrainingComponentService_SearchDeletedByDeletedDate_ValidationFaultFault_FaultMessage has occurred.");
            System.out.println(e.toString());
        }
            }
        {
        System.out.println("Invoking searchByModifiedDate...");
        au.gov.training.services.trainingcomponent.TrainingComponentModifiedSearchRequest _searchByModifiedDate_request = null;
        try {
            au.gov.training.services.trainingcomponent.TrainingComponentSearchResult _searchByModifiedDate__return = port.searchByModifiedDate(_searchByModifiedDate_request);
            System.out.println("searchByModifiedDate.result=" + _searchByModifiedDate__return);

        } catch (ITrainingComponentServiceSearchByModifiedDateValidationFaultFaultFaultMessage e) { 
            System.out.println("Expected exception: ITrainingComponentService_SearchByModifiedDate_ValidationFaultFault_FaultMessage has occurred.");
            System.out.println(e.toString());
        }
            }
        {
        System.out.println("Invoking getContactRoles...");
        try {
            au.gov.training.services.trainingcomponent.ArrayOfTrainingComponentContactRole _getContactRoles__return = port.getContactRoles();
            System.out.println("getContactRoles.result=" + _getContactRoles__return);

        } catch (ITrainingComponentServiceGetContactRolesValidationFaultFaultFaultMessage e) { 
            System.out.println("Expected exception: ITrainingComponentService_GetContactRoles_ValidationFaultFault_FaultMessage has occurred.");
            System.out.println(e.toString());
        }
            }
        {
        System.out.println("Invoking update...");
        au.gov.training.services.trainingcomponent.TrainingComponentUpdateRequest _update_request = null;
        try {
            port.update(_update_request);

        } catch (ITrainingComponentServiceUpdateValidationFaultFaultFaultMessage e) { 
            System.out.println("Expected exception: ITrainingComponentService_Update_ValidationFaultFault_FaultMessage has occurred.");
            System.out.println(e.toString());
        }
            }
        {
        System.out.println("Invoking getRecognitionManagers...");
        try {
            au.gov.training.services.trainingcomponent.ArrayOfRecognitionManager _getRecognitionManagers__return = port.getRecognitionManagers();
            System.out.println("getRecognitionManagers.result=" + _getRecognitionManagers__return);

        } catch (ITrainingComponentServiceGetRecognitionManagersValidationFaultFaultFaultMessage e) { 
            System.out.println("Expected exception: ITrainingComponentService_GetRecognitionManagers_ValidationFaultFault_FaultMessage has occurred.");
            System.out.println(e.toString());
        }
            }
        {
        System.out.println("Invoking transferDataManager...");
        au.gov.training.services.trainingcomponent.TrainingComponentTransferDataManagerRequest _transferDataManager_request = null;
        try {
            port.transferDataManager(_transferDataManager_request);

        } catch (ITrainingComponentServiceTransferDataManagerValidationFaultFaultFaultMessage e) { 
            System.out.println("Expected exception: ITrainingComponentService_TransferDataManager_ValidationFaultFault_FaultMessage has occurred.");
            System.out.println(e.toString());
        }
            }
        {
        System.out.println("Invoking getClassificationPurposes...");
        try {
            au.gov.training.services.trainingcomponent.ArrayOfClassificationPurpose _getClassificationPurposes__return = port.getClassificationPurposes();
            System.out.println("getClassificationPurposes.result=" + _getClassificationPurposes__return);

        } catch (ITrainingComponentServiceGetClassificationPurposesValidationFaultFaultFaultMessage e) { 
            System.out.println("Expected exception: ITrainingComponentService_GetClassificationPurposes_ValidationFaultFault_FaultMessage has occurred.");
            System.out.println(e.toString());
        }
            }
        {
        System.out.println("Invoking getDataManagers...");
        try {
            au.gov.training.services.trainingcomponent.ArrayOfDataManager _getDataManagers__return = port.getDataManagers();
            System.out.println("getDataManagers.result=" + _getDataManagers__return);

        } catch (ITrainingComponentServiceGetDataManagersValidationFaultFaultFaultMessage e) { 
            System.out.println("Expected exception: ITrainingComponentService_GetDataManagers_ValidationFaultFault_FaultMessage has occurred.");
            System.out.println(e.toString());
        }
            }
        {
        System.out.println("Invoking getAddressStates...");
        try {
            au.gov.training.services.trainingcomponent.ArrayOfAddressStates _getAddressStates__return = port.getAddressStates();
            System.out.println("getAddressStates.result=" + _getAddressStates__return);

        } catch (ITrainingComponentServiceGetAddressStatesValidationFaultFaultFaultMessage e) { 
            System.out.println("Expected exception: ITrainingComponentService_GetAddressStates_ValidationFaultFault_FaultMessage has occurred.");
            System.out.println(e.toString());
        }
            }

        System.exit(0);
    }

}
