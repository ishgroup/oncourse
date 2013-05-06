
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
 *         &lt;element name="GetDetailsResult" type="{http://training.gov.au/services/}Organisation" minOccurs="0"/>
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
    "getDetailsResult"
})
@XmlRootElement(name = "GetDetailsResponse")
public class GetDetailsResponse {

    @XmlElementRef(name = "GetDetailsResult", namespace = "http://training.gov.au/services/", type = JAXBElement.class, required = false)
    protected JAXBElement<Organisation> getDetailsResult;

    /**
     * Gets the value of the getDetailsResult property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Organisation }{@code >}
     *     
     */
    public JAXBElement<Organisation> getGetDetailsResult() {
        return getDetailsResult;
    }

    /**
     * Sets the value of the getDetailsResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Organisation }{@code >}
     *     
     */
    public void setGetDetailsResult(JAXBElement<Organisation> value) {
        this.getDetailsResult = value;
    }

}
