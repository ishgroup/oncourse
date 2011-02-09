
package ish.oncourse.webservices.v4.stubs.replication;

import java.util.Date;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.w3._2001.xmlschema.Adapter1;


/**
 * <p>Java class for contactStub complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="contactStub">
 *   &lt;complexContent>
 *     &lt;extension base="{http://repl.v4.soap.webservices.oncourse.ish/}replicationStub">
 *       &lt;sequence>
 *         &lt;element name="businessPhoneNumber" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="cookieHash" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="countryId" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="created" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="dateOfBirth" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="emailAddress" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="familyName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="faxNumber" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="givenName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="homePhoneNumber" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="isCompany" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="isMale" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="isMarketingViaEmailAllowed" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="isMarketingViaPostAllowed" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="isMarketingViaSMSAllowed" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="mobilePhoneNumber" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="modified" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="password" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="passwordHash" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="postcode" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="state" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="street" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="suburb" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="taxFileNumber" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="uniqueCode" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="college" type="{http://repl.v4.soap.webservices.oncourse.ish/}replicationStub"/>
 *         &lt;element name="student" type="{http://repl.v4.soap.webservices.oncourse.ish/}replicationStub"/>
 *         &lt;element name="tutor" type="{http://repl.v4.soap.webservices.oncourse.ish/}replicationStub"/>
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
    "created",
    "dateOfBirth",
    "emailAddress",
    "familyName",
    "faxNumber",
    "givenName",
    "homePhoneNumber",
    "isCompany",
    "isMale",
    "isMarketingViaEmailAllowed",
    "isMarketingViaPostAllowed",
    "isMarketingViaSMSAllowed",
    "mobilePhoneNumber",
    "modified",
    "password",
    "passwordHash",
    "postcode",
    "state",
    "street",
    "suburb",
    "taxFileNumber",
    "uniqueCode",
    "college",
    "student",
    "tutor"
})
public class ContactStub
    extends ReplicationStub
{

    @XmlElement(required = true)
    protected String businessPhoneNumber;
    @XmlElement(required = true)
    protected String cookieHash;
    protected long countryId;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter1 .class)
    @XmlSchemaType(name = "dateTime")
    protected Date created;
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
    protected boolean isCompany;
    protected boolean isMale;
    protected boolean isMarketingViaEmailAllowed;
    protected boolean isMarketingViaPostAllowed;
    protected boolean isMarketingViaSMSAllowed;
    @XmlElement(required = true)
    protected String mobilePhoneNumber;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter1 .class)
    @XmlSchemaType(name = "dateTime")
    protected Date modified;
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
    @XmlElement(required = true)
    protected ReplicationStub college;
    @XmlElement(required = true)
    protected ReplicationStub student;
    @XmlElement(required = true)
    protected ReplicationStub tutor;

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
     */
    public long getCountryId() {
        return countryId;
    }

    /**
     * Sets the value of the countryId property.
     * 
     */
    public void setCountryId(long value) {
        this.countryId = value;
    }

    /**
     * Gets the value of the created property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Date getCreated() {
        return created;
    }

    /**
     * Sets the value of the created property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCreated(Date value) {
        this.created = value;
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
     * Gets the value of the isCompany property.
     * 
     */
    public boolean isIsCompany() {
        return isCompany;
    }

    /**
     * Sets the value of the isCompany property.
     * 
     */
    public void setIsCompany(boolean value) {
        this.isCompany = value;
    }

    /**
     * Gets the value of the isMale property.
     * 
     */
    public boolean isIsMale() {
        return isMale;
    }

    /**
     * Sets the value of the isMale property.
     * 
     */
    public void setIsMale(boolean value) {
        this.isMale = value;
    }

    /**
     * Gets the value of the isMarketingViaEmailAllowed property.
     * 
     */
    public boolean isIsMarketingViaEmailAllowed() {
        return isMarketingViaEmailAllowed;
    }

    /**
     * Sets the value of the isMarketingViaEmailAllowed property.
     * 
     */
    public void setIsMarketingViaEmailAllowed(boolean value) {
        this.isMarketingViaEmailAllowed = value;
    }

    /**
     * Gets the value of the isMarketingViaPostAllowed property.
     * 
     */
    public boolean isIsMarketingViaPostAllowed() {
        return isMarketingViaPostAllowed;
    }

    /**
     * Sets the value of the isMarketingViaPostAllowed property.
     * 
     */
    public void setIsMarketingViaPostAllowed(boolean value) {
        this.isMarketingViaPostAllowed = value;
    }

    /**
     * Gets the value of the isMarketingViaSMSAllowed property.
     * 
     */
    public boolean isIsMarketingViaSMSAllowed() {
        return isMarketingViaSMSAllowed;
    }

    /**
     * Sets the value of the isMarketingViaSMSAllowed property.
     * 
     */
    public void setIsMarketingViaSMSAllowed(boolean value) {
        this.isMarketingViaSMSAllowed = value;
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
     * Gets the value of the modified property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Date getModified() {
        return modified;
    }

    /**
     * Sets the value of the modified property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setModified(Date value) {
        this.modified = value;
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
     * Gets the value of the college property.
     * 
     * @return
     *     possible object is
     *     {@link ReplicationStub }
     *     
     */
    public ReplicationStub getCollege() {
        return college;
    }

    /**
     * Sets the value of the college property.
     * 
     * @param value
     *     allowed object is
     *     {@link ReplicationStub }
     *     
     */
    public void setCollege(ReplicationStub value) {
        this.college = value;
    }

    /**
     * Gets the value of the student property.
     * 
     * @return
     *     possible object is
     *     {@link ReplicationStub }
     *     
     */
    public ReplicationStub getStudent() {
        return student;
    }

    /**
     * Sets the value of the student property.
     * 
     * @param value
     *     allowed object is
     *     {@link ReplicationStub }
     *     
     */
    public void setStudent(ReplicationStub value) {
        this.student = value;
    }

    /**
     * Gets the value of the tutor property.
     * 
     * @return
     *     possible object is
     *     {@link ReplicationStub }
     *     
     */
    public ReplicationStub getTutor() {
        return tutor;
    }

    /**
     * Sets the value of the tutor property.
     * 
     * @param value
     *     allowed object is
     *     {@link ReplicationStub }
     *     
     */
    public void setTutor(ReplicationStub value) {
        this.tutor = value;
    }

}
