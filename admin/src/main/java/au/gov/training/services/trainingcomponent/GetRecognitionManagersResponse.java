
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
 *         &lt;element name="GetRecognitionManagersResult" type="{http://training.gov.au/services/}ArrayOfRecognitionManager" minOccurs="0"/>
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
    "getRecognitionManagersResult"
})
@XmlRootElement(name = "GetRecognitionManagersResponse")
public class GetRecognitionManagersResponse {

    @XmlElementRef(name = "GetRecognitionManagersResult", namespace = "http://training.gov.au/services/", type = JAXBElement.class, required = false)
    protected JAXBElement<ArrayOfRecognitionManager> getRecognitionManagersResult;

    /**
     * Gets the value of the getRecognitionManagersResult property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfRecognitionManager }{@code >}
     *     
     */
    public JAXBElement<ArrayOfRecognitionManager> getGetRecognitionManagersResult() {
        return getRecognitionManagersResult;
    }

    /**
     * Sets the value of the getRecognitionManagersResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfRecognitionManager }{@code >}
     *     
     */
    public void setGetRecognitionManagersResult(JAXBElement<ArrayOfRecognitionManager> value) {
        this.getRecognitionManagersResult = value;
    }

}
