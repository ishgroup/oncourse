
package ish.oncourse.webservices.soap.v4;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for countryStub complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="countryStub">
 *   &lt;complexContent>
 *     &lt;extension base="{http://v4.soap.webservices.oncourse.ish/}soapReferenceStub">
 *       &lt;sequence>
 *         &lt;element name="asccssCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="created" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="ishVersion" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="isoCodeAlpha2" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="isoCodeAlpha3" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="isoCodeNumeric" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="modified" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="saccCode" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "countryStub", propOrder = {
    "asccssCode",
    "created",
    "ishVersion",
    "isoCodeAlpha2",
    "isoCodeAlpha3",
    "isoCodeNumeric",
    "modified",
    "name",
    "saccCode"
})
public class CountryStub
    extends SoapReferenceStub
{

    protected String asccssCode;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar created;
    protected Long ishVersion;
    protected String isoCodeAlpha2;
    protected String isoCodeAlpha3;
    protected Integer isoCodeNumeric;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar modified;
    protected String name;
    protected Integer saccCode;

    /**
     * Gets the value of the asccssCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAsccssCode() {
        return asccssCode;
    }

    /**
     * Sets the value of the asccssCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAsccssCode(String value) {
        this.asccssCode = value;
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
     * Gets the value of the isoCodeAlpha2 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIsoCodeAlpha2() {
        return isoCodeAlpha2;
    }

    /**
     * Sets the value of the isoCodeAlpha2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIsoCodeAlpha2(String value) {
        this.isoCodeAlpha2 = value;
    }

    /**
     * Gets the value of the isoCodeAlpha3 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIsoCodeAlpha3() {
        return isoCodeAlpha3;
    }

    /**
     * Sets the value of the isoCodeAlpha3 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIsoCodeAlpha3(String value) {
        this.isoCodeAlpha3 = value;
    }

    /**
     * Gets the value of the isoCodeNumeric property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getIsoCodeNumeric() {
        return isoCodeNumeric;
    }

    /**
     * Sets the value of the isoCodeNumeric property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setIsoCodeNumeric(Integer value) {
        this.isoCodeNumeric = value;
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
     * Gets the value of the saccCode property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getSaccCode() {
        return saccCode;
    }

    /**
     * Sets the value of the saccCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setSaccCode(Integer value) {
        this.saccCode = value;
    }

}
