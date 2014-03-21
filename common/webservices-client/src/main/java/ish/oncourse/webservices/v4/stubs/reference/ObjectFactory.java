
package ish.oncourse.webservices.v4.stubs.reference;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the ish.oncourse.webservices.v4.stubs.reference package. 
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

    private final static QName _TrainingPackage_QNAME = new QName("http://ref.v4.soap.webservices.oncourse.ish/", "trainingPackage");
    private final static QName _Module_QNAME = new QName("http://ref.v4.soap.webservices.oncourse.ish/", "module");
    private final static QName _Qualification_QNAME = new QName("http://ref.v4.soap.webservices.oncourse.ish/", "qualification");
    private final static QName _Country_QNAME = new QName("http://ref.v4.soap.webservices.oncourse.ish/", "country");
    private final static QName _ReferenceResult_QNAME = new QName("http://ref.v4.soap.webservices.oncourse.ish/", "referenceResult");
    private final static QName _Language_QNAME = new QName("http://ref.v4.soap.webservices.oncourse.ish/", "language");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: ish.oncourse.webservices.v4.stubs.reference
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ModuleStub }
     * 
     */
    public ModuleStub createModuleStub() {
        return new ModuleStub();
    }

    /**
     * Create an instance of {@link CountryStub }
     * 
     */
    public CountryStub createCountryStub() {
        return new CountryStub();
    }

    /**
     * Create an instance of {@link ReferenceResult }
     * 
     */
    public ReferenceResult createReferenceResult() {
        return new ReferenceResult();
    }

    /**
     * Create an instance of {@link QualificationStub }
     * 
     */
    public QualificationStub createQualificationStub() {
        return new QualificationStub();
    }

    /**
     * Create an instance of {@link LanguageStub }
     * 
     */
    public LanguageStub createLanguageStub() {
        return new LanguageStub();
    }

    /**
     * Create an instance of {@link TrainingPackageStub }
     * 
     */
    public TrainingPackageStub createTrainingPackageStub() {
        return new TrainingPackageStub();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TrainingPackageStub }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ref.v4.soap.webservices.oncourse.ish/", name = "trainingPackage")
    public JAXBElement<TrainingPackageStub> createTrainingPackage(TrainingPackageStub value) {
        return new JAXBElement<>(_TrainingPackage_QNAME, TrainingPackageStub.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ModuleStub }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ref.v4.soap.webservices.oncourse.ish/", name = "module")
    public JAXBElement<ModuleStub> createModule(ModuleStub value) {
        return new JAXBElement<>(_Module_QNAME, ModuleStub.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link QualificationStub }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ref.v4.soap.webservices.oncourse.ish/", name = "qualification")
    public JAXBElement<QualificationStub> createQualification(QualificationStub value) {
        return new JAXBElement<>(_Qualification_QNAME, QualificationStub.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CountryStub }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ref.v4.soap.webservices.oncourse.ish/", name = "country")
    public JAXBElement<CountryStub> createCountry(CountryStub value) {
        return new JAXBElement<>(_Country_QNAME, CountryStub.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ReferenceResult }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ref.v4.soap.webservices.oncourse.ish/", name = "referenceResult")
    public JAXBElement<ReferenceResult> createReferenceResult(ReferenceResult value) {
        return new JAXBElement<>(_ReferenceResult_QNAME, ReferenceResult.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link LanguageStub }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ref.v4.soap.webservices.oncourse.ish/", name = "language")
    public JAXBElement<LanguageStub> createLanguage(LanguageStub value) {
        return new JAXBElement<>(_Language_QNAME, LanguageStub.class, null, value);
    }

}
