
package au.gov.usi._2018.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ContactDetailsType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ContactDetailsType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="CountryOfResidenceCode"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="4"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="EmailAddress" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="254"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="Phone" type="{http://usi.gov.au/2018/ws}PhoneType" minOccurs="0"/&gt;
 *         &lt;choice minOccurs="0"&gt;
 *           &lt;element name="InternationalAddress"&gt;
 *             &lt;simpleType&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *                 &lt;maxLength value="250"/&gt;
 *               &lt;/restriction&gt;
 *             &lt;/simpleType&gt;
 *           &lt;/element&gt;
 *           &lt;element name="NationalAddress" type="{http://usi.gov.au/2018/ws}NationalAddressType"/&gt;
 *         &lt;/choice&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ContactDetailsType", propOrder = {
    "countryOfResidenceCode",
    "emailAddress",
    "phone",
    "internationalAddress",
    "nationalAddress"
})
public class ContactDetailsType {

    @XmlElement(name = "CountryOfResidenceCode", required = true)
    protected String countryOfResidenceCode;
    @XmlElement(name = "EmailAddress")
    protected String emailAddress;
    @XmlElement(name = "Phone")
    protected PhoneType phone;
    @XmlElement(name = "InternationalAddress")
    protected String internationalAddress;
    @XmlElement(name = "NationalAddress")
    protected NationalAddressType nationalAddress;

    /**
     * Gets the value of the countryOfResidenceCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCountryOfResidenceCode() {
        return countryOfResidenceCode;
    }

    /**
     * Sets the value of the countryOfResidenceCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCountryOfResidenceCode(String value) {
        this.countryOfResidenceCode = value;
    }

    /**
     * Gets the value of the emailAddress property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEmailAddress() {
        return emailAddress;
    }

    /**
     * Sets the value of the emailAddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEmailAddress(String value) {
        this.emailAddress = value;
    }

    /**
     * Gets the value of the phone property.
     * 
     * @return
     *     possible object is
     *     {@link PhoneType }
     *     
     */
    public PhoneType getPhone() {
        return phone;
    }

    /**
     * Sets the value of the phone property.
     * 
     * @param value
     *     allowed object is
     *     {@link PhoneType }
     *     
     */
    public void setPhone(PhoneType value) {
        this.phone = value;
    }

    /**
     * Gets the value of the internationalAddress property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInternationalAddress() {
        return internationalAddress;
    }

    /**
     * Sets the value of the internationalAddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInternationalAddress(String value) {
        this.internationalAddress = value;
    }

    /**
     * Gets the value of the nationalAddress property.
     * 
     * @return
     *     possible object is
     *     {@link NationalAddressType }
     *     
     */
    public NationalAddressType getNationalAddress() {
        return nationalAddress;
    }

    /**
     * Sets the value of the nationalAddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link NationalAddressType }
     *     
     */
    public void setNationalAddress(NationalAddressType value) {
        this.nationalAddress = value;
    }

}
