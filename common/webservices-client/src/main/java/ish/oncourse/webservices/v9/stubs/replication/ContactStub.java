
package ish.oncourse.webservices.v9.stubs.replication;

import java.util.Date;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for contactStub complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="contactStub">
 *   &lt;complexContent>
 *     &lt;extension base="{http://repl.v9.soap.webservices.oncourse.ish/}replicationStub">
 *       &lt;sequence>
 *         &lt;element name="businessPhoneNumber" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="cookieHash" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="countryId" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="dateOfBirth" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="emailAddress" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="familyName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="faxNumber" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="givenName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="homePhoneNumber" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="company" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="male" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="marketingViaEmailAllowed" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="marketingViaPostAllowed" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="marketingViaSMSAllowed" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="mobilePhoneNumber" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="password" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="passwordHash" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="postcode" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="state" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="street" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="suburb" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="taxFileNumber" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="uniqueCode" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="studentId" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="tutorId" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="middleName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="abn" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "contactStub", propOrder = {
    "businessPhoneNumber",
    "cookieHash",
    "countryId",
    "dateOfBirth",
    "emailAddress",
    "familyName",
    "faxNumber",
    "givenName",
    "homePhoneNumber",
    "company",
    "male",
    "marketingViaEmailAllowed",
    "marketingViaPostAllowed",
    "marketingViaSMSAllowed",
    "mobilePhoneNumber",
    "password",
    "passwordHash",
    "postcode",
    "state",
    "street",
    "suburb",
    "taxFileNumber",
    "uniqueCode",
    "studentId",
    "tutorId",
    "middleName",
    "abn"
})
public class ContactStub
    extends ReplicationStub
{

    @XmlElement(required = true)
    protected String businessPhoneNumber;
    @XmlElement(required = true)
    protected String cookieHash;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter2 .class)
    @XmlSchemaType(name = "long")
    protected Long countryId;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter1 .class)
    @XmlSchemaType(name = "dateTime")
    protected Date dateOfBirth;
    @XmlElement(required = true)
    protected String emailAddress;
    @XmlElement(required = true)
    protected String familyName;
    @XmlElement(required = true)
    protected String faxNumber;
    @XmlElement(required = true)
    protected String givenName;
    @XmlElement(required = true)
    protected String homePhoneNumber;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter4 .class)
    @XmlSchemaType(name = "boolean")
    protected Boolean company;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter4 .class)
    @XmlSchemaType(name = "boolean")
    protected Boolean male;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter4 .class)
    @XmlSchemaType(name = "boolean")
    protected Boolean marketingViaEmailAllowed;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter4 .class)
    @XmlSchemaType(name = "boolean")
    protected Boolean marketingViaPostAllowed;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter4 .class)
    @XmlSchemaType(name = "boolean")
    protected Boolean marketingViaSMSAllowed;
    @XmlElement(required = true)
    protected String mobilePhoneNumber;
    @XmlElement(required = true)
    protected String password;
    @XmlElement(required = true)
    protected String passwordHash;
    @XmlElement(required = true)
    protected String postcode;
    @XmlElement(required = true)
    protected String state;
    @XmlElement(required = true)
    protected String street;
    @XmlElement(required = true)
    protected String suburb;
    @XmlElement(required = true)
    protected String taxFileNumber;
    @XmlElement(required = true)
    protected String uniqueCode;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter2 .class)
    @XmlSchemaType(name = "long")
    protected Long studentId;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter2 .class)
    @XmlSchemaType(name = "long")
    protected Long tutorId;
    @XmlElement(required = true)
    protected String middleName;
    @XmlElement(required = true)
    protected String abn;

    /**
     * Gets the value of the businessPhoneNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBusinessPhoneNumber() {
        return businessPhoneNumber;
    }

    /**
     * Sets the value of the businessPhoneNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBusinessPhoneNumber(String value) {
        this.businessPhoneNumber = value;
    }

    /**
     * Gets the value of the cookieHash property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCookieHash() {
        return cookieHash;
    }

    /**
     * Sets the value of the cookieHash property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCookieHash(String value) {
        this.cookieHash = value;
    }

    /**
     * Gets the value of the countryId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Long getCountryId() {
        return countryId;
    }

    /**
     * Sets the value of the countryId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCountryId(Long value) {
        this.countryId = value;
    }

    /**
     * Gets the value of the dateOfBirth property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    /**
     * Sets the value of the dateOfBirth property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDateOfBirth(Date value) {
        this.dateOfBirth = value;
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
     * Gets the value of the faxNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFaxNumber() {
        return faxNumber;
    }

    /**
     * Sets the value of the faxNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFaxNumber(String value) {
        this.faxNumber = value;
    }

    /**
     * Gets the value of the givenName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGivenName() {
        return givenName;
    }

    /**
     * Sets the value of the givenName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGivenName(String value) {
        this.givenName = value;
    }

    /**
     * Gets the value of the homePhoneNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHomePhoneNumber() {
        return homePhoneNumber;
    }

    /**
     * Sets the value of the homePhoneNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHomePhoneNumber(String value) {
        this.homePhoneNumber = value;
    }

    /**
     * Gets the value of the company property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Boolean isCompany() {
        return company;
    }

    /**
     * Sets the value of the company property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCompany(Boolean value) {
        this.company = value;
    }

    /**
     * Gets the value of the male property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Boolean isMale() {
        return male;
    }

    /**
     * Sets the value of the male property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMale(Boolean value) {
        this.male = value;
    }

    /**
     * Gets the value of the marketingViaEmailAllowed property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Boolean isMarketingViaEmailAllowed() {
        return marketingViaEmailAllowed;
    }

    /**
     * Sets the value of the marketingViaEmailAllowed property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMarketingViaEmailAllowed(Boolean value) {
        this.marketingViaEmailAllowed = value;
    }

    /**
     * Gets the value of the marketingViaPostAllowed property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Boolean isMarketingViaPostAllowed() {
        return marketingViaPostAllowed;
    }

    /**
     * Sets the value of the marketingViaPostAllowed property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMarketingViaPostAllowed(Boolean value) {
        this.marketingViaPostAllowed = value;
    }

    /**
     * Gets the value of the marketingViaSMSAllowed property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Boolean isMarketingViaSMSAllowed() {
        return marketingViaSMSAllowed;
    }

    /**
     * Sets the value of the marketingViaSMSAllowed property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMarketingViaSMSAllowed(Boolean value) {
        this.marketingViaSMSAllowed = value;
    }

    /**
     * Gets the value of the mobilePhoneNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMobilePhoneNumber() {
        return mobilePhoneNumber;
    }

    /**
     * Sets the value of the mobilePhoneNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMobilePhoneNumber(String value) {
        this.mobilePhoneNumber = value;
    }

    /**
     * Gets the value of the password property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the value of the password property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPassword(String value) {
        this.password = value;
    }

    /**
     * Gets the value of the passwordHash property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPasswordHash() {
        return passwordHash;
    }

    /**
     * Sets the value of the passwordHash property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPasswordHash(String value) {
        this.passwordHash = value;
    }

    /**
     * Gets the value of the postcode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPostcode() {
        return postcode;
    }

    /**
     * Sets the value of the postcode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPostcode(String value) {
        this.postcode = value;
    }

    /**
     * Gets the value of the state property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getState() {
        return state;
    }

    /**
     * Sets the value of the state property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setState(String value) {
        this.state = value;
    }

    /**
     * Gets the value of the street property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStreet() {
        return street;
    }

    /**
     * Sets the value of the street property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStreet(String value) {
        this.street = value;
    }

    /**
     * Gets the value of the suburb property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSuburb() {
        return suburb;
    }

    /**
     * Sets the value of the suburb property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSuburb(String value) {
        this.suburb = value;
    }

    /**
     * Gets the value of the taxFileNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTaxFileNumber() {
        return taxFileNumber;
    }

    /**
     * Sets the value of the taxFileNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTaxFileNumber(String value) {
        this.taxFileNumber = value;
    }

    /**
     * Gets the value of the uniqueCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUniqueCode() {
        return uniqueCode;
    }

    /**
     * Sets the value of the uniqueCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUniqueCode(String value) {
        this.uniqueCode = value;
    }

    /**
     * Gets the value of the studentId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Long getStudentId() {
        return studentId;
    }

    /**
     * Sets the value of the studentId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStudentId(Long value) {
        this.studentId = value;
    }

    /**
     * Gets the value of the tutorId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Long getTutorId() {
        return tutorId;
    }

    /**
     * Sets the value of the tutorId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTutorId(Long value) {
        this.tutorId = value;
    }

    /**
     * Gets the value of the middleName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMiddleName() {
        return middleName;
    }

    /**
     * Sets the value of the middleName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMiddleName(String value) {
        this.middleName = value;
    }

    /**
     * Gets the value of the abn property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAbn() {
        return abn;
    }

    /**
     * Sets the value of the abn property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAbn(String value) {
        this.abn = value;
    }

}
