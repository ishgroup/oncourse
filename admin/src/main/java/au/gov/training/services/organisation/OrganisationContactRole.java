
package au.gov.training.services.organisation;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for OrganisationContactRole complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="OrganisationContactRole">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="AllowGroupContact" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="AllowMultipleCurrent" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="Description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="IsRequired" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="IsRequiredRto" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="MustCoverRegistrationPeriod" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="Role" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OrganisationContactRole", propOrder = {
    "allowGroupContact",
    "allowMultipleCurrent",
    "description",
    "isRequired",
    "isRequiredRto",
    "mustCoverRegistrationPeriod",
    "role"
})
public class OrganisationContactRole {

    @XmlElement(name = "AllowGroupContact")
    protected Boolean allowGroupContact;
    @XmlElement(name = "AllowMultipleCurrent")
    protected Boolean allowMultipleCurrent;
    @XmlElementRef(name = "Description", namespace = "http://training.gov.au/services/", type = JAXBElement.class, required = false)
    protected JAXBElement<String> description;
    @XmlElement(name = "IsRequired")
    protected Boolean isRequired;
    @XmlElement(name = "IsRequiredRto")
    protected Boolean isRequiredRto;
    @XmlElement(name = "MustCoverRegistrationPeriod")
    protected Boolean mustCoverRegistrationPeriod;
    @XmlElementRef(name = "Role", namespace = "http://training.gov.au/services/", type = JAXBElement.class, required = false)
    protected JAXBElement<String> role;

    /**
     * Gets the value of the allowGroupContact property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isAllowGroupContact() {
        return allowGroupContact;
    }

    /**
     * Sets the value of the allowGroupContact property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAllowGroupContact(Boolean value) {
        this.allowGroupContact = value;
    }

    /**
     * Gets the value of the allowMultipleCurrent property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isAllowMultipleCurrent() {
        return allowMultipleCurrent;
    }

    /**
     * Sets the value of the allowMultipleCurrent property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAllowMultipleCurrent(Boolean value) {
        this.allowMultipleCurrent = value;
    }

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setDescription(JAXBElement<String> value) {
        this.description = value;
    }

    /**
     * Gets the value of the isRequired property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIsRequired() {
        return isRequired;
    }

    /**
     * Sets the value of the isRequired property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsRequired(Boolean value) {
        this.isRequired = value;
    }

    /**
     * Gets the value of the isRequiredRto property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIsRequiredRto() {
        return isRequiredRto;
    }

    /**
     * Sets the value of the isRequiredRto property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsRequiredRto(Boolean value) {
        this.isRequiredRto = value;
    }

    /**
     * Gets the value of the mustCoverRegistrationPeriod property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isMustCoverRegistrationPeriod() {
        return mustCoverRegistrationPeriod;
    }

    /**
     * Sets the value of the mustCoverRegistrationPeriod property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setMustCoverRegistrationPeriod(Boolean value) {
        this.mustCoverRegistrationPeriod = value;
    }

    /**
     * Gets the value of the role property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getRole() {
        return role;
    }

    /**
     * Sets the value of the role property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setRole(JAXBElement<String> value) {
        this.role = value;
    }

}
