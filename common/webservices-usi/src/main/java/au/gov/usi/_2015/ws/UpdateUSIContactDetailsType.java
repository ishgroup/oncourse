
package au.gov.usi._2015.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for UpdateUSIContactDetailsType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UpdateUSIContactDetailsType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="OrgCode" type="{http://usi.gov.au/2015/ws}OrgCodeType"/>
 *         &lt;element name="USI" type="{http://usi.gov.au/2015/ws}USIType"/>
 *         &lt;element name="UserReference" type="{http://usi.gov.au/2015/ws}UserReferenceType"/>
 *         &lt;element name="ContactDetailsUpdate" type="{http://usi.gov.au/2015/ws}ContactDetailsUpdateType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UpdateUSIContactDetailsType", propOrder = {
    "orgCode",
    "usi",
    "userReference",
    "contactDetailsUpdate"
})
public class UpdateUSIContactDetailsType {

    @XmlElement(name = "OrgCode", required = true)
    protected String orgCode;
    @XmlElement(name = "USI", required = true)
    protected String usi;
    @XmlElement(name = "UserReference", required = true)
    protected String userReference;
    @XmlElement(name = "ContactDetailsUpdate", required = true)
    protected ContactDetailsUpdateType contactDetailsUpdate;

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
     * Gets the value of the usi property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUSI() {
        return usi;
    }

    /**
     * Sets the value of the usi property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUSI(String value) {
        this.usi = value;
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
     * Gets the value of the contactDetailsUpdate property.
     * 
     * @return
     *     possible object is
     *     {@link ContactDetailsUpdateType }
     *     
     */
    public ContactDetailsUpdateType getContactDetailsUpdate() {
        return contactDetailsUpdate;
    }

    /**
     * Sets the value of the contactDetailsUpdate property.
     * 
     * @param value
     *     allowed object is
     *     {@link ContactDetailsUpdateType }
     *     
     */
    public void setContactDetailsUpdate(ContactDetailsUpdateType value) {
        this.contactDetailsUpdate = value;
    }

}
