
package au.gov.usi._2020.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for BirthCertificateDocumentType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="BirthCertificateDocumentType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://usi.gov.au/2020/ws}DVSDocumentType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="RegistrationNumber" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;minLength value="1"/&gt;
 *               &lt;maxLength value="10"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="RegistrationState" type="{http://usi.gov.au/2020/ws}StateListType"/&gt;
 *         &lt;element name="RegistrationDate" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/&gt;
 *         &lt;element name="DatePrinted" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/&gt;
 *         &lt;element name="CertificateNumber" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;minLength value="1"/&gt;
 *               &lt;maxLength value="11"/&gt;
 *               &lt;pattern value="[A-Za-z0-9]*"/&gt;
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
@XmlType(name = "BirthCertificateDocumentType", propOrder = {
    "registrationNumber",
    "registrationState",
    "registrationDate",
    "datePrinted",
    "certificateNumber"
})
public class BirthCertificateDocumentType
    extends DVSDocumentType
{

    @XmlElement(name = "RegistrationNumber")
    protected String registrationNumber;
    @XmlElement(name = "RegistrationState", required = true)
    @XmlSchemaType(name = "string")
    protected StateListType registrationState;
    @XmlElement(name = "RegistrationDate")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar registrationDate;
    @XmlElement(name = "DatePrinted")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar datePrinted;
    @XmlElement(name = "CertificateNumber")
    protected String certificateNumber;

    /**
     * Gets the value of the registrationNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRegistrationNumber() {
        return registrationNumber;
    }

    /**
     * Sets the value of the registrationNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRegistrationNumber(String value) {
        this.registrationNumber = value;
    }

    /**
     * Gets the value of the registrationState property.
     * 
     * @return
     *     possible object is
     *     {@link StateListType }
     *     
     */
    public StateListType getRegistrationState() {
        return registrationState;
    }

    /**
     * Sets the value of the registrationState property.
     * 
     * @param value
     *     allowed object is
     *     {@link StateListType }
     *     
     */
    public void setRegistrationState(StateListType value) {
        this.registrationState = value;
    }

    /**
     * Gets the value of the registrationDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getRegistrationDate() {
        return registrationDate;
    }

    /**
     * Sets the value of the registrationDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setRegistrationDate(XMLGregorianCalendar value) {
        this.registrationDate = value;
    }

    /**
     * Gets the value of the datePrinted property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDatePrinted() {
        return datePrinted;
    }

    /**
     * Sets the value of the datePrinted property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDatePrinted(XMLGregorianCalendar value) {
        this.datePrinted = value;
    }

    /**
     * Gets the value of the certificateNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCertificateNumber() {
        return certificateNumber;
    }

    /**
     * Sets the value of the certificateNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCertificateNumber(String value) {
        this.certificateNumber = value;
    }

}
