
package ish.oncourse.webservices.v6.stubs.reference;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;
import ish.oncourse.webservices.util.GenericReferenceResult;


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
 *           &lt;element ref="{http://ref.v6.soap.webservices.oncourse.ish/}country"/>
 *           &lt;element ref="{http://ref.v6.soap.webservices.oncourse.ish/}language"/>
 *           &lt;element ref="{http://ref.v6.soap.webservices.oncourse.ish/}module"/>
 *           &lt;element ref="{http://ref.v6.soap.webservices.oncourse.ish/}trainingPackage"/>
 *           &lt;element ref="{http://ref.v6.soap.webservices.oncourse.ish/}qualification"/>
 *           &lt;element ref="{http://ref.v6.soap.webservices.oncourse.ish/}postcode"/>
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
public class ReferenceResult
    extends GenericReferenceResult
{

    @XmlElements({
        @XmlElement(name = "language", namespace = "http://ref.v6.soap.webservices.oncourse.ish/", type = LanguageStub.class),
        @XmlElement(name = "country", namespace = "http://ref.v6.soap.webservices.oncourse.ish/", type = CountryStub.class),
        @XmlElement(name = "qualification", namespace = "http://ref.v6.soap.webservices.oncourse.ish/", type = QualificationStub.class),
        @XmlElement(name = "postcode", namespace = "http://ref.v6.soap.webservices.oncourse.ish/", type = PostcodeStub.class),
        @XmlElement(name = "module", namespace = "http://ref.v6.soap.webservices.oncourse.ish/", type = ModuleStub.class),
        @XmlElement(name = "trainingPackage", namespace = "http://ref.v6.soap.webservices.oncourse.ish/", type = TrainingPackageStub.class)
    })
    protected List<ReferenceStub> countryOrLanguageOrModule;

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
     * {@link LanguageStub }
     * {@link CountryStub }
     * {@link QualificationStub }
     * {@link PostcodeStub }
     * {@link ModuleStub }
     * {@link TrainingPackageStub }
     * 
     * 
     */
    public List<ReferenceStub> getCountryOrLanguageOrModule() {
        if (countryOrLanguageOrModule == null) {
            countryOrLanguageOrModule = new ArrayList<ReferenceStub>();
        }
        return this.countryOrLanguageOrModule;
    }

}
