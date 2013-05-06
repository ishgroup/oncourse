
package au.gov.training.services.organisation;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="SearchByModifiedDateResult" type="{http://training.gov.au/services/}OrganisationSearchResult" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "searchByModifiedDateResult"
})
@XmlRootElement(name = "SearchByModifiedDateResponse")
public class SearchByModifiedDateResponse {

    @XmlElementRef(name = "SearchByModifiedDateResult", namespace = "http://training.gov.au/services/", type = JAXBElement.class, required = false)
    protected JAXBElement<OrganisationSearchResult> searchByModifiedDateResult;

    /**
     * Gets the value of the searchByModifiedDateResult property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link OrganisationSearchResult }{@code >}
     *     
     */
    public JAXBElement<OrganisationSearchResult> getSearchByModifiedDateResult() {
        return searchByModifiedDateResult;
    }

    /**
     * Sets the value of the searchByModifiedDateResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link OrganisationSearchResult }{@code >}
     *     
     */
    public void setSearchByModifiedDateResult(JAXBElement<OrganisationSearchResult> value) {
        this.searchByModifiedDateResult = value;
    }

}
