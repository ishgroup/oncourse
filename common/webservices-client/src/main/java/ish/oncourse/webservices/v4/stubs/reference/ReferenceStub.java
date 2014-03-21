
package ish.oncourse.webservices.v4.stubs.reference;

import ish.oncourse.webservices.util.GenericReferenceStub;
import org.w3._2001.xmlschema.Adapter2;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for referenceStub complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="referenceStub">
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
@XmlType(name = "referenceStub", propOrder = {
    "willowId"
})
@XmlSeeAlso({
    QualificationStub.class,
    ModuleStub.class,
    TrainingPackageStub.class,
    LanguageStub.class,
    CountryStub.class
})
public abstract class ReferenceStub implements GenericReferenceStub {

    @XmlElement(type = String.class)
    @XmlJavaTypeAdapter(Adapter2 .class)
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
