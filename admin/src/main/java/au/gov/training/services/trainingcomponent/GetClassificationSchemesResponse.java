
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
 *         &lt;element name="GetClassificationSchemesResult" type="{http://training.gov.au/services/}ArrayOfNrtClassificationSchemeResult" minOccurs="0"/>
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
    protected JAXBElement<ArrayOfNrtClassificationSchemeResult> getClassificationSchemesResult;

    /**
     * Gets the value of the getClassificationSchemesResult property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfNrtClassificationSchemeResult }{@code >}
     *     
     */
    public JAXBElement<ArrayOfNrtClassificationSchemeResult> getGetClassificationSchemesResult() {
        return getClassificationSchemesResult;
    }

    /**
     * Sets the value of the getClassificationSchemesResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfNrtClassificationSchemeResult }{@code >}
     *     
     */
    public void setGetClassificationSchemesResult(JAXBElement<ArrayOfNrtClassificationSchemeResult> value) {
        this.getClassificationSchemesResult = value;
    }

}
