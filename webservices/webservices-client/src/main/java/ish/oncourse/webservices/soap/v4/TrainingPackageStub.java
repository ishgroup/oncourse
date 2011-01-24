
package ish.oncourse.webservices.soap.v4;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for trainingPackageStub complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="trainingPackageStub">
 *   &lt;complexContent>
 *     &lt;extension base="{http://v4.soap.webservices.oncourse.ish/}soapReferenceStub">
 *       &lt;sequence>
 *         &lt;element name="copyrightCategory" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="copyrightContract" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="created" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="developer" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="endorsementFrom" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="endorsementTo" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="ishVersion" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="modified" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="nationalISC" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="purchaseFrom" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="title" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="type" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "trainingPackageStub", propOrder = {
    "copyrightCategory",
    "copyrightContract",
    "created",
    "developer",
    "endorsementFrom",
    "endorsementTo",
    "ishVersion",
    "modified",
    "nationalISC",
    "purchaseFrom",
    "title",
    "type"
})
public class TrainingPackageStub
    extends SoapReferenceStub
{

    protected String copyrightCategory;
    protected String copyrightContract;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar created;
    protected String developer;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar endorsementFrom;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar endorsementTo;
    protected Long ishVersion;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar modified;
    protected String nationalISC;
    protected String purchaseFrom;
    protected String title;
    protected String type;

    /**
     * Gets the value of the copyrightCategory property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCopyrightCategory() {
        return copyrightCategory;
    }

    /**
     * Sets the value of the copyrightCategory property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCopyrightCategory(String value) {
        this.copyrightCategory = value;
    }

    /**
     * Gets the value of the copyrightContract property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCopyrightContract() {
        return copyrightContract;
    }

    /**
     * Sets the value of the copyrightContract property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCopyrightContract(String value) {
        this.copyrightContract = value;
    }

    /**
     * Gets the value of the created property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getCreated() {
        return created;
    }

    /**
     * Sets the value of the created property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setCreated(XMLGregorianCalendar value) {
        this.created = value;
    }

    /**
     * Gets the value of the developer property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDeveloper() {
        return developer;
    }

    /**
     * Sets the value of the developer property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDeveloper(String value) {
        this.developer = value;
    }

    /**
     * Gets the value of the endorsementFrom property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getEndorsementFrom() {
        return endorsementFrom;
    }

    /**
     * Sets the value of the endorsementFrom property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setEndorsementFrom(XMLGregorianCalendar value) {
        this.endorsementFrom = value;
    }

    /**
     * Gets the value of the endorsementTo property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getEndorsementTo() {
        return endorsementTo;
    }

    /**
     * Sets the value of the endorsementTo property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setEndorsementTo(XMLGregorianCalendar value) {
        this.endorsementTo = value;
    }

    /**
     * Gets the value of the ishVersion property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getIshVersion() {
        return ishVersion;
    }

    /**
     * Sets the value of the ishVersion property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setIshVersion(Long value) {
        this.ishVersion = value;
    }

    /**
     * Gets the value of the modified property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getModified() {
        return modified;
    }

    /**
     * Sets the value of the modified property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setModified(XMLGregorianCalendar value) {
        this.modified = value;
    }

    /**
     * Gets the value of the nationalISC property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNationalISC() {
        return nationalISC;
    }

    /**
     * Sets the value of the nationalISC property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNationalISC(String value) {
        this.nationalISC = value;
    }

    /**
     * Gets the value of the purchaseFrom property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPurchaseFrom() {
        return purchaseFrom;
    }

    /**
     * Sets the value of the purchaseFrom property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPurchaseFrom(String value) {
        this.purchaseFrom = value;
    }

    /**
     * Gets the value of the title property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the value of the title property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTitle(String value) {
        this.title = value;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setType(String value) {
        this.type = value;
    }

}
