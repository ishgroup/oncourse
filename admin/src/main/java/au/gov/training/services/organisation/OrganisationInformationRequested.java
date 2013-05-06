
package au.gov.training.services.organisation;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for OrganisationInformationRequested complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="OrganisationInformationRequested">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ShowCodes" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="ShowContacts" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="ShowDataManagers" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="ShowExplicitScope" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="ShowImplicitScope" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="ShowLocations" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="ShowRegistrationManagers" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="ShowRegistrationPeriods" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="ShowResponsibleLegalPersons" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="ShowRestrictions" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="ShowRtoClassifications" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="ShowRtoDeliveryNotification" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="ShowTradingNames" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="ShowUrls" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OrganisationInformationRequested", propOrder = {
    "showCodes",
    "showContacts",
    "showDataManagers",
    "showExplicitScope",
    "showImplicitScope",
    "showLocations",
    "showRegistrationManagers",
    "showRegistrationPeriods",
    "showResponsibleLegalPersons",
    "showRestrictions",
    "showRtoClassifications",
    "showRtoDeliveryNotification",
    "showTradingNames",
    "showUrls"
})
public class OrganisationInformationRequested {

    @XmlElement(name = "ShowCodes")
    protected Boolean showCodes;
    @XmlElement(name = "ShowContacts")
    protected Boolean showContacts;
    @XmlElement(name = "ShowDataManagers")
    protected Boolean showDataManagers;
    @XmlElement(name = "ShowExplicitScope")
    protected Boolean showExplicitScope;
    @XmlElement(name = "ShowImplicitScope")
    protected Boolean showImplicitScope;
    @XmlElement(name = "ShowLocations")
    protected Boolean showLocations;
    @XmlElement(name = "ShowRegistrationManagers")
    protected Boolean showRegistrationManagers;
    @XmlElement(name = "ShowRegistrationPeriods")
    protected Boolean showRegistrationPeriods;
    @XmlElement(name = "ShowResponsibleLegalPersons")
    protected Boolean showResponsibleLegalPersons;
    @XmlElement(name = "ShowRestrictions")
    protected Boolean showRestrictions;
    @XmlElement(name = "ShowRtoClassifications")
    protected Boolean showRtoClassifications;
    @XmlElement(name = "ShowRtoDeliveryNotification")
    protected Boolean showRtoDeliveryNotification;
    @XmlElement(name = "ShowTradingNames")
    protected Boolean showTradingNames;
    @XmlElement(name = "ShowUrls")
    protected Boolean showUrls;

    /**
     * Gets the value of the showCodes property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isShowCodes() {
        return showCodes;
    }

    /**
     * Sets the value of the showCodes property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setShowCodes(Boolean value) {
        this.showCodes = value;
    }

    /**
     * Gets the value of the showContacts property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isShowContacts() {
        return showContacts;
    }

    /**
     * Sets the value of the showContacts property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setShowContacts(Boolean value) {
        this.showContacts = value;
    }

    /**
     * Gets the value of the showDataManagers property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isShowDataManagers() {
        return showDataManagers;
    }

    /**
     * Sets the value of the showDataManagers property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setShowDataManagers(Boolean value) {
        this.showDataManagers = value;
    }

    /**
     * Gets the value of the showExplicitScope property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isShowExplicitScope() {
        return showExplicitScope;
    }

    /**
     * Sets the value of the showExplicitScope property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setShowExplicitScope(Boolean value) {
        this.showExplicitScope = value;
    }

    /**
     * Gets the value of the showImplicitScope property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isShowImplicitScope() {
        return showImplicitScope;
    }

    /**
     * Sets the value of the showImplicitScope property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setShowImplicitScope(Boolean value) {
        this.showImplicitScope = value;
    }

    /**
     * Gets the value of the showLocations property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isShowLocations() {
        return showLocations;
    }

    /**
     * Sets the value of the showLocations property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setShowLocations(Boolean value) {
        this.showLocations = value;
    }

    /**
     * Gets the value of the showRegistrationManagers property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isShowRegistrationManagers() {
        return showRegistrationManagers;
    }

    /**
     * Sets the value of the showRegistrationManagers property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setShowRegistrationManagers(Boolean value) {
        this.showRegistrationManagers = value;
    }

    /**
     * Gets the value of the showRegistrationPeriods property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isShowRegistrationPeriods() {
        return showRegistrationPeriods;
    }

    /**
     * Sets the value of the showRegistrationPeriods property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setShowRegistrationPeriods(Boolean value) {
        this.showRegistrationPeriods = value;
    }

    /**
     * Gets the value of the showResponsibleLegalPersons property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isShowResponsibleLegalPersons() {
        return showResponsibleLegalPersons;
    }

    /**
     * Sets the value of the showResponsibleLegalPersons property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setShowResponsibleLegalPersons(Boolean value) {
        this.showResponsibleLegalPersons = value;
    }

    /**
     * Gets the value of the showRestrictions property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isShowRestrictions() {
        return showRestrictions;
    }

    /**
     * Sets the value of the showRestrictions property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setShowRestrictions(Boolean value) {
        this.showRestrictions = value;
    }

    /**
     * Gets the value of the showRtoClassifications property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isShowRtoClassifications() {
        return showRtoClassifications;
    }

    /**
     * Sets the value of the showRtoClassifications property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setShowRtoClassifications(Boolean value) {
        this.showRtoClassifications = value;
    }

    /**
     * Gets the value of the showRtoDeliveryNotification property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isShowRtoDeliveryNotification() {
        return showRtoDeliveryNotification;
    }

    /**
     * Sets the value of the showRtoDeliveryNotification property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setShowRtoDeliveryNotification(Boolean value) {
        this.showRtoDeliveryNotification = value;
    }

    /**
     * Gets the value of the showTradingNames property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isShowTradingNames() {
        return showTradingNames;
    }

    /**
     * Sets the value of the showTradingNames property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setShowTradingNames(Boolean value) {
        this.showTradingNames = value;
    }

    /**
     * Gets the value of the showUrls property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isShowUrls() {
        return showUrls;
    }

    /**
     * Sets the value of the showUrls property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setShowUrls(Boolean value) {
        this.showUrls = value;
    }

}
