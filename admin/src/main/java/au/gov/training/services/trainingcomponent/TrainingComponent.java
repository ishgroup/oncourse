
package au.gov.training.services.trainingcomponent;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlType;
import org.datacontract.schemas._2004._07.system.DateTimeOffset;


/**
 * <p>Java class for TrainingComponent complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TrainingComponent">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Classifications" type="{http://training.gov.au/services/}ArrayOfClassification" minOccurs="0"/>
 *         &lt;element name="Code" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="CompletionMapping" type="{http://training.gov.au/services/}ArrayOfNrtCompletion" minOccurs="0"/>
 *         &lt;element name="ComponentType" type="{http://training.gov.au/services/}TrainingComponentTypes" minOccurs="0"/>
 *         &lt;element name="Contacts" type="{http://training.gov.au/services/}ArrayOfContact" minOccurs="0"/>
 *         &lt;element name="CreatedDate" type="{http://schemas.datacontract.org/2004/07/System}DateTimeOffset" minOccurs="0"/>
 *         &lt;element name="CurrencyPeriods" type="{http://training.gov.au/services/}ArrayOfNrtCurrencyPeriod" minOccurs="0"/>
 *         &lt;element name="DataManagers" type="{http://training.gov.au/services/}ArrayOfDataManagerAssignment" minOccurs="0"/>
 *         &lt;element name="IsConfidential" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="IscOrganisationCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="MappingInformation" type="{http://training.gov.au/services/}ArrayOfMapping" minOccurs="0"/>
 *         &lt;element name="ParentCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ParentTitle" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="RecognitionManagers" type="{http://training.gov.au/services/}ArrayOfRecognitionManagerAssignment" minOccurs="0"/>
 *         &lt;element name="Releases" type="{http://training.gov.au/services/}ArrayOfRelease" minOccurs="0"/>
 *         &lt;element name="Title" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="UpdatedDate" type="{http://schemas.datacontract.org/2004/07/System}DateTimeOffset" minOccurs="0"/>
 *         &lt;element name="UsageRecommendations" type="{http://training.gov.au/services/}ArrayOfUsageRecommendation" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TrainingComponent", propOrder = {
    "classifications",
    "code",
    "completionMapping",
    "componentType",
    "contacts",
    "createdDate",
    "currencyPeriods",
    "dataManagers",
    "isConfidential",
    "iscOrganisationCode",
    "mappingInformation",
    "parentCode",
    "parentTitle",
    "recognitionManagers",
    "releases",
    "title",
    "updatedDate",
    "usageRecommendations"
})
public class TrainingComponent {

    @XmlElementRef(name = "Classifications", namespace = "http://training.gov.au/services/", type = JAXBElement.class)
    protected JAXBElement<ArrayOfClassification> classifications;
    @XmlElementRef(name = "Code", namespace = "http://training.gov.au/services/", type = JAXBElement.class)
    protected JAXBElement<String> code;
    @XmlElementRef(name = "CompletionMapping", namespace = "http://training.gov.au/services/", type = JAXBElement.class)
    protected JAXBElement<ArrayOfNrtCompletion> completionMapping;
    @XmlList
    @XmlElement(name = "ComponentType")
    protected List<String> componentType;
    @XmlElementRef(name = "Contacts", namespace = "http://training.gov.au/services/", type = JAXBElement.class)
    protected JAXBElement<ArrayOfContact> contacts;
    @XmlElement(name = "CreatedDate")
    protected DateTimeOffset createdDate;
    @XmlElementRef(name = "CurrencyPeriods", namespace = "http://training.gov.au/services/", type = JAXBElement.class)
    protected JAXBElement<ArrayOfNrtCurrencyPeriod> currencyPeriods;
    @XmlElementRef(name = "DataManagers", namespace = "http://training.gov.au/services/", type = JAXBElement.class)
    protected JAXBElement<ArrayOfDataManagerAssignment> dataManagers;
    @XmlElement(name = "IsConfidential")
    protected Boolean isConfidential;
    @XmlElementRef(name = "IscOrganisationCode", namespace = "http://training.gov.au/services/", type = JAXBElement.class)
    protected JAXBElement<String> iscOrganisationCode;
    @XmlElementRef(name = "MappingInformation", namespace = "http://training.gov.au/services/", type = JAXBElement.class)
    protected JAXBElement<ArrayOfMapping> mappingInformation;
    @XmlElementRef(name = "ParentCode", namespace = "http://training.gov.au/services/", type = JAXBElement.class)
    protected JAXBElement<String> parentCode;
    @XmlElementRef(name = "ParentTitle", namespace = "http://training.gov.au/services/", type = JAXBElement.class)
    protected JAXBElement<String> parentTitle;
    @XmlElementRef(name = "RecognitionManagers", namespace = "http://training.gov.au/services/", type = JAXBElement.class)
    protected JAXBElement<ArrayOfRecognitionManagerAssignment> recognitionManagers;
    @XmlElementRef(name = "Releases", namespace = "http://training.gov.au/services/", type = JAXBElement.class)
    protected JAXBElement<ArrayOfRelease> releases;
    @XmlElementRef(name = "Title", namespace = "http://training.gov.au/services/", type = JAXBElement.class)
    protected JAXBElement<String> title;
    @XmlElement(name = "UpdatedDate")
    protected DateTimeOffset updatedDate;
    @XmlElementRef(name = "UsageRecommendations", namespace = "http://training.gov.au/services/", type = JAXBElement.class)
    protected JAXBElement<ArrayOfUsageRecommendation> usageRecommendations;

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
        this.classifications = ((JAXBElement<ArrayOfClassification> ) value);
    }

    /**
     * Gets the value of the code property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getCode() {
        return code;
    }

    /**
     * Sets the value of the code property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setCode(JAXBElement<String> value) {
        this.code = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the completionMapping property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfNrtCompletion }{@code >}
     *     
     */
    public JAXBElement<ArrayOfNrtCompletion> getCompletionMapping() {
        return completionMapping;
    }

    /**
     * Sets the value of the completionMapping property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfNrtCompletion }{@code >}
     *     
     */
    public void setCompletionMapping(JAXBElement<ArrayOfNrtCompletion> value) {
        this.completionMapping = ((JAXBElement<ArrayOfNrtCompletion> ) value);
    }

    /**
     * Gets the value of the componentType property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the componentType property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getComponentType().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getComponentType() {
        if (componentType == null) {
            componentType = new ArrayList<>();
        }
        return this.componentType;
    }

    /**
     * Gets the value of the contacts property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfContact }{@code >}
     *     
     */
    public JAXBElement<ArrayOfContact> getContacts() {
        return contacts;
    }

    /**
     * Sets the value of the contacts property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfContact }{@code >}
     *     
     */
    public void setContacts(JAXBElement<ArrayOfContact> value) {
        this.contacts = ((JAXBElement<ArrayOfContact> ) value);
    }

    /**
     * Gets the value of the createdDate property.
     * 
     * @return
     *     possible object is
     *     {@link DateTimeOffset }
     *     
     */
    public DateTimeOffset getCreatedDate() {
        return createdDate;
    }

    /**
     * Sets the value of the createdDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link DateTimeOffset }
     *     
     */
    public void setCreatedDate(DateTimeOffset value) {
        this.createdDate = value;
    }

    /**
     * Gets the value of the currencyPeriods property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfNrtCurrencyPeriod }{@code >}
     *     
     */
    public JAXBElement<ArrayOfNrtCurrencyPeriod> getCurrencyPeriods() {
        return currencyPeriods;
    }

    /**
     * Sets the value of the currencyPeriods property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfNrtCurrencyPeriod }{@code >}
     *     
     */
    public void setCurrencyPeriods(JAXBElement<ArrayOfNrtCurrencyPeriod> value) {
        this.currencyPeriods = ((JAXBElement<ArrayOfNrtCurrencyPeriod> ) value);
    }

    /**
     * Gets the value of the dataManagers property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfDataManagerAssignment }{@code >}
     *     
     */
    public JAXBElement<ArrayOfDataManagerAssignment> getDataManagers() {
        return dataManagers;
    }

    /**
     * Sets the value of the dataManagers property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfDataManagerAssignment }{@code >}
     *     
     */
    public void setDataManagers(JAXBElement<ArrayOfDataManagerAssignment> value) {
        this.dataManagers = ((JAXBElement<ArrayOfDataManagerAssignment> ) value);
    }

    /**
     * Gets the value of the isConfidential property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIsConfidential() {
        return isConfidential;
    }

    /**
     * Sets the value of the isConfidential property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsConfidential(Boolean value) {
        this.isConfidential = value;
    }

    /**
     * Gets the value of the iscOrganisationCode property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getIscOrganisationCode() {
        return iscOrganisationCode;
    }

    /**
     * Sets the value of the iscOrganisationCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setIscOrganisationCode(JAXBElement<String> value) {
        this.iscOrganisationCode = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the mappingInformation property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfMapping }{@code >}
     *     
     */
    public JAXBElement<ArrayOfMapping> getMappingInformation() {
        return mappingInformation;
    }

    /**
     * Sets the value of the mappingInformation property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfMapping }{@code >}
     *     
     */
    public void setMappingInformation(JAXBElement<ArrayOfMapping> value) {
        this.mappingInformation = ((JAXBElement<ArrayOfMapping> ) value);
    }

    /**
     * Gets the value of the parentCode property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getParentCode() {
        return parentCode;
    }

    /**
     * Sets the value of the parentCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setParentCode(JAXBElement<String> value) {
        this.parentCode = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the parentTitle property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getParentTitle() {
        return parentTitle;
    }

    /**
     * Sets the value of the parentTitle property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setParentTitle(JAXBElement<String> value) {
        this.parentTitle = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the recognitionManagers property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfRecognitionManagerAssignment }{@code >}
     *     
     */
    public JAXBElement<ArrayOfRecognitionManagerAssignment> getRecognitionManagers() {
        return recognitionManagers;
    }

    /**
     * Sets the value of the recognitionManagers property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfRecognitionManagerAssignment }{@code >}
     *     
     */
    public void setRecognitionManagers(JAXBElement<ArrayOfRecognitionManagerAssignment> value) {
        this.recognitionManagers = ((JAXBElement<ArrayOfRecognitionManagerAssignment> ) value);
    }

    /**
     * Gets the value of the releases property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfRelease }{@code >}
     *     
     */
    public JAXBElement<ArrayOfRelease> getReleases() {
        return releases;
    }

    /**
     * Sets the value of the releases property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfRelease }{@code >}
     *     
     */
    public void setReleases(JAXBElement<ArrayOfRelease> value) {
        this.releases = ((JAXBElement<ArrayOfRelease> ) value);
    }

    /**
     * Gets the value of the title property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getTitle() {
        return title;
    }

    /**
     * Sets the value of the title property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setTitle(JAXBElement<String> value) {
        this.title = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the updatedDate property.
     * 
     * @return
     *     possible object is
     *     {@link DateTimeOffset }
     *     
     */
    public DateTimeOffset getUpdatedDate() {
        return updatedDate;
    }

    /**
     * Sets the value of the updatedDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link DateTimeOffset }
     *     
     */
    public void setUpdatedDate(DateTimeOffset value) {
        this.updatedDate = value;
    }

    /**
     * Gets the value of the usageRecommendations property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfUsageRecommendation }{@code >}
     *     
     */
    public JAXBElement<ArrayOfUsageRecommendation> getUsageRecommendations() {
        return usageRecommendations;
    }

    /**
     * Sets the value of the usageRecommendations property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfUsageRecommendation }{@code >}
     *     
     */
    public void setUsageRecommendations(JAXBElement<ArrayOfUsageRecommendation> value) {
        this.usageRecommendations = ((JAXBElement<ArrayOfUsageRecommendation> ) value);
    }

}
