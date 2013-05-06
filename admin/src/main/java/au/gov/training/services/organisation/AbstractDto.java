
package au.gov.training.services.organisation;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for AbstractDto complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AbstractDto">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ActionOnEntity" type="{http://training.gov.au/services/}ActionOnEntity" minOccurs="0"/>
 *         &lt;element name="EndDate" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         &lt;element name="StartDate" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AbstractDto", propOrder = {
    "actionOnEntity",
    "endDate",
    "startDate"
})
@XmlSeeAlso({
    Classification.class,
    Url.class,
    ClassificationValue.class,
    ResponsibleLegalPerson.class,
    OrganisationLocation.class,
    TradingName.class,
    Scope.class,
    OrganisationCode.class,
    RegistrationPeriod.class,
    RegistrationManagerAssignment.class,
    DataManagerAssignment.class,
    Contact.class,
    RtoRestriction.class
})
public class AbstractDto {

    @XmlElement(name = "ActionOnEntity")
    protected ActionOnEntity actionOnEntity;
    @XmlElementRef(name = "EndDate", namespace = "http://training.gov.au/services/", type = JAXBElement.class, required = false)
    protected JAXBElement<XMLGregorianCalendar> endDate;
    @XmlElementRef(name = "StartDate", namespace = "http://training.gov.au/services/", type = JAXBElement.class, required = false)
    protected JAXBElement<XMLGregorianCalendar> startDate;

    /**
     * Gets the value of the actionOnEntity property.
     * 
     * @return
     *     possible object is
     *     {@link ActionOnEntity }
     *     
     */
    public ActionOnEntity getActionOnEntity() {
        return actionOnEntity;
    }

    /**
     * Sets the value of the actionOnEntity property.
     * 
     * @param value
     *     allowed object is
     *     {@link ActionOnEntity }
     *     
     */
    public void setActionOnEntity(ActionOnEntity value) {
        this.actionOnEntity = value;
    }

    /**
     * Gets the value of the endDate property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public JAXBElement<XMLGregorianCalendar> getEndDate() {
        return endDate;
    }

    /**
     * Sets the value of the endDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public void setEndDate(JAXBElement<XMLGregorianCalendar> value) {
        this.endDate = value;
    }

    /**
     * Gets the value of the startDate property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public JAXBElement<XMLGregorianCalendar> getStartDate() {
        return startDate;
    }

    /**
     * Sets the value of the startDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public void setStartDate(JAXBElement<XMLGregorianCalendar> value) {
        this.startDate = value;
    }

}
