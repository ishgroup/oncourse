
package ish.oncourse.webservices.v7.stubs.reference;

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
 * &lt;complexType name="referenceResult"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;choice maxOccurs="unbounded" minOccurs="0"&gt;
 *           &lt;element ref="{http://ref.v7.soap.webservices.oncourse.ish/}country"/&gt;
 *           &lt;element ref="{http://ref.v7.soap.webservices.oncourse.ish/}language"/&gt;
 *           &lt;element ref="{http://ref.v7.soap.webservices.oncourse.ish/}module"/&gt;
 *           &lt;element ref="{http://ref.v7.soap.webservices.oncourse.ish/}trainingPackage"/&gt;
 *           &lt;element ref="{http://ref.v7.soap.webservices.oncourse.ish/}qualification"/&gt;
 *           &lt;element ref="{http://ref.v7.soap.webservices.oncourse.ish/}postcode"/&gt;
 *         &lt;/choice&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
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
        @XmlElement(name = "country", namespace = "http://ref.v7.soap.webservices.oncourse.ish/", type = CountryStub.class),
        @XmlElement(name = "language", namespace = "http://ref.v7.soap.webservices.oncourse.ish/", type = LanguageStub.class),
        @XmlElement(name = "module", namespace = "http://ref.v7.soap.webservices.oncourse.ish/", type = ModuleStub.class),
        @XmlElement(name = "trainingPackage", namespace = "http://ref.v7.soap.webservices.oncourse.ish/", type = TrainingPackageStub.class),
        @XmlElement(name = "qualification", namespace = "http://ref.v7.soap.webservices.oncourse.ish/", type = QualificationStub.class),
        @XmlElement(name = "postcode", namespace = "http://ref.v7.soap.webservices.oncourse.ish/", type = PostcodeStub.class)
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
     * {@link CountryStub }
     * {@link LanguageStub }
     * {@link ModuleStub }
     * {@link TrainingPackageStub }
     * {@link QualificationStub }
     * {@link PostcodeStub }
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
