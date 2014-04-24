
package ish.oncourse.webservices.v6.stubs.replication;

import java.util.Date;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import ish.oncourse.webservices.util.adapters.StringToBooleanAdapter;
import ish.oncourse.webservices.util.adapters.StringToDateAdapter;
import ish.oncourse.webservices.util.adapters.StringToLongAdapter;


/**
 * <p>Java class for systemUserStub complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="systemUserStub">
 *   &lt;complexContent>
 *     &lt;extension base="{http://repl.v6.soap.webservices.oncourse.ish/}replicationStub">
 *       &lt;sequence>
 *         &lt;element name="editCMS" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="editTara" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="email" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="firstName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="surname" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="password" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="login" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="lastLoginIP" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="lastLoginOn" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="isActive" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="isAdmin" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="defaultAdministrationCentreId" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "systemUserStub", propOrder = {
    "editCMS",
    "editTara",
    "email",
    "firstName",
    "surname",
    "password",
    "login",
    "lastLoginIP",
    "lastLoginOn",
    "isActive",
    "isAdmin",
    "defaultAdministrationCentreId"
})
public class SystemUserStub
    extends ReplicationStub
{

    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(StringToBooleanAdapter.class)
    @XmlSchemaType(name = "boolean")
    protected Boolean editCMS;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(StringToBooleanAdapter.class)
    @XmlSchemaType(name = "boolean")
    protected Boolean editTara;
    @XmlElement(required = true)
    protected String email;
    @XmlElement(required = true)
    protected String firstName;
    @XmlElement(required = true)
    protected String surname;
    @XmlElement(required = true)
    protected String password;
    @XmlElement(required = true)
    protected String login;
    @XmlElement(required = true)
    protected String lastLoginIP;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(StringToDateAdapter.class)
    @XmlSchemaType(name = "dateTime")
    protected Date lastLoginOn;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(StringToBooleanAdapter.class)
    @XmlSchemaType(name = "boolean")
    protected Boolean isActive;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(StringToBooleanAdapter.class)
    @XmlSchemaType(name = "boolean")
    protected Boolean isAdmin;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(StringToLongAdapter.class)
    @XmlSchemaType(name = "long")
    protected Long defaultAdministrationCentreId;

    /**
     * Gets the value of the editCMS property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Boolean isEditCMS() {
        return editCMS;
    }

    /**
     * Sets the value of the editCMS property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEditCMS(Boolean value) {
        this.editCMS = value;
    }

    /**
     * Gets the value of the editTara property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Boolean isEditTara() {
        return editTara;
    }

    /**
     * Sets the value of the editTara property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEditTara(Boolean value) {
        this.editTara = value;
    }

    /**
     * Gets the value of the email property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the value of the email property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEmail(String value) {
        this.email = value;
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
     * Gets the value of the surname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSurname() {
        return surname;
    }

    /**
     * Sets the value of the surname property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSurname(String value) {
        this.surname = value;
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
     * Gets the value of the login property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLogin() {
        return login;
    }

    /**
     * Sets the value of the login property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLogin(String value) {
        this.login = value;
    }

    /**
     * Gets the value of the lastLoginIP property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLastLoginIP() {
        return lastLoginIP;
    }

    /**
     * Sets the value of the lastLoginIP property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLastLoginIP(String value) {
        this.lastLoginIP = value;
    }

    /**
     * Gets the value of the lastLoginOn property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Date getLastLoginOn() {
        return lastLoginOn;
    }

    /**
     * Sets the value of the lastLoginOn property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLastLoginOn(Date value) {
        this.lastLoginOn = value;
    }

    /**
     * Gets the value of the isActive property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Boolean isIsActive() {
        return isActive;
    }

    /**
     * Sets the value of the isActive property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIsActive(Boolean value) {
        this.isActive = value;
    }

    /**
     * Gets the value of the isAdmin property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Boolean isIsAdmin() {
        return isAdmin;
    }

    /**
     * Sets the value of the isAdmin property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIsAdmin(Boolean value) {
        this.isAdmin = value;
    }

    /**
     * Gets the value of the defaultAdministrationCentreId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Long getDefaultAdministrationCentreId() {
        return defaultAdministrationCentreId;
    }

    /**
     * Sets the value of the defaultAdministrationCentreId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDefaultAdministrationCentreId(Long value) {
        this.defaultAdministrationCentreId = value;
    }

}
