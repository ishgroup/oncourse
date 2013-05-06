
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
 *         &lt;element name="GetLookupResult" type="{http://training.gov.au/services/}ArrayOfLookup" minOccurs="0"/>
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
    "getLookupResult"
})
@XmlRootElement(name = "GetLookupResponse")
public class GetLookupResponse {

    @XmlElementRef(name = "GetLookupResult", namespace = "http://training.gov.au/services/", type = JAXBElement.class, required = false)
    protected JAXBElement<ArrayOfLookup> getLookupResult;

    /**
     * Gets the value of the getLookupResult property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfLookup }{@code >}
     *     
     */
    public JAXBElement<ArrayOfLookup> getGetLookupResult() {
        return getLookupResult;
    }

    /**
     * Sets the value of the getLookupResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfLookup }{@code >}
     *     
     */
    public void setGetLookupResult(JAXBElement<ArrayOfLookup> value) {
        this.getLookupResult = value;
    }

}
