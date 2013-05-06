
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
 *         &lt;element name="GetDataManagersResult" type="{http://training.gov.au/services/}ArrayOfDataManager" minOccurs="0"/>
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
    "getDataManagersResult"
})
@XmlRootElement(name = "GetDataManagersResponse")
public class GetDataManagersResponse {

    @XmlElementRef(name = "GetDataManagersResult", namespace = "http://training.gov.au/services/", type = JAXBElement.class, required = false)
    protected JAXBElement<ArrayOfDataManager> getDataManagersResult;

    /**
     * Gets the value of the getDataManagersResult property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfDataManager }{@code >}
     *     
     */
    public JAXBElement<ArrayOfDataManager> getGetDataManagersResult() {
        return getDataManagersResult;
    }

    /**
     * Sets the value of the getDataManagersResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfDataManager }{@code >}
     *     
     */
    public void setGetDataManagersResult(JAXBElement<ArrayOfDataManager> value) {
        this.getDataManagersResult = value;
    }

}
