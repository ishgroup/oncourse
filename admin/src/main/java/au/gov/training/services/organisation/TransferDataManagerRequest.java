
package au.gov.training.services.organisation;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for TransferDataManagerRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TransferDataManagerRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="DataManagerCode" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="EffectiveDate" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         &lt;element name="OrganisationCode" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="RegistrationManagerCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TransferDataManagerRequest", propOrder = {
    "dataManagerCode",
    "effectiveDate",
    "organisationCode",
    "registrationManagerCode"
})
public class TransferDataManagerRequest {

    @XmlElement(name = "DataManagerCode", required = true, nillable = true)
    protected String dataManagerCode;
    @XmlElementRef(name = "EffectiveDate", namespace = "http://training.gov.au/services/", type = JAXBElement.class, required = false)
    protected JAXBElement<XMLGregorianCalendar> effectiveDate;
    @XmlElement(name = "OrganisationCode", required = true, nillable = true)
    protected String organisationCode;
    @XmlElementRef(name = "RegistrationManagerCode", namespace = "http://training.gov.au/services/", type = JAXBElement.class, required = false)
    protected JAXBElement<String> registrationManagerCode;

    /**
     * Gets the value of the dataManagerCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDataManagerCode() {
        return dataManagerCode;
    }

    /**
     * Sets the value of the dataManagerCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDataManagerCode(String value) {
        this.dataManagerCode = value;
    }

    /**
     * Gets the value of the effectiveDate property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public JAXBElement<XMLGregorianCalendar> getEffectiveDate() {
        return effectiveDate;
    }

    /**
     * Sets the value of the effectiveDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public void setEffectiveDate(JAXBElement<XMLGregorianCalendar> value) {
        this.effectiveDate = value;
    }

    /**
     * Gets the value of the organisationCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrganisationCode() {
        return organisationCode;
    }

    /**
     * Sets the value of the organisationCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrganisationCode(String value) {
        this.organisationCode = value;
    }

    /**
     * Gets the value of the registrationManagerCode property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getRegistrationManagerCode() {
        return registrationManagerCode;
    }

    /**
     * Sets the value of the registrationManagerCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setRegistrationManagerCode(JAXBElement<String> value) {
        this.registrationManagerCode = value;
    }

}
