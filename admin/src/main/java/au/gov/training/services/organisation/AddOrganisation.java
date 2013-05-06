
package au.gov.training.services.organisation;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="rtoDto" type="{http://training.gov.au/services/}Organisation" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "rtoDto"
})
@XmlRootElement(name = "AddOrganisation")
public class AddOrganisation {

    @XmlElementRef(name = "rtoDto", namespace = "http://training.gov.au/services/", type = JAXBElement.class, required = false)
    protected JAXBElement<Organisation> rtoDto;

    /**
     * Gets the value of the rtoDto property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Organisation }{@code >}
     *     
     */
    public JAXBElement<Organisation> getRtoDto() {
        return rtoDto;
    }

    /**
     * Sets the value of the rtoDto property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Organisation }{@code >}
     *     
     */
    public void setRtoDto(JAXBElement<Organisation> value) {
        this.rtoDto = value;
    }

}
