
package au.gov.usi._2013.ws;

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
 * &lt;complexType name="ContactDetailsType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="PreferredMethod">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="Mobile"/>
 *               &lt;enumeration value="Email"/>
 *               &lt;enumeration value="Mail"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="CountryOfResidence">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="50"/>
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
 *         &lt;element name="Phone" type="{http://usi.gov.au/2013/ws}PhoneType" minOccurs="0"/>
 *         &lt;choice minOccurs="0">
 *           &lt;element name="InternationalAddress">
 *             &lt;simpleType>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                 &lt;maxLength value="250"/>
 *                 &lt;pattern value="[A-Za-z0-9 , .'/-]*"/>
 *               &lt;/restriction>
 *             &lt;/simpleType>
 *           &lt;/element>
 *           &lt;element name="NationalAddress" type="{http://usi.gov.au/2013/ws}NationalAddressType"/>
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
@XmlType(name = "ContactDetailsType", propOrder = {
    "preferredMethod",
    "countryOfResidence",
    "emailAddress",
    "phone",
    "internationalAddress",
    "nationalAddress"
})
public class ContactDetailsType {

    @XmlElement(name = "PreferredMethod", required = true)
    protected String preferredMethod;
    @XmlElement(name = "CountryOfResidence", required = true)
    protected String countryOfResidence;
    @XmlElement(name = "EmailAddress")
    protected String emailAddress;
    @XmlElement(name = "Phone")
    protected PhoneType phone;
    @XmlElement(name = "InternationalAddress")
    protected String internationalAddress;
    @XmlElement(name = "NationalAddress")
    protected NationalAddressType nationalAddress;

    /**
     * Gets the value of the preferredMethod property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPreferredMethod() {
        return preferredMethod;
    }

    /**
     * Sets the value of the preferredMethod property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPreferredMethod(String value) {
        this.preferredMethod = value;
    }

    /**
     * Gets the value of the countryOfResidence property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCountryOfResidence() {
        return countryOfResidence;
    }

    /**
     * Sets the value of the countryOfResidence property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCountryOfResidence(String value) {
        this.countryOfResidence = value;
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
