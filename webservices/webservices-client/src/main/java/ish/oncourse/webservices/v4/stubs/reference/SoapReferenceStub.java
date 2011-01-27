
package ish.oncourse.webservices.v4.stubs.reference;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for soapReferenceStub complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="soapReferenceStub">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="willowId" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "soapReferenceStub", propOrder = {
    "willowId"
})
@XmlSeeAlso({
    QualificationStub.class,
    ModuleStub.class,
    TrainingPackageStub.class,
    LanguageStub.class,
    CountryStub.class
})
public abstract class SoapReferenceStub {

    protected Long willowId;

    /**
     * Gets the value of the willowId property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
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
     *     {@link Long }
     *     
     */
    public void setWillowId(Long value) {
        this.willowId = value;
    }

}
