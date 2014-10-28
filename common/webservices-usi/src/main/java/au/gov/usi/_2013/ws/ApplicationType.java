
package au.gov.usi._2013.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ApplicationType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ApplicationType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ApplicationId" type="{http://usi.gov.au/2013/ws}ApplicationIDType"/>
 *         &lt;element name="DVSCheckRequired" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="UserReference" type="{http://usi.gov.au/2013/ws}UserReferenceType"/>
 *         &lt;element name="ContactDetails" type="{http://usi.gov.au/2013/ws}ContactDetailsType"/>
 *         &lt;element name="PersonalDetails" type="{http://usi.gov.au/2013/ws}PersonalDetailsType"/>
 *         &lt;element name="DVSDocument" type="{http://usi.gov.au/2013/ws}DVSDocumentType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ApplicationType", propOrder = {
    "applicationId",
    "dvsCheckRequired",
    "userReference",
    "contactDetails",
    "personalDetails",
    "dvsDocument"
})
public class ApplicationType {

    @XmlElement(name = "ApplicationId", required = true)
    protected String applicationId;
    @XmlElement(name = "DVSCheckRequired")
    protected boolean dvsCheckRequired;
    @XmlElement(name = "UserReference", required = true)
    protected String userReference;
    @XmlElement(name = "ContactDetails", required = true)
    protected ContactDetailsType contactDetails;
    @XmlElement(name = "PersonalDetails", required = true)
    protected PersonalDetailsType personalDetails;
    @XmlElement(name = "DVSDocument")
    protected DVSDocumentType dvsDocument;

    /**
     * Gets the value of the applicationId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getApplicationId() {
        return applicationId;
    }

    /**
     * Sets the value of the applicationId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setApplicationId(String value) {
        this.applicationId = value;
    }

    /**
     * Gets the value of the dvsCheckRequired property.
     * 
     */
    public boolean isDVSCheckRequired() {
        return dvsCheckRequired;
    }

    /**
     * Sets the value of the dvsCheckRequired property.
     * 
     */
    public void setDVSCheckRequired(boolean value) {
        this.dvsCheckRequired = value;
    }

    /**
     * Gets the value of the userReference property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUserReference() {
        return userReference;
    }

    /**
     * Sets the value of the userReference property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUserReference(String value) {
        this.userReference = value;
    }

    /**
     * Gets the value of the contactDetails property.
     * 
     * @return
     *     possible object is
     *     {@link ContactDetailsType }
     *     
     */
    public ContactDetailsType getContactDetails() {
        return contactDetails;
    }

    /**
     * Sets the value of the contactDetails property.
     * 
     * @param value
     *     allowed object is
     *     {@link ContactDetailsType }
     *     
     */
    public void setContactDetails(ContactDetailsType value) {
        this.contactDetails = value;
    }

    /**
     * Gets the value of the personalDetails property.
     * 
     * @return
     *     possible object is
     *     {@link PersonalDetailsType }
     *     
     */
    public PersonalDetailsType getPersonalDetails() {
        return personalDetails;
    }

    /**
     * Sets the value of the personalDetails property.
     * 
     * @param value
     *     allowed object is
     *     {@link PersonalDetailsType }
     *     
     */
    public void setPersonalDetails(PersonalDetailsType value) {
        this.personalDetails = value;
    }

    /**
     * Gets the value of the dvsDocument property.
     * 
     * @return
     *     possible object is
     *     {@link DVSDocumentType }
     *     
     */
    public DVSDocumentType getDVSDocument() {
        return dvsDocument;
    }

    /**
     * Sets the value of the dvsDocument property.
     * 
     * @param value
     *     allowed object is
     *     {@link DVSDocumentType }
     *     
     */
    public void setDVSDocument(DVSDocumentType value) {
        this.dvsDocument = value;
    }

}
