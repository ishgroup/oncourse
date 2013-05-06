
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
 *         &lt;element name="GetClassificationPurposesResult" type="{http://training.gov.au/services/}ArrayOfClassificationPurpose" minOccurs="0"/>
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
    "getClassificationPurposesResult"
})
@XmlRootElement(name = "GetClassificationPurposesResponse")
public class GetClassificationPurposesResponse {

    @XmlElementRef(name = "GetClassificationPurposesResult", namespace = "http://training.gov.au/services/", type = JAXBElement.class, required = false)
    protected JAXBElement<ArrayOfClassificationPurpose> getClassificationPurposesResult;

    /**
     * Gets the value of the getClassificationPurposesResult property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfClassificationPurpose }{@code >}
     *     
     */
    public JAXBElement<ArrayOfClassificationPurpose> getGetClassificationPurposesResult() {
        return getClassificationPurposesResult;
    }

    /**
     * Sets the value of the getClassificationPurposesResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfClassificationPurpose }{@code >}
     *     
     */
    public void setGetClassificationPurposesResult(JAXBElement<ArrayOfClassificationPurpose> value) {
        this.getClassificationPurposesResult = value;
    }

}
