
package ish.oncourse.webservices.v25.stubs.replication;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for concessionTypeStub complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="concessionTypeStub"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://repl.v25.soap.webservices.oncourse.ish/}replicationStub"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="credentialExpiryDays" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="hasConcessionNumber" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="hasExpiryDate" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="isConcession" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="isEnabled" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="requiresCredentialCheck" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="createdBy" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "concessionTypeStub", propOrder = {
    "credentialExpiryDays",
    "hasConcessionNumber",
    "hasExpiryDate",
    "isConcession",
    "isEnabled",
    "name",
    "requiresCredentialCheck",
    "createdBy"
})
public class ConcessionTypeStub
    extends ReplicationStub
{

    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter4 .class)
    @XmlSchemaType(name = "int")
    protected Integer credentialExpiryDays;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter1 .class)
    @XmlSchemaType(name = "boolean")
    protected Boolean hasConcessionNumber;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter1 .class)
    @XmlSchemaType(name = "boolean")
    protected Boolean hasExpiryDate;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter1 .class)
    @XmlSchemaType(name = "boolean")
    protected Boolean isConcession;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter1 .class)
    @XmlSchemaType(name = "boolean")
    protected Boolean isEnabled;
    @XmlElement(required = true)
    protected String name;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter1 .class)
    @XmlSchemaType(name = "boolean")
    protected Boolean requiresCredentialCheck;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter3 .class)
    @XmlSchemaType(name = "long")
    protected Long createdBy;

    /**
     * Gets the value of the credentialExpiryDays property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Integer getCredentialExpiryDays() {
        return credentialExpiryDays;
    }

    /**
     * Sets the value of the credentialExpiryDays property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCredentialExpiryDays(Integer value) {
        this.credentialExpiryDays = value;
    }

    /**
     * Gets the value of the hasConcessionNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Boolean isHasConcessionNumber() {
        return hasConcessionNumber;
    }

    /**
     * Sets the value of the hasConcessionNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHasConcessionNumber(Boolean value) {
        this.hasConcessionNumber = value;
    }

    /**
     * Gets the value of the hasExpiryDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Boolean isHasExpiryDate() {
        return hasExpiryDate;
    }

    /**
     * Sets the value of the hasExpiryDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHasExpiryDate(Boolean value) {
        this.hasExpiryDate = value;
    }

    /**
     * Gets the value of the isConcession property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Boolean isIsConcession() {
        return isConcession;
    }

    /**
     * Sets the value of the isConcession property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIsConcession(Boolean value) {
        this.isConcession = value;
    }

    /**
     * Gets the value of the isEnabled property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Boolean isIsEnabled() {
        return isEnabled;
    }

    /**
     * Sets the value of the isEnabled property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIsEnabled(Boolean value) {
        this.isEnabled = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the requiresCredentialCheck property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Boolean isRequiresCredentialCheck() {
        return requiresCredentialCheck;
    }

    /**
     * Sets the value of the requiresCredentialCheck property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRequiresCredentialCheck(Boolean value) {
        this.requiresCredentialCheck = value;
    }

    /**
     * Gets the value of the createdBy property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Long getCreatedBy() {
        return createdBy;
    }

    /**
     * Sets the value of the createdBy property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCreatedBy(Long value) {
        this.createdBy = value;
    }

}
