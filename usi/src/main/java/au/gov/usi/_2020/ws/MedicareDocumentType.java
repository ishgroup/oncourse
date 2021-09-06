
package au.gov.usi._2020.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for MedicareDocumentType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="MedicareDocumentType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://usi.gov.au/2020/ws}DVSDocumentType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="MedicareCardNumber"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;length value="10"/&gt;
 *               &lt;pattern value="[0-9]*"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="IndividualRefNumber"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;length value="1"/&gt;
 *               &lt;pattern value="[1-9]"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="ExpiryDate"&gt;
 *           &lt;simpleType&gt;
 *             &lt;union memberTypes=" {http://www.w3.org/2001/XMLSchema}date {http://www.w3.org/2001/XMLSchema}gYearMonth"&gt;
 *             &lt;/union&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="CardColour"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;enumeration value="Green"/&gt;
 *               &lt;enumeration value="Blue"/&gt;
 *               &lt;enumeration value="Yellow"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="NameLine1"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="27"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="NameLine2" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="25"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="NameLine3" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="23"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="NameLine4" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="21"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MedicareDocumentType", propOrder = {
    "medicareCardNumber",
    "individualRefNumber",
    "expiryDate",
    "cardColour",
    "nameLine1",
    "nameLine2",
    "nameLine3",
    "nameLine4"
})
public class MedicareDocumentType
    extends DVSDocumentType
{

    @XmlElement(name = "MedicareCardNumber", required = true)
    protected String medicareCardNumber;
    @XmlElement(name = "IndividualRefNumber", required = true)
    protected String individualRefNumber;
    @XmlElement(name = "ExpiryDate", required = true)
    protected String expiryDate;
    @XmlElement(name = "CardColour", required = true)
    protected String cardColour;
    @XmlElement(name = "NameLine1", required = true)
    protected String nameLine1;
    @XmlElement(name = "NameLine2")
    protected String nameLine2;
    @XmlElement(name = "NameLine3")
    protected String nameLine3;
    @XmlElement(name = "NameLine4")
    protected String nameLine4;

    /**
     * Gets the value of the medicareCardNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMedicareCardNumber() {
        return medicareCardNumber;
    }

    /**
     * Sets the value of the medicareCardNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMedicareCardNumber(String value) {
        this.medicareCardNumber = value;
    }

    /**
     * Gets the value of the individualRefNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIndividualRefNumber() {
        return individualRefNumber;
    }

    /**
     * Sets the value of the individualRefNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIndividualRefNumber(String value) {
        this.individualRefNumber = value;
    }

    /**
     * Gets the value of the expiryDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExpiryDate() {
        return expiryDate;
    }

    /**
     * Sets the value of the expiryDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExpiryDate(String value) {
        this.expiryDate = value;
    }

    /**
     * Gets the value of the cardColour property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCardColour() {
        return cardColour;
    }

    /**
     * Sets the value of the cardColour property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCardColour(String value) {
        this.cardColour = value;
    }

    /**
     * Gets the value of the nameLine1 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNameLine1() {
        return nameLine1;
    }

    /**
     * Sets the value of the nameLine1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNameLine1(String value) {
        this.nameLine1 = value;
    }

    /**
     * Gets the value of the nameLine2 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNameLine2() {
        return nameLine2;
    }

    /**
     * Sets the value of the nameLine2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNameLine2(String value) {
        this.nameLine2 = value;
    }

    /**
     * Gets the value of the nameLine3 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNameLine3() {
        return nameLine3;
    }

    /**
     * Sets the value of the nameLine3 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNameLine3(String value) {
        this.nameLine3 = value;
    }

    /**
     * Gets the value of the nameLine4 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNameLine4() {
        return nameLine4;
    }

    /**
     * Sets the value of the nameLine4 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNameLine4(String value) {
        this.nameLine4 = value;
    }

}
