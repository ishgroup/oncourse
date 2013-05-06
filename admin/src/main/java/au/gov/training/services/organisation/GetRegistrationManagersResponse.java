
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
 *         &lt;element name="GetRegistrationManagersResult" type="{http://training.gov.au/services/}ArrayOfRegistrationManager" minOccurs="0"/>
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
    "getRegistrationManagersResult"
})
@XmlRootElement(name = "GetRegistrationManagersResponse")
public class GetRegistrationManagersResponse {

    @XmlElementRef(name = "GetRegistrationManagersResult", namespace = "http://training.gov.au/services/", type = JAXBElement.class, required = false)
    protected JAXBElement<ArrayOfRegistrationManager> getRegistrationManagersResult;

    /**
     * Gets the value of the getRegistrationManagersResult property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfRegistrationManager }{@code >}
     *     
     */
    public JAXBElement<ArrayOfRegistrationManager> getGetRegistrationManagersResult() {
        return getRegistrationManagersResult;
    }

    /**
     * Sets the value of the getRegistrationManagersResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfRegistrationManager }{@code >}
     *     
     */
    public void setGetRegistrationManagersResult(JAXBElement<ArrayOfRegistrationManager> value) {
        this.getRegistrationManagersResult = value;
    }

}
