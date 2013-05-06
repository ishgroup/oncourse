
package au.gov.training.services.organisation;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for DeliveryNotification complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DeliveryNotification">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ActionOnEntity" type="{http://training.gov.au/services/}ActionOnEntity" minOccurs="0"/>
 *         &lt;element name="DateOfChange" type="{http://www.w3.org/2001/XMLSchema}date"/>
 *         &lt;element name="GeographicAreas" type="{http://training.gov.au/services/}ArrayOfDeliveryNotificationGeographicArea"/>
 *         &lt;element name="IsCessation" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="NotificationDate" type="{http://www.w3.org/2001/XMLSchema}date"/>
 *         &lt;element name="Scopes" type="{http://training.gov.au/services/}ArrayOfDeliveryNotificationScope" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DeliveryNotification", propOrder = {
    "actionOnEntity",
    "dateOfChange",
    "geographicAreas",
    "isCessation",
    "notificationDate",
    "scopes"
})
public class DeliveryNotification {

    @XmlElement(name = "ActionOnEntity")
    protected ActionOnEntity actionOnEntity;
    @XmlElement(name = "DateOfChange", required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar dateOfChange;
    @XmlElement(name = "GeographicAreas", required = true, nillable = true)
    protected ArrayOfDeliveryNotificationGeographicArea geographicAreas;
    @XmlElement(name = "IsCessation")
    protected boolean isCessation;
    @XmlElement(name = "NotificationDate", required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar notificationDate;
    @XmlElementRef(name = "Scopes", namespace = "http://training.gov.au/services/", type = JAXBElement.class, required = false)
    protected JAXBElement<ArrayOfDeliveryNotificationScope> scopes;

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
     * Gets the value of the dateOfChange property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDateOfChange() {
        return dateOfChange;
    }

    /**
     * Sets the value of the dateOfChange property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDateOfChange(XMLGregorianCalendar value) {
        this.dateOfChange = value;
    }

    /**
     * Gets the value of the geographicAreas property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfDeliveryNotificationGeographicArea }
     *     
     */
    public ArrayOfDeliveryNotificationGeographicArea getGeographicAreas() {
        return geographicAreas;
    }

    /**
     * Sets the value of the geographicAreas property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfDeliveryNotificationGeographicArea }
     *     
     */
    public void setGeographicAreas(ArrayOfDeliveryNotificationGeographicArea value) {
        this.geographicAreas = value;
    }

    /**
     * Gets the value of the isCessation property.
     * 
     */
    public boolean isIsCessation() {
        return isCessation;
    }

    /**
     * Sets the value of the isCessation property.
     * 
     */
    public void setIsCessation(boolean value) {
        this.isCessation = value;
    }

    /**
     * Gets the value of the notificationDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getNotificationDate() {
        return notificationDate;
    }

    /**
     * Sets the value of the notificationDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setNotificationDate(XMLGregorianCalendar value) {
        this.notificationDate = value;
    }

    /**
     * Gets the value of the scopes property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfDeliveryNotificationScope }{@code >}
     *     
     */
    public JAXBElement<ArrayOfDeliveryNotificationScope> getScopes() {
        return scopes;
    }

    /**
     * Sets the value of the scopes property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfDeliveryNotificationScope }{@code >}
     *     
     */
    public void setScopes(JAXBElement<ArrayOfDeliveryNotificationScope> value) {
        this.scopes = value;
    }

}
