
package au.gov.training.services.trainingcomponent;

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
 *         &lt;element name="SearchResult" type="{http://training.gov.au/services/}TrainingComponentSearchResult" minOccurs="0"/>
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
    "searchResult"
})
@XmlRootElement(name = "SearchResponse")
public class SearchResponse {

    @XmlElementRef(name = "SearchResult", namespace = "http://training.gov.au/services/", type = JAXBElement.class, required = false)
    protected JAXBElement<TrainingComponentSearchResult> searchResult;

    /**
     * Gets the value of the searchResult property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link TrainingComponentSearchResult }{@code >}
     *     
     */
    public JAXBElement<TrainingComponentSearchResult> getSearchResult() {
        return searchResult;
    }

    /**
     * Sets the value of the searchResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link TrainingComponentSearchResult }{@code >}
     *     
     */
    public void setSearchResult(JAXBElement<TrainingComponentSearchResult> value) {
        this.searchResult = value;
    }

}
