
package au.gov.training.services.organisation;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for OrganisationDetailsRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="OrganisationDetailsRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Code" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="InformationRequested" type="{http://training.gov.au/services/}OrganisationInformationRequested" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OrganisationDetailsRequest", propOrder = {
    "code",
    "informationRequested"
})
public class OrganisationDetailsRequest {

    @XmlElement(name = "Code", required = true, nillable = true)
    protected String code;
    @XmlElementRef(name = "InformationRequested", namespace = "http://training.gov.au/services/", type = JAXBElement.class, required = false)
    protected JAXBElement<OrganisationInformationRequested> informationRequested;

    /**
     * Gets the value of the code property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCode() {
        return code;
    }

    /**
     * Sets the value of the code property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCode(String value) {
        this.code = value;
    }

    /**
     * Gets the value of the informationRequested property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link OrganisationInformationRequested }{@code >}
     *     
     */
    public JAXBElement<OrganisationInformationRequested> getInformationRequested() {
        return informationRequested;
    }

    /**
     * Sets the value of the informationRequested property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link OrganisationInformationRequested }{@code >}
     *     
     */
    public void setInformationRequested(JAXBElement<OrganisationInformationRequested> value) {
        this.informationRequested = value;
    }

}
