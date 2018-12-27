
package au.gov.usi._2018.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ContactDetailsLocateType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ContactDetailsLocateType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="CountryOfResidenceCode" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="4"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="EmailAddress" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="254"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="Phone" type="{http://usi.gov.au/2018/ws}PhoneType" minOccurs="0"/>
 *         &lt;choice minOccurs="0">
 *           &lt;element name="InternationalAddress">
 *             &lt;simpleType>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                 &lt;maxLength value="250"/>
 *               &lt;/restriction>
 *             &lt;/simpleType>
 *           &lt;/element>
 *           &lt;element name="NationalAddress" type="{http://usi.gov.au/2018/ws}NationalAddressType"/>
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
@XmlType(name = "ContactDetailsLocateType", propOrder = {
    "countryOfResidenceCode",
    "emailAddress",
    "phone",
    "internationalAddress",
    "nationalAddress"
})
public class ContactDetailsLocateType {

    @XmlElement(name = "CountryOfResidenceCode")
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
