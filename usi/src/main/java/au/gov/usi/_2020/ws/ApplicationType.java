
package au.gov.usi._2020.ws;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ApplicationType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ApplicationType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="ApplicationId" type="{http://usi.gov.au/2020/ws}ApplicationIDType"/&gt;
 *         &lt;element name="DVSCheckRequired" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="UserReference" type="{http://usi.gov.au/2020/ws}UserReferenceType"/&gt;
 *         &lt;element name="ContactDetails" type="{http://usi.gov.au/2020/ws}ContactDetailsType"/&gt;
 *         &lt;element name="PersonalDetails" type="{http://usi.gov.au/2020/ws}PersonalDetailsType"/&gt;
 *         &lt;element name="DVSDocument" type="{http://usi.gov.au/2020/ws}DVSDocumentType" minOccurs="0"/&gt;
 *         &lt;element name="NonDvsDocumentTypeId" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="NonDvsDocumentTypeOther" type="{http://usi.gov.au/2020/ws}NonDvsDocumentTypeOtherType" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
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
    @XmlElementRef(name = "NonDvsDocumentTypeId", namespace = "http://usi.gov.au/2020/ws", type = JAXBElement.class, required = false)
    protected JAXBElement<Integer> nonDvsDocumentTypeId;
    @XmlElementRef(name = "NonDvsDocumentTypeOther", namespace = "http://usi.gov.au/2020/ws", type = JAXBElement.class, required = false)
    protected JAXBElement<String> nonDvsDocumentTypeOther;

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
     *     {@link JAXBElement }{@code <}{@link Integer }{@code >}
     *     
     */
    public JAXBElement<Integer> getNonDvsDocumentTypeId() {
        return nonDvsDocumentTypeId;
    }

    /**
     * Sets the value of the nonDvsDocumentTypeId property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Integer }{@code >}
     *     
     */
    public void setNonDvsDocumentTypeId(JAXBElement<Integer> value) {
        this.nonDvsDocumentTypeId = value;
    }

    /**
     * Gets the value of the nonDvsDocumentTypeOther property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getNonDvsDocumentTypeOther() {
        return nonDvsDocumentTypeOther;
    }

    /**
     * Sets the value of the nonDvsDocumentTypeOther property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setNonDvsDocumentTypeOther(JAXBElement<String> value) {
        this.nonDvsDocumentTypeOther = value;
    }

}
