
package au.gov.training.services.trainingcomponent;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for TrainingComponentInformationRequested complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TrainingComponentInformationRequested">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ShowClassifications" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="ShowCompletionMapping" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="ShowComponents" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="ShowContacts" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="ShowCurrencyPeriods" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="ShowDataManagers" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="ShowFiles" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="ShowMappingInformation" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="ShowRecognitionManagers" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="ShowReleases" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="ShowUnitGrid" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="ShowUsageRecommendation" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TrainingComponentInformationRequested", propOrder = {
    "showClassifications",
    "showCompletionMapping",
    "showComponents",
    "showContacts",
    "showCurrencyPeriods",
    "showDataManagers",
    "showFiles",
    "showMappingInformation",
    "showRecognitionManagers",
    "showReleases",
    "showUnitGrid",
    "showUsageRecommendation"
})
@XmlSeeAlso({
    TrainingComponentInformationRequested2 .class
})
public class TrainingComponentInformationRequested {

    @XmlElement(name = "ShowClassifications")
    protected Boolean showClassifications;
    @XmlElement(name = "ShowCompletionMapping")
    protected Boolean showCompletionMapping;
    @XmlElement(name = "ShowComponents")
    protected Boolean showComponents;
    @XmlElement(name = "ShowContacts")
    protected Boolean showContacts;
    @XmlElement(name = "ShowCurrencyPeriods")
    protected Boolean showCurrencyPeriods;
    @XmlElement(name = "ShowDataManagers")
    protected Boolean showDataManagers;
    @XmlElement(name = "ShowFiles")
    protected Boolean showFiles;
    @XmlElement(name = "ShowMappingInformation")
    protected Boolean showMappingInformation;
    @XmlElement(name = "ShowRecognitionManagers")
    protected Boolean showRecognitionManagers;
    @XmlElement(name = "ShowReleases")
    protected Boolean showReleases;
    @XmlElement(name = "ShowUnitGrid")
    protected Boolean showUnitGrid;
    @XmlElement(name = "ShowUsageRecommendation")
    protected Boolean showUsageRecommendation;

    /**
     * Gets the value of the showClassifications property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isShowClassifications() {
        return showClassifications;
    }

    /**
     * Sets the value of the showClassifications property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setShowClassifications(Boolean value) {
        this.showClassifications = value;
    }

    /**
     * Gets the value of the showCompletionMapping property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isShowCompletionMapping() {
        return showCompletionMapping;
    }

    /**
     * Sets the value of the showCompletionMapping property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setShowCompletionMapping(Boolean value) {
        this.showCompletionMapping = value;
    }

    /**
     * Gets the value of the showComponents property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isShowComponents() {
        return showComponents;
    }

    /**
     * Sets the value of the showComponents property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setShowComponents(Boolean value) {
        this.showComponents = value;
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
     * Gets the value of the showCurrencyPeriods property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isShowCurrencyPeriods() {
        return showCurrencyPeriods;
    }

    /**
     * Sets the value of the showCurrencyPeriods property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setShowCurrencyPeriods(Boolean value) {
        this.showCurrencyPeriods = value;
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
     * Gets the value of the showFiles property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isShowFiles() {
        return showFiles;
    }

    /**
     * Sets the value of the showFiles property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setShowFiles(Boolean value) {
        this.showFiles = value;
    }

    /**
     * Gets the value of the showMappingInformation property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isShowMappingInformation() {
        return showMappingInformation;
    }

    /**
     * Sets the value of the showMappingInformation property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setShowMappingInformation(Boolean value) {
        this.showMappingInformation = value;
    }

    /**
     * Gets the value of the showRecognitionManagers property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isShowRecognitionManagers() {
        return showRecognitionManagers;
    }

    /**
     * Sets the value of the showRecognitionManagers property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setShowRecognitionManagers(Boolean value) {
        this.showRecognitionManagers = value;
    }

    /**
     * Gets the value of the showReleases property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isShowReleases() {
        return showReleases;
    }

    /**
     * Sets the value of the showReleases property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setShowReleases(Boolean value) {
        this.showReleases = value;
    }

    /**
     * Gets the value of the showUnitGrid property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isShowUnitGrid() {
        return showUnitGrid;
    }

    /**
     * Sets the value of the showUnitGrid property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setShowUnitGrid(Boolean value) {
        this.showUnitGrid = value;
    }

    /**
     * Gets the value of the showUsageRecommendation property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isShowUsageRecommendation() {
        return showUsageRecommendation;
    }

    /**
     * Sets the value of the showUsageRecommendation property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setShowUsageRecommendation(Boolean value) {
        this.showUsageRecommendation = value;
    }

}
