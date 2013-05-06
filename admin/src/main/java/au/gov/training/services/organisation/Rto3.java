
package au.gov.training.services.organisation;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Rto3 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Rto3">
 *   &lt;complexContent>
 *     &lt;extension base="{http://training.gov.au/services/}Organisation2">
 *       &lt;sequence>
 *         &lt;element name="Classifications" type="{http://training.gov.au/services/}ArrayOfClassification" minOccurs="0"/>
 *         &lt;element name="DeliveryNotifications" type="{http://training.gov.au/services/}ArrayOfDeliveryNotification" minOccurs="0"/>
 *         &lt;element name="RegistrationManagers" type="{http://training.gov.au/services/}ArrayOfRegistrationManagerAssignment" minOccurs="0"/>
 *         &lt;element name="RegistrationPeriods" type="{http://training.gov.au/services/}ArrayOfRegistrationPeriod" minOccurs="0"/>
 *         &lt;element name="RegistrationStatus" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Restrictions" type="{http://training.gov.au/services/}ArrayOfRtoRestriction2" minOccurs="0"/>
 *         &lt;element name="Scopes" type="{http://training.gov.au/services/}ArrayOfScope2" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Rto3", propOrder = {
    "classifications",
    "deliveryNotifications",
    "registrationManagers",
    "registrationPeriods",
    "registrationStatus",
    "restrictions",
    "scopes"
})
public class Rto3
    extends Organisation2
{

    @XmlElementRef(name = "Classifications", namespace = "http://training.gov.au/services/", type = JAXBElement.class, required = false)
    protected JAXBElement<ArrayOfClassification> classifications;
    @XmlElementRef(name = "DeliveryNotifications", namespace = "http://training.gov.au/services/", type = JAXBElement.class, required = false)
    protected JAXBElement<ArrayOfDeliveryNotification> deliveryNotifications;
    @XmlElementRef(name = "RegistrationManagers", namespace = "http://training.gov.au/services/", type = JAXBElement.class, required = false)
    protected JAXBElement<ArrayOfRegistrationManagerAssignment> registrationManagers;
    @XmlElementRef(name = "RegistrationPeriods", namespace = "http://training.gov.au/services/", type = JAXBElement.class, required = false)
    protected JAXBElement<ArrayOfRegistrationPeriod> registrationPeriods;
    @XmlElementRef(name = "RegistrationStatus", namespace = "http://training.gov.au/services/", type = JAXBElement.class, required = false)
    protected JAXBElement<String> registrationStatus;
    @XmlElementRef(name = "Restrictions", namespace = "http://training.gov.au/services/", type = JAXBElement.class, required = false)
    protected JAXBElement<ArrayOfRtoRestriction2> restrictions;
    @XmlElementRef(name = "Scopes", namespace = "http://training.gov.au/services/", type = JAXBElement.class, required = false)
    protected JAXBElement<ArrayOfScope2> scopes;

    /**
     * Gets the value of the classifications property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfClassification }{@code >}
     *     
     */
    public JAXBElement<ArrayOfClassification> getClassifications() {
        return classifications;
    }

    /**
     * Sets the value of the classifications property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfClassification }{@code >}
     *     
     */
    public void setClassifications(JAXBElement<ArrayOfClassification> value) {
        this.classifications = value;
    }

    /**
     * Gets the value of the deliveryNotifications property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfDeliveryNotification }{@code >}
     *     
     */
    public JAXBElement<ArrayOfDeliveryNotification> getDeliveryNotifications() {
        return deliveryNotifications;
    }

    /**
     * Sets the value of the deliveryNotifications property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfDeliveryNotification }{@code >}
     *     
     */
    public void setDeliveryNotifications(JAXBElement<ArrayOfDeliveryNotification> value) {
        this.deliveryNotifications = value;
    }

    /**
     * Gets the value of the registrationManagers property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfRegistrationManagerAssignment }{@code >}
     *     
     */
    public JAXBElement<ArrayOfRegistrationManagerAssignment> getRegistrationManagers() {
        return registrationManagers;
    }

    /**
     * Sets the value of the registrationManagers property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfRegistrationManagerAssignment }{@code >}
     *     
     */
    public void setRegistrationManagers(JAXBElement<ArrayOfRegistrationManagerAssignment> value) {
        this.registrationManagers = value;
    }

    /**
     * Gets the value of the registrationPeriods property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfRegistrationPeriod }{@code >}
     *     
     */
    public JAXBElement<ArrayOfRegistrationPeriod> getRegistrationPeriods() {
        return registrationPeriods;
    }

    /**
     * Sets the value of the registrationPeriods property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfRegistrationPeriod }{@code >}
     *     
     */
    public void setRegistrationPeriods(JAXBElement<ArrayOfRegistrationPeriod> value) {
        this.registrationPeriods = value;
    }

    /**
     * Gets the value of the registrationStatus property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getRegistrationStatus() {
        return registrationStatus;
    }

    /**
     * Sets the value of the registrationStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setRegistrationStatus(JAXBElement<String> value) {
        this.registrationStatus = value;
    }

    /**
     * Gets the value of the restrictions property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfRtoRestriction2 }{@code >}
     *     
     */
    public JAXBElement<ArrayOfRtoRestriction2> getRestrictions() {
        return restrictions;
    }

    /**
     * Sets the value of the restrictions property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfRtoRestriction2 }{@code >}
     *     
     */
    public void setRestrictions(JAXBElement<ArrayOfRtoRestriction2> value) {
        this.restrictions = value;
    }

    /**
     * Gets the value of the scopes property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfScope2 }{@code >}
     *     
     */
    public JAXBElement<ArrayOfScope2> getScopes() {
        return scopes;
    }

    /**
     * Sets the value of the scopes property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfScope2 }{@code >}
     *     
     */
    public void setScopes(JAXBElement<ArrayOfScope2> value) {
        this.scopes = value;
    }

}
