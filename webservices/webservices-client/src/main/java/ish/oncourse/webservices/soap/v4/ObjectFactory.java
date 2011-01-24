
package ish.oncourse.webservices.soap.v4;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the ish.oncourse.webservices.soap.v4 package. 
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

    private final static QName _GetRecords_QNAME = new QName("http://v4.soap.webservices.oncourse.ish/", "getRecords");
    private final static QName _GetRecordsResponse_QNAME = new QName("http://v4.soap.webservices.oncourse.ish/", "getRecordsResponse");
    private final static QName _GetMaximumVersionResponse_QNAME = new QName("http://v4.soap.webservices.oncourse.ish/", "getMaximumVersionResponse");
    private final static QName _GetMaximumVersion_QNAME = new QName("http://v4.soap.webservices.oncourse.ish/", "getMaximumVersion");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: ish.oncourse.webservices.soap.v4
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GetMaximumVersionResponse }
     * 
     */
    public GetMaximumVersionResponse createGetMaximumVersionResponse() {
        return new GetMaximumVersionResponse();
    }

    /**
     * Create an instance of {@link GetRecords }
     * 
     */
    public GetRecords createGetRecords() {
        return new GetRecords();
    }

    /**
     * Create an instance of {@link CountryStub }
     * 
     */
    public CountryStub createCountryStub() {
        return new CountryStub();
    }

    /**
     * Create an instance of {@link GetMaximumVersion }
     * 
     */
    public GetMaximumVersion createGetMaximumVersion() {
        return new GetMaximumVersion();
    }

    /**
     * Create an instance of {@link QualificationStub }
     * 
     */
    public QualificationStub createQualificationStub() {
        return new QualificationStub();
    }

    /**
     * Create an instance of {@link GetRecordsResponse }
     * 
     */
    public GetRecordsResponse createGetRecordsResponse() {
        return new GetRecordsResponse();
    }

    /**
     * Create an instance of {@link TrainingPackageStub }
     * 
     */
    public TrainingPackageStub createTrainingPackageStub() {
        return new TrainingPackageStub();
    }

    /**
     * Create an instance of {@link ModuleStub }
     * 
     */
    public ModuleStub createModuleStub() {
        return new ModuleStub();
    }

    /**
     * Create an instance of {@link LanguageStub }
     * 
     */
    public LanguageStub createLanguageStub() {
        return new LanguageStub();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetRecords }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://v4.soap.webservices.oncourse.ish/", name = "getRecords")
    public JAXBElement<GetRecords> createGetRecords(GetRecords value) {
        return new JAXBElement<GetRecords>(_GetRecords_QNAME, GetRecords.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetRecordsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://v4.soap.webservices.oncourse.ish/", name = "getRecordsResponse")
    public JAXBElement<GetRecordsResponse> createGetRecordsResponse(GetRecordsResponse value) {
        return new JAXBElement<GetRecordsResponse>(_GetRecordsResponse_QNAME, GetRecordsResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetMaximumVersionResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://v4.soap.webservices.oncourse.ish/", name = "getMaximumVersionResponse")
    public JAXBElement<GetMaximumVersionResponse> createGetMaximumVersionResponse(GetMaximumVersionResponse value) {
        return new JAXBElement<GetMaximumVersionResponse>(_GetMaximumVersionResponse_QNAME, GetMaximumVersionResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetMaximumVersion }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://v4.soap.webservices.oncourse.ish/", name = "getMaximumVersion")
    public JAXBElement<GetMaximumVersion> createGetMaximumVersion(GetMaximumVersion value) {
        return new JAXBElement<GetMaximumVersion>(_GetMaximumVersion_QNAME, GetMaximumVersion.class, null, value);
    }

}
