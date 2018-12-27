
package au.gov.usi._2018.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for UpdateUSIPersonalDetailsType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UpdateUSIPersonalDetailsType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="OrgCode" type="{http://usi.gov.au/2018/ws}OrgCodeType"/>
 *         &lt;element name="USI" type="{http://usi.gov.au/2018/ws}USIType"/>
 *         &lt;element name="DVSCheckRequired" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="UserReference" type="{http://usi.gov.au/2018/ws}UserReferenceType"/>
 *         &lt;element name="PersonalDetailsModifier">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="FirstName"/>
 *               &lt;enumeration value="MiddleName"/>
 *               &lt;enumeration value="FamilyName"/>
 *               &lt;enumeration value="SingleName"/>
 *               &lt;enumeration value="DateOfBirth"/>
 *               &lt;enumeration value="CountryOfBirth"/>
 *               &lt;enumeration value="TownCityOfBirth"/>
 *               &lt;enumeration value="Gender"/>
 *               &lt;enumeration value="PreferredFirstName"/>
 *               &lt;enumeration value="PreferredFamilyName"/>
 *               &lt;enumeration value="PreferredSingleName"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="PersonalDetailsUpdate" type="{http://usi.gov.au/2018/ws}PersonalDetailsUpdateType"/>
 *         &lt;element name="DVSDocument" type="{http://usi.gov.au/2018/ws}DVSDocumentType" minOccurs="0"/>
 *         &lt;element name="NonDvsDocumentTypeId" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="NonDvsDocumentTypeOther" type="{http://usi.gov.au/2018/ws}NonDvsDocumentTypeOtherType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UpdateUSIPersonalDetailsType", propOrder = {
    "orgCode",
    "usi",
    "dvsCheckRequired",
    "userReference",
    "personalDetailsModifier",
    "personalDetailsUpdate",
    "dvsDocument",
    "nonDvsDocumentTypeId",
    "nonDvsDocumentTypeOther"
})
public class UpdateUSIPersonalDetailsType {

    @XmlElement(name = "OrgCode", required = true)
    protected String orgCode;
    @XmlElement(name = "USI", required = true)
    protected String usi;
    @XmlElement(name = "DVSCheckRequired")
    protected boolean dvsCheckRequired;
    @XmlElement(name = "UserReference", required = true)
    protected String userReference;
    @XmlElement(name = "PersonalDetailsModifier", required = true)
    protected String personalDetailsModifier;
    @XmlElement(name = "PersonalDetailsUpdate", required = true)
    protected PersonalDetailsUpdateType personalDetailsUpdate;
    @XmlElement(name = "DVSDocument")
    protected DVSDocumentType dvsDocument;
    @XmlElement(name = "NonDvsDocumentTypeId")
    protected Integer nonDvsDocumentTypeId;
    @XmlElement(name = "NonDvsDocumentTypeOther")
    protected String nonDvsDocumentTypeOther;

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
     * Gets the value of the personalDetailsModifier property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPersonalDetailsModifier() {
        return personalDetailsModifier;
    }

    /**
     * Sets the value of the personalDetailsModifier property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPersonalDetailsModifier(String value) {
        this.personalDetailsModifier = value;
    }

    /**
     * Gets the value of the personalDetailsUpdate property.
     * 
     * @return
     *     possible object is
     *     {@link PersonalDetailsUpdateType }
     *     
     */
    public PersonalDetailsUpdateType getPersonalDetailsUpdate() {
        return personalDetailsUpdate;
    }

    /**
     * Sets the value of the personalDetailsUpdate property.
     * 
     * @param value
     *     allowed object is
     *     {@link PersonalDetailsUpdateType }
     *     
     */
    public void setPersonalDetailsUpdate(PersonalDetailsUpdateType value) {
        this.personalDetailsUpdate = value;
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
