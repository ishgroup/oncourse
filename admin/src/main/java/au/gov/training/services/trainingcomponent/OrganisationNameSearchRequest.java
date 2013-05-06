
package au.gov.training.services.trainingcomponent;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;
import com.microsoft.schemas._2003._10.serialization.arrays.ArrayOfstring;


/**
 * <p>Java class for OrganisationNameSearchRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="OrganisationNameSearchRequest">
 *   &lt;complexContent>
 *     &lt;extension base="{http://training.gov.au/services/}AbstractPageRequest">
 *       &lt;sequence>
 *         &lt;element name="ClassificationFilters" type="{http://training.gov.au/services/}ClassificationFilters" minOccurs="0"/>
 *         &lt;element name="CurrentNamesOnly" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="ExcludeNotRtos" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="ExcludeRtoWithoutActiveRegistration" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="Filter" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="IncludeCode" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="RegistrationManagers" type="{http://schemas.microsoft.com/2003/10/Serialization/Arrays}ArrayOfstring" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OrganisationNameSearchRequest", propOrder = {
    "classificationFilters",
    "currentNamesOnly",
    "excludeNotRtos",
    "excludeRtoWithoutActiveRegistration",
    "filter",
    "includeCode",
    "registrationManagers"
})
public class OrganisationNameSearchRequest
    extends AbstractPageRequest
{

    @XmlElementRef(name = "ClassificationFilters", namespace = "http://training.gov.au/services/", type = JAXBElement.class, required = false)
    protected JAXBElement<ClassificationFilters> classificationFilters;
    @XmlElement(name = "CurrentNamesOnly")
    protected Boolean currentNamesOnly;
    @XmlElement(name = "ExcludeNotRtos")
    protected Boolean excludeNotRtos;
    @XmlElement(name = "ExcludeRtoWithoutActiveRegistration")
    protected Boolean excludeRtoWithoutActiveRegistration;
    @XmlElement(name = "Filter", required = true, nillable = true)
    protected String filter;
    @XmlElement(name = "IncludeCode")
    protected Boolean includeCode;
    @XmlElementRef(name = "RegistrationManagers", namespace = "http://training.gov.au/services/", type = JAXBElement.class, required = false)
    protected JAXBElement<ArrayOfstring> registrationManagers;

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
     * Gets the value of the currentNamesOnly property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isCurrentNamesOnly() {
        return currentNamesOnly;
    }

    /**
     * Sets the value of the currentNamesOnly property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setCurrentNamesOnly(Boolean value) {
        this.currentNamesOnly = value;
    }

    /**
     * Gets the value of the excludeNotRtos property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isExcludeNotRtos() {
        return excludeNotRtos;
    }

    /**
     * Sets the value of the excludeNotRtos property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setExcludeNotRtos(Boolean value) {
        this.excludeNotRtos = value;
    }

    /**
     * Gets the value of the excludeRtoWithoutActiveRegistration property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isExcludeRtoWithoutActiveRegistration() {
        return excludeRtoWithoutActiveRegistration;
    }

    /**
     * Sets the value of the excludeRtoWithoutActiveRegistration property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setExcludeRtoWithoutActiveRegistration(Boolean value) {
        this.excludeRtoWithoutActiveRegistration = value;
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
     * Gets the value of the includeCode property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIncludeCode() {
        return includeCode;
    }

    /**
     * Sets the value of the includeCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIncludeCode(Boolean value) {
        this.includeCode = value;
    }

    /**
     * Gets the value of the registrationManagers property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfstring }{@code >}
     *     
     */
    public JAXBElement<ArrayOfstring> getRegistrationManagers() {
        return registrationManagers;
    }

    /**
     * Sets the value of the registrationManagers property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfstring }{@code >}
     *     
     */
    public void setRegistrationManagers(JAXBElement<ArrayOfstring> value) {
        this.registrationManagers = value;
    }

}
