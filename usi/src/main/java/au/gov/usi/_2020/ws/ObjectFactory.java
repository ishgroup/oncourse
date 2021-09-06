
package au.gov.usi._2020.ws;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the au.gov.usi._2020.ws package. 
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

    private final static QName _BulkUpload_QNAME = new QName("http://usi.gov.au/2020/ws", "BulkUpload");
    private final static QName _BulkUploadResponse_QNAME = new QName("http://usi.gov.au/2020/ws", "BulkUploadResponse");
    private final static QName _BulkUploadRetrieve_QNAME = new QName("http://usi.gov.au/2020/ws", "BulkUploadRetrieve");
    private final static QName _BulkUploadRetrieveResponse_QNAME = new QName("http://usi.gov.au/2020/ws", "BulkUploadRetrieveResponse");
    private final static QName _CreateUSI_QNAME = new QName("http://usi.gov.au/2020/ws", "CreateUSI");
    private final static QName _CreateUSIResponse_QNAME = new QName("http://usi.gov.au/2020/ws", "CreateUSIResponse");
    private final static QName _VerifyUSI_QNAME = new QName("http://usi.gov.au/2020/ws", "VerifyUSI");
    private final static QName _VerifyUSIResponse_QNAME = new QName("http://usi.gov.au/2020/ws", "VerifyUSIResponse");
    private final static QName _BulkVerifyUSI_QNAME = new QName("http://usi.gov.au/2020/ws", "BulkVerifyUSI");
    private final static QName _BulkVerifyUSIResponse_QNAME = new QName("http://usi.gov.au/2020/ws", "BulkVerifyUSIResponse");
    private final static QName _GetNonDvsDocumentTypesResponse_QNAME = new QName("http://usi.gov.au/2020/ws", "GetNonDvsDocumentTypesResponse");
    private final static QName _GetNonDvsDocumentTypes_QNAME = new QName("http://usi.gov.au/2020/ws", "GetNonDvsDocumentTypes");
    private final static QName _UpdateUSIContactDetails_QNAME = new QName("http://usi.gov.au/2020/ws", "UpdateUSIContactDetails");
    private final static QName _UpdateUSIContactDetailsResponse_QNAME = new QName("http://usi.gov.au/2020/ws", "UpdateUSIContactDetailsResponse");
    private final static QName _UpdateUSIPersonalDetails_QNAME = new QName("http://usi.gov.au/2020/ws", "UpdateUSIPersonalDetails");
    private final static QName _UpdateUSIPersonalDetailsResponse_QNAME = new QName("http://usi.gov.au/2020/ws", "UpdateUSIPersonalDetailsResponse");
    private final static QName _LocateUSI_QNAME = new QName("http://usi.gov.au/2020/ws", "LocateUSI");
    private final static QName _LocateUSIResponse_QNAME = new QName("http://usi.gov.au/2020/ws", "LocateUSIResponse");
    private final static QName _GetCountries_QNAME = new QName("http://usi.gov.au/2020/ws", "GetCountries");
    private final static QName _GetCountriesResponse_QNAME = new QName("http://usi.gov.au/2020/ws", "GetCountriesResponse");
    private final static QName _ErrorInfo_QNAME = new QName("http://usi.gov.au/2020/ws", "ErrorInfo");
    private final static QName _ArrayOfErrorInfo_QNAME = new QName("http://usi.gov.au/2020/ws", "ArrayOfErrorInfo");
    private final static QName _ApplicationTypeNonDvsDocumentTypeId_QNAME = new QName("http://usi.gov.au/2020/ws", "NonDvsDocumentTypeId");
    private final static QName _ApplicationTypeNonDvsDocumentTypeOther_QNAME = new QName("http://usi.gov.au/2020/ws", "NonDvsDocumentTypeOther");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: au.gov.usi._2020.ws
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ApplicationResponseType }
     * 
     */
    public ApplicationResponseType createApplicationResponseType() {
        return new ApplicationResponseType();
    }

    /**
     * Create an instance of {@link GetCountriesResponseType }
     * 
     */
    public GetCountriesResponseType createGetCountriesResponseType() {
        return new GetCountriesResponseType();
    }

    /**
     * Create an instance of {@link LocateUSIResponseType }
     * 
     */
    public LocateUSIResponseType createLocateUSIResponseType() {
        return new LocateUSIResponseType();
    }

    /**
     * Create an instance of {@link UpdateUSIPersonalDetailsResponseType }
     * 
     */
    public UpdateUSIPersonalDetailsResponseType createUpdateUSIPersonalDetailsResponseType() {
        return new UpdateUSIPersonalDetailsResponseType();
    }

    /**
     * Create an instance of {@link UpdateUSIContactDetailsResponseType }
     * 
     */
    public UpdateUSIContactDetailsResponseType createUpdateUSIContactDetailsResponseType() {
        return new UpdateUSIContactDetailsResponseType();
    }

    /**
     * Create an instance of {@link GetNonDvsDocumentTypesResponseType }
     * 
     */
    public GetNonDvsDocumentTypesResponseType createGetNonDvsDocumentTypesResponseType() {
        return new GetNonDvsDocumentTypesResponseType();
    }

    /**
     * Create an instance of {@link BulkVerifyUSIResponseType }
     * 
     */
    public BulkVerifyUSIResponseType createBulkVerifyUSIResponseType() {
        return new BulkVerifyUSIResponseType();
    }

    /**
     * Create an instance of {@link BulkVerifyUSIType }
     * 
     */
    public BulkVerifyUSIType createBulkVerifyUSIType() {
        return new BulkVerifyUSIType();
    }

    /**
     * Create an instance of {@link BulkUploadRetrieveResponseType }
     * 
     */
    public BulkUploadRetrieveResponseType createBulkUploadRetrieveResponseType() {
        return new BulkUploadRetrieveResponseType();
    }

    /**
     * Create an instance of {@link BulkUploadType }
     * 
     */
    public BulkUploadType createBulkUploadType() {
        return new BulkUploadType();
    }

    /**
     * Create an instance of {@link BulkUploadResponseType }
     * 
     */
    public BulkUploadResponseType createBulkUploadResponseType() {
        return new BulkUploadResponseType();
    }

    /**
     * Create an instance of {@link BulkUploadRetrieveType }
     * 
     */
    public BulkUploadRetrieveType createBulkUploadRetrieveType() {
        return new BulkUploadRetrieveType();
    }

    /**
     * Create an instance of {@link CreateUSIType }
     * 
     */
    public CreateUSIType createCreateUSIType() {
        return new CreateUSIType();
    }

    /**
     * Create an instance of {@link CreateUSIResponseType }
     * 
     */
    public CreateUSIResponseType createCreateUSIResponseType() {
        return new CreateUSIResponseType();
    }

    /**
     * Create an instance of {@link VerifyUSIType }
     * 
     */
    public VerifyUSIType createVerifyUSIType() {
        return new VerifyUSIType();
    }

    /**
     * Create an instance of {@link VerifyUSIResponseType }
     * 
     */
    public VerifyUSIResponseType createVerifyUSIResponseType() {
        return new VerifyUSIResponseType();
    }

    /**
     * Create an instance of {@link GetNonDvsDocumentTypesType }
     * 
     */
    public GetNonDvsDocumentTypesType createGetNonDvsDocumentTypesType() {
        return new GetNonDvsDocumentTypesType();
    }

    /**
     * Create an instance of {@link UpdateUSIContactDetailsType }
     * 
     */
    public UpdateUSIContactDetailsType createUpdateUSIContactDetailsType() {
        return new UpdateUSIContactDetailsType();
    }

    /**
     * Create an instance of {@link UpdateUSIPersonalDetailsType }
     * 
     */
    public UpdateUSIPersonalDetailsType createUpdateUSIPersonalDetailsType() {
        return new UpdateUSIPersonalDetailsType();
    }

    /**
     * Create an instance of {@link LocateUSIType }
     * 
     */
    public LocateUSIType createLocateUSIType() {
        return new LocateUSIType();
    }

    /**
     * Create an instance of {@link GetCountriesType }
     * 
     */
    public GetCountriesType createGetCountriesType() {
        return new GetCountriesType();
    }

    /**
     * Create an instance of {@link ErrorInfo }
     * 
     */
    public ErrorInfo createErrorInfo() {
        return new ErrorInfo();
    }

    /**
     * Create an instance of {@link ArrayOfErrorInfo }
     * 
     */
    public ArrayOfErrorInfo createArrayOfErrorInfo() {
        return new ArrayOfErrorInfo();
    }

    /**
     * Create an instance of {@link NonDvsDocumentTypeType }
     * 
     */
    public NonDvsDocumentTypeType createNonDvsDocumentTypeType() {
        return new NonDvsDocumentTypeType();
    }

    /**
     * Create an instance of {@link CountryDetailsType }
     * 
     */
    public CountryDetailsType createCountryDetailsType() {
        return new CountryDetailsType();
    }

    /**
     * Create an instance of {@link ApplicationType }
     * 
     */
    public ApplicationType createApplicationType() {
        return new ApplicationType();
    }

    /**
     * Create an instance of {@link BirthCertificateDocumentType }
     * 
     */
    public BirthCertificateDocumentType createBirthCertificateDocumentType() {
        return new BirthCertificateDocumentType();
    }

    /**
     * Create an instance of {@link VerificationType }
     * 
     */
    public VerificationType createVerificationType() {
        return new VerificationType();
    }

    /**
     * Create an instance of {@link VerificationResponseType }
     * 
     */
    public VerificationResponseType createVerificationResponseType() {
        return new VerificationResponseType();
    }

    /**
     * Create an instance of {@link CertificateOfRegistrationByDescentDocumentType }
     * 
     */
    public CertificateOfRegistrationByDescentDocumentType createCertificateOfRegistrationByDescentDocumentType() {
        return new CertificateOfRegistrationByDescentDocumentType();
    }

    /**
     * Create an instance of {@link CitizenshipCertificateDocumentType }
     * 
     */
    public CitizenshipCertificateDocumentType createCitizenshipCertificateDocumentType() {
        return new CitizenshipCertificateDocumentType();
    }

    /**
     * Create an instance of {@link ContactDetailsLocateType }
     * 
     */
    public ContactDetailsLocateType createContactDetailsLocateType() {
        return new ContactDetailsLocateType();
    }

    /**
     * Create an instance of {@link ContactDetailsUpdateType }
     * 
     */
    public ContactDetailsUpdateType createContactDetailsUpdateType() {
        return new ContactDetailsUpdateType();
    }

    /**
     * Create an instance of {@link ContactDetailsType }
     * 
     */
    public ContactDetailsType createContactDetailsType() {
        return new ContactDetailsType();
    }

    /**
     * Create an instance of {@link DriversLicenceDocumentType }
     * 
     */
    public DriversLicenceDocumentType createDriversLicenceDocumentType() {
        return new DriversLicenceDocumentType();
    }

    /**
     * Create an instance of {@link ImmiCardDocumentType }
     * 
     */
    public ImmiCardDocumentType createImmiCardDocumentType() {
        return new ImmiCardDocumentType();
    }

    /**
     * Create an instance of {@link MedicareDocumentType }
     * 
     */
    public MedicareDocumentType createMedicareDocumentType() {
        return new MedicareDocumentType();
    }

    /**
     * Create an instance of {@link CentrelinkCardType }
     * 
     */
    public CentrelinkCardType createCentrelinkCardType() {
        return new CentrelinkCardType();
    }

    /**
     * Create an instance of {@link NationalAddressType }
     * 
     */
    public NationalAddressType createNationalAddressType() {
        return new NationalAddressType();
    }

    /**
     * Create an instance of {@link PassportDocumentType }
     * 
     */
    public PassportDocumentType createPassportDocumentType() {
        return new PassportDocumentType();
    }

    /**
     * Create an instance of {@link PersonalDetailsLocateType }
     * 
     */
    public PersonalDetailsLocateType createPersonalDetailsLocateType() {
        return new PersonalDetailsLocateType();
    }

    /**
     * Create an instance of {@link PersonalDetailsUpdateType }
     * 
     */
    public PersonalDetailsUpdateType createPersonalDetailsUpdateType() {
        return new PersonalDetailsUpdateType();
    }

    /**
     * Create an instance of {@link PersonalDetailsType }
     * 
     */
    public PersonalDetailsType createPersonalDetailsType() {
        return new PersonalDetailsType();
    }

    /**
     * Create an instance of {@link PhoneType }
     * 
     */
    public PhoneType createPhoneType() {
        return new PhoneType();
    }

    /**
     * Create an instance of {@link VisaDocumentType }
     * 
     */
    public VisaDocumentType createVisaDocumentType() {
        return new VisaDocumentType();
    }

    /**
     * Create an instance of {@link ErrorType }
     * 
     */
    public ErrorType createErrorType() {
        return new ErrorType();
    }

    /**
     * Create an instance of {@link ApplicationResponseType.Errors }
     * 
     */
    public ApplicationResponseType.Errors createApplicationResponseTypeErrors() {
        return new ApplicationResponseType.Errors();
    }

    /**
     * Create an instance of {@link GetCountriesResponseType.Countries }
     * 
     */
    public GetCountriesResponseType.Countries createGetCountriesResponseTypeCountries() {
        return new GetCountriesResponseType.Countries();
    }

    /**
     * Create an instance of {@link LocateUSIResponseType.Errors }
     * 
     */
    public LocateUSIResponseType.Errors createLocateUSIResponseTypeErrors() {
        return new LocateUSIResponseType.Errors();
    }

    /**
     * Create an instance of {@link UpdateUSIPersonalDetailsResponseType.Errors }
     * 
     */
    public UpdateUSIPersonalDetailsResponseType.Errors createUpdateUSIPersonalDetailsResponseTypeErrors() {
        return new UpdateUSIPersonalDetailsResponseType.Errors();
    }

    /**
     * Create an instance of {@link UpdateUSIContactDetailsResponseType.Errors }
     * 
     */
    public UpdateUSIContactDetailsResponseType.Errors createUpdateUSIContactDetailsResponseTypeErrors() {
        return new UpdateUSIContactDetailsResponseType.Errors();
    }

    /**
     * Create an instance of {@link GetNonDvsDocumentTypesResponseType.NonDvsDocumentTypes }
     * 
     */
    public GetNonDvsDocumentTypesResponseType.NonDvsDocumentTypes createGetNonDvsDocumentTypesResponseTypeNonDvsDocumentTypes() {
        return new GetNonDvsDocumentTypesResponseType.NonDvsDocumentTypes();
    }

    /**
     * Create an instance of {@link BulkVerifyUSIResponseType.VerificationResponses }
     * 
     */
    public BulkVerifyUSIResponseType.VerificationResponses createBulkVerifyUSIResponseTypeVerificationResponses() {
        return new BulkVerifyUSIResponseType.VerificationResponses();
    }

    /**
     * Create an instance of {@link BulkVerifyUSIType.Verifications }
     * 
     */
    public BulkVerifyUSIType.Verifications createBulkVerifyUSITypeVerifications() {
        return new BulkVerifyUSIType.Verifications();
    }

    /**
     * Create an instance of {@link BulkUploadRetrieveResponseType.Applications }
     * 
     */
    public BulkUploadRetrieveResponseType.Applications createBulkUploadRetrieveResponseTypeApplications() {
        return new BulkUploadRetrieveResponseType.Applications();
    }

    /**
     * Create an instance of {@link BulkUploadType.Applications }
     * 
     */
    public BulkUploadType.Applications createBulkUploadTypeApplications() {
        return new BulkUploadType.Applications();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BulkUploadType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link BulkUploadType }{@code >}
     */
    @XmlElementDecl(namespace = "http://usi.gov.au/2020/ws", name = "BulkUpload")
    public JAXBElement<BulkUploadType> createBulkUpload(BulkUploadType value) {
        return new JAXBElement<BulkUploadType>(_BulkUpload_QNAME, BulkUploadType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BulkUploadResponseType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link BulkUploadResponseType }{@code >}
     */
    @XmlElementDecl(namespace = "http://usi.gov.au/2020/ws", name = "BulkUploadResponse")
    public JAXBElement<BulkUploadResponseType> createBulkUploadResponse(BulkUploadResponseType value) {
        return new JAXBElement<BulkUploadResponseType>(_BulkUploadResponse_QNAME, BulkUploadResponseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BulkUploadRetrieveType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link BulkUploadRetrieveType }{@code >}
     */
    @XmlElementDecl(namespace = "http://usi.gov.au/2020/ws", name = "BulkUploadRetrieve")
    public JAXBElement<BulkUploadRetrieveType> createBulkUploadRetrieve(BulkUploadRetrieveType value) {
        return new JAXBElement<BulkUploadRetrieveType>(_BulkUploadRetrieve_QNAME, BulkUploadRetrieveType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BulkUploadRetrieveResponseType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link BulkUploadRetrieveResponseType }{@code >}
     */
    @XmlElementDecl(namespace = "http://usi.gov.au/2020/ws", name = "BulkUploadRetrieveResponse")
    public JAXBElement<BulkUploadRetrieveResponseType> createBulkUploadRetrieveResponse(BulkUploadRetrieveResponseType value) {
        return new JAXBElement<BulkUploadRetrieveResponseType>(_BulkUploadRetrieveResponse_QNAME, BulkUploadRetrieveResponseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CreateUSIType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link CreateUSIType }{@code >}
     */
    @XmlElementDecl(namespace = "http://usi.gov.au/2020/ws", name = "CreateUSI")
    public JAXBElement<CreateUSIType> createCreateUSI(CreateUSIType value) {
        return new JAXBElement<CreateUSIType>(_CreateUSI_QNAME, CreateUSIType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CreateUSIResponseType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link CreateUSIResponseType }{@code >}
     */
    @XmlElementDecl(namespace = "http://usi.gov.au/2020/ws", name = "CreateUSIResponse")
    public JAXBElement<CreateUSIResponseType> createCreateUSIResponse(CreateUSIResponseType value) {
        return new JAXBElement<CreateUSIResponseType>(_CreateUSIResponse_QNAME, CreateUSIResponseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link VerifyUSIType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link VerifyUSIType }{@code >}
     */
    @XmlElementDecl(namespace = "http://usi.gov.au/2020/ws", name = "VerifyUSI")
    public JAXBElement<VerifyUSIType> createVerifyUSI(VerifyUSIType value) {
        return new JAXBElement<VerifyUSIType>(_VerifyUSI_QNAME, VerifyUSIType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link VerifyUSIResponseType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link VerifyUSIResponseType }{@code >}
     */
    @XmlElementDecl(namespace = "http://usi.gov.au/2020/ws", name = "VerifyUSIResponse")
    public JAXBElement<VerifyUSIResponseType> createVerifyUSIResponse(VerifyUSIResponseType value) {
        return new JAXBElement<VerifyUSIResponseType>(_VerifyUSIResponse_QNAME, VerifyUSIResponseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BulkVerifyUSIType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link BulkVerifyUSIType }{@code >}
     */
    @XmlElementDecl(namespace = "http://usi.gov.au/2020/ws", name = "BulkVerifyUSI")
    public JAXBElement<BulkVerifyUSIType> createBulkVerifyUSI(BulkVerifyUSIType value) {
        return new JAXBElement<BulkVerifyUSIType>(_BulkVerifyUSI_QNAME, BulkVerifyUSIType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BulkVerifyUSIResponseType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link BulkVerifyUSIResponseType }{@code >}
     */
    @XmlElementDecl(namespace = "http://usi.gov.au/2020/ws", name = "BulkVerifyUSIResponse")
    public JAXBElement<BulkVerifyUSIResponseType> createBulkVerifyUSIResponse(BulkVerifyUSIResponseType value) {
        return new JAXBElement<BulkVerifyUSIResponseType>(_BulkVerifyUSIResponse_QNAME, BulkVerifyUSIResponseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetNonDvsDocumentTypesResponseType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link GetNonDvsDocumentTypesResponseType }{@code >}
     */
    @XmlElementDecl(namespace = "http://usi.gov.au/2020/ws", name = "GetNonDvsDocumentTypesResponse")
    public JAXBElement<GetNonDvsDocumentTypesResponseType> createGetNonDvsDocumentTypesResponse(GetNonDvsDocumentTypesResponseType value) {
        return new JAXBElement<GetNonDvsDocumentTypesResponseType>(_GetNonDvsDocumentTypesResponse_QNAME, GetNonDvsDocumentTypesResponseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetNonDvsDocumentTypesType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link GetNonDvsDocumentTypesType }{@code >}
     */
    @XmlElementDecl(namespace = "http://usi.gov.au/2020/ws", name = "GetNonDvsDocumentTypes")
    public JAXBElement<GetNonDvsDocumentTypesType> createGetNonDvsDocumentTypes(GetNonDvsDocumentTypesType value) {
        return new JAXBElement<GetNonDvsDocumentTypesType>(_GetNonDvsDocumentTypes_QNAME, GetNonDvsDocumentTypesType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UpdateUSIContactDetailsType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link UpdateUSIContactDetailsType }{@code >}
     */
    @XmlElementDecl(namespace = "http://usi.gov.au/2020/ws", name = "UpdateUSIContactDetails")
    public JAXBElement<UpdateUSIContactDetailsType> createUpdateUSIContactDetails(UpdateUSIContactDetailsType value) {
        return new JAXBElement<UpdateUSIContactDetailsType>(_UpdateUSIContactDetails_QNAME, UpdateUSIContactDetailsType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UpdateUSIContactDetailsResponseType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link UpdateUSIContactDetailsResponseType }{@code >}
     */
    @XmlElementDecl(namespace = "http://usi.gov.au/2020/ws", name = "UpdateUSIContactDetailsResponse")
    public JAXBElement<UpdateUSIContactDetailsResponseType> createUpdateUSIContactDetailsResponse(UpdateUSIContactDetailsResponseType value) {
        return new JAXBElement<UpdateUSIContactDetailsResponseType>(_UpdateUSIContactDetailsResponse_QNAME, UpdateUSIContactDetailsResponseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UpdateUSIPersonalDetailsType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link UpdateUSIPersonalDetailsType }{@code >}
     */
    @XmlElementDecl(namespace = "http://usi.gov.au/2020/ws", name = "UpdateUSIPersonalDetails")
    public JAXBElement<UpdateUSIPersonalDetailsType> createUpdateUSIPersonalDetails(UpdateUSIPersonalDetailsType value) {
        return new JAXBElement<UpdateUSIPersonalDetailsType>(_UpdateUSIPersonalDetails_QNAME, UpdateUSIPersonalDetailsType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UpdateUSIPersonalDetailsResponseType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link UpdateUSIPersonalDetailsResponseType }{@code >}
     */
    @XmlElementDecl(namespace = "http://usi.gov.au/2020/ws", name = "UpdateUSIPersonalDetailsResponse")
    public JAXBElement<UpdateUSIPersonalDetailsResponseType> createUpdateUSIPersonalDetailsResponse(UpdateUSIPersonalDetailsResponseType value) {
        return new JAXBElement<UpdateUSIPersonalDetailsResponseType>(_UpdateUSIPersonalDetailsResponse_QNAME, UpdateUSIPersonalDetailsResponseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link LocateUSIType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link LocateUSIType }{@code >}
     */
    @XmlElementDecl(namespace = "http://usi.gov.au/2020/ws", name = "LocateUSI")
    public JAXBElement<LocateUSIType> createLocateUSI(LocateUSIType value) {
        return new JAXBElement<LocateUSIType>(_LocateUSI_QNAME, LocateUSIType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link LocateUSIResponseType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link LocateUSIResponseType }{@code >}
     */
    @XmlElementDecl(namespace = "http://usi.gov.au/2020/ws", name = "LocateUSIResponse")
    public JAXBElement<LocateUSIResponseType> createLocateUSIResponse(LocateUSIResponseType value) {
        return new JAXBElement<LocateUSIResponseType>(_LocateUSIResponse_QNAME, LocateUSIResponseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetCountriesType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link GetCountriesType }{@code >}
     */
    @XmlElementDecl(namespace = "http://usi.gov.au/2020/ws", name = "GetCountries")
    public JAXBElement<GetCountriesType> createGetCountries(GetCountriesType value) {
        return new JAXBElement<GetCountriesType>(_GetCountries_QNAME, GetCountriesType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetCountriesResponseType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link GetCountriesResponseType }{@code >}
     */
    @XmlElementDecl(namespace = "http://usi.gov.au/2020/ws", name = "GetCountriesResponse")
    public JAXBElement<GetCountriesResponseType> createGetCountriesResponse(GetCountriesResponseType value) {
        return new JAXBElement<GetCountriesResponseType>(_GetCountriesResponse_QNAME, GetCountriesResponseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ErrorInfo }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link ErrorInfo }{@code >}
     */
    @XmlElementDecl(namespace = "http://usi.gov.au/2020/ws", name = "ErrorInfo")
    public JAXBElement<ErrorInfo> createErrorInfo(ErrorInfo value) {
        return new JAXBElement<ErrorInfo>(_ErrorInfo_QNAME, ErrorInfo.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfErrorInfo }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link ArrayOfErrorInfo }{@code >}
     */
    @XmlElementDecl(namespace = "http://usi.gov.au/2020/ws", name = "ArrayOfErrorInfo")
    public JAXBElement<ArrayOfErrorInfo> createArrayOfErrorInfo(ArrayOfErrorInfo value) {
        return new JAXBElement<ArrayOfErrorInfo>(_ArrayOfErrorInfo_QNAME, ArrayOfErrorInfo.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Integer }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Integer }{@code >}
     */
    @XmlElementDecl(namespace = "http://usi.gov.au/2020/ws", name = "NonDvsDocumentTypeId", scope = ApplicationType.class)
    public JAXBElement<Integer> createApplicationTypeNonDvsDocumentTypeId(Integer value) {
        return new JAXBElement<Integer>(_ApplicationTypeNonDvsDocumentTypeId_QNAME, Integer.class, ApplicationType.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "http://usi.gov.au/2020/ws", name = "NonDvsDocumentTypeOther", scope = ApplicationType.class)
    public JAXBElement<String> createApplicationTypeNonDvsDocumentTypeOther(String value) {
        return new JAXBElement<String>(_ApplicationTypeNonDvsDocumentTypeOther_QNAME, String.class, ApplicationType.class, value);
    }

}
