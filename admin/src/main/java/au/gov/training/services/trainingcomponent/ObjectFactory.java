
package au.gov.training.services.trainingcomponent;

import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import com.microsoft.schemas._2003._10.serialization.arrays.ArrayOfstring;
import org.datacontract.schemas._2004._07.system.DateTimeOffset;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the au.gov.training.services.trainingcomponent package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _TrainingComponentTransferDataManagerRequest_QNAME = new QName("http://training.gov.au/services/", "TrainingComponentTransferDataManagerRequest");
    private final static QName _ActionOnEntity_QNAME = new QName("http://training.gov.au/services/", "ActionOnEntity");
    private final static QName _DataManager_QNAME = new QName("http://training.gov.au/services/", "DataManager");
    private final static QName _ArrayOfTrainingComponentContactRole_QNAME = new QName("http://training.gov.au/services/", "ArrayOfTrainingComponentContactRole");
    private final static QName _Mapping_QNAME = new QName("http://training.gov.au/services/", "Mapping");
    private final static QName _ArrayOfRecognitionManager_QNAME = new QName("http://training.gov.au/services/", "ArrayOfRecognitionManager");
    private final static QName _AddressStates_QNAME = new QName("http://training.gov.au/services/", "AddressStates");
    private final static QName _ArrayOfDataManagerAssignment_QNAME = new QName("http://training.gov.au/services/", "ArrayOfDataManagerAssignment");
    private final static QName _ArrayOfRecognitionManagerAssignment_QNAME = new QName("http://training.gov.au/services/", "ArrayOfRecognitionManagerAssignment");
    private final static QName _GenericListPropertyOfNrtCompletionNrtCompletion8TYN1AE7_QNAME = new QName("http://training.gov.au/services/", "GenericListPropertyOfNrtCompletionNrtCompletion8TYN1aE7");
    private final static QName _ValidationCode_QNAME = new QName("http://training.gov.au/services/", "ValidationCode");
    private final static QName _ValidationError_QNAME = new QName("http://training.gov.au/services/", "ValidationError");
    private final static QName _NrtCurrencyPeriod2_QNAME = new QName("http://training.gov.au/services/", "NrtCurrencyPeriod2");
    private final static QName _ArrayOfClassification_QNAME = new QName("http://training.gov.au/services/", "ArrayOfClassification");
    private final static QName _Contact_QNAME = new QName("http://training.gov.au/services/", "Contact");
    private final static QName _CompletionMappingList_QNAME = new QName("http://training.gov.au/services/", "CompletionMappingList");
    private final static QName _DataManagerAssignment_QNAME = new QName("http://training.gov.au/services/", "DataManagerAssignment");
    private final static QName _ArrayOfDataManager_QNAME = new QName("http://training.gov.au/services/", "ArrayOfDataManager");
    private final static QName _Lookup_QNAME = new QName("http://training.gov.au/services/", "Lookup");
    private final static QName _ArrayOfMapping_QNAME = new QName("http://training.gov.au/services/", "ArrayOfMapping");
    private final static QName _LookupName_QNAME = new QName("http://training.gov.au/services/", "LookupName");
    private final static QName _ArrayOfValidationCode_QNAME = new QName("http://training.gov.au/services/", "ArrayOfValidationCode");
    private final static QName _ClassificationFilters_QNAME = new QName("http://training.gov.au/services/", "ClassificationFilters");
    private final static QName _TrainingComponentDeleteRequest_QNAME = new QName("http://training.gov.au/services/", "TrainingComponentDeleteRequest");
    private final static QName _TrainingComponentDetailsRequest_QNAME = new QName("http://training.gov.au/services/", "TrainingComponentDetailsRequest");
    private final static QName _OrganisationNameSearchRequest_QNAME = new QName("http://training.gov.au/services/", "OrganisationNameSearchRequest");
    private final static QName _ArrayOfContact_QNAME = new QName("http://training.gov.au/services/", "ArrayOfContact");
    private final static QName _NrtCompletion_QNAME = new QName("http://training.gov.au/services/", "NrtCompletion");
    private final static QName _SearchResult_QNAME = new QName("http://training.gov.au/services/", "SearchResult");
    private final static QName _ReleaseFile_QNAME = new QName("http://training.gov.au/services/", "ReleaseFile");
    private final static QName _UnitGridEntry3_QNAME = new QName("http://training.gov.au/services/", "UnitGridEntry3");
    private final static QName _ArrayOfRelease_QNAME = new QName("http://training.gov.au/services/", "ArrayOfRelease");
    private final static QName _NrtClassificationSchemeResult_QNAME = new QName("http://training.gov.au/services/", "NrtClassificationSchemeResult");
    private final static QName _ArrayOfReleaseFile_QNAME = new QName("http://training.gov.au/services/", "ArrayOfReleaseFile");
    private final static QName _ArrayOfClassificationPurpose_QNAME = new QName("http://training.gov.au/services/", "ArrayOfClassificationPurpose");
    private final static QName _LookupRequest_QNAME = new QName("http://training.gov.au/services/", "LookupRequest");
    private final static QName _TrainingComponentSummary_QNAME = new QName("http://training.gov.au/services/", "TrainingComponentSummary");
    private final static QName _ClassificationPurpose_QNAME = new QName("http://training.gov.au/services/", "ClassificationPurpose");
    private final static QName _ArrayOfNrtCompletion_QNAME = new QName("http://training.gov.au/services/", "ArrayOfNrtCompletion");
    private final static QName _GenericListPropertyOfContactTrainingComponentContact8TYN1AE7_QNAME = new QName("http://training.gov.au/services/", "GenericListPropertyOfContactTrainingComponentContact8TYN1aE7");
    private final static QName _UnitGridEntry_QNAME = new QName("http://training.gov.au/services/", "UnitGridEntry");
    private final static QName _Release_QNAME = new QName("http://training.gov.au/services/", "Release");
    private final static QName _RecognitionManagerAssignment_QNAME = new QName("http://training.gov.au/services/", "RecognitionManagerAssignment");
    private final static QName _ValidationErrorSeverity_QNAME = new QName("http://training.gov.au/services/", "ValidationErrorSeverity");
    private final static QName _AbstractPageRequest_QNAME = new QName("http://training.gov.au/services/", "AbstractPageRequest");
    private final static QName _AbstractDto_QNAME = new QName("http://training.gov.au/services/", "AbstractDto");
    private final static QName _ArrayOfReleaseCompanionVolumeLink_QNAME = new QName("http://training.gov.au/services/", "ArrayOfReleaseCompanionVolumeLink");
    private final static QName _TrainingComponentInformationRequested2_QNAME = new QName("http://training.gov.au/services/", "TrainingComponentInformationRequested2");
    private final static QName _ArrayOfNrtCurrencyPeriod_QNAME = new QName("http://training.gov.au/services/", "ArrayOfNrtCurrencyPeriod");
    private final static QName _ReleaseComponent_QNAME = new QName("http://training.gov.au/services/", "ReleaseComponent");
    private final static QName _TrainingComponentSearchRequest_QNAME = new QName("http://training.gov.au/services/", "TrainingComponentSearchRequest");
    private final static QName _TrainingComponentSearchResult_QNAME = new QName("http://training.gov.au/services/", "TrainingComponentSearchResult");
    private final static QName _ArrayOfLookup_QNAME = new QName("http://training.gov.au/services/", "ArrayOfLookup");
    private final static QName _TrainingComponent2_QNAME = new QName("http://training.gov.au/services/", "TrainingComponent2");
    private final static QName _OrganisationScopeSearchRequest_QNAME = new QName("http://training.gov.au/services/", "OrganisationScopeSearchRequest");
    private final static QName _CurrencyPeriodList_QNAME = new QName("http://training.gov.au/services/", "CurrencyPeriodList");
    private final static QName _UsageRecommendation_QNAME = new QName("http://training.gov.au/services/", "UsageRecommendation");
    private final static QName _TrainingComponent_QNAME = new QName("http://training.gov.au/services/", "TrainingComponent");
    private final static QName _DeleteOperation_QNAME = new QName("http://training.gov.au/services/", "DeleteOperation");
    private final static QName _TrainingComponent3_QNAME = new QName("http://training.gov.au/services/", "TrainingComponent3");
    private final static QName _ReleaseCompanionVolumeLink_QNAME = new QName("http://training.gov.au/services/", "ReleaseCompanionVolumeLink");
    private final static QName _Address_QNAME = new QName("http://training.gov.au/services/", "Address");
    private final static QName _RecognitionManager_QNAME = new QName("http://training.gov.au/services/", "RecognitionManager");
    private final static QName _NrtCurrencyPeriod_QNAME = new QName("http://training.gov.au/services/", "NrtCurrencyPeriod");
    private final static QName _TrainingComponentContactRole_QNAME = new QName("http://training.gov.au/services/", "TrainingComponentContactRole");
    private final static QName _ArrayOfClassificationValue_QNAME = new QName("http://training.gov.au/services/", "ArrayOfClassificationValue");
    private final static QName _ActionOnCollection_QNAME = new QName("http://training.gov.au/services/", "ActionOnCollection");
    private final static QName _ArrayOfUsageRecommendation_QNAME = new QName("http://training.gov.au/services/", "ArrayOfUsageRecommendation");
    private final static QName _ClassificationValue_QNAME = new QName("http://training.gov.au/services/", "ClassificationValue");
    private final static QName _TrainingComponentClassificationList_QNAME = new QName("http://training.gov.au/services/", "TrainingComponentClassificationList");
    private final static QName _TrainingComponentTypes_QNAME = new QName("http://training.gov.au/services/", "TrainingComponentTypes");
    private final static QName _GenericListPropertyOfClassificationNrtClassification8TYN1AE7_QNAME = new QName("http://training.gov.au/services/", "GenericListPropertyOfClassificationNrtClassification8TYN1aE7");
    private final static QName _ArrayOfTrainingComponentSummary_QNAME = new QName("http://training.gov.au/services/", "ArrayOfTrainingComponentSummary");
    private final static QName _Release3_QNAME = new QName("http://training.gov.au/services/", "Release3");
    private final static QName _Release4_QNAME = new QName("http://training.gov.au/services/", "Release4");
    private final static QName _Classification_QNAME = new QName("http://training.gov.au/services/", "Classification");
    private final static QName _TrainingComponentInformationRequested_QNAME = new QName("http://training.gov.au/services/", "TrainingComponentInformationRequested");
    private final static QName _ArrayOfReleaseComponent_QNAME = new QName("http://training.gov.au/services/", "ArrayOfReleaseComponent");
    private final static QName _TrainingComponentSummary2_QNAME = new QName("http://training.gov.au/services/", "TrainingComponentSummary2");
    private final static QName _DeletedSearchRequest_QNAME = new QName("http://training.gov.au/services/", "DeletedSearchRequest");
    private final static QName _TrainingComponentTypeFilter_QNAME = new QName("http://training.gov.au/services/", "TrainingComponentTypeFilter");
    private final static QName _ArrayOfNrtClassificationSchemeResult_QNAME = new QName("http://training.gov.au/services/", "ArrayOfNrtClassificationSchemeResult");
    private final static QName _ArrayOfUnitGridEntry_QNAME = new QName("http://training.gov.au/services/", "ArrayOfUnitGridEntry");
    private final static QName _ArrayOfValidationError_QNAME = new QName("http://training.gov.au/services/", "ArrayOfValidationError");
    private final static QName _ArrayOfDeletedTrainingComponent_QNAME = new QName("http://training.gov.au/services/", "ArrayOfDeletedTrainingComponent");
    private final static QName _GenericListPropertyOfNrtCurrencyPeriodNrtCurrencyPeriod8TYN1AE7_QNAME = new QName("http://training.gov.au/services/", "GenericListPropertyOfNrtCurrencyPeriodNrtCurrencyPeriod8TYN1aE7");
    private final static QName _TrainingComponentContactList_QNAME = new QName("http://training.gov.au/services/", "TrainingComponentContactList");
    private final static QName _TrainingComponentUpdateRequest_QNAME = new QName("http://training.gov.au/services/", "TrainingComponentUpdateRequest");
    private final static QName _DeletedTrainingComponent_QNAME = new QName("http://training.gov.au/services/", "DeletedTrainingComponent");
    private final static QName _ValidationFault_QNAME = new QName("http://training.gov.au/services/", "ValidationFault");
    private final static QName _TrainingComponentSummary3_QNAME = new QName("http://training.gov.au/services/", "TrainingComponentSummary3");
    private final static QName _ArrayOfAddressStates_QNAME = new QName("http://training.gov.au/services/", "ArrayOfAddressStates");
    private final static QName _TrainingComponentModifiedSearchRequest_QNAME = new QName("http://training.gov.au/services/", "TrainingComponentModifiedSearchRequest");
    private final static QName _TrainingComponentUpdateRequestTitle_QNAME = new QName("http://training.gov.au/services/", "Title");
    private final static QName _SearchResultResults_QNAME = new QName("http://training.gov.au/services/", "Results");
    private final static QName _GetValidationCodesResponseGetValidationCodesResult_QNAME = new QName("http://training.gov.au/services/", "GetValidationCodesResult");
    private final static QName _UnitGridEntryCode_QNAME = new QName("http://training.gov.au/services/", "Code");
    private final static QName _DeletedSearchRequestEndDate_QNAME = new QName("http://training.gov.au/services/", "EndDate");
    private final static QName _DeletedSearchRequestStartDate_QNAME = new QName("http://training.gov.au/services/", "StartDate");
    private final static QName _TrainingComponentModifiedSearchRequestDataManagerFilter_QNAME = new QName("http://training.gov.au/services/", "DataManagerFilter");
    private final static QName _TrainingComponentDetailsRequestInformationRequest_QNAME = new QName("http://training.gov.au/services/", "InformationRequest");
    private final static QName _SearchDeletedByDeletedDateResponseSearchDeletedByDeletedDateResult_QNAME = new QName("http://training.gov.au/services/", "SearchDeletedByDeletedDateResult");
    private final static QName _UnitGridEntry3IsEssential_QNAME = new QName("http://training.gov.au/services/", "IsEssential");
    private final static QName _ValidationErrorValidationTarget_QNAME = new QName("http://training.gov.au/services/", "ValidationTarget");
    private final static QName _ValidationErrorSubCode_QNAME = new QName("http://training.gov.au/services/", "SubCode");
    private final static QName _ValidationErrorMessage_QNAME = new QName("http://training.gov.au/services/", "Message");
    private final static QName _ValidationErrorContext_QNAME = new QName("http://training.gov.au/services/", "Context");
    private final static QName _TrainingComponentUsageRecommendations_QNAME = new QName("http://training.gov.au/services/", "UsageRecommendations");
    private final static QName _TrainingComponentCompletionMapping_QNAME = new QName("http://training.gov.au/services/", "CompletionMapping");
    private final static QName _TrainingComponentRecognitionManagers_QNAME = new QName("http://training.gov.au/services/", "RecognitionManagers");
    private final static QName _TrainingComponentMappingInformation_QNAME = new QName("http://training.gov.au/services/", "MappingInformation");
    private final static QName _TrainingComponentClassifications_QNAME = new QName("http://training.gov.au/services/", "Classifications");
    private final static QName _TrainingComponentContacts_QNAME = new QName("http://training.gov.au/services/", "Contacts");
    private final static QName _TrainingComponentDataManagers_QNAME = new QName("http://training.gov.au/services/", "DataManagers");
    private final static QName _TrainingComponentIscOrganisationCode_QNAME = new QName("http://training.gov.au/services/", "IscOrganisationCode");
    private final static QName _TrainingComponentCurrencyPeriods_QNAME = new QName("http://training.gov.au/services/", "CurrencyPeriods");
    private final static QName _TrainingComponentReleases_QNAME = new QName("http://training.gov.au/services/", "Releases");
    private final static QName _TrainingComponentParentCode_QNAME = new QName("http://training.gov.au/services/", "ParentCode");
    private final static QName _TrainingComponentParentTitle_QNAME = new QName("http://training.gov.au/services/", "ParentTitle");
    private final static QName _GetDetailsRequest_QNAME = new QName("http://training.gov.au/services/", "request");
    private final static QName _DataManagerRegistrationManagerCode_QNAME = new QName("http://training.gov.au/services/", "RegistrationManagerCode");
    private final static QName _DataManagerRecognitionManagerCode_QNAME = new QName("http://training.gov.au/services/", "RecognitionManagerCode");
    private final static QName _DataManagerDescription_QNAME = new QName("http://training.gov.au/services/", "Description");
    private final static QName _GetClassificationPurposesResponseGetClassificationPurposesResult_QNAME = new QName("http://training.gov.au/services/", "GetClassificationPurposesResult");
    private final static QName _ReleaseComponentReleaseDate_QNAME = new QName("http://training.gov.au/services/", "ReleaseDate");
    private final static QName _ReleaseComponentReleaseCurrency_QNAME = new QName("http://training.gov.au/services/", "ReleaseCurrency");
    private final static QName _ReleaseComponentReleaseNumber_QNAME = new QName("http://training.gov.au/services/", "ReleaseNumber");
    private final static QName _GetAddressStatesResponseGetAddressStatesResult_QNAME = new QName("http://training.gov.au/services/", "GetAddressStatesResult");
    private final static QName _GetDataManagersResponseGetDataManagersResult_QNAME = new QName("http://training.gov.au/services/", "GetDataManagersResult");
    private final static QName _ReleaseFileRelativePath_QNAME = new QName("http://training.gov.au/services/", "RelativePath");
    private final static QName _TrainingComponentSummary3CurrencyStatus_QNAME = new QName("http://training.gov.au/services/", "CurrencyStatus");
    private final static QName _OrganisationNameSearchRequestRegistrationManagers_QNAME = new QName("http://training.gov.au/services/", "RegistrationManagers");
    private final static QName _GetRecognitionManagersResponseGetRecognitionManagersResult_QNAME = new QName("http://training.gov.au/services/", "GetRecognitionManagersResult");
    private final static QName _GetLookupResponseGetLookupResult_QNAME = new QName("http://training.gov.au/services/", "GetLookupResult");
    private final static QName _ReleaseCompanionVolumeLinkPublishedComponentType_QNAME = new QName("http://training.gov.au/services/", "PublishedComponentType");
    private final static QName _ReleaseCompanionVolumeLinkLinkUrl_QNAME = new QName("http://training.gov.au/services/", "LinkUrl");
    private final static QName _ReleaseCompanionVolumeLinkLinkText_QNAME = new QName("http://training.gov.au/services/", "LinkText");
    private final static QName _ReleaseCompanionVolumeLinkLinkNotes_QNAME = new QName("http://training.gov.au/services/", "LinkNotes");
    private final static QName _AddressStatesAbbreviation_QNAME = new QName("http://training.gov.au/services/", "Abbreviation");
    private final static QName _TrainingComponentContactRoleRole_QNAME = new QName("http://training.gov.au/services/", "Role");
    private final static QName _MappingMapsToCode_QNAME = new QName("http://training.gov.au/services/", "MapsToCode");
    private final static QName _MappingNotes_QNAME = new QName("http://training.gov.au/services/", "Notes");
    private final static QName _MappingMapsToTitle_QNAME = new QName("http://training.gov.au/services/", "MapsToTitle");
    private final static QName _DeletedTrainingComponentNationalCode_QNAME = new QName("http://training.gov.au/services/", "NationalCode");
    private final static QName _TrainingComponentTransferDataManagerRequestEffectiveDate_QNAME = new QName("http://training.gov.au/services/", "EffectiveDate");
    private final static QName _NrtCurrencyPeriodAuthority_QNAME = new QName("http://training.gov.au/services/", "Authority");
    private final static QName _ContactPostalAddress_QNAME = new QName("http://training.gov.au/services/", "PostalAddress");
    private final static QName _ContactLastName_QNAME = new QName("http://training.gov.au/services/", "LastName");
    private final static QName _ContactRoleCode_QNAME = new QName("http://training.gov.au/services/", "RoleCode");
    private final static QName _ContactPhone_QNAME = new QName("http://training.gov.au/services/", "Phone");
    private final static QName _ContactFax_QNAME = new QName("http://training.gov.au/services/", "Fax");
    private final static QName _ContactOrganisationName_QNAME = new QName("http://training.gov.au/services/", "OrganisationName");
    private final static QName _ContactGroupName_QNAME = new QName("http://training.gov.au/services/", "GroupName");
    private final static QName _ContactFirstName_QNAME = new QName("http://training.gov.au/services/", "FirstName");
    private final static QName _ContactMobile_QNAME = new QName("http://training.gov.au/services/", "Mobile");
    private final static QName _ContactJobTitle_QNAME = new QName("http://training.gov.au/services/", "JobTitle");
    private final static QName _ContactTypeCode_QNAME = new QName("http://training.gov.au/services/", "TypeCode");
    private final static QName _ContactEmail_QNAME = new QName("http://training.gov.au/services/", "Email");
    private final static QName _GetClassificationSchemesResponseGetClassificationSchemesResult_QNAME = new QName("http://training.gov.au/services/", "GetClassificationSchemesResult");
    private final static QName _GetDetailsResponseGetDetailsResult_QNAME = new QName("http://training.gov.au/services/", "GetDetailsResult");
    private final static QName _NrtCurrencyPeriod2EndReasonCode_QNAME = new QName("http://training.gov.au/services/", "EndReasonCode");
    private final static QName _NrtCurrencyPeriod2EndComment_QNAME = new QName("http://training.gov.au/services/", "EndComment");
    private final static QName _AddressCountryCode_QNAME = new QName("http://training.gov.au/services/", "CountryCode");
    private final static QName _AddressSuburb_QNAME = new QName("http://training.gov.au/services/", "Suburb");
    private final static QName _AddressStateOverseas_QNAME = new QName("http://training.gov.au/services/", "StateOverseas");
    private final static QName _AddressStateCode_QNAME = new QName("http://training.gov.au/services/", "StateCode");
    private final static QName _AddressLine1_QNAME = new QName("http://training.gov.au/services/", "Line1");
    private final static QName _AddressLine2_QNAME = new QName("http://training.gov.au/services/", "Line2");
    private final static QName _AddressPostcode_QNAME = new QName("http://training.gov.au/services/", "Postcode");
    private final static QName _ReleaseComponents_QNAME = new QName("http://training.gov.au/services/", "Components");
    private final static QName _ReleaseNqcEndorsementDate_QNAME = new QName("http://training.gov.au/services/", "NqcEndorsementDate");
    private final static QName _ReleaseMinisterialAgreementDate_QNAME = new QName("http://training.gov.au/services/", "MinisterialAgreementDate");
    private final static QName _ReleaseIscApprovalDate_QNAME = new QName("http://training.gov.au/services/", "IscApprovalDate");
    private final static QName _ReleaseCurrency_QNAME = new QName("http://training.gov.au/services/", "Currency");
    private final static QName _ReleaseApprovalProcess_QNAME = new QName("http://training.gov.au/services/", "ApprovalProcess");
    private final static QName _ReleaseUnitGrid_QNAME = new QName("http://training.gov.au/services/", "UnitGrid");
    private final static QName _ReleaseFiles_QNAME = new QName("http://training.gov.au/services/", "Files");
    private final static QName _SearchByModifiedDateResponseSearchByModifiedDateResult_QNAME = new QName("http://training.gov.au/services/", "SearchByModifiedDateResult");
    private final static QName _TrainingComponentSummaryIsCurrent_QNAME = new QName("http://training.gov.au/services/", "IsCurrent");
    private final static QName _Release4CompanionVolumeLinks_QNAME = new QName("http://training.gov.au/services/", "CompanionVolumeLinks");
    private final static QName _ValidationFaultErrors_QNAME = new QName("http://training.gov.au/services/", "Errors");
    private final static QName _GetContactRolesResponseGetContactRolesResult_QNAME = new QName("http://training.gov.au/services/", "GetContactRolesResult");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: au.gov.training.services.trainingcomponent
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ClassificationFilters }
     * 
     */
    public ClassificationFilters createClassificationFilters() {
        return new ClassificationFilters();
    }

    /**
     * Create an instance of {@link GetContactRoles }
     * 
     */
    public GetContactRoles createGetContactRoles() {
        return new GetContactRoles();
    }

    /**
     * Create an instance of {@link Search }
     * 
     */
    public Search createSearch() {
        return new Search();
    }

    /**
     * Create an instance of {@link TrainingComponentSearchRequest }
     * 
     */
    public TrainingComponentSearchRequest createTrainingComponentSearchRequest() {
        return new TrainingComponentSearchRequest();
    }

    /**
     * Create an instance of {@link ArrayOfUsageRecommendation }
     * 
     */
    public ArrayOfUsageRecommendation createArrayOfUsageRecommendation() {
        return new ArrayOfUsageRecommendation();
    }

    /**
     * Create an instance of {@link GetClassificationPurposesResponse }
     * 
     */
    public GetClassificationPurposesResponse createGetClassificationPurposesResponse() {
        return new GetClassificationPurposesResponse();
    }

    /**
     * Create an instance of {@link ArrayOfClassificationPurpose }
     * 
     */
    public ArrayOfClassificationPurpose createArrayOfClassificationPurpose() {
        return new ArrayOfClassificationPurpose();
    }

    /**
     * Create an instance of {@link SearchDeletedByDeletedDate }
     * 
     */
    public SearchDeletedByDeletedDate createSearchDeletedByDeletedDate() {
        return new SearchDeletedByDeletedDate();
    }

    /**
     * Create an instance of {@link DeletedSearchRequest }
     * 
     */
    public DeletedSearchRequest createDeletedSearchRequest() {
        return new DeletedSearchRequest();
    }

    /**
     * Create an instance of {@link ArrayOfClassificationValue }
     * 
     */
    public ArrayOfClassificationValue createArrayOfClassificationValue() {
        return new ArrayOfClassificationValue();
    }

    /**
     * Create an instance of {@link TrainingComponentContactRole }
     * 
     */
    public TrainingComponentContactRole createTrainingComponentContactRole() {
        return new TrainingComponentContactRole();
    }

    /**
     * Create an instance of {@link NrtCurrencyPeriod }
     * 
     */
    public NrtCurrencyPeriod createNrtCurrencyPeriod() {
        return new NrtCurrencyPeriod();
    }

    /**
     * Create an instance of {@link RecognitionManager }
     * 
     */
    public RecognitionManager createRecognitionManager() {
        return new RecognitionManager();
    }

    /**
     * Create an instance of {@link ArrayOfTrainingComponentSummary }
     * 
     */
    public ArrayOfTrainingComponentSummary createArrayOfTrainingComponentSummary() {
        return new ArrayOfTrainingComponentSummary();
    }

    /**
     * Create an instance of {@link GenericListPropertyOfClassificationNrtClassification8TYN1AE7 }
     * 
     */
    public GenericListPropertyOfClassificationNrtClassification8TYN1AE7 createGenericListPropertyOfClassificationNrtClassification8TYN1AE7() {
        return new GenericListPropertyOfClassificationNrtClassification8TYN1AE7();
    }

    /**
     * Create an instance of {@link GetAddressStates }
     * 
     */
    public GetAddressStates createGetAddressStates() {
        return new GetAddressStates();
    }

    /**
     * Create an instance of {@link Classification }
     * 
     */
    public Classification createClassification() {
        return new Classification();
    }

    /**
     * Create an instance of {@link Release4 }
     * 
     */
    public Release4 createRelease4() {
        return new Release4();
    }

    /**
     * Create an instance of {@link Release3 }
     * 
     */
    public Release3 createRelease3() {
        return new Release3();
    }

    /**
     * Create an instance of {@link TrainingComponentClassificationList }
     * 
     */
    public TrainingComponentClassificationList createTrainingComponentClassificationList() {
        return new TrainingComponentClassificationList();
    }

    /**
     * Create an instance of {@link ClassificationValue }
     * 
     */
    public ClassificationValue createClassificationValue() {
        return new ClassificationValue();
    }

    /**
     * Create an instance of {@link ArrayOfNrtClassificationSchemeResult }
     * 
     */
    public ArrayOfNrtClassificationSchemeResult createArrayOfNrtClassificationSchemeResult() {
        return new ArrayOfNrtClassificationSchemeResult();
    }

    /**
     * Create an instance of {@link TrainingComponentTypeFilter }
     * 
     */
    public TrainingComponentTypeFilter createTrainingComponentTypeFilter() {
        return new TrainingComponentTypeFilter();
    }

    /**
     * Create an instance of {@link GetContactRolesResponse }
     * 
     */
    public GetContactRolesResponse createGetContactRolesResponse() {
        return new GetContactRolesResponse();
    }

    /**
     * Create an instance of {@link ArrayOfTrainingComponentContactRole }
     * 
     */
    public ArrayOfTrainingComponentContactRole createArrayOfTrainingComponentContactRole() {
        return new ArrayOfTrainingComponentContactRole();
    }

    /**
     * Create an instance of {@link TrainingComponentSummary2 }
     * 
     */
    public TrainingComponentSummary2 createTrainingComponentSummary2() {
        return new TrainingComponentSummary2();
    }

    /**
     * Create an instance of {@link ArrayOfReleaseComponent }
     * 
     */
    public ArrayOfReleaseComponent createArrayOfReleaseComponent() {
        return new ArrayOfReleaseComponent();
    }

    /**
     * Create an instance of {@link TrainingComponentInformationRequested }
     * 
     */
    public TrainingComponentInformationRequested createTrainingComponentInformationRequested() {
        return new TrainingComponentInformationRequested();
    }

    /**
     * Create an instance of {@link ValidationFault }
     * 
     */
    public ValidationFault createValidationFault() {
        return new ValidationFault();
    }

    /**
     * Create an instance of {@link DeletedTrainingComponent }
     * 
     */
    public DeletedTrainingComponent createDeletedTrainingComponent() {
        return new DeletedTrainingComponent();
    }

    /**
     * Create an instance of {@link TrainingComponentUpdateRequest }
     * 
     */
    public TrainingComponentUpdateRequest createTrainingComponentUpdateRequest() {
        return new TrainingComponentUpdateRequest();
    }

    /**
     * Create an instance of {@link ArrayOfDeletedTrainingComponent }
     * 
     */
    public ArrayOfDeletedTrainingComponent createArrayOfDeletedTrainingComponent() {
        return new ArrayOfDeletedTrainingComponent();
    }

    /**
     * Create an instance of {@link GenericListPropertyOfNrtCurrencyPeriodNrtCurrencyPeriod8TYN1AE7 }
     * 
     */
    public GenericListPropertyOfNrtCurrencyPeriodNrtCurrencyPeriod8TYN1AE7 createGenericListPropertyOfNrtCurrencyPeriodNrtCurrencyPeriod8TYN1AE7() {
        return new GenericListPropertyOfNrtCurrencyPeriodNrtCurrencyPeriod8TYN1AE7();
    }

    /**
     * Create an instance of {@link TrainingComponentContactList }
     * 
     */
    public TrainingComponentContactList createTrainingComponentContactList() {
        return new TrainingComponentContactList();
    }

    /**
     * Create an instance of {@link ArrayOfAddressStates }
     * 
     */
    public ArrayOfAddressStates createArrayOfAddressStates() {
        return new ArrayOfAddressStates();
    }

    /**
     * Create an instance of {@link TrainingComponentModifiedSearchRequest }
     * 
     */
    public TrainingComponentModifiedSearchRequest createTrainingComponentModifiedSearchRequest() {
        return new TrainingComponentModifiedSearchRequest();
    }

    /**
     * Create an instance of {@link TrainingComponentSummary3 }
     * 
     */
    public TrainingComponentSummary3 createTrainingComponentSummary3() {
        return new TrainingComponentSummary3();
    }

    /**
     * Create an instance of {@link ArrayOfValidationError }
     * 
     */
    public ArrayOfValidationError createArrayOfValidationError() {
        return new ArrayOfValidationError();
    }

    /**
     * Create an instance of {@link ArrayOfUnitGridEntry }
     * 
     */
    public ArrayOfUnitGridEntry createArrayOfUnitGridEntry() {
        return new ArrayOfUnitGridEntry();
    }

    /**
     * Create an instance of {@link AbstractDto }
     * 
     */
    public AbstractDto createAbstractDto() {
        return new AbstractDto();
    }

    /**
     * Create an instance of {@link TransferDataManager }
     * 
     */
    public TransferDataManager createTransferDataManager() {
        return new TransferDataManager();
    }

    /**
     * Create an instance of {@link TrainingComponentTransferDataManagerRequest }
     * 
     */
    public TrainingComponentTransferDataManagerRequest createTrainingComponentTransferDataManagerRequest() {
        return new TrainingComponentTransferDataManagerRequest();
    }

    /**
     * Create an instance of {@link GetDetails }
     * 
     */
    public GetDetails createGetDetails() {
        return new GetDetails();
    }

    /**
     * Create an instance of {@link TrainingComponentDetailsRequest }
     * 
     */
    public TrainingComponentDetailsRequest createTrainingComponentDetailsRequest() {
        return new TrainingComponentDetailsRequest();
    }

    /**
     * Create an instance of {@link AbstractPageRequest }
     * 
     */
    public AbstractPageRequest createAbstractPageRequest() {
        return new AbstractPageRequest();
    }

    /**
     * Create an instance of {@link GetAddressStatesResponse }
     * 
     */
    public GetAddressStatesResponse createGetAddressStatesResponse() {
        return new GetAddressStatesResponse();
    }

    /**
     * Create an instance of {@link ReleaseComponent }
     * 
     */
    public ReleaseComponent createReleaseComponent() {
        return new ReleaseComponent();
    }

    /**
     * Create an instance of {@link ArrayOfNrtCurrencyPeriod }
     * 
     */
    public ArrayOfNrtCurrencyPeriod createArrayOfNrtCurrencyPeriod() {
        return new ArrayOfNrtCurrencyPeriod();
    }

    /**
     * Create an instance of {@link TrainingComponentSearchResult }
     * 
     */
    public TrainingComponentSearchResult createTrainingComponentSearchResult() {
        return new TrainingComponentSearchResult();
    }

    /**
     * Create an instance of {@link ArrayOfReleaseCompanionVolumeLink }
     * 
     */
    public ArrayOfReleaseCompanionVolumeLink createArrayOfReleaseCompanionVolumeLink() {
        return new ArrayOfReleaseCompanionVolumeLink();
    }

    /**
     * Create an instance of {@link TrainingComponentInformationRequested2 }
     * 
     */
    public TrainingComponentInformationRequested2 createTrainingComponentInformationRequested2() {
        return new TrainingComponentInformationRequested2();
    }

    /**
     * Create an instance of {@link AddResponse }
     * 
     */
    public AddResponse createAddResponse() {
        return new AddResponse();
    }

    /**
     * Create an instance of {@link UsageRecommendation }
     * 
     */
    public UsageRecommendation createUsageRecommendation() {
        return new UsageRecommendation();
    }

    /**
     * Create an instance of {@link TrainingComponent }
     * 
     */
    public TrainingComponent createTrainingComponent() {
        return new TrainingComponent();
    }

    /**
     * Create an instance of {@link TrainingComponent2 }
     * 
     */
    public TrainingComponent2 createTrainingComponent2() {
        return new TrainingComponent2();
    }

    /**
     * Create an instance of {@link OrganisationScopeSearchRequest }
     * 
     */
    public OrganisationScopeSearchRequest createOrganisationScopeSearchRequest() {
        return new OrganisationScopeSearchRequest();
    }

    /**
     * Create an instance of {@link CurrencyPeriodList }
     * 
     */
    public CurrencyPeriodList createCurrencyPeriodList() {
        return new CurrencyPeriodList();
    }

    /**
     * Create an instance of {@link ArrayOfLookup }
     * 
     */
    public ArrayOfLookup createArrayOfLookup() {
        return new ArrayOfLookup();
    }

    /**
     * Create an instance of {@link Address }
     * 
     */
    public Address createAddress() {
        return new Address();
    }

    /**
     * Create an instance of {@link GetClassificationPurposes }
     * 
     */
    public GetClassificationPurposes createGetClassificationPurposes() {
        return new GetClassificationPurposes();
    }

    /**
     * Create an instance of {@link GetLookup }
     * 
     */
    public GetLookup createGetLookup() {
        return new GetLookup();
    }

    /**
     * Create an instance of {@link LookupRequest }
     * 
     */
    public LookupRequest createLookupRequest() {
        return new LookupRequest();
    }

    /**
     * Create an instance of {@link ReleaseCompanionVolumeLink }
     * 
     */
    public ReleaseCompanionVolumeLink createReleaseCompanionVolumeLink() {
        return new ReleaseCompanionVolumeLink();
    }

    /**
     * Create an instance of {@link TrainingComponent3 }
     * 
     */
    public TrainingComponent3 createTrainingComponent3() {
        return new TrainingComponent3();
    }

    /**
     * Create an instance of {@link GetLookupResponse }
     * 
     */
    public GetLookupResponse createGetLookupResponse() {
        return new GetLookupResponse();
    }

    /**
     * Create an instance of {@link OrganisationNameSearchRequest }
     * 
     */
    public OrganisationNameSearchRequest createOrganisationNameSearchRequest() {
        return new OrganisationNameSearchRequest();
    }

    /**
     * Create an instance of {@link GetRecognitionManagersResponse }
     * 
     */
    public GetRecognitionManagersResponse createGetRecognitionManagersResponse() {
        return new GetRecognitionManagersResponse();
    }

    /**
     * Create an instance of {@link ArrayOfRecognitionManager }
     * 
     */
    public ArrayOfRecognitionManager createArrayOfRecognitionManager() {
        return new ArrayOfRecognitionManager();
    }

    /**
     * Create an instance of {@link SearchByModifiedDateResponse }
     * 
     */
    public SearchByModifiedDateResponse createSearchByModifiedDateResponse() {
        return new SearchByModifiedDateResponse();
    }

    /**
     * Create an instance of {@link TrainingComponentDeleteRequest }
     * 
     */
    public TrainingComponentDeleteRequest createTrainingComponentDeleteRequest() {
        return new TrainingComponentDeleteRequest();
    }

    /**
     * Create an instance of {@link NrtCompletion }
     * 
     */
    public NrtCompletion createNrtCompletion() {
        return new NrtCompletion();
    }

    /**
     * Create an instance of {@link ArrayOfContact }
     * 
     */
    public ArrayOfContact createArrayOfContact() {
        return new ArrayOfContact();
    }

    /**
     * Create an instance of {@link ReleaseFile }
     * 
     */
    public ReleaseFile createReleaseFile() {
        return new ReleaseFile();
    }

    /**
     * Create an instance of {@link SearchResult }
     * 
     */
    public SearchResult createSearchResult() {
        return new SearchResult();
    }

    /**
     * Create an instance of {@link Add }
     * 
     */
    public Add createAdd() {
        return new Add();
    }

    /**
     * Create an instance of {@link Delete }
     * 
     */
    public Delete createDelete() {
        return new Delete();
    }

    /**
     * Create an instance of {@link UpdateResponse }
     * 
     */
    public UpdateResponse createUpdateResponse() {
        return new UpdateResponse();
    }

    /**
     * Create an instance of {@link GetServerTimeResponse }
     * 
     */
    public GetServerTimeResponse createGetServerTimeResponse() {
        return new GetServerTimeResponse();
    }

    /**
     * Create an instance of {@link ArrayOfReleaseFile }
     * 
     */
    public ArrayOfReleaseFile createArrayOfReleaseFile() {
        return new ArrayOfReleaseFile();
    }

    /**
     * Create an instance of {@link GetClassificationSchemesResponse }
     * 
     */
    public GetClassificationSchemesResponse createGetClassificationSchemesResponse() {
        return new GetClassificationSchemesResponse();
    }

    /**
     * Create an instance of {@link GetClassificationSchemes }
     * 
     */
    public GetClassificationSchemes createGetClassificationSchemes() {
        return new GetClassificationSchemes();
    }

    /**
     * Create an instance of {@link GetValidationCodesResponse }
     * 
     */
    public GetValidationCodesResponse createGetValidationCodesResponse() {
        return new GetValidationCodesResponse();
    }

    /**
     * Create an instance of {@link ArrayOfValidationCode }
     * 
     */
    public ArrayOfValidationCode createArrayOfValidationCode() {
        return new ArrayOfValidationCode();
    }

    /**
     * Create an instance of {@link UnitGridEntry3 }
     * 
     */
    public UnitGridEntry3 createUnitGridEntry3() {
        return new UnitGridEntry3();
    }

    /**
     * Create an instance of {@link NrtClassificationSchemeResult }
     * 
     */
    public NrtClassificationSchemeResult createNrtClassificationSchemeResult() {
        return new NrtClassificationSchemeResult();
    }

    /**
     * Create an instance of {@link ArrayOfRelease }
     * 
     */
    public ArrayOfRelease createArrayOfRelease() {
        return new ArrayOfRelease();
    }

    /**
     * Create an instance of {@link Release }
     * 
     */
    public Release createRelease() {
        return new Release();
    }

    /**
     * Create an instance of {@link RecognitionManagerAssignment }
     * 
     */
    public RecognitionManagerAssignment createRecognitionManagerAssignment() {
        return new RecognitionManagerAssignment();
    }

    /**
     * Create an instance of {@link UnitGridEntry }
     * 
     */
    public UnitGridEntry createUnitGridEntry() {
        return new UnitGridEntry();
    }

    /**
     * Create an instance of {@link GetRecognitionManagers }
     * 
     */
    public GetRecognitionManagers createGetRecognitionManagers() {
        return new GetRecognitionManagers();
    }

    /**
     * Create an instance of {@link SearchByModifiedDate }
     * 
     */
    public SearchByModifiedDate createSearchByModifiedDate() {
        return new SearchByModifiedDate();
    }

    /**
     * Create an instance of {@link ArrayOfNrtCompletion }
     * 
     */
    public ArrayOfNrtCompletion createArrayOfNrtCompletion() {
        return new ArrayOfNrtCompletion();
    }

    /**
     * Create an instance of {@link GenericListPropertyOfContactTrainingComponentContact8TYN1AE7 }
     * 
     */
    public GenericListPropertyOfContactTrainingComponentContact8TYN1AE7 createGenericListPropertyOfContactTrainingComponentContact8TYN1AE7() {
        return new GenericListPropertyOfContactTrainingComponentContact8TYN1AE7();
    }

    /**
     * Create an instance of {@link GetDataManagers }
     * 
     */
    public GetDataManagers createGetDataManagers() {
        return new GetDataManagers();
    }

    /**
     * Create an instance of {@link DeleteResponse }
     * 
     */
    public DeleteResponse createDeleteResponse() {
        return new DeleteResponse();
    }

    /**
     * Create an instance of {@link ClassificationPurpose }
     * 
     */
    public ClassificationPurpose createClassificationPurpose() {
        return new ClassificationPurpose();
    }

    /**
     * Create an instance of {@link TrainingComponentSummary }
     * 
     */
    public TrainingComponentSummary createTrainingComponentSummary() {
        return new TrainingComponentSummary();
    }

    /**
     * Create an instance of {@link GetDetailsResponse }
     * 
     */
    public GetDetailsResponse createGetDetailsResponse() {
        return new GetDetailsResponse();
    }

    /**
     * Create an instance of {@link TransferDataManagerResponse }
     * 
     */
    public TransferDataManagerResponse createTransferDataManagerResponse() {
        return new TransferDataManagerResponse();
    }

    /**
     * Create an instance of {@link GetDataManagersResponse }
     * 
     */
    public GetDataManagersResponse createGetDataManagersResponse() {
        return new GetDataManagersResponse();
    }

    /**
     * Create an instance of {@link ArrayOfDataManager }
     * 
     */
    public ArrayOfDataManager createArrayOfDataManager() {
        return new ArrayOfDataManager();
    }

    /**
     * Create an instance of {@link Update }
     * 
     */
    public Update createUpdate() {
        return new Update();
    }

    /**
     * Create an instance of {@link DataManager }
     * 
     */
    public DataManager createDataManager() {
        return new DataManager();
    }

    /**
     * Create an instance of {@link SearchResponse }
     * 
     */
    public SearchResponse createSearchResponse() {
        return new SearchResponse();
    }

    /**
     * Create an instance of {@link ArrayOfDataManagerAssignment }
     * 
     */
    public ArrayOfDataManagerAssignment createArrayOfDataManagerAssignment() {
        return new ArrayOfDataManagerAssignment();
    }

    /**
     * Create an instance of {@link SearchDeletedByDeletedDateResponse }
     * 
     */
    public SearchDeletedByDeletedDateResponse createSearchDeletedByDeletedDateResponse() {
        return new SearchDeletedByDeletedDateResponse();
    }

    /**
     * Create an instance of {@link Mapping }
     * 
     */
    public Mapping createMapping() {
        return new Mapping();
    }

    /**
     * Create an instance of {@link AddressStates }
     * 
     */
    public AddressStates createAddressStates() {
        return new AddressStates();
    }

    /**
     * Create an instance of {@link ArrayOfClassification }
     * 
     */
    public ArrayOfClassification createArrayOfClassification() {
        return new ArrayOfClassification();
    }

    /**
     * Create an instance of {@link DataManagerAssignment }
     * 
     */
    public DataManagerAssignment createDataManagerAssignment() {
        return new DataManagerAssignment();
    }

    /**
     * Create an instance of {@link CompletionMappingList }
     * 
     */
    public CompletionMappingList createCompletionMappingList() {
        return new CompletionMappingList();
    }

    /**
     * Create an instance of {@link Contact }
     * 
     */
    public Contact createContact() {
        return new Contact();
    }

    /**
     * Create an instance of {@link ValidationCode }
     * 
     */
    public ValidationCode createValidationCode() {
        return new ValidationCode();
    }

    /**
     * Create an instance of {@link GenericListPropertyOfNrtCompletionNrtCompletion8TYN1AE7 }
     * 
     */
    public GenericListPropertyOfNrtCompletionNrtCompletion8TYN1AE7 createGenericListPropertyOfNrtCompletionNrtCompletion8TYN1AE7() {
        return new GenericListPropertyOfNrtCompletionNrtCompletion8TYN1AE7();
    }

    /**
     * Create an instance of {@link ArrayOfRecognitionManagerAssignment }
     * 
     */
    public ArrayOfRecognitionManagerAssignment createArrayOfRecognitionManagerAssignment() {
        return new ArrayOfRecognitionManagerAssignment();
    }

    /**
     * Create an instance of {@link NrtCurrencyPeriod2 }
     * 
     */
    public NrtCurrencyPeriod2 createNrtCurrencyPeriod2() {
        return new NrtCurrencyPeriod2();
    }

    /**
     * Create an instance of {@link ValidationError }
     * 
     */
    public ValidationError createValidationError() {
        return new ValidationError();
    }

    /**
     * Create an instance of {@link GetValidationCodes }
     * 
     */
    public GetValidationCodes createGetValidationCodes() {
        return new GetValidationCodes();
    }

    /**
     * Create an instance of {@link GetServerTime }
     * 
     */
    public GetServerTime createGetServerTime() {
        return new GetServerTime();
    }

    /**
     * Create an instance of {@link Lookup }
     * 
     */
    public Lookup createLookup() {
        return new Lookup();
    }

    /**
     * Create an instance of {@link ArrayOfMapping }
     * 
     */
    public ArrayOfMapping createArrayOfMapping() {
        return new ArrayOfMapping();
    }

    /**
     * Create an instance of {@link ClassificationFilters.ClassificationFilter }
     * 
     */
    public ClassificationFilters.ClassificationFilter createClassificationFiltersClassificationFilter() {
        return new ClassificationFilters.ClassificationFilter();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TrainingComponentTransferDataManagerRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "TrainingComponentTransferDataManagerRequest")
    public JAXBElement<TrainingComponentTransferDataManagerRequest> createTrainingComponentTransferDataManagerRequest(TrainingComponentTransferDataManagerRequest value) {
        return new JAXBElement<TrainingComponentTransferDataManagerRequest>(_TrainingComponentTransferDataManagerRequest_QNAME, TrainingComponentTransferDataManagerRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ActionOnEntity }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "ActionOnEntity")
    public JAXBElement<ActionOnEntity> createActionOnEntity(ActionOnEntity value) {
        return new JAXBElement<ActionOnEntity>(_ActionOnEntity_QNAME, ActionOnEntity.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DataManager }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "DataManager")
    public JAXBElement<DataManager> createDataManager(DataManager value) {
        return new JAXBElement<DataManager>(_DataManager_QNAME, DataManager.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfTrainingComponentContactRole }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "ArrayOfTrainingComponentContactRole")
    public JAXBElement<ArrayOfTrainingComponentContactRole> createArrayOfTrainingComponentContactRole(ArrayOfTrainingComponentContactRole value) {
        return new JAXBElement<ArrayOfTrainingComponentContactRole>(_ArrayOfTrainingComponentContactRole_QNAME, ArrayOfTrainingComponentContactRole.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Mapping }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "Mapping")
    public JAXBElement<Mapping> createMapping(Mapping value) {
        return new JAXBElement<Mapping>(_Mapping_QNAME, Mapping.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfRecognitionManager }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "ArrayOfRecognitionManager")
    public JAXBElement<ArrayOfRecognitionManager> createArrayOfRecognitionManager(ArrayOfRecognitionManager value) {
        return new JAXBElement<ArrayOfRecognitionManager>(_ArrayOfRecognitionManager_QNAME, ArrayOfRecognitionManager.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AddressStates }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "AddressStates")
    public JAXBElement<AddressStates> createAddressStates(AddressStates value) {
        return new JAXBElement<AddressStates>(_AddressStates_QNAME, AddressStates.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfDataManagerAssignment }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "ArrayOfDataManagerAssignment")
    public JAXBElement<ArrayOfDataManagerAssignment> createArrayOfDataManagerAssignment(ArrayOfDataManagerAssignment value) {
        return new JAXBElement<ArrayOfDataManagerAssignment>(_ArrayOfDataManagerAssignment_QNAME, ArrayOfDataManagerAssignment.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfRecognitionManagerAssignment }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "ArrayOfRecognitionManagerAssignment")
    public JAXBElement<ArrayOfRecognitionManagerAssignment> createArrayOfRecognitionManagerAssignment(ArrayOfRecognitionManagerAssignment value) {
        return new JAXBElement<ArrayOfRecognitionManagerAssignment>(_ArrayOfRecognitionManagerAssignment_QNAME, ArrayOfRecognitionManagerAssignment.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GenericListPropertyOfNrtCompletionNrtCompletion8TYN1AE7 }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "GenericListPropertyOfNrtCompletionNrtCompletion8TYN1aE7")
    public JAXBElement<GenericListPropertyOfNrtCompletionNrtCompletion8TYN1AE7> createGenericListPropertyOfNrtCompletionNrtCompletion8TYN1AE7(GenericListPropertyOfNrtCompletionNrtCompletion8TYN1AE7 value) {
        return new JAXBElement<GenericListPropertyOfNrtCompletionNrtCompletion8TYN1AE7>(_GenericListPropertyOfNrtCompletionNrtCompletion8TYN1AE7_QNAME, GenericListPropertyOfNrtCompletionNrtCompletion8TYN1AE7 .class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ValidationCode }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "ValidationCode")
    public JAXBElement<ValidationCode> createValidationCode(ValidationCode value) {
        return new JAXBElement<ValidationCode>(_ValidationCode_QNAME, ValidationCode.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ValidationError }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "ValidationError")
    public JAXBElement<ValidationError> createValidationError(ValidationError value) {
        return new JAXBElement<ValidationError>(_ValidationError_QNAME, ValidationError.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NrtCurrencyPeriod2 }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "NrtCurrencyPeriod2")
    public JAXBElement<NrtCurrencyPeriod2> createNrtCurrencyPeriod2(NrtCurrencyPeriod2 value) {
        return new JAXBElement<NrtCurrencyPeriod2>(_NrtCurrencyPeriod2_QNAME, NrtCurrencyPeriod2 .class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfClassification }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "ArrayOfClassification")
    public JAXBElement<ArrayOfClassification> createArrayOfClassification(ArrayOfClassification value) {
        return new JAXBElement<ArrayOfClassification>(_ArrayOfClassification_QNAME, ArrayOfClassification.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Contact }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "Contact")
    public JAXBElement<Contact> createContact(Contact value) {
        return new JAXBElement<Contact>(_Contact_QNAME, Contact.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CompletionMappingList }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "CompletionMappingList")
    public JAXBElement<CompletionMappingList> createCompletionMappingList(CompletionMappingList value) {
        return new JAXBElement<CompletionMappingList>(_CompletionMappingList_QNAME, CompletionMappingList.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DataManagerAssignment }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "DataManagerAssignment")
    public JAXBElement<DataManagerAssignment> createDataManagerAssignment(DataManagerAssignment value) {
        return new JAXBElement<DataManagerAssignment>(_DataManagerAssignment_QNAME, DataManagerAssignment.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfDataManager }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "ArrayOfDataManager")
    public JAXBElement<ArrayOfDataManager> createArrayOfDataManager(ArrayOfDataManager value) {
        return new JAXBElement<ArrayOfDataManager>(_ArrayOfDataManager_QNAME, ArrayOfDataManager.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Lookup }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "Lookup")
    public JAXBElement<Lookup> createLookup(Lookup value) {
        return new JAXBElement<Lookup>(_Lookup_QNAME, Lookup.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfMapping }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "ArrayOfMapping")
    public JAXBElement<ArrayOfMapping> createArrayOfMapping(ArrayOfMapping value) {
        return new JAXBElement<ArrayOfMapping>(_ArrayOfMapping_QNAME, ArrayOfMapping.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link LookupName }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "LookupName")
    public JAXBElement<LookupName> createLookupName(LookupName value) {
        return new JAXBElement<LookupName>(_LookupName_QNAME, LookupName.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfValidationCode }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "ArrayOfValidationCode")
    public JAXBElement<ArrayOfValidationCode> createArrayOfValidationCode(ArrayOfValidationCode value) {
        return new JAXBElement<ArrayOfValidationCode>(_ArrayOfValidationCode_QNAME, ArrayOfValidationCode.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ClassificationFilters }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "ClassificationFilters")
    public JAXBElement<ClassificationFilters> createClassificationFilters(ClassificationFilters value) {
        return new JAXBElement<ClassificationFilters>(_ClassificationFilters_QNAME, ClassificationFilters.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TrainingComponentDeleteRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "TrainingComponentDeleteRequest")
    public JAXBElement<TrainingComponentDeleteRequest> createTrainingComponentDeleteRequest(TrainingComponentDeleteRequest value) {
        return new JAXBElement<TrainingComponentDeleteRequest>(_TrainingComponentDeleteRequest_QNAME, TrainingComponentDeleteRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TrainingComponentDetailsRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "TrainingComponentDetailsRequest")
    public JAXBElement<TrainingComponentDetailsRequest> createTrainingComponentDetailsRequest(TrainingComponentDetailsRequest value) {
        return new JAXBElement<TrainingComponentDetailsRequest>(_TrainingComponentDetailsRequest_QNAME, TrainingComponentDetailsRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link OrganisationNameSearchRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "OrganisationNameSearchRequest")
    public JAXBElement<OrganisationNameSearchRequest> createOrganisationNameSearchRequest(OrganisationNameSearchRequest value) {
        return new JAXBElement<OrganisationNameSearchRequest>(_OrganisationNameSearchRequest_QNAME, OrganisationNameSearchRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfContact }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "ArrayOfContact")
    public JAXBElement<ArrayOfContact> createArrayOfContact(ArrayOfContact value) {
        return new JAXBElement<ArrayOfContact>(_ArrayOfContact_QNAME, ArrayOfContact.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NrtCompletion }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "NrtCompletion")
    public JAXBElement<NrtCompletion> createNrtCompletion(NrtCompletion value) {
        return new JAXBElement<NrtCompletion>(_NrtCompletion_QNAME, NrtCompletion.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SearchResult }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "SearchResult")
    public JAXBElement<SearchResult> createSearchResult(SearchResult value) {
        return new JAXBElement<SearchResult>(_SearchResult_QNAME, SearchResult.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ReleaseFile }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "ReleaseFile")
    public JAXBElement<ReleaseFile> createReleaseFile(ReleaseFile value) {
        return new JAXBElement<ReleaseFile>(_ReleaseFile_QNAME, ReleaseFile.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UnitGridEntry3 }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "UnitGridEntry3")
    public JAXBElement<UnitGridEntry3> createUnitGridEntry3(UnitGridEntry3 value) {
        return new JAXBElement<UnitGridEntry3>(_UnitGridEntry3_QNAME, UnitGridEntry3 .class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfRelease }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "ArrayOfRelease")
    public JAXBElement<ArrayOfRelease> createArrayOfRelease(ArrayOfRelease value) {
        return new JAXBElement<ArrayOfRelease>(_ArrayOfRelease_QNAME, ArrayOfRelease.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NrtClassificationSchemeResult }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "NrtClassificationSchemeResult")
    public JAXBElement<NrtClassificationSchemeResult> createNrtClassificationSchemeResult(NrtClassificationSchemeResult value) {
        return new JAXBElement<NrtClassificationSchemeResult>(_NrtClassificationSchemeResult_QNAME, NrtClassificationSchemeResult.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfReleaseFile }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "ArrayOfReleaseFile")
    public JAXBElement<ArrayOfReleaseFile> createArrayOfReleaseFile(ArrayOfReleaseFile value) {
        return new JAXBElement<ArrayOfReleaseFile>(_ArrayOfReleaseFile_QNAME, ArrayOfReleaseFile.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfClassificationPurpose }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "ArrayOfClassificationPurpose")
    public JAXBElement<ArrayOfClassificationPurpose> createArrayOfClassificationPurpose(ArrayOfClassificationPurpose value) {
        return new JAXBElement<ArrayOfClassificationPurpose>(_ArrayOfClassificationPurpose_QNAME, ArrayOfClassificationPurpose.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link LookupRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "LookupRequest")
    public JAXBElement<LookupRequest> createLookupRequest(LookupRequest value) {
        return new JAXBElement<LookupRequest>(_LookupRequest_QNAME, LookupRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TrainingComponentSummary }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "TrainingComponentSummary")
    public JAXBElement<TrainingComponentSummary> createTrainingComponentSummary(TrainingComponentSummary value) {
        return new JAXBElement<TrainingComponentSummary>(_TrainingComponentSummary_QNAME, TrainingComponentSummary.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ClassificationPurpose }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "ClassificationPurpose")
    public JAXBElement<ClassificationPurpose> createClassificationPurpose(ClassificationPurpose value) {
        return new JAXBElement<ClassificationPurpose>(_ClassificationPurpose_QNAME, ClassificationPurpose.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfNrtCompletion }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "ArrayOfNrtCompletion")
    public JAXBElement<ArrayOfNrtCompletion> createArrayOfNrtCompletion(ArrayOfNrtCompletion value) {
        return new JAXBElement<ArrayOfNrtCompletion>(_ArrayOfNrtCompletion_QNAME, ArrayOfNrtCompletion.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GenericListPropertyOfContactTrainingComponentContact8TYN1AE7 }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "GenericListPropertyOfContactTrainingComponentContact8TYN1aE7")
    public JAXBElement<GenericListPropertyOfContactTrainingComponentContact8TYN1AE7> createGenericListPropertyOfContactTrainingComponentContact8TYN1AE7(GenericListPropertyOfContactTrainingComponentContact8TYN1AE7 value) {
        return new JAXBElement<GenericListPropertyOfContactTrainingComponentContact8TYN1AE7>(_GenericListPropertyOfContactTrainingComponentContact8TYN1AE7_QNAME, GenericListPropertyOfContactTrainingComponentContact8TYN1AE7 .class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UnitGridEntry }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "UnitGridEntry")
    public JAXBElement<UnitGridEntry> createUnitGridEntry(UnitGridEntry value) {
        return new JAXBElement<UnitGridEntry>(_UnitGridEntry_QNAME, UnitGridEntry.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Release }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "Release")
    public JAXBElement<Release> createRelease(Release value) {
        return new JAXBElement<Release>(_Release_QNAME, Release.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RecognitionManagerAssignment }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "RecognitionManagerAssignment")
    public JAXBElement<RecognitionManagerAssignment> createRecognitionManagerAssignment(RecognitionManagerAssignment value) {
        return new JAXBElement<RecognitionManagerAssignment>(_RecognitionManagerAssignment_QNAME, RecognitionManagerAssignment.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ValidationErrorSeverity }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "ValidationErrorSeverity")
    public JAXBElement<ValidationErrorSeverity> createValidationErrorSeverity(ValidationErrorSeverity value) {
        return new JAXBElement<ValidationErrorSeverity>(_ValidationErrorSeverity_QNAME, ValidationErrorSeverity.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractPageRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "AbstractPageRequest")
    public JAXBElement<AbstractPageRequest> createAbstractPageRequest(AbstractPageRequest value) {
        return new JAXBElement<AbstractPageRequest>(_AbstractPageRequest_QNAME, AbstractPageRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractDto }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "AbstractDto")
    public JAXBElement<AbstractDto> createAbstractDto(AbstractDto value) {
        return new JAXBElement<AbstractDto>(_AbstractDto_QNAME, AbstractDto.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfReleaseCompanionVolumeLink }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "ArrayOfReleaseCompanionVolumeLink")
    public JAXBElement<ArrayOfReleaseCompanionVolumeLink> createArrayOfReleaseCompanionVolumeLink(ArrayOfReleaseCompanionVolumeLink value) {
        return new JAXBElement<ArrayOfReleaseCompanionVolumeLink>(_ArrayOfReleaseCompanionVolumeLink_QNAME, ArrayOfReleaseCompanionVolumeLink.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TrainingComponentInformationRequested2 }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "TrainingComponentInformationRequested2")
    public JAXBElement<TrainingComponentInformationRequested2> createTrainingComponentInformationRequested2(TrainingComponentInformationRequested2 value) {
        return new JAXBElement<TrainingComponentInformationRequested2>(_TrainingComponentInformationRequested2_QNAME, TrainingComponentInformationRequested2 .class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfNrtCurrencyPeriod }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "ArrayOfNrtCurrencyPeriod")
    public JAXBElement<ArrayOfNrtCurrencyPeriod> createArrayOfNrtCurrencyPeriod(ArrayOfNrtCurrencyPeriod value) {
        return new JAXBElement<ArrayOfNrtCurrencyPeriod>(_ArrayOfNrtCurrencyPeriod_QNAME, ArrayOfNrtCurrencyPeriod.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ReleaseComponent }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "ReleaseComponent")
    public JAXBElement<ReleaseComponent> createReleaseComponent(ReleaseComponent value) {
        return new JAXBElement<ReleaseComponent>(_ReleaseComponent_QNAME, ReleaseComponent.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TrainingComponentSearchRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "TrainingComponentSearchRequest")
    public JAXBElement<TrainingComponentSearchRequest> createTrainingComponentSearchRequest(TrainingComponentSearchRequest value) {
        return new JAXBElement<TrainingComponentSearchRequest>(_TrainingComponentSearchRequest_QNAME, TrainingComponentSearchRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TrainingComponentSearchResult }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "TrainingComponentSearchResult")
    public JAXBElement<TrainingComponentSearchResult> createTrainingComponentSearchResult(TrainingComponentSearchResult value) {
        return new JAXBElement<TrainingComponentSearchResult>(_TrainingComponentSearchResult_QNAME, TrainingComponentSearchResult.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfLookup }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "ArrayOfLookup")
    public JAXBElement<ArrayOfLookup> createArrayOfLookup(ArrayOfLookup value) {
        return new JAXBElement<ArrayOfLookup>(_ArrayOfLookup_QNAME, ArrayOfLookup.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TrainingComponent2 }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "TrainingComponent2")
    public JAXBElement<TrainingComponent2> createTrainingComponent2(TrainingComponent2 value) {
        return new JAXBElement<TrainingComponent2>(_TrainingComponent2_QNAME, TrainingComponent2 .class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link OrganisationScopeSearchRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "OrganisationScopeSearchRequest")
    public JAXBElement<OrganisationScopeSearchRequest> createOrganisationScopeSearchRequest(OrganisationScopeSearchRequest value) {
        return new JAXBElement<OrganisationScopeSearchRequest>(_OrganisationScopeSearchRequest_QNAME, OrganisationScopeSearchRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CurrencyPeriodList }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "CurrencyPeriodList")
    public JAXBElement<CurrencyPeriodList> createCurrencyPeriodList(CurrencyPeriodList value) {
        return new JAXBElement<CurrencyPeriodList>(_CurrencyPeriodList_QNAME, CurrencyPeriodList.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UsageRecommendation }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "UsageRecommendation")
    public JAXBElement<UsageRecommendation> createUsageRecommendation(UsageRecommendation value) {
        return new JAXBElement<UsageRecommendation>(_UsageRecommendation_QNAME, UsageRecommendation.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TrainingComponent }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "TrainingComponent")
    public JAXBElement<TrainingComponent> createTrainingComponent(TrainingComponent value) {
        return new JAXBElement<TrainingComponent>(_TrainingComponent_QNAME, TrainingComponent.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DeleteOperation }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "DeleteOperation")
    public JAXBElement<DeleteOperation> createDeleteOperation(DeleteOperation value) {
        return new JAXBElement<DeleteOperation>(_DeleteOperation_QNAME, DeleteOperation.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TrainingComponent3 }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "TrainingComponent3")
    public JAXBElement<TrainingComponent3> createTrainingComponent3(TrainingComponent3 value) {
        return new JAXBElement<TrainingComponent3>(_TrainingComponent3_QNAME, TrainingComponent3 .class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ReleaseCompanionVolumeLink }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "ReleaseCompanionVolumeLink")
    public JAXBElement<ReleaseCompanionVolumeLink> createReleaseCompanionVolumeLink(ReleaseCompanionVolumeLink value) {
        return new JAXBElement<ReleaseCompanionVolumeLink>(_ReleaseCompanionVolumeLink_QNAME, ReleaseCompanionVolumeLink.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Address }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "Address")
    public JAXBElement<Address> createAddress(Address value) {
        return new JAXBElement<Address>(_Address_QNAME, Address.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RecognitionManager }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "RecognitionManager")
    public JAXBElement<RecognitionManager> createRecognitionManager(RecognitionManager value) {
        return new JAXBElement<RecognitionManager>(_RecognitionManager_QNAME, RecognitionManager.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NrtCurrencyPeriod }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "NrtCurrencyPeriod")
    public JAXBElement<NrtCurrencyPeriod> createNrtCurrencyPeriod(NrtCurrencyPeriod value) {
        return new JAXBElement<NrtCurrencyPeriod>(_NrtCurrencyPeriod_QNAME, NrtCurrencyPeriod.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TrainingComponentContactRole }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "TrainingComponentContactRole")
    public JAXBElement<TrainingComponentContactRole> createTrainingComponentContactRole(TrainingComponentContactRole value) {
        return new JAXBElement<TrainingComponentContactRole>(_TrainingComponentContactRole_QNAME, TrainingComponentContactRole.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfClassificationValue }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "ArrayOfClassificationValue")
    public JAXBElement<ArrayOfClassificationValue> createArrayOfClassificationValue(ArrayOfClassificationValue value) {
        return new JAXBElement<ArrayOfClassificationValue>(_ArrayOfClassificationValue_QNAME, ArrayOfClassificationValue.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ActionOnCollection }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "ActionOnCollection")
    public JAXBElement<ActionOnCollection> createActionOnCollection(ActionOnCollection value) {
        return new JAXBElement<ActionOnCollection>(_ActionOnCollection_QNAME, ActionOnCollection.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfUsageRecommendation }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "ArrayOfUsageRecommendation")
    public JAXBElement<ArrayOfUsageRecommendation> createArrayOfUsageRecommendation(ArrayOfUsageRecommendation value) {
        return new JAXBElement<ArrayOfUsageRecommendation>(_ArrayOfUsageRecommendation_QNAME, ArrayOfUsageRecommendation.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ClassificationValue }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "ClassificationValue")
    public JAXBElement<ClassificationValue> createClassificationValue(ClassificationValue value) {
        return new JAXBElement<ClassificationValue>(_ClassificationValue_QNAME, ClassificationValue.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TrainingComponentClassificationList }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "TrainingComponentClassificationList")
    public JAXBElement<TrainingComponentClassificationList> createTrainingComponentClassificationList(TrainingComponentClassificationList value) {
        return new JAXBElement<TrainingComponentClassificationList>(_TrainingComponentClassificationList_QNAME, TrainingComponentClassificationList.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link List }{@code <}{@link String }{@code >}{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "TrainingComponentTypes")
    public JAXBElement<List<String>> createTrainingComponentTypes(List<String> value) {
        return new JAXBElement<List<String>>(_TrainingComponentTypes_QNAME, ((Class) List.class), null, ((List<String> ) value));
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GenericListPropertyOfClassificationNrtClassification8TYN1AE7 }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "GenericListPropertyOfClassificationNrtClassification8TYN1aE7")
    public JAXBElement<GenericListPropertyOfClassificationNrtClassification8TYN1AE7> createGenericListPropertyOfClassificationNrtClassification8TYN1AE7(GenericListPropertyOfClassificationNrtClassification8TYN1AE7 value) {
        return new JAXBElement<GenericListPropertyOfClassificationNrtClassification8TYN1AE7>(_GenericListPropertyOfClassificationNrtClassification8TYN1AE7_QNAME, GenericListPropertyOfClassificationNrtClassification8TYN1AE7 .class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfTrainingComponentSummary }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "ArrayOfTrainingComponentSummary")
    public JAXBElement<ArrayOfTrainingComponentSummary> createArrayOfTrainingComponentSummary(ArrayOfTrainingComponentSummary value) {
        return new JAXBElement<ArrayOfTrainingComponentSummary>(_ArrayOfTrainingComponentSummary_QNAME, ArrayOfTrainingComponentSummary.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Release3 }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "Release3")
    public JAXBElement<Release3> createRelease3(Release3 value) {
        return new JAXBElement<Release3>(_Release3_QNAME, Release3 .class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Release4 }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "Release4")
    public JAXBElement<Release4> createRelease4(Release4 value) {
        return new JAXBElement<Release4>(_Release4_QNAME, Release4 .class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Classification }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "Classification")
    public JAXBElement<Classification> createClassification(Classification value) {
        return new JAXBElement<Classification>(_Classification_QNAME, Classification.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TrainingComponentInformationRequested }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "TrainingComponentInformationRequested")
    public JAXBElement<TrainingComponentInformationRequested> createTrainingComponentInformationRequested(TrainingComponentInformationRequested value) {
        return new JAXBElement<TrainingComponentInformationRequested>(_TrainingComponentInformationRequested_QNAME, TrainingComponentInformationRequested.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfReleaseComponent }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "ArrayOfReleaseComponent")
    public JAXBElement<ArrayOfReleaseComponent> createArrayOfReleaseComponent(ArrayOfReleaseComponent value) {
        return new JAXBElement<ArrayOfReleaseComponent>(_ArrayOfReleaseComponent_QNAME, ArrayOfReleaseComponent.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TrainingComponentSummary2 }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "TrainingComponentSummary2")
    public JAXBElement<TrainingComponentSummary2> createTrainingComponentSummary2(TrainingComponentSummary2 value) {
        return new JAXBElement<TrainingComponentSummary2>(_TrainingComponentSummary2_QNAME, TrainingComponentSummary2 .class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DeletedSearchRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "DeletedSearchRequest")
    public JAXBElement<DeletedSearchRequest> createDeletedSearchRequest(DeletedSearchRequest value) {
        return new JAXBElement<DeletedSearchRequest>(_DeletedSearchRequest_QNAME, DeletedSearchRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TrainingComponentTypeFilter }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "TrainingComponentTypeFilter")
    public JAXBElement<TrainingComponentTypeFilter> createTrainingComponentTypeFilter(TrainingComponentTypeFilter value) {
        return new JAXBElement<TrainingComponentTypeFilter>(_TrainingComponentTypeFilter_QNAME, TrainingComponentTypeFilter.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfNrtClassificationSchemeResult }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "ArrayOfNrtClassificationSchemeResult")
    public JAXBElement<ArrayOfNrtClassificationSchemeResult> createArrayOfNrtClassificationSchemeResult(ArrayOfNrtClassificationSchemeResult value) {
        return new JAXBElement<ArrayOfNrtClassificationSchemeResult>(_ArrayOfNrtClassificationSchemeResult_QNAME, ArrayOfNrtClassificationSchemeResult.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfUnitGridEntry }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "ArrayOfUnitGridEntry")
    public JAXBElement<ArrayOfUnitGridEntry> createArrayOfUnitGridEntry(ArrayOfUnitGridEntry value) {
        return new JAXBElement<ArrayOfUnitGridEntry>(_ArrayOfUnitGridEntry_QNAME, ArrayOfUnitGridEntry.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfValidationError }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "ArrayOfValidationError")
    public JAXBElement<ArrayOfValidationError> createArrayOfValidationError(ArrayOfValidationError value) {
        return new JAXBElement<ArrayOfValidationError>(_ArrayOfValidationError_QNAME, ArrayOfValidationError.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfDeletedTrainingComponent }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "ArrayOfDeletedTrainingComponent")
    public JAXBElement<ArrayOfDeletedTrainingComponent> createArrayOfDeletedTrainingComponent(ArrayOfDeletedTrainingComponent value) {
        return new JAXBElement<ArrayOfDeletedTrainingComponent>(_ArrayOfDeletedTrainingComponent_QNAME, ArrayOfDeletedTrainingComponent.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GenericListPropertyOfNrtCurrencyPeriodNrtCurrencyPeriod8TYN1AE7 }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "GenericListPropertyOfNrtCurrencyPeriodNrtCurrencyPeriod8TYN1aE7")
    public JAXBElement<GenericListPropertyOfNrtCurrencyPeriodNrtCurrencyPeriod8TYN1AE7> createGenericListPropertyOfNrtCurrencyPeriodNrtCurrencyPeriod8TYN1AE7(GenericListPropertyOfNrtCurrencyPeriodNrtCurrencyPeriod8TYN1AE7 value) {
        return new JAXBElement<GenericListPropertyOfNrtCurrencyPeriodNrtCurrencyPeriod8TYN1AE7>(_GenericListPropertyOfNrtCurrencyPeriodNrtCurrencyPeriod8TYN1AE7_QNAME, GenericListPropertyOfNrtCurrencyPeriodNrtCurrencyPeriod8TYN1AE7 .class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TrainingComponentContactList }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "TrainingComponentContactList")
    public JAXBElement<TrainingComponentContactList> createTrainingComponentContactList(TrainingComponentContactList value) {
        return new JAXBElement<TrainingComponentContactList>(_TrainingComponentContactList_QNAME, TrainingComponentContactList.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TrainingComponentUpdateRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "TrainingComponentUpdateRequest")
    public JAXBElement<TrainingComponentUpdateRequest> createTrainingComponentUpdateRequest(TrainingComponentUpdateRequest value) {
        return new JAXBElement<TrainingComponentUpdateRequest>(_TrainingComponentUpdateRequest_QNAME, TrainingComponentUpdateRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DeletedTrainingComponent }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "DeletedTrainingComponent")
    public JAXBElement<DeletedTrainingComponent> createDeletedTrainingComponent(DeletedTrainingComponent value) {
        return new JAXBElement<DeletedTrainingComponent>(_DeletedTrainingComponent_QNAME, DeletedTrainingComponent.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ValidationFault }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "ValidationFault")
    public JAXBElement<ValidationFault> createValidationFault(ValidationFault value) {
        return new JAXBElement<ValidationFault>(_ValidationFault_QNAME, ValidationFault.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TrainingComponentSummary3 }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "TrainingComponentSummary3")
    public JAXBElement<TrainingComponentSummary3> createTrainingComponentSummary3(TrainingComponentSummary3 value) {
        return new JAXBElement<TrainingComponentSummary3>(_TrainingComponentSummary3_QNAME, TrainingComponentSummary3 .class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfAddressStates }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "ArrayOfAddressStates")
    public JAXBElement<ArrayOfAddressStates> createArrayOfAddressStates(ArrayOfAddressStates value) {
        return new JAXBElement<ArrayOfAddressStates>(_ArrayOfAddressStates_QNAME, ArrayOfAddressStates.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TrainingComponentModifiedSearchRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "TrainingComponentModifiedSearchRequest")
    public JAXBElement<TrainingComponentModifiedSearchRequest> createTrainingComponentModifiedSearchRequest(TrainingComponentModifiedSearchRequest value) {
        return new JAXBElement<TrainingComponentModifiedSearchRequest>(_TrainingComponentModifiedSearchRequest_QNAME, TrainingComponentModifiedSearchRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TrainingComponentClassificationList }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "TrainingComponentClassificationList", scope = TrainingComponentUpdateRequest.class)
    public JAXBElement<TrainingComponentClassificationList> createTrainingComponentUpdateRequestTrainingComponentClassificationList(TrainingComponentClassificationList value) {
        return new JAXBElement<TrainingComponentClassificationList>(_TrainingComponentClassificationList_QNAME, TrainingComponentClassificationList.class, TrainingComponentUpdateRequest.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "Title", scope = TrainingComponentUpdateRequest.class)
    public JAXBElement<String> createTrainingComponentUpdateRequestTitle(String value) {
        return new JAXBElement<String>(_TrainingComponentUpdateRequestTitle_QNAME, String.class, TrainingComponentUpdateRequest.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TrainingComponentContactList }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "TrainingComponentContactList", scope = TrainingComponentUpdateRequest.class)
    public JAXBElement<TrainingComponentContactList> createTrainingComponentUpdateRequestTrainingComponentContactList(TrainingComponentContactList value) {
        return new JAXBElement<TrainingComponentContactList>(_TrainingComponentContactList_QNAME, TrainingComponentContactList.class, TrainingComponentUpdateRequest.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CurrencyPeriodList }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "CurrencyPeriodList", scope = TrainingComponentUpdateRequest.class)
    public JAXBElement<CurrencyPeriodList> createTrainingComponentUpdateRequestCurrencyPeriodList(CurrencyPeriodList value) {
        return new JAXBElement<CurrencyPeriodList>(_CurrencyPeriodList_QNAME, CurrencyPeriodList.class, TrainingComponentUpdateRequest.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CompletionMappingList }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "CompletionMappingList", scope = TrainingComponentUpdateRequest.class)
    public JAXBElement<CompletionMappingList> createTrainingComponentUpdateRequestCompletionMappingList(CompletionMappingList value) {
        return new JAXBElement<CompletionMappingList>(_CompletionMappingList_QNAME, CompletionMappingList.class, TrainingComponentUpdateRequest.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfTrainingComponentSummary }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "Results", scope = SearchResult.class)
    public JAXBElement<ArrayOfTrainingComponentSummary> createSearchResultResults(ArrayOfTrainingComponentSummary value) {
        return new JAXBElement<ArrayOfTrainingComponentSummary>(_SearchResultResults_QNAME, ArrayOfTrainingComponentSummary.class, SearchResult.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfValidationCode }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "GetValidationCodesResult", scope = GetValidationCodesResponse.class)
    public JAXBElement<ArrayOfValidationCode> createGetValidationCodesResponseGetValidationCodesResult(ArrayOfValidationCode value) {
        return new JAXBElement<ArrayOfValidationCode>(_GetValidationCodesResponseGetValidationCodesResult_QNAME, ArrayOfValidationCode.class, GetValidationCodesResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "Title", scope = UnitGridEntry.class)
    public JAXBElement<String> createUnitGridEntryTitle(String value) {
        return new JAXBElement<String>(_TrainingComponentUpdateRequestTitle_QNAME, String.class, UnitGridEntry.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "Code", scope = UnitGridEntry.class)
    public JAXBElement<String> createUnitGridEntryCode(String value) {
        return new JAXBElement<String>(_UnitGridEntryCode_QNAME, String.class, UnitGridEntry.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DateTimeOffset }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "EndDate", scope = DeletedSearchRequest.class)
    public JAXBElement<DateTimeOffset> createDeletedSearchRequestEndDate(DateTimeOffset value) {
        return new JAXBElement<DateTimeOffset>(_DeletedSearchRequestEndDate_QNAME, DateTimeOffset.class, DeletedSearchRequest.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DateTimeOffset }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "StartDate", scope = DeletedSearchRequest.class)
    public JAXBElement<DateTimeOffset> createDeletedSearchRequestStartDate(DateTimeOffset value) {
        return new JAXBElement<DateTimeOffset>(_DeletedSearchRequestStartDate_QNAME, DateTimeOffset.class, DeletedSearchRequest.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TrainingComponentTypeFilter }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "TrainingComponentTypes", scope = TrainingComponentModifiedSearchRequest.class)
    public JAXBElement<TrainingComponentTypeFilter> createTrainingComponentModifiedSearchRequestTrainingComponentTypes(TrainingComponentTypeFilter value) {
        return new JAXBElement<TrainingComponentTypeFilter>(_TrainingComponentTypes_QNAME, TrainingComponentTypeFilter.class, TrainingComponentModifiedSearchRequest.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DateTimeOffset }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "EndDate", scope = TrainingComponentModifiedSearchRequest.class)
    public JAXBElement<DateTimeOffset> createTrainingComponentModifiedSearchRequestEndDate(DateTimeOffset value) {
        return new JAXBElement<DateTimeOffset>(_DeletedSearchRequestEndDate_QNAME, DateTimeOffset.class, TrainingComponentModifiedSearchRequest.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfstring }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "DataManagerFilter", scope = TrainingComponentModifiedSearchRequest.class)
    public JAXBElement<ArrayOfstring> createTrainingComponentModifiedSearchRequestDataManagerFilter(ArrayOfstring value) {
        return new JAXBElement<ArrayOfstring>(_TrainingComponentModifiedSearchRequestDataManagerFilter_QNAME, ArrayOfstring.class, TrainingComponentModifiedSearchRequest.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DateTimeOffset }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "StartDate", scope = TrainingComponentModifiedSearchRequest.class)
    public JAXBElement<DateTimeOffset> createTrainingComponentModifiedSearchRequestStartDate(DateTimeOffset value) {
        return new JAXBElement<DateTimeOffset>(_DeletedSearchRequestStartDate_QNAME, DateTimeOffset.class, TrainingComponentModifiedSearchRequest.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TrainingComponentInformationRequested }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "InformationRequest", scope = TrainingComponentDetailsRequest.class)
    public JAXBElement<TrainingComponentInformationRequested> createTrainingComponentDetailsRequestInformationRequest(TrainingComponentInformationRequested value) {
        return new JAXBElement<TrainingComponentInformationRequested>(_TrainingComponentDetailsRequestInformationRequest_QNAME, TrainingComponentInformationRequested.class, TrainingComponentDetailsRequest.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfDeletedTrainingComponent }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "SearchDeletedByDeletedDateResult", scope = SearchDeletedByDeletedDateResponse.class)
    public JAXBElement<ArrayOfDeletedTrainingComponent> createSearchDeletedByDeletedDateResponseSearchDeletedByDeletedDateResult(ArrayOfDeletedTrainingComponent value) {
        return new JAXBElement<ArrayOfDeletedTrainingComponent>(_SearchDeletedByDeletedDateResponseSearchDeletedByDeletedDateResult_QNAME, ArrayOfDeletedTrainingComponent.class, SearchDeletedByDeletedDateResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boolean }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "IsEssential", scope = UnitGridEntry3 .class)
    public JAXBElement<Boolean> createUnitGridEntry3IsEssential(Boolean value) {
        return new JAXBElement<Boolean>(_UnitGridEntry3IsEssential_QNAME, Boolean.class, UnitGridEntry3 .class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "Code", scope = ValidationError.class)
    public JAXBElement<String> createValidationErrorCode(String value) {
        return new JAXBElement<String>(_UnitGridEntryCode_QNAME, String.class, ValidationError.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "ValidationTarget", scope = ValidationError.class)
    public JAXBElement<String> createValidationErrorValidationTarget(String value) {
        return new JAXBElement<String>(_ValidationErrorValidationTarget_QNAME, String.class, ValidationError.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "SubCode", scope = ValidationError.class)
    public JAXBElement<String> createValidationErrorSubCode(String value) {
        return new JAXBElement<String>(_ValidationErrorSubCode_QNAME, String.class, ValidationError.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "Message", scope = ValidationError.class)
    public JAXBElement<String> createValidationErrorMessage(String value) {
        return new JAXBElement<String>(_ValidationErrorMessage_QNAME, String.class, ValidationError.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "Context", scope = ValidationError.class)
    public JAXBElement<String> createValidationErrorContext(String value) {
        return new JAXBElement<String>(_ValidationErrorContext_QNAME, String.class, ValidationError.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfUsageRecommendation }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "UsageRecommendations", scope = TrainingComponent.class)
    public JAXBElement<ArrayOfUsageRecommendation> createTrainingComponentUsageRecommendations(ArrayOfUsageRecommendation value) {
        return new JAXBElement<ArrayOfUsageRecommendation>(_TrainingComponentUsageRecommendations_QNAME, ArrayOfUsageRecommendation.class, TrainingComponent.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "Title", scope = TrainingComponent.class)
    public JAXBElement<String> createTrainingComponentTitle(String value) {
        return new JAXBElement<String>(_TrainingComponentUpdateRequestTitle_QNAME, String.class, TrainingComponent.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "Code", scope = TrainingComponent.class)
    public JAXBElement<String> createTrainingComponentCode(String value) {
        return new JAXBElement<String>(_UnitGridEntryCode_QNAME, String.class, TrainingComponent.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfNrtCompletion }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "CompletionMapping", scope = TrainingComponent.class)
    public JAXBElement<ArrayOfNrtCompletion> createTrainingComponentCompletionMapping(ArrayOfNrtCompletion value) {
        return new JAXBElement<ArrayOfNrtCompletion>(_TrainingComponentCompletionMapping_QNAME, ArrayOfNrtCompletion.class, TrainingComponent.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfRecognitionManagerAssignment }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "RecognitionManagers", scope = TrainingComponent.class)
    public JAXBElement<ArrayOfRecognitionManagerAssignment> createTrainingComponentRecognitionManagers(ArrayOfRecognitionManagerAssignment value) {
        return new JAXBElement<ArrayOfRecognitionManagerAssignment>(_TrainingComponentRecognitionManagers_QNAME, ArrayOfRecognitionManagerAssignment.class, TrainingComponent.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfMapping }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "MappingInformation", scope = TrainingComponent.class)
    public JAXBElement<ArrayOfMapping> createTrainingComponentMappingInformation(ArrayOfMapping value) {
        return new JAXBElement<ArrayOfMapping>(_TrainingComponentMappingInformation_QNAME, ArrayOfMapping.class, TrainingComponent.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfClassification }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "Classifications", scope = TrainingComponent.class)
    public JAXBElement<ArrayOfClassification> createTrainingComponentClassifications(ArrayOfClassification value) {
        return new JAXBElement<ArrayOfClassification>(_TrainingComponentClassifications_QNAME, ArrayOfClassification.class, TrainingComponent.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfContact }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "Contacts", scope = TrainingComponent.class)
    public JAXBElement<ArrayOfContact> createTrainingComponentContacts(ArrayOfContact value) {
        return new JAXBElement<ArrayOfContact>(_TrainingComponentContacts_QNAME, ArrayOfContact.class, TrainingComponent.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfDataManagerAssignment }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "DataManagers", scope = TrainingComponent.class)
    public JAXBElement<ArrayOfDataManagerAssignment> createTrainingComponentDataManagers(ArrayOfDataManagerAssignment value) {
        return new JAXBElement<ArrayOfDataManagerAssignment>(_TrainingComponentDataManagers_QNAME, ArrayOfDataManagerAssignment.class, TrainingComponent.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "IscOrganisationCode", scope = TrainingComponent.class)
    public JAXBElement<String> createTrainingComponentIscOrganisationCode(String value) {
        return new JAXBElement<String>(_TrainingComponentIscOrganisationCode_QNAME, String.class, TrainingComponent.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfNrtCurrencyPeriod }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "CurrencyPeriods", scope = TrainingComponent.class)
    public JAXBElement<ArrayOfNrtCurrencyPeriod> createTrainingComponentCurrencyPeriods(ArrayOfNrtCurrencyPeriod value) {
        return new JAXBElement<ArrayOfNrtCurrencyPeriod>(_TrainingComponentCurrencyPeriods_QNAME, ArrayOfNrtCurrencyPeriod.class, TrainingComponent.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfRelease }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "Releases", scope = TrainingComponent.class)
    public JAXBElement<ArrayOfRelease> createTrainingComponentReleases(ArrayOfRelease value) {
        return new JAXBElement<ArrayOfRelease>(_TrainingComponentReleases_QNAME, ArrayOfRelease.class, TrainingComponent.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "ParentCode", scope = TrainingComponent.class)
    public JAXBElement<String> createTrainingComponentParentCode(String value) {
        return new JAXBElement<String>(_TrainingComponentParentCode_QNAME, String.class, TrainingComponent.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "ParentTitle", scope = TrainingComponent.class)
    public JAXBElement<String> createTrainingComponentParentTitle(String value) {
        return new JAXBElement<String>(_TrainingComponentParentTitle_QNAME, String.class, TrainingComponent.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TrainingComponentDetailsRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "request", scope = GetDetails.class)
    public JAXBElement<TrainingComponentDetailsRequest> createGetDetailsRequest(TrainingComponentDetailsRequest value) {
        return new JAXBElement<TrainingComponentDetailsRequest>(_GetDetailsRequest_QNAME, TrainingComponentDetailsRequest.class, GetDetails.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "RegistrationManagerCode", scope = DataManager.class)
    public JAXBElement<String> createDataManagerRegistrationManagerCode(String value) {
        return new JAXBElement<String>(_DataManagerRegistrationManagerCode_QNAME, String.class, DataManager.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "Code", scope = DataManager.class)
    public JAXBElement<String> createDataManagerCode(String value) {
        return new JAXBElement<String>(_UnitGridEntryCode_QNAME, String.class, DataManager.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "RecognitionManagerCode", scope = DataManager.class)
    public JAXBElement<String> createDataManagerRecognitionManagerCode(String value) {
        return new JAXBElement<String>(_DataManagerRecognitionManagerCode_QNAME, String.class, DataManager.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "Description", scope = DataManager.class)
    public JAXBElement<String> createDataManagerDescription(String value) {
        return new JAXBElement<String>(_DataManagerDescription_QNAME, String.class, DataManager.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TrainingComponent }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "request", scope = Add.class)
    public JAXBElement<TrainingComponent> createAddRequest(TrainingComponent value) {
        return new JAXBElement<TrainingComponent>(_GetDetailsRequest_QNAME, TrainingComponent.class, Add.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfClassificationPurpose }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "GetClassificationPurposesResult", scope = GetClassificationPurposesResponse.class)
    public JAXBElement<ArrayOfClassificationPurpose> createGetClassificationPurposesResponseGetClassificationPurposesResult(ArrayOfClassificationPurpose value) {
        return new JAXBElement<ArrayOfClassificationPurpose>(_GetClassificationPurposesResponseGetClassificationPurposesResult_QNAME, ArrayOfClassificationPurpose.class, GetClassificationPurposesResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "Title", scope = ReleaseComponent.class)
    public JAXBElement<String> createReleaseComponentTitle(String value) {
        return new JAXBElement<String>(_TrainingComponentUpdateRequestTitle_QNAME, String.class, ReleaseComponent.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "ReleaseDate", scope = ReleaseComponent.class)
    public JAXBElement<String> createReleaseComponentReleaseDate(String value) {
        return new JAXBElement<String>(_ReleaseComponentReleaseDate_QNAME, String.class, ReleaseComponent.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "Code", scope = ReleaseComponent.class)
    public JAXBElement<String> createReleaseComponentCode(String value) {
        return new JAXBElement<String>(_UnitGridEntryCode_QNAME, String.class, ReleaseComponent.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "ReleaseCurrency", scope = ReleaseComponent.class)
    public JAXBElement<String> createReleaseComponentReleaseCurrency(String value) {
        return new JAXBElement<String>(_ReleaseComponentReleaseCurrency_QNAME, String.class, ReleaseComponent.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "ReleaseNumber", scope = ReleaseComponent.class)
    public JAXBElement<String> createReleaseComponentReleaseNumber(String value) {
        return new JAXBElement<String>(_ReleaseComponentReleaseNumber_QNAME, String.class, ReleaseComponent.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfAddressStates }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "GetAddressStatesResult", scope = GetAddressStatesResponse.class)
    public JAXBElement<ArrayOfAddressStates> createGetAddressStatesResponseGetAddressStatesResult(ArrayOfAddressStates value) {
        return new JAXBElement<ArrayOfAddressStates>(_GetAddressStatesResponseGetAddressStatesResult_QNAME, ArrayOfAddressStates.class, GetAddressStatesResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TrainingComponentModifiedSearchRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "request", scope = SearchByModifiedDate.class)
    public JAXBElement<TrainingComponentModifiedSearchRequest> createSearchByModifiedDateRequest(TrainingComponentModifiedSearchRequest value) {
        return new JAXBElement<TrainingComponentModifiedSearchRequest>(_GetDetailsRequest_QNAME, TrainingComponentModifiedSearchRequest.class, SearchByModifiedDate.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "Code", scope = Lookup.class)
    public JAXBElement<String> createLookupCode(String value) {
        return new JAXBElement<String>(_UnitGridEntryCode_QNAME, String.class, Lookup.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "Description", scope = Lookup.class)
    public JAXBElement<String> createLookupDescription(String value) {
        return new JAXBElement<String>(_DataManagerDescription_QNAME, String.class, Lookup.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfDataManager }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "GetDataManagersResult", scope = GetDataManagersResponse.class)
    public JAXBElement<ArrayOfDataManager> createGetDataManagersResponseGetDataManagersResult(ArrayOfDataManager value) {
        return new JAXBElement<ArrayOfDataManager>(_GetDataManagersResponseGetDataManagersResult_QNAME, ArrayOfDataManager.class, GetDataManagersResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "RelativePath", scope = ReleaseFile.class)
    public JAXBElement<String> createReleaseFileRelativePath(String value) {
        return new JAXBElement<String>(_ReleaseFileRelativePath_QNAME, String.class, ReleaseFile.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link LookupRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "request", scope = GetLookup.class)
    public JAXBElement<LookupRequest> createGetLookupRequest(LookupRequest value) {
        return new JAXBElement<LookupRequest>(_GetDetailsRequest_QNAME, LookupRequest.class, GetLookup.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TrainingComponentSearchRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "request", scope = Search.class)
    public JAXBElement<TrainingComponentSearchRequest> createSearchRequest(TrainingComponentSearchRequest value) {
        return new JAXBElement<TrainingComponentSearchRequest>(_GetDetailsRequest_QNAME, TrainingComponentSearchRequest.class, Search.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "CurrencyStatus", scope = TrainingComponentSummary3 .class)
    public JAXBElement<String> createTrainingComponentSummary3CurrencyStatus(String value) {
        return new JAXBElement<String>(_TrainingComponentSummary3CurrencyStatus_QNAME, String.class, TrainingComponentSummary3 .class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ClassificationFilters }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "ClassificationFilters", scope = OrganisationNameSearchRequest.class)
    public JAXBElement<ClassificationFilters> createOrganisationNameSearchRequestClassificationFilters(ClassificationFilters value) {
        return new JAXBElement<ClassificationFilters>(_ClassificationFilters_QNAME, ClassificationFilters.class, OrganisationNameSearchRequest.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfstring }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "RegistrationManagers", scope = OrganisationNameSearchRequest.class)
    public JAXBElement<ArrayOfstring> createOrganisationNameSearchRequestRegistrationManagers(ArrayOfstring value) {
        return new JAXBElement<ArrayOfstring>(_OrganisationNameSearchRequestRegistrationManagers_QNAME, ArrayOfstring.class, OrganisationNameSearchRequest.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfRecognitionManager }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "GetRecognitionManagersResult", scope = GetRecognitionManagersResponse.class)
    public JAXBElement<ArrayOfRecognitionManager> createGetRecognitionManagersResponseGetRecognitionManagersResult(ArrayOfRecognitionManager value) {
        return new JAXBElement<ArrayOfRecognitionManager>(_GetRecognitionManagersResponseGetRecognitionManagersResult_QNAME, ArrayOfRecognitionManager.class, GetRecognitionManagersResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfLookup }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "GetLookupResult", scope = GetLookupResponse.class)
    public JAXBElement<ArrayOfLookup> createGetLookupResponseGetLookupResult(ArrayOfLookup value) {
        return new JAXBElement<ArrayOfLookup>(_GetLookupResponseGetLookupResult_QNAME, ArrayOfLookup.class, GetLookupResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "EndDate", scope = AbstractDto.class)
    public JAXBElement<XMLGregorianCalendar> createAbstractDtoEndDate(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_DeletedSearchRequestEndDate_QNAME, XMLGregorianCalendar.class, AbstractDto.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "StartDate", scope = AbstractDto.class)
    public JAXBElement<XMLGregorianCalendar> createAbstractDtoStartDate(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_DeletedSearchRequestStartDate_QNAME, XMLGregorianCalendar.class, AbstractDto.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "PublishedComponentType", scope = ReleaseCompanionVolumeLink.class)
    public JAXBElement<String> createReleaseCompanionVolumeLinkPublishedComponentType(String value) {
        return new JAXBElement<String>(_ReleaseCompanionVolumeLinkPublishedComponentType_QNAME, String.class, ReleaseCompanionVolumeLink.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "LinkUrl", scope = ReleaseCompanionVolumeLink.class)
    public JAXBElement<String> createReleaseCompanionVolumeLinkLinkUrl(String value) {
        return new JAXBElement<String>(_ReleaseCompanionVolumeLinkLinkUrl_QNAME, String.class, ReleaseCompanionVolumeLink.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "LinkText", scope = ReleaseCompanionVolumeLink.class)
    public JAXBElement<String> createReleaseCompanionVolumeLinkLinkText(String value) {
        return new JAXBElement<String>(_ReleaseCompanionVolumeLinkLinkText_QNAME, String.class, ReleaseCompanionVolumeLink.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "LinkNotes", scope = ReleaseCompanionVolumeLink.class)
    public JAXBElement<String> createReleaseCompanionVolumeLinkLinkNotes(String value) {
        return new JAXBElement<String>(_ReleaseCompanionVolumeLinkLinkNotes_QNAME, String.class, ReleaseCompanionVolumeLink.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "Code", scope = ValidationCode.class)
    public JAXBElement<String> createValidationCodeCode(String value) {
        return new JAXBElement<String>(_UnitGridEntryCode_QNAME, String.class, ValidationCode.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "SubCode", scope = ValidationCode.class)
    public JAXBElement<String> createValidationCodeSubCode(String value) {
        return new JAXBElement<String>(_ValidationErrorSubCode_QNAME, String.class, ValidationCode.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "Message", scope = ValidationCode.class)
    public JAXBElement<String> createValidationCodeMessage(String value) {
        return new JAXBElement<String>(_ValidationErrorMessage_QNAME, String.class, ValidationCode.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "Abbreviation", scope = AddressStates.class)
    public JAXBElement<String> createAddressStatesAbbreviation(String value) {
        return new JAXBElement<String>(_AddressStatesAbbreviation_QNAME, String.class, AddressStates.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "Code", scope = AddressStates.class)
    public JAXBElement<String> createAddressStatesCode(String value) {
        return new JAXBElement<String>(_UnitGridEntryCode_QNAME, String.class, AddressStates.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "Description", scope = AddressStates.class)
    public JAXBElement<String> createAddressStatesDescription(String value) {
        return new JAXBElement<String>(_DataManagerDescription_QNAME, String.class, AddressStates.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "Role", scope = TrainingComponentContactRole.class)
    public JAXBElement<String> createTrainingComponentContactRoleRole(String value) {
        return new JAXBElement<String>(_TrainingComponentContactRoleRole_QNAME, String.class, TrainingComponentContactRole.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "Description", scope = TrainingComponentContactRole.class)
    public JAXBElement<String> createTrainingComponentContactRoleDescription(String value) {
        return new JAXBElement<String>(_DataManagerDescription_QNAME, String.class, TrainingComponentContactRole.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TrainingComponentDeleteRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "request", scope = Delete.class)
    public JAXBElement<TrainingComponentDeleteRequest> createDeleteRequest(TrainingComponentDeleteRequest value) {
        return new JAXBElement<TrainingComponentDeleteRequest>(_GetDetailsRequest_QNAME, TrainingComponentDeleteRequest.class, Delete.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "MapsToCode", scope = Mapping.class)
    public JAXBElement<String> createMappingMapsToCode(String value) {
        return new JAXBElement<String>(_MappingMapsToCode_QNAME, String.class, Mapping.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "Title", scope = Mapping.class)
    public JAXBElement<String> createMappingTitle(String value) {
        return new JAXBElement<String>(_TrainingComponentUpdateRequestTitle_QNAME, String.class, Mapping.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "Code", scope = Mapping.class)
    public JAXBElement<String> createMappingCode(String value) {
        return new JAXBElement<String>(_UnitGridEntryCode_QNAME, String.class, Mapping.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "Notes", scope = Mapping.class)
    public JAXBElement<String> createMappingNotes(String value) {
        return new JAXBElement<String>(_MappingNotes_QNAME, String.class, Mapping.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "MapsToTitle", scope = Mapping.class)
    public JAXBElement<String> createMappingMapsToTitle(String value) {
        return new JAXBElement<String>(_MappingMapsToTitle_QNAME, String.class, Mapping.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "NationalCode", scope = DeletedTrainingComponent.class)
    public JAXBElement<String> createDeletedTrainingComponentNationalCode(String value) {
        return new JAXBElement<String>(_DeletedTrainingComponentNationalCode_QNAME, String.class, DeletedTrainingComponent.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "EffectiveDate", scope = TrainingComponentTransferDataManagerRequest.class)
    public JAXBElement<XMLGregorianCalendar> createTrainingComponentTransferDataManagerRequestEffectiveDate(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_TrainingComponentTransferDataManagerRequestEffectiveDate_QNAME, XMLGregorianCalendar.class, TrainingComponentTransferDataManagerRequest.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "RecognitionManagerCode", scope = TrainingComponentTransferDataManagerRequest.class)
    public JAXBElement<String> createTrainingComponentTransferDataManagerRequestRecognitionManagerCode(String value) {
        return new JAXBElement<String>(_DataManagerRecognitionManagerCode_QNAME, String.class, TrainingComponentTransferDataManagerRequest.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "Authority", scope = NrtCurrencyPeriod.class)
    public JAXBElement<String> createNrtCurrencyPeriodAuthority(String value) {
        return new JAXBElement<String>(_NrtCurrencyPeriodAuthority_QNAME, String.class, NrtCurrencyPeriod.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Address }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "PostalAddress", scope = Contact.class)
    public JAXBElement<Address> createContactPostalAddress(Address value) {
        return new JAXBElement<Address>(_ContactPostalAddress_QNAME, Address.class, Contact.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "Title", scope = Contact.class)
    public JAXBElement<String> createContactTitle(String value) {
        return new JAXBElement<String>(_TrainingComponentUpdateRequestTitle_QNAME, String.class, Contact.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "LastName", scope = Contact.class)
    public JAXBElement<String> createContactLastName(String value) {
        return new JAXBElement<String>(_ContactLastName_QNAME, String.class, Contact.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "RoleCode", scope = Contact.class)
    public JAXBElement<String> createContactRoleCode(String value) {
        return new JAXBElement<String>(_ContactRoleCode_QNAME, String.class, Contact.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "Phone", scope = Contact.class)
    public JAXBElement<String> createContactPhone(String value) {
        return new JAXBElement<String>(_ContactPhone_QNAME, String.class, Contact.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "Fax", scope = Contact.class)
    public JAXBElement<String> createContactFax(String value) {
        return new JAXBElement<String>(_ContactFax_QNAME, String.class, Contact.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "OrganisationName", scope = Contact.class)
    public JAXBElement<String> createContactOrganisationName(String value) {
        return new JAXBElement<String>(_ContactOrganisationName_QNAME, String.class, Contact.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "GroupName", scope = Contact.class)
    public JAXBElement<String> createContactGroupName(String value) {
        return new JAXBElement<String>(_ContactGroupName_QNAME, String.class, Contact.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "FirstName", scope = Contact.class)
    public JAXBElement<String> createContactFirstName(String value) {
        return new JAXBElement<String>(_ContactFirstName_QNAME, String.class, Contact.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "Mobile", scope = Contact.class)
    public JAXBElement<String> createContactMobile(String value) {
        return new JAXBElement<String>(_ContactMobile_QNAME, String.class, Contact.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "JobTitle", scope = Contact.class)
    public JAXBElement<String> createContactJobTitle(String value) {
        return new JAXBElement<String>(_ContactJobTitle_QNAME, String.class, Contact.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "TypeCode", scope = Contact.class)
    public JAXBElement<String> createContactTypeCode(String value) {
        return new JAXBElement<String>(_ContactTypeCode_QNAME, String.class, Contact.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "Email", scope = Contact.class)
    public JAXBElement<String> createContactEmail(String value) {
        return new JAXBElement<String>(_ContactEmail_QNAME, String.class, Contact.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfNrtClassificationSchemeResult }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "GetClassificationSchemesResult", scope = GetClassificationSchemesResponse.class)
    public JAXBElement<ArrayOfNrtClassificationSchemeResult> createGetClassificationSchemesResponseGetClassificationSchemesResult(ArrayOfNrtClassificationSchemeResult value) {
        return new JAXBElement<ArrayOfNrtClassificationSchemeResult>(_GetClassificationSchemesResponseGetClassificationSchemesResult_QNAME, ArrayOfNrtClassificationSchemeResult.class, GetClassificationSchemesResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TrainingComponentSearchResult }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "SearchResult", scope = SearchResponse.class)
    public JAXBElement<TrainingComponentSearchResult> createSearchResponseSearchResult(TrainingComponentSearchResult value) {
        return new JAXBElement<TrainingComponentSearchResult>(_SearchResult_QNAME, TrainingComponentSearchResult.class, SearchResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TrainingComponent }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "GetDetailsResult", scope = GetDetailsResponse.class)
    public JAXBElement<TrainingComponent> createGetDetailsResponseGetDetailsResult(TrainingComponent value) {
        return new JAXBElement<TrainingComponent>(_GetDetailsResponseGetDetailsResult_QNAME, TrainingComponent.class, GetDetailsResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TrainingComponentTypeFilter }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "TrainingComponentTypes", scope = TrainingComponentSearchRequest.class)
    public JAXBElement<TrainingComponentTypeFilter> createTrainingComponentSearchRequestTrainingComponentTypes(TrainingComponentTypeFilter value) {
        return new JAXBElement<TrainingComponentTypeFilter>(_TrainingComponentTypes_QNAME, TrainingComponentTypeFilter.class, TrainingComponentSearchRequest.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ClassificationFilters }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "ClassificationFilters", scope = TrainingComponentSearchRequest.class)
    public JAXBElement<ClassificationFilters> createTrainingComponentSearchRequestClassificationFilters(ClassificationFilters value) {
        return new JAXBElement<ClassificationFilters>(_ClassificationFilters_QNAME, ClassificationFilters.class, TrainingComponentSearchRequest.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "EndReasonCode", scope = NrtCurrencyPeriod2 .class)
    public JAXBElement<String> createNrtCurrencyPeriod2EndReasonCode(String value) {
        return new JAXBElement<String>(_NrtCurrencyPeriod2EndReasonCode_QNAME, String.class, NrtCurrencyPeriod2 .class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "EndComment", scope = NrtCurrencyPeriod2 .class)
    public JAXBElement<String> createNrtCurrencyPeriod2EndComment(String value) {
        return new JAXBElement<String>(_NrtCurrencyPeriod2EndComment_QNAME, String.class, NrtCurrencyPeriod2 .class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "CountryCode", scope = Address.class)
    public JAXBElement<String> createAddressCountryCode(String value) {
        return new JAXBElement<String>(_AddressCountryCode_QNAME, String.class, Address.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "Suburb", scope = Address.class)
    public JAXBElement<String> createAddressSuburb(String value) {
        return new JAXBElement<String>(_AddressSuburb_QNAME, String.class, Address.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "StateOverseas", scope = Address.class)
    public JAXBElement<String> createAddressStateOverseas(String value) {
        return new JAXBElement<String>(_AddressStateOverseas_QNAME, String.class, Address.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "StateCode", scope = Address.class)
    public JAXBElement<String> createAddressStateCode(String value) {
        return new JAXBElement<String>(_AddressStateCode_QNAME, String.class, Address.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "Line1", scope = Address.class)
    public JAXBElement<String> createAddressLine1(String value) {
        return new JAXBElement<String>(_AddressLine1_QNAME, String.class, Address.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "Line2", scope = Address.class)
    public JAXBElement<String> createAddressLine2(String value) {
        return new JAXBElement<String>(_AddressLine2_QNAME, String.class, Address.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "Postcode", scope = Address.class)
    public JAXBElement<String> createAddressPostcode(String value) {
        return new JAXBElement<String>(_AddressPostcode_QNAME, String.class, Address.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "Code", scope = DataManagerAssignment.class)
    public JAXBElement<String> createDataManagerAssignmentCode(String value) {
        return new JAXBElement<String>(_UnitGridEntryCode_QNAME, String.class, DataManagerAssignment.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfReleaseComponent }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "Components", scope = Release.class)
    public JAXBElement<ArrayOfReleaseComponent> createReleaseComponents(ArrayOfReleaseComponent value) {
        return new JAXBElement<ArrayOfReleaseComponent>(_ReleaseComponents_QNAME, ArrayOfReleaseComponent.class, Release.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "NqcEndorsementDate", scope = Release.class)
    public JAXBElement<XMLGregorianCalendar> createReleaseNqcEndorsementDate(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_ReleaseNqcEndorsementDate_QNAME, XMLGregorianCalendar.class, Release.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "MinisterialAgreementDate", scope = Release.class)
    public JAXBElement<XMLGregorianCalendar> createReleaseMinisterialAgreementDate(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_ReleaseMinisterialAgreementDate_QNAME, XMLGregorianCalendar.class, Release.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "IscApprovalDate", scope = Release.class)
    public JAXBElement<XMLGregorianCalendar> createReleaseIscApprovalDate(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_ReleaseIscApprovalDate_QNAME, XMLGregorianCalendar.class, Release.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "Currency", scope = Release.class)
    public JAXBElement<String> createReleaseCurrency(String value) {
        return new JAXBElement<String>(_ReleaseCurrency_QNAME, String.class, Release.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "ApprovalProcess", scope = Release.class)
    public JAXBElement<String> createReleaseApprovalProcess(String value) {
        return new JAXBElement<String>(_ReleaseApprovalProcess_QNAME, String.class, Release.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfUnitGridEntry }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "UnitGrid", scope = Release.class)
    public JAXBElement<ArrayOfUnitGridEntry> createReleaseUnitGrid(ArrayOfUnitGridEntry value) {
        return new JAXBElement<ArrayOfUnitGridEntry>(_ReleaseUnitGrid_QNAME, ArrayOfUnitGridEntry.class, Release.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "ReleaseNumber", scope = Release.class)
    public JAXBElement<String> createReleaseReleaseNumber(String value) {
        return new JAXBElement<String>(_ReleaseComponentReleaseNumber_QNAME, String.class, Release.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfReleaseFile }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "Files", scope = Release.class)
    public JAXBElement<ArrayOfReleaseFile> createReleaseFiles(ArrayOfReleaseFile value) {
        return new JAXBElement<ArrayOfReleaseFile>(_ReleaseFiles_QNAME, ArrayOfReleaseFile.class, Release.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TrainingComponentSearchResult }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "SearchByModifiedDateResult", scope = SearchByModifiedDateResponse.class)
    public JAXBElement<TrainingComponentSearchResult> createSearchByModifiedDateResponseSearchByModifiedDateResult(TrainingComponentSearchResult value) {
        return new JAXBElement<TrainingComponentSearchResult>(_SearchByModifiedDateResponseSearchByModifiedDateResult_QNAME, TrainingComponentSearchResult.class, SearchByModifiedDateResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "Code", scope = RecognitionManagerAssignment.class)
    public JAXBElement<String> createRecognitionManagerAssignmentCode(String value) {
        return new JAXBElement<String>(_UnitGridEntryCode_QNAME, String.class, RecognitionManagerAssignment.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DeletedSearchRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "request", scope = SearchDeletedByDeletedDate.class)
    public JAXBElement<DeletedSearchRequest> createSearchDeletedByDeletedDateRequest(DeletedSearchRequest value) {
        return new JAXBElement<DeletedSearchRequest>(_GetDetailsRequest_QNAME, DeletedSearchRequest.class, SearchDeletedByDeletedDate.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TrainingComponentUpdateRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "request", scope = Update.class)
    public JAXBElement<TrainingComponentUpdateRequest> createUpdateRequest(TrainingComponentUpdateRequest value) {
        return new JAXBElement<TrainingComponentUpdateRequest>(_GetDetailsRequest_QNAME, TrainingComponentUpdateRequest.class, Update.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "Title", scope = TrainingComponentSummary.class)
    public JAXBElement<String> createTrainingComponentSummaryTitle(String value) {
        return new JAXBElement<String>(_TrainingComponentUpdateRequestTitle_QNAME, String.class, TrainingComponentSummary.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boolean }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "IsCurrent", scope = TrainingComponentSummary.class)
    public JAXBElement<Boolean> createTrainingComponentSummaryIsCurrent(Boolean value) {
        return new JAXBElement<Boolean>(_TrainingComponentSummaryIsCurrent_QNAME, Boolean.class, TrainingComponentSummary.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "Code", scope = TrainingComponentSummary.class)
    public JAXBElement<String> createTrainingComponentSummaryCode(String value) {
        return new JAXBElement<String>(_UnitGridEntryCode_QNAME, String.class, TrainingComponentSummary.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TrainingComponentTransferDataManagerRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "request", scope = TransferDataManager.class)
    public JAXBElement<TrainingComponentTransferDataManagerRequest> createTransferDataManagerRequest(TrainingComponentTransferDataManagerRequest value) {
        return new JAXBElement<TrainingComponentTransferDataManagerRequest>(_GetDetailsRequest_QNAME, TrainingComponentTransferDataManagerRequest.class, TransferDataManager.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfReleaseCompanionVolumeLink }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "CompanionVolumeLinks", scope = Release4 .class)
    public JAXBElement<ArrayOfReleaseCompanionVolumeLink> createRelease4CompanionVolumeLinks(ArrayOfReleaseCompanionVolumeLink value) {
        return new JAXBElement<ArrayOfReleaseCompanionVolumeLink>(_Release4CompanionVolumeLinks_QNAME, ArrayOfReleaseCompanionVolumeLink.class, Release4 .class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfstring }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "RegistrationManagers", scope = OrganisationScopeSearchRequest.class)
    public JAXBElement<ArrayOfstring> createOrganisationScopeSearchRequestRegistrationManagers(ArrayOfstring value) {
        return new JAXBElement<ArrayOfstring>(_OrganisationNameSearchRequestRegistrationManagers_QNAME, ArrayOfstring.class, OrganisationScopeSearchRequest.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfValidationError }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "Errors", scope = ValidationFault.class)
    public JAXBElement<ArrayOfValidationError> createValidationFaultErrors(ArrayOfValidationError value) {
        return new JAXBElement<ArrayOfValidationError>(_ValidationFaultErrors_QNAME, ArrayOfValidationError.class, ValidationFault.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfTrainingComponentContactRole }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://training.gov.au/services/", name = "GetContactRolesResult", scope = GetContactRolesResponse.class)
    public JAXBElement<ArrayOfTrainingComponentContactRole> createGetContactRolesResponseGetContactRolesResult(ArrayOfTrainingComponentContactRole value) {
        return new JAXBElement<ArrayOfTrainingComponentContactRole>(_GetContactRolesResponseGetContactRolesResult_QNAME, ArrayOfTrainingComponentContactRole.class, GetContactRolesResponse.class, value);
    }

}
