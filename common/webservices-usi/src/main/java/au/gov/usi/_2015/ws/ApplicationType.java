
package au.gov.usi._2015.ws;

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
 *         &lt;element name="ApplicationId" type="{http://usi.gov.au/2015/ws}ApplicationIDType"/>
 *         &lt;element name="DVSCheckRequired" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="UserReference" type="{http://usi.gov.au/2015/ws}UserReferenceType"/>
 *         &lt;element name="ContactDetails" type="{http://usi.gov.au/2015/ws}ContactDetailsType"/>
 *         &lt;element name="PersonalDetails" type="{http://usi.gov.au/2015/ws}PersonalDetailsType"/>
 *         &lt;choice>
 *           &lt;element name="DVSDocument" type="{http://usi.gov.au/2015/ws}DVSDocumentType"/>
 *           &lt;sequence>
 *             &lt;element name="NonDvsDocumentTypeId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *             &lt;element name="NonDvsDocumentTypeOther" type="{http://usi.gov.au/2015/ws}NonDvsDocumentTypeOtherType" minOccurs="0"/>
 *           &lt;/sequence>
 *         &lt;/choice>
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
    "dvsDocument",
    "nonDvsDocumentTypeId",
    "nonDvsDocumentTypeOther"
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
    @XmlElement(name = "NonDvsDocumentTypeId")
    protected Integer nonDvsDocumentTypeId;
    @XmlElement(name = "NonDvsDocumentTypeOther")
    protected String nonDvsDocumentTypeOther;

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

    /**
     * Gets the value of the nonDvsDocumentTypeId property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getNonDvsDocumentTypeId() {
        return nonDvsDocumentTypeId;
    }

    /**
     * Sets the value of the nonDvsDocumentTypeId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setNonDvsDocumentTypeId(Integer value) {
        this.nonDvsDocumentTypeId = value;
    }

    /**
     * Gets the value of the nonDvsDocumentTypeOther property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNonDvsDocumentTypeOther() {
        return nonDvsDocumentTypeOther;
    }

    /**
     * Sets the value of the nonDvsDocumentTypeOther property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNonDvsDocumentTypeOther(String value) {
        this.nonDvsDocumentTypeOther = value;
    }

}
