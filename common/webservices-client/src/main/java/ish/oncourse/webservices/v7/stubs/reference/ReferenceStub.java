
package ish.oncourse.webservices.v7.stubs.reference;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import ish.oncourse.webservices.util.GenericReferenceStub;


/**
 * <p>Java class for referenceStub complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="referenceStub"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="willowId" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "referenceStub", propOrder = {
    "willowId"
})
@XmlSeeAlso({
    CountryStub.class,
    LanguageStub.class,
    ModuleStub.class,
    QualificationStub.class,
    TrainingPackageStub.class,
    PostcodeStub.class
})
public abstract class ReferenceStub implements GenericReferenceStub
{

    @XmlElement(type = String.class)
    @XmlJavaTypeAdapter(Adapter3 .class)
    @XmlSchemaType(name = "long")
    protected Long willowId;

    /**
     * Gets the value of the willowId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Long getWillowId() {
        return willowId;
    }

    /**
     * Sets the value of the willowId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWillowId(Long value) {
        this.willowId = value;
    }

}
