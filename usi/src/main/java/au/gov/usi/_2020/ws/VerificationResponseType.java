
package au.gov.usi._2020.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for VerificationResponseType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="VerificationResponseType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="USIStatus"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;enumeration value="Valid"/&gt;
 *               &lt;enumeration value="Invalid"/&gt;
 *               &lt;enumeration value="Deactivated"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;choice&gt;
 *           &lt;sequence&gt;
 *             &lt;element name="FirstName" type="{http://usi.gov.au/2020/ws}MatchResultType" minOccurs="0"/&gt;
 *             &lt;element name="FamilyName" type="{http://usi.gov.au/2020/ws}MatchResultType" minOccurs="0"/&gt;
 *           &lt;/sequence&gt;
 *           &lt;element name="SingleName" type="{http://usi.gov.au/2020/ws}MatchResultType" minOccurs="0"/&gt;
 *         &lt;/choice&gt;
 *         &lt;element name="DateOfBirth" type="{http://usi.gov.au/2020/ws}MatchResultType" minOccurs="0"/&gt;
 *         &lt;element name="USI" type="{http://usi.gov.au/2020/ws}USIType"/&gt;
 *         &lt;element name="RecordId"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int"&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "VerificationResponseType", propOrder = {
    "usiStatus",
    "firstName",
    "familyName",
    "singleName",
    "dateOfBirth",
    "usi",
    "recordId"
})
public class VerificationResponseType {

    @XmlElement(name = "USIStatus", required = true)
    protected String usiStatus;
    @XmlElement(name = "FirstName")
    @XmlSchemaType(name = "string")
    protected MatchResultType firstName;
    @XmlElement(name = "FamilyName")
    @XmlSchemaType(name = "string")
    protected MatchResultType familyName;
    @XmlElement(name = "SingleName")
    @XmlSchemaType(name = "string")
    protected MatchResultType singleName;
    @XmlElement(name = "DateOfBirth")
    @XmlSchemaType(name = "string")
    protected MatchResultType dateOfBirth;
    @XmlElement(name = "USI", required = true)
    protected String usi;
    @XmlElement(name = "RecordId")
    protected int recordId;

    /**
     * Gets the value of the usiStatus property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUSIStatus() {
        return usiStatus;
    }

    /**
     * Sets the value of the usiStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUSIStatus(String value) {
        this.usiStatus = value;
    }

    /**
     * Gets the value of the firstName property.
     * 
     * @return
     *     possible object is
     *     {@link MatchResultType }
     *     
     */
    public MatchResultType getFirstName() {
        return firstName;
    }

    /**
     * Sets the value of the firstName property.
     * 
     * @param value
     *     allowed object is
     *     {@link MatchResultType }
     *     
     */
    public void setFirstName(MatchResultType value) {
        this.firstName = value;
    }

    /**
     * Gets the value of the familyName property.
     * 
     * @return
     *     possible object is
     *     {@link MatchResultType }
     *     
     */
    public MatchResultType getFamilyName() {
        return familyName;
    }

    /**
     * Sets the value of the familyName property.
     * 
     * @param value
     *     allowed object is
     *     {@link MatchResultType }
     *     
     */
    public void setFamilyName(MatchResultType value) {
        this.familyName = value;
    }

    /**
     * Gets the value of the singleName property.
     * 
     * @return
     *     possible object is
     *     {@link MatchResultType }
     *     
     */
    public MatchResultType getSingleName() {
        return singleName;
    }

    /**
     * Sets the value of the singleName property.
     * 
     * @param value
     *     allowed object is
     *     {@link MatchResultType }
     *     
     */
    public void setSingleName(MatchResultType value) {
        this.singleName = value;
    }

    /**
     * Gets the value of the dateOfBirth property.
     * 
     * @return
     *     possible object is
     *     {@link MatchResultType }
     *     
     */
    public MatchResultType getDateOfBirth() {
        return dateOfBirth;
    }

    /**
     * Sets the value of the dateOfBirth property.
     * 
     * @param value
     *     allowed object is
     *     {@link MatchResultType }
     *     
     */
    public void setDateOfBirth(MatchResultType value) {
        this.dateOfBirth = value;
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
     * Gets the value of the recordId property.
     * 
     */
    public int getRecordId() {
        return recordId;
    }

    /**
     * Sets the value of the recordId property.
     * 
     */
    public void setRecordId(int value) {
        this.recordId = value;
    }

}
