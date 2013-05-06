
package au.gov.training.services.organisation;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for RtoUpdateRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RtoUpdateRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ClassificationList" type="{http://training.gov.au/services/}ClassificationList" minOccurs="0"/>
 *         &lt;element name="Code" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="CodeList" type="{http://training.gov.au/services/}OrganisationCodeList" minOccurs="0"/>
 *         &lt;element name="ContactList" type="{http://training.gov.au/services/}ContactList" minOccurs="0"/>
 *         &lt;element name="DeliveryNotificationList" type="{http://training.gov.au/services/}DeliveryNotificationList" minOccurs="0"/>
 *         &lt;element name="LocationList" type="{http://training.gov.au/services/}LocationList" minOccurs="0"/>
 *         &lt;element name="RegistrationPeriodList" type="{http://training.gov.au/services/}RegistrationPeriodList" minOccurs="0"/>
 *         &lt;element name="ResponsibleLegalPersonList" type="{http://training.gov.au/services/}ResponsibleLegalPersonList" minOccurs="0"/>
 *         &lt;element name="RestrictionList" type="{http://training.gov.au/services/}RestrictionList" minOccurs="0"/>
 *         &lt;element name="ScopeList" type="{http://training.gov.au/services/}ScopeList" minOccurs="0"/>
 *         &lt;element name="TradingNameList" type="{http://training.gov.au/services/}TradingNameList" minOccurs="0"/>
 *         &lt;element name="UrlList" type="{http://training.gov.au/services/}UrlList" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RtoUpdateRequest", propOrder = {
    "classificationList",
    "code",
    "codeList",
    "contactList",
    "deliveryNotificationList",
    "locationList",
    "registrationPeriodList",
    "responsibleLegalPersonList",
    "restrictionList",
    "scopeList",
    "tradingNameList",
    "urlList"
})
public class RtoUpdateRequest {

    @XmlElementRef(name = "ClassificationList", namespace = "http://training.gov.au/services/", type = JAXBElement.class, required = false)
    protected JAXBElement<ClassificationList> classificationList;
    @XmlElement(name = "Code", required = true, nillable = true)
    protected String code;
    @XmlElementRef(name = "CodeList", namespace = "http://training.gov.au/services/", type = JAXBElement.class, required = false)
    protected JAXBElement<OrganisationCodeList> codeList;
    @XmlElementRef(name = "ContactList", namespace = "http://training.gov.au/services/", type = JAXBElement.class, required = false)
    protected JAXBElement<ContactList> contactList;
    @XmlElementRef(name = "DeliveryNotificationList", namespace = "http://training.gov.au/services/", type = JAXBElement.class, required = false)
    protected JAXBElement<DeliveryNotificationList> deliveryNotificationList;
    @XmlElementRef(name = "LocationList", namespace = "http://training.gov.au/services/", type = JAXBElement.class, required = false)
    protected JAXBElement<LocationList> locationList;
    @XmlElementRef(name = "RegistrationPeriodList", namespace = "http://training.gov.au/services/", type = JAXBElement.class, required = false)
    protected JAXBElement<RegistrationPeriodList> registrationPeriodList;
    @XmlElementRef(name = "ResponsibleLegalPersonList", namespace = "http://training.gov.au/services/", type = JAXBElement.class, required = false)
    protected JAXBElement<ResponsibleLegalPersonList> responsibleLegalPersonList;
    @XmlElementRef(name = "RestrictionList", namespace = "http://training.gov.au/services/", type = JAXBElement.class, required = false)
    protected JAXBElement<RestrictionList> restrictionList;
    @XmlElementRef(name = "ScopeList", namespace = "http://training.gov.au/services/", type = JAXBElement.class, required = false)
    protected JAXBElement<ScopeList> scopeList;
    @XmlElementRef(name = "TradingNameList", namespace = "http://training.gov.au/services/", type = JAXBElement.class, required = false)
    protected JAXBElement<TradingNameList> tradingNameList;
    @XmlElementRef(name = "UrlList", namespace = "http://training.gov.au/services/", type = JAXBElement.class, required = false)
    protected JAXBElement<UrlList> urlList;

    /**
     * Gets the value of the classificationList property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ClassificationList }{@code >}
     *     
     */
    public JAXBElement<ClassificationList> getClassificationList() {
        return classificationList;
    }

    /**
     * Sets the value of the classificationList property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ClassificationList }{@code >}
     *     
     */
    public void setClassificationList(JAXBElement<ClassificationList> value) {
        this.classificationList = value;
    }

    /**
     * Gets the value of the code property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCode() {
        return code;
    }

    /**
     * Sets the value of the code property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCode(String value) {
        this.code = value;
    }

    /**
     * Gets the value of the codeList property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link OrganisationCodeList }{@code >}
     *     
     */
    public JAXBElement<OrganisationCodeList> getCodeList() {
        return codeList;
    }

    /**
     * Sets the value of the codeList property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link OrganisationCodeList }{@code >}
     *     
     */
    public void setCodeList(JAXBElement<OrganisationCodeList> value) {
        this.codeList = value;
    }

    /**
     * Gets the value of the contactList property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ContactList }{@code >}
     *     
     */
    public JAXBElement<ContactList> getContactList() {
        return contactList;
    }

    /**
     * Sets the value of the contactList property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ContactList }{@code >}
     *     
     */
    public void setContactList(JAXBElement<ContactList> value) {
        this.contactList = value;
    }

    /**
     * Gets the value of the deliveryNotificationList property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link DeliveryNotificationList }{@code >}
     *     
     */
    public JAXBElement<DeliveryNotificationList> getDeliveryNotificationList() {
        return deliveryNotificationList;
    }

    /**
     * Sets the value of the deliveryNotificationList property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link DeliveryNotificationList }{@code >}
     *     
     */
    public void setDeliveryNotificationList(JAXBElement<DeliveryNotificationList> value) {
        this.deliveryNotificationList = value;
    }

    /**
     * Gets the value of the locationList property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link LocationList }{@code >}
     *     
     */
    public JAXBElement<LocationList> getLocationList() {
        return locationList;
    }

    /**
     * Sets the value of the locationList property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link LocationList }{@code >}
     *     
     */
    public void setLocationList(JAXBElement<LocationList> value) {
        this.locationList = value;
    }

    /**
     * Gets the value of the registrationPeriodList property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link RegistrationPeriodList }{@code >}
     *     
     */
    public JAXBElement<RegistrationPeriodList> getRegistrationPeriodList() {
        return registrationPeriodList;
    }

    /**
     * Sets the value of the registrationPeriodList property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link RegistrationPeriodList }{@code >}
     *     
     */
    public void setRegistrationPeriodList(JAXBElement<RegistrationPeriodList> value) {
        this.registrationPeriodList = value;
    }

    /**
     * Gets the value of the responsibleLegalPersonList property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ResponsibleLegalPersonList }{@code >}
     *     
     */
    public JAXBElement<ResponsibleLegalPersonList> getResponsibleLegalPersonList() {
        return responsibleLegalPersonList;
    }

    /**
     * Sets the value of the responsibleLegalPersonList property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ResponsibleLegalPersonList }{@code >}
     *     
     */
    public void setResponsibleLegalPersonList(JAXBElement<ResponsibleLegalPersonList> value) {
        this.responsibleLegalPersonList = value;
    }

    /**
     * Gets the value of the restrictionList property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link RestrictionList }{@code >}
     *     
     */
    public JAXBElement<RestrictionList> getRestrictionList() {
        return restrictionList;
    }

    /**
     * Sets the value of the restrictionList property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link RestrictionList }{@code >}
     *     
     */
    public void setRestrictionList(JAXBElement<RestrictionList> value) {
        this.restrictionList = value;
    }

    /**
     * Gets the value of the scopeList property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ScopeList }{@code >}
     *     
     */
    public JAXBElement<ScopeList> getScopeList() {
        return scopeList;
    }

    /**
     * Sets the value of the scopeList property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ScopeList }{@code >}
     *     
     */
    public void setScopeList(JAXBElement<ScopeList> value) {
        this.scopeList = value;
    }

    /**
     * Gets the value of the tradingNameList property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link TradingNameList }{@code >}
     *     
     */
    public JAXBElement<TradingNameList> getTradingNameList() {
        return tradingNameList;
    }

    /**
     * Sets the value of the tradingNameList property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link TradingNameList }{@code >}
     *     
     */
    public void setTradingNameList(JAXBElement<TradingNameList> value) {
        this.tradingNameList = value;
    }

    /**
     * Gets the value of the urlList property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link UrlList }{@code >}
     *     
     */
    public JAXBElement<UrlList> getUrlList() {
        return urlList;
    }

    /**
     * Sets the value of the urlList property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link UrlList }{@code >}
     *     
     */
    public void setUrlList(JAXBElement<UrlList> value) {
        this.urlList = value;
    }

}
