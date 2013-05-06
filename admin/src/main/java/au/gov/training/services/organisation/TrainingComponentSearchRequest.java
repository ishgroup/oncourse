
package au.gov.training.services.organisation;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for TrainingComponentSearchRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TrainingComponentSearchRequest">
 *   &lt;complexContent>
 *     &lt;extension base="{http://training.gov.au/services/}AbstractPageRequest">
 *       &lt;sequence>
 *         &lt;element name="ClassificationFilters" type="{http://training.gov.au/services/}ClassificationFilters" minOccurs="0"/>
 *         &lt;element name="Filter" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="IncludeDeleted" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="IncludeSuperseeded" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="SearchCode" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="SearchTitle" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="TrainingComponentTypes" type="{http://training.gov.au/services/}TrainingComponentTypeFilter" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TrainingComponentSearchRequest", propOrder = {
    "classificationFilters",
    "filter",
    "includeDeleted",
    "includeSuperseeded",
    "searchCode",
    "searchTitle",
    "trainingComponentTypes"
})
public class TrainingComponentSearchRequest
    extends AbstractPageRequest
{

    @XmlElementRef(name = "ClassificationFilters", namespace = "http://training.gov.au/services/", type = JAXBElement.class, required = false)
    protected JAXBElement<ClassificationFilters> classificationFilters;
    @XmlElement(name = "Filter", required = true, nillable = true)
    protected String filter;
    @XmlElement(name = "IncludeDeleted")
    protected Boolean includeDeleted;
    @XmlElement(name = "IncludeSuperseeded")
    protected Boolean includeSuperseeded;
    @XmlElement(name = "SearchCode")
    protected Boolean searchCode;
    @XmlElement(name = "SearchTitle")
    protected Boolean searchTitle;
    @XmlElementRef(name = "TrainingComponentTypes", namespace = "http://training.gov.au/services/", type = JAXBElement.class, required = false)
    protected JAXBElement<TrainingComponentTypeFilter> trainingComponentTypes;

    /**
     * Gets the value of the classificationFilters property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ClassificationFilters }{@code >}
     *     
     */
    public JAXBElement<ClassificationFilters> getClassificationFilters() {
        return classificationFilters;
    }

    /**
     * Sets the value of the classificationFilters property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ClassificationFilters }{@code >}
     *     
     */
    public void setClassificationFilters(JAXBElement<ClassificationFilters> value) {
        this.classificationFilters = value;
    }

    /**
     * Gets the value of the filter property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFilter() {
        return filter;
    }

    /**
     * Sets the value of the filter property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFilter(String value) {
        this.filter = value;
    }

    /**
     * Gets the value of the includeDeleted property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIncludeDeleted() {
        return includeDeleted;
    }

    /**
     * Sets the value of the includeDeleted property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIncludeDeleted(Boolean value) {
        this.includeDeleted = value;
    }

    /**
     * Gets the value of the includeSuperseeded property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIncludeSuperseeded() {
        return includeSuperseeded;
    }

    /**
     * Sets the value of the includeSuperseeded property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIncludeSuperseeded(Boolean value) {
        this.includeSuperseeded = value;
    }

    /**
     * Gets the value of the searchCode property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isSearchCode() {
        return searchCode;
    }

    /**
     * Sets the value of the searchCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setSearchCode(Boolean value) {
        this.searchCode = value;
    }

    /**
     * Gets the value of the searchTitle property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isSearchTitle() {
        return searchTitle;
    }

    /**
     * Sets the value of the searchTitle property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setSearchTitle(Boolean value) {
        this.searchTitle = value;
    }

    /**
     * Gets the value of the trainingComponentTypes property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link TrainingComponentTypeFilter }{@code >}
     *     
     */
    public JAXBElement<TrainingComponentTypeFilter> getTrainingComponentTypes() {
        return trainingComponentTypes;
    }

    /**
     * Sets the value of the trainingComponentTypes property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link TrainingComponentTypeFilter }{@code >}
     *     
     */
    public void setTrainingComponentTypes(JAXBElement<TrainingComponentTypeFilter> value) {
        this.trainingComponentTypes = value;
    }

}
