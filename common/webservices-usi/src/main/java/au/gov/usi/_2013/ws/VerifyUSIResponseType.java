
package au.gov.usi._2013.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for VerifyUSIResponseType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="VerifyUSIResponseType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="USIStatus">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="Valid"/>
 *               &lt;enumeration value="Invalid"/>
 *               &lt;enumeration value="Deactivated"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;choice>
 *           &lt;sequence>
 *             &lt;element name="FirstName" type="{http://usi.gov.au/2013/ws}MatchResultType" minOccurs="0"/>
 *             &lt;element name="FamilyName" type="{http://usi.gov.au/2013/ws}MatchResultType" minOccurs="0"/>
 *           &lt;/sequence>
 *           &lt;element name="SingleName" type="{http://usi.gov.au/2013/ws}MatchResultType" minOccurs="0"/>
 *         &lt;/choice>
 *         &lt;element name="DateOfBirth" type="{http://usi.gov.au/2013/ws}MatchResultType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "VerifyUSIResponseType", propOrder = {
    "usiStatus",
    "firstName",
    "familyName",
    "singleName",
    "dateOfBirth"
})
public class VerifyUSIResponseType {

    @XmlElement(name = "USIStatus", required = true)
    protected String usiStatus;
    @XmlElement(name = "FirstName")
    protected MatchResultType firstName;
    @XmlElement(name = "FamilyName")
    protected MatchResultType familyName;
    @XmlElement(name = "SingleName")
    protected MatchResultType singleName;
    @XmlElement(name = "DateOfBirth")
    protected MatchResultType dateOfBirth;

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

}
