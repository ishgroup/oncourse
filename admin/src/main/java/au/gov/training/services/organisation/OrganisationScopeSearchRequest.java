
package au.gov.training.services.organisation;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;
import com.microsoft.schemas._2003._10.serialization.arrays.ArrayOfstring;


/**
 * <p>Java class for OrganisationScopeSearchRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="OrganisationScopeSearchRequest">
 *   &lt;complexContent>
 *     &lt;extension base="{http://training.gov.au/services/}AbstractPageRequest">
 *       &lt;sequence>
 *         &lt;element name="Filter" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="IncludeImplicitScope" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="RegistrationManagers" type="{http://schemas.microsoft.com/2003/10/Serialization/Arrays}ArrayOfstring" minOccurs="0"/>
 *         &lt;element name="SearchTrainingPackageContents" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OrganisationScopeSearchRequest", propOrder = {
    "filter",
    "includeImplicitScope",
    "registrationManagers",
    "searchTrainingPackageContents"
})
public class OrganisationScopeSearchRequest
    extends AbstractPageRequest
{

    @XmlElement(name = "Filter", required = true, nillable = true)
    protected String filter;
    @XmlElement(name = "IncludeImplicitScope")
    protected Boolean includeImplicitScope;
    @XmlElementRef(name = "RegistrationManagers", namespace = "http://training.gov.au/services/", type = JAXBElement.class, required = false)
    protected JAXBElement<ArrayOfstring> registrationManagers;
    @XmlElement(name = "SearchTrainingPackageContents")
    protected Boolean searchTrainingPackageContents;

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
     * Gets the value of the includeImplicitScope property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIncludeImplicitScope() {
        return includeImplicitScope;
    }

    /**
     * Sets the value of the includeImplicitScope property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIncludeImplicitScope(Boolean value) {
        this.includeImplicitScope = value;
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

    /**
     * Gets the value of the searchTrainingPackageContents property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isSearchTrainingPackageContents() {
        return searchTrainingPackageContents;
    }

    /**
     * Sets the value of the searchTrainingPackageContents property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setSearchTrainingPackageContents(Boolean value) {
        this.searchTrainingPackageContents = value;
    }

}
