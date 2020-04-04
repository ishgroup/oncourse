
package au.gov.usi._2018.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for LocateUSIType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="LocateUSIType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="OrgCode" type="{http://usi.gov.au/2018/ws}OrgCodeType"/&gt;
 *         &lt;element name="UserReference" type="{http://usi.gov.au/2018/ws}UserReferenceType"/&gt;
 *         &lt;element name="PersonalDetails" type="{http://usi.gov.au/2018/ws}PersonalDetailsLocateType"/&gt;
 *         &lt;element name="ContactDetails" type="{http://usi.gov.au/2018/ws}ContactDetailsLocateType"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LocateUSIType", propOrder = {
    "orgCode",
    "userReference",
    "personalDetails",
    "contactDetails"
})
public class LocateUSIType {

    @XmlElement(name = "OrgCode", required = true)
    protected String orgCode;
    @XmlElement(name = "UserReference", required = true)
    protected String userReference;
    @XmlElement(name = "PersonalDetails", required = true)
    protected PersonalDetailsLocateType personalDetails;
    @XmlElement(name = "ContactDetails", required = true)
    protected ContactDetailsLocateType contactDetails;

    /**
     * Gets the value of the orgCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrgCode() {
        return orgCode;
    }

    /**
     * Sets the value of the orgCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrgCode(String value) {
        this.orgCode = value;
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
     * Gets the value of the personalDetails property.
     * 
     * @return
     *     possible object is
     *     {@link PersonalDetailsLocateType }
     *     
     */
    public PersonalDetailsLocateType getPersonalDetails() {
        return personalDetails;
    }

    /**
     * Sets the value of the personalDetails property.
     * 
     * @param value
     *     allowed object is
     *     {@link PersonalDetailsLocateType }
     *     
     */
    public void setPersonalDetails(PersonalDetailsLocateType value) {
        this.personalDetails = value;
    }

    /**
     * Gets the value of the contactDetails property.
     * 
     * @return
     *     possible object is
     *     {@link ContactDetailsLocateType }
     *     
     */
    public ContactDetailsLocateType getContactDetails() {
        return contactDetails;
    }

    /**
     * Sets the value of the contactDetails property.
     * 
     * @param value
     *     allowed object is
     *     {@link ContactDetailsLocateType }
     *     
     */
    public void setContactDetails(ContactDetailsLocateType value) {
        this.contactDetails = value;
    }

}
