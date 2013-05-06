
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
 *         &lt;element name="GetAddressStatesResult" type="{http://training.gov.au/services/}ArrayOfAddressStates" minOccurs="0"/>
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
    "getAddressStatesResult"
})
@XmlRootElement(name = "GetAddressStatesResponse")
public class GetAddressStatesResponse {

    @XmlElementRef(name = "GetAddressStatesResult", namespace = "http://training.gov.au/services/", type = JAXBElement.class, required = false)
    protected JAXBElement<ArrayOfAddressStates> getAddressStatesResult;

    /**
     * Gets the value of the getAddressStatesResult property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfAddressStates }{@code >}
     *     
     */
    public JAXBElement<ArrayOfAddressStates> getGetAddressStatesResult() {
        return getAddressStatesResult;
    }

    /**
     * Sets the value of the getAddressStatesResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfAddressStates }{@code >}
     *     
     */
    public void setGetAddressStatesResult(JAXBElement<ArrayOfAddressStates> value) {
        this.getAddressStatesResult = value;
    }

}
