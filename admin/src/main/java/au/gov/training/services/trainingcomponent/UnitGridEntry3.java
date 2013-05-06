
package au.gov.training.services.trainingcomponent;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for UnitGridEntry3 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UnitGridEntry3">
 *   &lt;complexContent>
 *     &lt;extension base="{http://training.gov.au/services/}UnitGridEntry">
 *       &lt;sequence>
 *         &lt;element name="IsEssential" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UnitGridEntry3", propOrder = {
    "isEssential"
})
public class UnitGridEntry3
    extends UnitGridEntry
{

    @XmlElementRef(name = "IsEssential", namespace = "http://training.gov.au/services/", type = JAXBElement.class, required = false)
    protected JAXBElement<Boolean> isEssential;

    /**
     * Gets the value of the isEssential property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Boolean }{@code >}
     *     
     */
    public JAXBElement<Boolean> getIsEssential() {
        return isEssential;
    }

    /**
     * Sets the value of the isEssential property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Boolean }{@code >}
     *     
     */
    public void setIsEssential(JAXBElement<Boolean> value) {
        this.isEssential = value;
    }

}
