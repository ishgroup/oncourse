
package au.gov.usi._2020.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for VerificationType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="VerificationType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="RecordId"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int"&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="USI" type="{http://usi.gov.au/2020/ws}USIType"/&gt;
 *         &lt;choice&gt;
 *           &lt;sequence&gt;
 *             &lt;element name="FirstName"&gt;
 *               &lt;simpleType&gt;
 *                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *                   &lt;maxLength value="40"/&gt;
 *                 &lt;/restriction&gt;
 *               &lt;/simpleType&gt;
 *             &lt;/element&gt;
 *             &lt;element name="FamilyName"&gt;
 *               &lt;simpleType&gt;
 *                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *                   &lt;maxLength value="40"/&gt;
 *                 &lt;/restriction&gt;
 *               &lt;/simpleType&gt;
 *             &lt;/element&gt;
 *           &lt;/sequence&gt;
 *           &lt;element name="SingleName"&gt;
 *             &lt;simpleType&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *                 &lt;maxLength value="40"/&gt;
 *               &lt;/restriction&gt;
 *             &lt;/simpleType&gt;
 *           &lt;/element&gt;
 *         &lt;/choice&gt;
 *         &lt;element name="DateOfBirth" type="{http://www.w3.org/2001/XMLSchema}date"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "VerificationType", propOrder = {
    "recordId",
    "usi",
    "firstName",
    "familyName",
    "singleName",
    "dateOfBirth"
})
public class VerificationType {

    @XmlElement(name = "RecordId")
    protected int recordId;
    @XmlElement(name = "USI", required = true)
    protected String usi;
    @XmlElement(name = "FirstName")
    protected String firstName;
    @XmlElement(name = "FamilyName")
    protected String familyName;
    @XmlElement(name = "SingleName")
    protected String singleName;
    @XmlElement(name = "DateOfBirth", required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar dateOfBirth;

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
     * Gets the value of the firstName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the value of the firstName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFirstName(String value) {
        this.firstName = value;
    }

    /**
     * Gets the value of the familyName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFamilyName() {
        return familyName;
    }

    /**
     * Sets the value of the familyName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFamilyName(String value) {
        this.familyName = value;
    }

    /**
     * Gets the value of the singleName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSingleName() {
        return singleName;
    }

    /**
     * Sets the value of the singleName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSingleName(String value) {
        this.singleName = value;
    }

    /**
     * Gets the value of the dateOfBirth property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDateOfBirth() {
        return dateOfBirth;
    }

    /**
     * Sets the value of the dateOfBirth property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDateOfBirth(XMLGregorianCalendar value) {
        this.dateOfBirth = value;
    }

}
