
package ish.oncourse.webservices.v4.stubs.reference;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for referenceResult complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="referenceResult">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice maxOccurs="unbounded" minOccurs="0">
 *           &lt;element ref="{http://ref.v4.soap.webservices.oncourse.ish/}country"/>
 *           &lt;element ref="{http://ref.v4.soap.webservices.oncourse.ish/}language"/>
 *           &lt;element ref="{http://ref.v4.soap.webservices.oncourse.ish/}module"/>
 *           &lt;element ref="{http://ref.v4.soap.webservices.oncourse.ish/}trainingPackage"/>
 *           &lt;element ref="{http://ref.v4.soap.webservices.oncourse.ish/}qualification"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "referenceResult", propOrder = {
    "countryOrLanguageOrModule"
})
public class ReferenceResult {

    @XmlElements({
        @XmlElement(name = "module", namespace = "http://ref.v4.soap.webservices.oncourse.ish/", type = ModuleStub.class),
        @XmlElement(name = "country", namespace = "http://ref.v4.soap.webservices.oncourse.ish/", type = CountryStub.class),
        @XmlElement(name = "qualification", namespace = "http://ref.v4.soap.webservices.oncourse.ish/", type = QualificationStub.class),
        @XmlElement(name = "language", namespace = "http://ref.v4.soap.webservices.oncourse.ish/", type = LanguageStub.class),
        @XmlElement(name = "trainingPackage", namespace = "http://ref.v4.soap.webservices.oncourse.ish/", type = TrainingPackageStub.class)
    })
    protected List<SoapReferenceStub> countryOrLanguageOrModule;

    /**
     * Gets the value of the countryOrLanguageOrModule property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the countryOrLanguageOrModule property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCountryOrLanguageOrModule().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ModuleStub }
     * {@link CountryStub }
     * {@link QualificationStub }
     * {@link LanguageStub }
     * {@link TrainingPackageStub }
     * 
     * 
     */
    public List<SoapReferenceStub> getCountryOrLanguageOrModule() {
        if (countryOrLanguageOrModule == null) {
            countryOrLanguageOrModule = new ArrayList<SoapReferenceStub>();
        }
        return this.countryOrLanguageOrModule;
    }

}
