
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
 *         &lt;element name="GetContactRolesResult" type="{http://training.gov.au/services/}ArrayOfOrganisationContactRole" minOccurs="0"/>
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
    "getContactRolesResult"
})
@XmlRootElement(name = "GetContactRolesResponse")
public class GetContactRolesResponse {

    @XmlElementRef(name = "GetContactRolesResult", namespace = "http://training.gov.au/services/", type = JAXBElement.class, required = false)
    protected JAXBElement<ArrayOfOrganisationContactRole> getContactRolesResult;

    /**
     * Gets the value of the getContactRolesResult property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfOrganisationContactRole }{@code >}
     *     
     */
    public JAXBElement<ArrayOfOrganisationContactRole> getGetContactRolesResult() {
        return getContactRolesResult;
    }

    /**
     * Sets the value of the getContactRolesResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfOrganisationContactRole }{@code >}
     *     
     */
    public void setGetContactRolesResult(JAXBElement<ArrayOfOrganisationContactRole> value) {
        this.getContactRolesResult = value;
    }

}
