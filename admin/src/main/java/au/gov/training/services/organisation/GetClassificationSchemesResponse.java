
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
 *         &lt;element name="GetClassificationSchemesResult" type="{http://training.gov.au/services/}ArrayOfRtoClassificationSchemeResult" minOccurs="0"/>
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
    "getClassificationSchemesResult"
})
@XmlRootElement(name = "GetClassificationSchemesResponse")
public class GetClassificationSchemesResponse {

    @XmlElementRef(name = "GetClassificationSchemesResult", namespace = "http://training.gov.au/services/", type = JAXBElement.class, required = false)
    protected JAXBElement<ArrayOfRtoClassificationSchemeResult> getClassificationSchemesResult;

    /**
     * Gets the value of the getClassificationSchemesResult property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfRtoClassificationSchemeResult }{@code >}
     *     
     */
    public JAXBElement<ArrayOfRtoClassificationSchemeResult> getGetClassificationSchemesResult() {
        return getClassificationSchemesResult;
    }

    /**
     * Sets the value of the getClassificationSchemesResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfRtoClassificationSchemeResult }{@code >}
     *     
     */
    public void setGetClassificationSchemesResult(JAXBElement<ArrayOfRtoClassificationSchemeResult> value) {
        this.getClassificationSchemesResult = value;
    }

}
