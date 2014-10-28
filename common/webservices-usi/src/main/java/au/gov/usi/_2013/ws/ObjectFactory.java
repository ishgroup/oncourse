
package au.gov.usi._2013.ws;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the au.gov.usi._2013.ws package. 
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

    private final static QName _ErrorInfo_QNAME = new QName("http://usi.gov.au/2013/ws", "ErrorInfo");
    private final static QName _BulkUploadResponse_QNAME = new QName("http://usi.gov.au/2013/ws", "BulkUploadResponse");
    private final static QName _BulkVerifyUSI_QNAME = new QName("http://usi.gov.au/2013/ws", "BulkVerifyUSI");
    private final static QName _CreateUSI_QNAME = new QName("http://usi.gov.au/2013/ws", "CreateUSI");
    private final static QName _BulkUploadRetrieve_QNAME = new QName("http://usi.gov.au/2013/ws", "BulkUploadRetrieve");
    private final static QName _VerifyUSI_QNAME = new QName("http://usi.gov.au/2013/ws", "VerifyUSI");
    private final static QName _BulkUploadRetrieveResponse_QNAME = new QName("http://usi.gov.au/2013/ws", "BulkUploadRetrieveResponse");
    private final static QName _CreateUSIResponse_QNAME = new QName("http://usi.gov.au/2013/ws", "CreateUSIResponse");
    private final static QName _BulkVerifyUSIResponse_QNAME = new QName("http://usi.gov.au/2013/ws", "BulkVerifyUSIResponse");
    private final static QName _BulkUpload_QNAME = new QName("http://usi.gov.au/2013/ws", "BulkUpload");
    private final static QName _VerifyUSIResponse_QNAME = new QName("http://usi.gov.au/2013/ws", "VerifyUSIResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: au.gov.usi._2013.ws
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link CreateUSIType }
     * 
     */
    public CreateUSIType createCreateUSIType() {
        return new CreateUSIType();
    }

    /**
     * Create an instance of {@link BulkUploadRetrieveResponseType.Applications }
     * 
     */
    public BulkUploadRetrieveResponseType.Applications createBulkUploadRetrieveResponseTypeApplications() {
        return new BulkUploadRetrieveResponseType.Applications();
    }

    /**
     * Create an instance of {@link BulkUploadRetrieveResponseType }
     * 
     */
    public BulkUploadRetrieveResponseType createBulkUploadRetrieveResponseType() {
        return new BulkUploadRetrieveResponseType();
    }

    /**
     * Create an instance of {@link ApplicationType }
     * 
     */
    public ApplicationType createApplicationType() {
        return new ApplicationType();
    }

    /**
     * Create an instance of {@link ApplicationResponseType }
     * 
     */
    public ApplicationResponseType createApplicationResponseType() {
        return new ApplicationResponseType();
    }

    /**
     * Create an instance of {@link VerifyUSIType }
     * 
     */
    public VerifyUSIType createVerifyUSIType() {
        return new VerifyUSIType();
    }

    /**
     * Create an instance of {@link MedicareDocumentType }
     * 
     */
    public MedicareDocumentType createMedicareDocumentType() {
        return new MedicareDocumentType();
    }

    /**
     * Create an instance of {@link BulkUploadRetrieveType }
     * 
     */
    public BulkUploadRetrieveType createBulkUploadRetrieveType() {
        return new BulkUploadRetrieveType();
    }

    /**
     * Create an instance of {@link PassportDocumentType }
     * 
     */
    public PassportDocumentType createPassportDocumentType() {
        return new PassportDocumentType();
    }

    /**
     * Create an instance of {@link DriversLicenceDocumentType }
     * 
     */
    public DriversLicenceDocumentType createDriversLicenceDocumentType() {
        return new DriversLicenceDocumentType();
    }

    /**
     * Create an instance of {@link NationalAddressType }
     * 
     */
    public NationalAddressType createNationalAddressType() {
        return new NationalAddressType();
    }

    /**
     * Create an instance of {@link CreateUSIResponseType }
     * 
     */
    public CreateUSIResponseType createCreateUSIResponseType() {
        return new CreateUSIResponseType();
    }

    /**
     * Create an instance of {@link PersonalDetailsType }
     * 
     */
    public PersonalDetailsType createPersonalDetailsType() {
        return new PersonalDetailsType();
    }

    /**
     * Create an instance of {@link BulkVerifyUSIResponseType }
     * 
     */
    public BulkVerifyUSIResponseType createBulkVerifyUSIResponseType() {
        return new BulkVerifyUSIResponseType();
    }

    /**
     * Create an instance of {@link CitizenshipCertificateDocumentType }
     * 
     */
    public CitizenshipCertificateDocumentType createCitizenshipCertificateDocumentType() {
        return new CitizenshipCertificateDocumentType();
    }

    /**
     * Create an instance of {@link ErrorType }
     * 
     */
    public ErrorType createErrorType() {
        return new ErrorType();
    }

    /**
     * Create an instance of {@link PhoneType }
     * 
     */
    public PhoneType createPhoneType() {
        return new PhoneType();
    }

    /**
     * Create an instance of {@link VerificationType }
     * 
     */
    public VerificationType createVerificationType() {
        return new VerificationType();
    }

    /**
     * Create an instance of {@link BulkVerifyUSIResponseType.VerificationResponses }
     * 
     */
    public BulkVerifyUSIResponseType.VerificationResponses createBulkVerifyUSIResponseTypeVerificationResponses() {
        return new BulkVerifyUSIResponseType.VerificationResponses();
    }

    /**
     * Create an instance of {@link CertificateOfRegistrationByDescentDocumentType }
     * 
     */
    public CertificateOfRegistrationByDescentDocumentType createCertificateOfRegistrationByDescentDocumentType() {
        return new CertificateOfRegistrationByDescentDocumentType();
    }

    /**
     * Create an instance of {@link BulkVerifyUSIType.Verifications }
     * 
     */
    public BulkVerifyUSIType.Verifications createBulkVerifyUSITypeVerifications() {
        return new BulkVerifyUSIType.Verifications();
    }

    /**
     * Create an instance of {@link VerifyUSIResponseType }
     * 
     */
    public VerifyUSIResponseType createVerifyUSIResponseType() {
        return new VerifyUSIResponseType();
    }

    /**
     * Create an instance of {@link ApplicationResponseType.Errors }
     * 
     */
    public ApplicationResponseType.Errors createApplicationResponseTypeErrors() {
        return new ApplicationResponseType.Errors();
    }

    /**
     * Create an instance of {@link BulkUploadResponseType }
     * 
     */
    public BulkUploadResponseType createBulkUploadResponseType() {
        return new BulkUploadResponseType();
    }

    /**
     * Create an instance of {@link BulkUploadType.Applications }
     * 
     */
    public BulkUploadType.Applications createBulkUploadTypeApplications() {
        return new BulkUploadType.Applications();
    }

    /**
     * Create an instance of {@link VerificationResponseType }
     * 
     */
    public VerificationResponseType createVerificationResponseType() {
        return new VerificationResponseType();
    }

    /**
     * Create an instance of {@link BirthCertificateDocumentType }
     * 
     */
    public BirthCertificateDocumentType createBirthCertificateDocumentType() {
        return new BirthCertificateDocumentType();
    }

    /**
     * Create an instance of {@link ImmiCardDocumentType }
     * 
     */
    public ImmiCardDocumentType createImmiCardDocumentType() {
        return new ImmiCardDocumentType();
    }

    /**
     * Create an instance of {@link ErrorInfoType }
     * 
     */
    public ErrorInfoType createErrorInfoType() {
        return new ErrorInfoType();
    }

    /**
     * Create an instance of {@link BulkVerifyUSIType }
     * 
     */
    public BulkVerifyUSIType createBulkVerifyUSIType() {
        return new BulkVerifyUSIType();
    }

    /**
     * Create an instance of {@link ContactDetailsType }
     * 
     */
    public ContactDetailsType createContactDetailsType() {
        return new ContactDetailsType();
    }

    /**
     * Create an instance of {@link VisaDocumentType }
     * 
     */
    public VisaDocumentType createVisaDocumentType() {
        return new VisaDocumentType();
    }

    /**
     * Create an instance of {@link BulkUploadType }
     * 
     */
    public BulkUploadType createBulkUploadType() {
        return new BulkUploadType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ErrorInfoType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://usi.gov.au/2013/ws", name = "ErrorInfo")
    public JAXBElement<ErrorInfoType> createErrorInfo(ErrorInfoType value) {
        return new JAXBElement<ErrorInfoType>(_ErrorInfo_QNAME, ErrorInfoType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BulkUploadResponseType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://usi.gov.au/2013/ws", name = "BulkUploadResponse")
    public JAXBElement<BulkUploadResponseType> createBulkUploadResponse(BulkUploadResponseType value) {
        return new JAXBElement<BulkUploadResponseType>(_BulkUploadResponse_QNAME, BulkUploadResponseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BulkVerifyUSIType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://usi.gov.au/2013/ws", name = "BulkVerifyUSI")
    public JAXBElement<BulkVerifyUSIType> createBulkVerifyUSI(BulkVerifyUSIType value) {
        return new JAXBElement<BulkVerifyUSIType>(_BulkVerifyUSI_QNAME, BulkVerifyUSIType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CreateUSIType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://usi.gov.au/2013/ws", name = "CreateUSI")
    public JAXBElement<CreateUSIType> createCreateUSI(CreateUSIType value) {
        return new JAXBElement<CreateUSIType>(_CreateUSI_QNAME, CreateUSIType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BulkUploadRetrieveType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://usi.gov.au/2013/ws", name = "BulkUploadRetrieve")
    public JAXBElement<BulkUploadRetrieveType> createBulkUploadRetrieve(BulkUploadRetrieveType value) {
        return new JAXBElement<BulkUploadRetrieveType>(_BulkUploadRetrieve_QNAME, BulkUploadRetrieveType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link VerifyUSIType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://usi.gov.au/2013/ws", name = "VerifyUSI")
    public JAXBElement<VerifyUSIType> createVerifyUSI(VerifyUSIType value) {
        return new JAXBElement<VerifyUSIType>(_VerifyUSI_QNAME, VerifyUSIType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BulkUploadRetrieveResponseType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://usi.gov.au/2013/ws", name = "BulkUploadRetrieveResponse")
    public JAXBElement<BulkUploadRetrieveResponseType> createBulkUploadRetrieveResponse(BulkUploadRetrieveResponseType value) {
        return new JAXBElement<BulkUploadRetrieveResponseType>(_BulkUploadRetrieveResponse_QNAME, BulkUploadRetrieveResponseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CreateUSIResponseType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://usi.gov.au/2013/ws", name = "CreateUSIResponse")
    public JAXBElement<CreateUSIResponseType> createCreateUSIResponse(CreateUSIResponseType value) {
        return new JAXBElement<CreateUSIResponseType>(_CreateUSIResponse_QNAME, CreateUSIResponseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BulkVerifyUSIResponseType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://usi.gov.au/2013/ws", name = "BulkVerifyUSIResponse")
    public JAXBElement<BulkVerifyUSIResponseType> createBulkVerifyUSIResponse(BulkVerifyUSIResponseType value) {
        return new JAXBElement<BulkVerifyUSIResponseType>(_BulkVerifyUSIResponse_QNAME, BulkVerifyUSIResponseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BulkUploadType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://usi.gov.au/2013/ws", name = "BulkUpload")
    public JAXBElement<BulkUploadType> createBulkUpload(BulkUploadType value) {
        return new JAXBElement<BulkUploadType>(_BulkUpload_QNAME, BulkUploadType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link VerifyUSIResponseType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://usi.gov.au/2013/ws", name = "VerifyUSIResponse")
    public JAXBElement<VerifyUSIResponseType> createVerifyUSIResponse(VerifyUSIResponseType value) {
        return new JAXBElement<VerifyUSIResponseType>(_VerifyUSIResponse_QNAME, VerifyUSIResponseType.class, null, value);
    }

}
